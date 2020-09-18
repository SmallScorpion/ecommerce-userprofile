package com.warehouse.ecommerceuserprofile.etls.platlform;

import com.alibaba.fastjson.JSON;
import com.warehouse.ecommerceuserprofile.utils.SparkUtils;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息的提取(饼图)
 */
public class MemberEtl {
    public static void main(String[] args) {
        // 1. 初始化一个spark session
        SparkSession session = SparkUtils.initSession();

        // 2. 写sql查询想要的数据
        List<MemberSex> memberSexes = memberSexEtl(session);
        List<MemberChannel> memberChannels = memberChannelEtl(session);
        List<MemberMpSub> memberMpSubs = memberMpSubEtl(session);
        MemberHeat memberHeat = memberHeatEtl(session);

        // 3. 拼成想要展示的结果，提供给前端页面调用
        MemberVo memberVo = new MemberVo();
        memberVo.setMemberSexes(memberSexes);
        memberVo.setMemberChannels(memberChannels);
        memberVo.setMemberMpSubs(memberMpSubs);
        memberVo.setMemberHeat(memberHeat);

        // 直接控制台打印输出
        System.out.println(JSON.toJSONString(memberVo));
    }

    // 统计平台用户性别分布
    public static List<MemberSex> memberSexEtl(SparkSession session){
        // 写sql，查询得到一个DataSet
        Dataset<Row> dataset = session.sql("select sex as memberSex, count(id) as sexCount " +
                " from ecommerce.t_member group by sex");

        // 先将Dataset转换成list
        List<String> list = dataset.toJSON().collectAsList();

        // 将list转成流，进行每一行数据的遍历，转换成MemberSex
        List<MemberSex> result = list.stream()
                .map( str -> JSON.parseObject(str, MemberSex.class) )
                .collect(Collectors.toList());

        return result;
    }

    // 用户注册渠道的分布统计
    public static List<MemberChannel> memberChannelEtl(SparkSession session){
        Dataset<Row> dataset = session.sql("select member_channel as memberChannel, count(id) as channelCount " +
                " from ecommerce.t_member group by member_channel");

        List<String> list = dataset.toJSON().collectAsList();

        // 将list转成流，进行每一行数据的遍历，转换成MemberSex
        List<MemberChannel> result = list.stream()
                .map( str -> JSON.parseObject(str, MemberChannel.class) )
                .collect(Collectors.toList());

        return result;
    }

    // 媒体平台关注分布统计
    public static List<MemberMpSub> memberMpSubEtl(SparkSession session){
        Dataset<Row> dataset = session.sql("select count(if(mp_open_id != 'null',true,null)) as subCount," +
                " count(if(mp_open_id = 'null',true,null)) as unSubCount" +
                " from ecommerce.t_member");

        List<String> list = dataset.toJSON().collectAsList();

        // 将list转成流，进行每一行数据的遍历，转换成MemberSex
        List<MemberMpSub> result = list.stream()
                .map( str -> JSON.parseObject(str, MemberMpSub.class) )
                .collect(Collectors.toList());

        return result;
    }

    // 平台用户热度分布统计
    public static MemberHeat memberHeatEtl(SparkSession session){
        // reg，complete从用户表中提取
        Dataset<Row> reg_complete_count = session.sql(
                "select count(if(phone = 'null', true, null)) as reg, " +
                        " count(if(phone != 'null', true, null)) as complete " +
                        " from ecommerce.t_member");
        // order, orderAgain从订单表中提取
        Dataset<Row> order_andAgain_count = session.sql(
                "select count(if(t.orderCount = 1, true, null)) as order, " +
                        " count(if(t.orderCount >= 2, true, null)) as orderAgain " +
                        " from (select count(order_id) as orderCount, member_id from ecommerce.t_order group by member_id) as t");
        // coupon, 从t_coupon_member表提取，对member_id进行去重，然后count
        Dataset<Row> coupon_count = session.sql(
                "select count(distinct member_id) as coupon from ecommerce.t_coupon_member");

        // 最终将三个查询结果连接在一起，做cross join
        Dataset<Row> heat = coupon_count.crossJoin(reg_complete_count).crossJoin(order_andAgain_count);

        List<String> list = heat.toJSON().collectAsList();

        // 将list转成流，进行每一行数据的遍历，转换成MemberSex
        List<MemberHeat> result = list.stream()
                .map( str -> JSON.parseObject(str, MemberHeat.class) )
                .collect(Collectors.toList());

        return result.get(0);
    }

    // 定义一个最终想要生成的VO，用来展示饼图的数据信息
    @Data
    static class MemberVo{
        // 由四部分构成
        private List<MemberSex> memberSexes;    // 性别统计信息
        private List<MemberChannel> memberChannels;   // 渠道统计信息
        private List<MemberMpSub> memberMpSubs;    // 是否关注媒体平台统计
        private MemberHeat memberHeat;    // 用户热度统计
    }

    @Data
    static class MemberSex{
        private Integer memberSex;    // 性别编号
        private Integer sexCount;    // 当前性别的count数量
    }

    @Data
    static class MemberChannel{
        private Integer memberChannel;    // 渠道编号
        private Integer channelCount;
    }

    @Data
    static class MemberMpSub{
        private Integer subCount;     // 已关注的用户统计数
        private Integer unSubCount;
    }

    @Data
    static class MemberHeat{
        private Integer reg;    // 只注册但未填写手机号的用户统计数
        private Integer complete;    // 完善了信息，填写了手机号的用户统计数
        private Integer order;    // 下过订单的用户统计数
        private Integer orderAgain;     // 复购用户统计数
        private Integer coupon;    // 购买过消费券的用户统计数
    }
}