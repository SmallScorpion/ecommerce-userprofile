SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_commodity
-- ----------------------------
DROP TABLE IF EXISTS `t_commodity`;
CREATE TABLE `t_commodity` (
  `id` int(11) NOT NULL,
  `commodity_name` varchar(50) DEFAULT NULL,
  `commodity_price` decimal(10,2) DEFAULT NULL,
  `commodity_cate_one` int(11) DEFAULT NULL,
  `commodity_cate_two` int(11) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `status` int(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_commodity
-- ----------------------------
INSERT INTO `t_commodity` VALUES ('1', '卡布奇诺', '18.00', '0', '1', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('2', '美式', '20.00', '0', '1', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('3', '拿铁', '20.00', '0', '1', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('4', '摩卡', '22.00', '0', '1', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('5', '橙汁', '15.00', '0', '2', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('6', '石榴汁', '15.00', '0', '2', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('7', '西瓜汁', '15.00', '0', '2', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('8', '芒果汁', '16.00', '0', '2', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('9', '猕猴桃汁', '17.00', '0', '2', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('10', '沙琪玛', '8.00', '0', '3', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('11', '面包干', '8.00', '0', '3', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('12', '酸奶', '8.00', '0', '3', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('13', '火腿', '8.00', '0', '3', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('14', '汉堡', '12.00', '0', '3', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('15', '咖啡杯', '55.00', '0', '4', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('16', '咖啡机', '2500.00', '0', '4', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('17', '果汁机', '2000.00', '0', '4', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('18', '咖啡物语', '1500.00', '0', '4', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');
INSERT INTO `t_commodity` VALUES ('19', '钥匙扣', '10.00', '0', '4', '1', '1', '2019-10-02 08:53:52', '2019-10-02 08:53:52');

-- ----------------------------
-- Table structure for t_commodity_cate
-- ----------------------------
DROP TABLE IF EXISTS `t_commodity_cate`;
CREATE TABLE `t_commodity_cate` (
  `id` int(11) NOT NULL,
  `cate_name` varchar(50) DEFAULT NULL,
  `cate_parent_id` int(11) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `status` int(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_commodity_cate
-- ----------------------------
INSERT INTO `t_commodity_cate` VALUES ('1', '咖啡', '0', '1', '1', '2019-10-03 08:46:56', '2019-10-03 08:46:56');
INSERT INTO `t_commodity_cate` VALUES ('2', '果汁', '0', '1', '1', '2019-10-03 08:46:56', '2019-10-03 08:46:56');
INSERT INTO `t_commodity_cate` VALUES ('3', '小食', '0', '1', '1', '2019-10-03 08:46:56', '2019-10-03 08:46:56');
INSERT INTO `t_commodity_cate` VALUES ('4', '周边', '0', '1', '1', '2019-10-03 08:46:56', '2019-10-03 08:46:56');
