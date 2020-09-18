package com.warehouse.ecommerceuserprofile.etls.platlform;

import com.alibaba.fastjson.JSONObject;
import com.warehouse.ecommerceuserprofile.utils.DateStyle;
import com.warehouse.ecommerceuserprofile.utils.DateUtil;
import com.warehouse.ecommerceuserprofile.utils.SparkUtils;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 平台近期数据增量(折线图)
 */
public class GrowthEtl {
    public static void main(String[] args) {
        SparkSession session = SparkUtils.initSession();
        // 自定义方法，提取每一天的数据，作为List返回
        List<EveryDayCountVo> everyDayCountVoList = growthEtl(session);
        // 打印输出
        System.out.println(everyDayCountVoList);
    }

    // 自定义VO，主要是平台用户每天的统计数据
    @Data
    static class EveryDayCountVo {
        private String day;    // 当前的日期
        private Integer newRegCount;    // 当天新增注册人数
        private Integer totalMemberCount;    // 当天为止，平台总注册人数
        private Integer totalOrderCount;    // 当天为止，平台总订单数
        private BigDecimal gmv;    // 当天为止，平台订单交易总流水金额
    }

    private static List<EveryDayCountVo> growthEtl(SparkSession session){
        // 因为只考虑近7天之内，所以应该要有日期时间的判断
        LocalDate now = LocalDate.of(2019, Month.NOVEMBER, 30);
        Date nowDay = Date.from( now.atStartOfDay(ZoneId.systemDefault()).toInstant() );
        Date sevenDayBefore = DateUtil.addDay(nowDay, -7);

        // 1. 统计近七天每天用户注册量（新增和总量）
        String memberEtlSql = "select date_format(create_time, 'yyyy-MM-dd') as day, " +
                " count(id) as newRegCount, max(id) as totalMemberCount " +
                " from ecommerce.t_member where create_time >= '%s' " +
                " group by date_format(create_time, 'yyyy-MM-dd') order by day";
        memberEtlSql = String.format(memberEtlSql, DateUtil.DateToString( sevenDayBefore, DateStyle.YYYY_MM_DD_HH_MM_SS ));

        Dataset<Row> memberRegDs = session.sql(memberEtlSql);

        // 2. 统计近七天每天用户订单数量和总流水（新增流水）
        String orderEtlSql = "select date_format(create_time, 'yyyy-MM-dd') as day, " +
                " max(order_id) as totalOrderCount, sum(origin_price) as gmv " +
                " from ecommerce.t_order where create_time >= '%s' " +
                " group by date_format(create_time, 'yyyy-MM-dd') order by day";

        orderEtlSql = String.format(orderEtlSql, DateUtil.DateToString( sevenDayBefore, DateStyle.YYYY_MM_DD_HH_MM_SS ));

        Dataset<Row> memberOrderDs = session.sql(orderEtlSql);

        // 3. 连接两个查询结果，合并成一个Dataset，转换包装成VO
        Dataset<Tuple2<Row, Row>> tuple2Dataset = memberRegDs.joinWith(
                memberOrderDs,
                memberRegDs.col("day").equalTo(memberOrderDs.col("day")),
                "inner"
        );
        // 先转换成List，然后遍历，取出每一天的数据，转成VO，放入新的list
        List<Tuple2<Row, Row>> tuple2s = tuple2Dataset.collectAsList();
        List<EveryDayCountVo> everyDayCountVos = new ArrayList<>();
        for( Tuple2<Row, Row> tuple2: tuple2s ){
            // 先拿到每一天的数据
            Row row1 = tuple2._1();
            Row row2 = tuple2._2();

            // 定义一个JSONObject，用来存放VO里的每个字段
            JSONObject object = new JSONObject();

            // 提取Row类型里的所有字段
            String[] fields = row1.schema().fieldNames();
            for( String field: fields ){
                Object value = row1.getAs(field);
                object.put(field, value);
            }
            fields = row2.schema().fieldNames();
            for( String field: fields ){
                Object value = row2.getAs(field);
                object.put(field, value);
            }
            // 把当前天的数据包装成VO，放入List中
            EveryDayCountVo everyDayCountVo = object.toJavaObject(EveryDayCountVo.class);

            everyDayCountVos.add(everyDayCountVo);
        }

        // 4. 求出七天前，再之前的所有订单流水总和
        String preGmvSql = "select sum(origin_price) as totalGmv from ecommerce.t_order where create_time < '%s'";
        preGmvSql = String.format(preGmvSql, DateUtil.DateToString( sevenDayBefore, DateStyle.YYYY_MM_DD_HH_MM_SS ));
        Dataset<Row> preGmvDs = session.sql(preGmvSql);
        // 只是一个double类型的结果，取出来
        double previousGmv = preGmvDs.collectAsList().get(0).getDouble(0);
        BigDecimal preGmv = BigDecimal.valueOf(previousGmv);

        // 5. 遍历之前得到的每天数据，在preGmv的基础上叠加，得到gmv的总量
        BigDecimal currentGmv = preGmv;
        for( int i = 0; i < everyDayCountVos.size(); i++ ){
            // 获取每天的统计数据
            EveryDayCountVo everyDayCountVo = everyDayCountVos.get(i);
            currentGmv = currentGmv.add(everyDayCountVo.getGmv());     // 加上当前day的gmv新增量

            everyDayCountVo.setGmv(currentGmv);
        }

        return everyDayCountVos;
    }
}