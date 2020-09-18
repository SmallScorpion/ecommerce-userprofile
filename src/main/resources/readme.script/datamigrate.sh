#!/bin/bash

sq()
{
./sqoop import \
--connect jdbc:mysql://localhost:3306/ecommerce \
--username root \
--password 123456 \
--table $1 \
--num-mappers 1 \
--hive-import \
--fields-terminated-by "\t" \
--hive-overwrite \
--hive-database ecommerce \
--hive-table $1
}

sq t_commodity
sq t_commodity_cate
sq t_coupon
sq t_coupon_member
sq t_coupon_order
sq t_delivery
sq t_feedback
sq t_member
sq t_member_addr
sq t_order
sq t_order_commodity
sq t_shop
sq t_shop_order
sq t_user