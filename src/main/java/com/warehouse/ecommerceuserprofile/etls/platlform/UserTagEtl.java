package com.warehouse.ecommerceuserprofile.etls.platlform;

import com.warehouse.ecommerceuserprofile.utils.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;

public class UserTagEtl {
    public static void main(String[] args) {
        SparkSession session = SparkUtils.initSession();
        // 自定义一个方法，将联表查询得到的用户标签写入es
        userTagEtl(session);
    }

    private static void userTagEtl(SparkSession session) {
        // 1. 从member表里提取用户基本信息标签
        Dataset<Row> memberBase = session.sql(
                "select id as memberId, phone, sex, member_channel as channel, mp_open_id as subOpenId, " +
                        " address_default_id as address, date_format(create_time, 'yyyy-MM-dd') as regTime " +
                        " from ecommerce.t_member");
        // 2. 从订单表和订单商品表中，提取用户的购买行为特征
        Dataset<Row> orderBehavior = session.sql(
                "select o.member_id as memberId, count(o.order_id) as orderCount, " +
                        " date_format( max(o.create_time), 'yyyy-MM-dd' ) as orderTime, " +
                        " sum(o.pay_price) as orderMoney, " +
                        " collect_list(distinct oc.commodity_id) as favGoods " +
                        " from ecommerce.t_order as o left join ecommerce.t_order_commodity as oc " +
                        " on o.order_id = oc.order_id group by o.member_id");

        // 3. 从coupon_member表以及coupon表中，提取用户购买消费券的信息，作为消费能力标签
        // 首单免费券获取时间
        Dataset<Row> freeCoupon = session.sql(
                "select member_id as memberId, date_format(create_time, 'yyyy-MM-dd') as freeCouponTime " +
                        " from ecommerce.t_coupon_member where coupon_id = 1");
        // 多次购买消费券时间
        Dataset<Row> couponTimes = session.sql(
                "select member_id as memberId, collect_list(date_format(create_time, 'yyyy-MM-dd')) as couponTimes " +
                        " from ecommerce.t_coupon_member where coupon_id != 1 group by member_id");
        // 总的花费金额，默认是消费券面额的一半
        Dataset<Row> chargeMoney = session.sql(
                "select cm.member_id as memberId, sum(c.coupon_price/2) as chargeMoney " +
                        " from ecommerce.t_coupon_member as cm left join ecommerce.t_coupon as c " +
                        " on cm.coupon_id = c.id where cm.coupon_channel = 1 group by cm.member_id");

        // 4. 从配送表和反馈表中提取用户的配送和反馈行为信息
        Dataset<Row> overTime = session.sql(
                "select member_id as memberId, (to_unix_timestamp(max(arrive_time)) - to_unix_timestamp(max(pick_time)) ) as overTime " +
                        " from ecommerce.t_delivery group by member_id");
        Dataset<Row> feedback = session.sql(
                "select fb.member_id as memberId, fb.feedback_type as feedBack " +
                        " from ecommerce.t_feedback as fb " +
                        " right join ( select max(id) as fid from ecommerce.t_feedback group by member_id ) as t" +
                        " on fb.id = t.fid");

        // 5. 基于上面得到的结果集，做连接操作，得到一张每个用户的标签表
        memberBase.registerTempTable("memberBase");
        orderBehavior.registerTempTable("orderBehavior");
        freeCoupon.registerTempTable("freeCoupon");
        couponTimes.registerTempTable("couponTimes");
        chargeMoney.registerTempTable("chargeMoney");
        overTime.registerTempTable("overTime");
        feedback.registerTempTable("feedBack");

        Dataset<Row> result = session.sql(
                "select m.*, o.orderCount, o.orderTime, o.orderMoney, o.favGoods, " +
                        " fc.freeCouponTime, ct.couponTimes, cm.chargeMoney, ot.overTime, fb.feedBack " +
                        " from memberBase as m " +
                        " left join orderBehavior as o on m.memberId = o.memberId " +
                        " left join freeCoupon as fc on m.memberId = fc.memberId " +
                        " left join couponTimes as ct on m.memberId = ct.memberId" +
                        " left join chargeMoney as cm on m.memberId = cm.memberId" +
                        " left join overTime as ot on m.memberId = ot.memberId" +
                        " left join feedBack as fb on m.memberId = fb.memberId");

        // 调用es连接器的保存方法，将结果集写入es
        JavaEsSparkSQL.saveToEs(result, "usertag/_doc");
    }
}