package com.warehouse.ecommerceuserprofile.etls.platlform;

import com.alibaba.fastjson.JSON;
import com.warehouse.ecommerceuserprofile.utils.DateStyle;
import com.warehouse.ecommerceuserprofile.utils.DateUtil;
import com.warehouse.ecommerceuserprofile.utils.SparkUtils;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  营销提醒统计（饼图）
 */
public class MarketingRemindEtl {
    public static void main(String[] args) {
        SparkSession session = SparkUtils.initSession();

        // 自定义函数，查询近七天内每天的用户获取优惠券数量
        List<FreeCountVo> freeCountVoList = freeCountEtl(session);
        List<CouponCountVo> couponCountVoList = couponCountEtl(session);

        System.out.println(freeCountVoList);
        System.out.println(couponCountVoList);
    }

    private static List<FreeCountVo> freeCountEtl(SparkSession session){
        LocalDate now = LocalDate.of(2019, Month.NOVEMBER, 30);
        Date nowDay = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date sevenDayBefore = DateUtil.addDay(nowDay, -7);

        String freeCountSql = "select date_format(create_time, 'yyyy-MM-dd') as day, " +
                " count(member_id) as freeCount from ecommerce.t_coupon_member " +
                " where coupon_id = 1 and coupon_channel = 2 and create_time >= '%s'" +
                " group by date_format(create_time, 'yyyy-MM-dd')";
        freeCountSql = String.format(freeCountSql, DateUtil.DateToString( sevenDayBefore, DateStyle.YYYY_MM_DD_HH_MM_SS ));

        Dataset<Row> dataset = session.sql(freeCountSql);

        // 将结果数据转换成VO，返回
        List<FreeCountVo> result =  dataset.toJSON().collectAsList()
                .stream()
                .map(str -> JSON.parseObject(str, FreeCountVo.class))
                .collect(Collectors.toList());
        return result;
    }

    private static List<CouponCountVo> couponCountEtl(SparkSession session){
        LocalDate now = LocalDate.of(2019, Month.NOVEMBER, 30);
        Date nowDay = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date sevenDayBefore = DateUtil.addDay(nowDay, -7);

        String couponCountSql = "select date_format(create_time, 'yyyy-MM-dd') as day, " +
                " count(member_id) as couponCount from ecommerce.t_coupon_member " +
                " where coupon_id != 1 and create_time >= '%s'" +
                " group by date_format(create_time, 'yyyy-MM-dd')";
        couponCountSql = String.format(couponCountSql, DateUtil.DateToString( sevenDayBefore, DateStyle.YYYY_MM_DD_HH_MM_SS ));

        Dataset<Row> dataset = session.sql(couponCountSql);

        // 将结果数据转换成VO，返回
        List<CouponCountVo> result =  dataset.toJSON().collectAsList()
                .stream()
                .map(str -> JSON.parseObject(str, CouponCountVo.class))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class FreeCountVo{
        private String day;
        private Integer freeCount;
    }
    @Data
    static class CouponCountVo{
        private String day;
        private Integer couponCount;
    }
}
