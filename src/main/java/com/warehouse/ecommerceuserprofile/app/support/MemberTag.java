package com.warehouse.ecommerceuserprofile.app.support;

import lombok.Data;

import java.util.List;


@Data
public class MemberTag {
    // 1. 用户基本信息
    private String memberId;
    private String phone;
    private String sex;
    private String channel;
    private String subOpenId;
    private String address;
    private String regTime;
    // 2. 用户下单行为特征
    private Long orderCount;
    private String orderTime;
    private Double orderMoney;
    private List<String> favGoods;
    // 3. 用户消费能力标签
    private String freeCouponTime;
    private List<String> couponTimes;
    private Double chargeMoney;
    // 4. 服务反馈标签
    private Long overTime;
    private Integer feedBack;
}
