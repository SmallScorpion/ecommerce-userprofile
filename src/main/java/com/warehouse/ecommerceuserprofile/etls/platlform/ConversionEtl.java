package com.warehouse.ecommerceuserprofile.etls.platlform;

import com.warehouse.ecommerceuserprofile.utils.SparkUtils;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * 用户行为转化率分析（漏斗图）
 */
public class ConversionEtl {
    public static void main(String[] args) {
        SparkSession session = SparkUtils.initSession();

        ConversionVo conversionVo = behaviorConversionCountEtl(session);

        System.out.println(conversionVo);
    }

    private static ConversionVo behaviorConversionCountEtl(SparkSession session){
        // 1. 查询order表，提取下单的用户
        Dataset<Row> orderMember = session.sql(
                "select distinct(member_id) from ecommerce.t_order where order_status = 2");

        // 2. 查询order表，提取复购的用户
        Dataset<Row> orderAgainMember = session.sql(
                "select t.member_id as member_id" +
                        " from (select count(order_id) as orderCount, member_id from ecommerce.t_order " +
                        " where order_status = 2 group by member_id) as t where t.orderCount >= 2");

        // 3. 查询coupon_member表，提取购买优惠券的用户
        Dataset<Row> chargeMember = session.sql(
                "select distinct(member_id) as member_id from ecommerce.t_coupon_member " +
                        " where coupon_id != 1 and coupon_channel = 1");

        // 因为储值的用户，不一定是复购的用户，所以做转化率分析时要取交集
        Dataset<Row> chargeAndOrderAgainMember = chargeMember.join(
                orderAgainMember,
                orderAgainMember.col("member_id").equalTo(chargeMember.col("member_id")),
                "inner"
        );

        // 统计各层的数量
        long orderCount = orderMember.count();
        long orderAgainCount = orderAgainMember.count();
        long chargeCount = chargeAndOrderAgainMember.count();

        // 包装成VO
        ConversionVo conversionVo = new ConversionVo();
        conversionVo.setView(1000L);
        conversionVo.setClick(800L);
        conversionVo.setOrder(orderCount);
        conversionVo.setOrderAgain(orderAgainCount);
        conversionVo.setChargeCoupon(chargeCount);

        return conversionVo;
    }

    // 定义一个VO，保存当前平台用户某种行为的总量
    @Data
    static class ConversionVo{
        private Long view;    // 浏览行为
        private Long click;   // 点击行为（查看详情）
        private Long order;    // 下单购买行为
        private Long orderAgain;    // 复购行为
        private Long chargeCoupon;    // 购买优惠券，储值行为
    }
}
