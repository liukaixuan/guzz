/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50085
Source Host           : localhost:3306
Source Database       : bigvote

Target Server Type    : MYSQL
Target Server Version : 50085
File Encoding         : 65001

Date: 2010-10-24 19:33:43
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `tb_anti_cheat_policy`
-- ----------------------------
DROP TABLE IF EXISTS `tb_anti_cheat_policy`;
CREATE TABLE `tb_anti_cheat_policy` (
  `ac_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `ac_maxLife` int(11) default '600',
  `ac_allowedCount` int(11) default '1',
  `ac_Name` varchar(64) NOT NULL,
  `ac_policyImpl` varchar(64) NOT NULL,
  `ac_limitedField` varchar(64) default '',
  `ac_createdTime` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`ac_id`),
  KEY `idx_acp_vid` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_anti_cheat_policy
-- ----------------------------
INSERT INTO `tb_anti_cheat_policy` VALUES ('3', '1', '900', '1', '图形验证码', 'imageCode', '', '2010-01-20 10:57:59');
INSERT INTO `tb_anti_cheat_policy` VALUES ('4', '10', '900', '1', '同1个IP900秒内最多允许投1票', 'IP', '', '2010-02-23 12:39:48');
INSERT INTO `tb_anti_cheat_policy` VALUES ('6', '11', '900', '1', '图形验证码', 'imageCode', '', '2010-03-22 14:34:25');
INSERT INTO `tb_anti_cheat_policy` VALUES ('7', '1', '90024234', '12314', '同1个用户90024234秒内最多允许投12314票', 'cookie', '', '2010-03-22 14:44:56');
INSERT INTO `tb_anti_cheat_policy` VALUES ('9', '11', '900', '1', '用户必须登录，且每人900秒内最多允许投1票', 'loginUser', 'rememberBasic', '2010-03-23 10:32:08');
INSERT INTO `tb_anti_cheat_policy` VALUES ('10', '11', '9000', '1', '用户必须登录，且每人9000秒内最多允许投1票', 'loginUser', 'ignore', '2010-03-23 10:32:23');
INSERT INTO `tb_anti_cheat_policy` VALUES ('11', '12', '900', '1', '1 user in 900 seconds can only vote 1 times', 'cookie', '', '2010-09-16 08:35:37');

-- ----------------------------
-- Table structure for `tb_big_vote`
-- ----------------------------
DROP TABLE IF EXISTS `tb_big_vote`;
CREATE TABLE `tb_big_vote` (
  `vote_id` int(11) NOT NULL auto_increment,
  `vote_name` varchar(128) NOT NULL,
  `vote_people` int(11) default '0',
  `vote_num` int(11) default '0',
  `vote_status` int(11) default '0',
  `vote_createdTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `maxItemsPerVote` int(11) default '0',
  `vote_addedNum` int(11) default '0',
  `vote_territoryPolicy` varchar(32) default 'city',
  `vote_channelId` int(11) NOT NULL,
  `vote_endTime` datetime default NULL,
  `vote_beginTime` timestamp NULL default NULL,
  `vote_addedVotePeople` int(11) default '0',
  `vote_resultShowVotePeople` bit(1) default b'0',
  `vote_resultShowVoteNum` bit(1) default b'0',
  `vote_resultShowPercent` bit(1) default b'0',
  `vote_verboseLog` int(11) default '0',
  `vote_questionSum` bit(1) default b'0',
  `vote_jsonOpenLevel` int(11) default '0',
  `resultShowCityAnalysis` int(11) default '0',
  `resultStatOldCode` varchar(32) default NULL,
  `resultStatNewCode` varchar(32) default NULL,
  `resultRetUrl` varchar(255) default NULL,
  `vote_resultShowCityAnalysis` int(11) default '0',
  `vote_resultStatOldCode` varchar(32) default NULL,
  `vote_resultStatNewCode` varchar(32) default NULL,
  `vote_resultRetUrl` varchar(255) default NULL,
  `vote_resultShowCityNum` int(11) default '10',
  `vote_language` varchar(10) default 'zh_CN',
  PRIMARY KEY  (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_big_vote
-- ----------------------------
INSERT INTO `tb_big_vote` VALUES ('1', '数字组', '1000110', '2000223', '1', '2010-01-20 13:52:42', '11', '0', 'none', '1', null, '2009-11-06 13:40:00', '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('2', 'xxxx2', '2', '1', '1', '2009-10-12 09:19:54', '2', '0', 'city', '1', null, '2009-11-06 15:29:00', '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('3', '5353453545', '2', '4', '1', '2009-10-21 14:18:55', '0', '0', 'IP', '0', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('4', '', '0', '0', '0', '2009-09-28 09:37:29', '0', '0', 'city', '0', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('5', '全国最美丽城市评选', '6', '10', '1', '2009-12-25 16:33:29', '10', '110', 'IP', '1', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', '', '', 'http://ent.cntv.cn/movie/other/jianguodaye/videopage/index.shtml', '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('6', '2222222222', '0', '0', '0', '2009-10-21 14:28:17', '0', '0', 'IP', '0', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('7', '详详细细', '0', '0', '0', '2009-10-27 10:22:57', '0', '0', 'IP', '0', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('8', '投票创建后无法删除', '0', '0', '0', '2009-11-02 15:06:43', '10', '0', 'IP', '1', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('9', 'zzzzzzzzzzzzzz', '0', '0', '0', '2009-11-20 17:32:25', '0', '0', 'IP', '1', null, null, '0', '\0', '\0', '\0', '0', '\0', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('10', '1111', '3', '3', '1', '2010-02-23 12:40:14', '100', '0', 'IP', '3', null, null, '0', '', '', '', '1', '\0', '0', '0', null, null, null, '1', '', '', '', '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('11', '111111111', '4', '4', '1', '2010-01-18 18:27:18', '100', '0', 'IP', '1', null, null, '12', '', '', '', '2', '\0', '2', '0', null, null, null, '0', '', '', '', '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('12', '111', '0', '0', '0', '2009-12-10 15:44:12', '0', '0', 'IP', '1', null, null, '0', '', '', '', '0', '', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');
INSERT INTO `tb_big_vote` VALUES ('13', '2222', '4', '4', '1', '2010-09-16 08:29:42', '0', '0', 'IP', '1', null, null, '0', '', '', '', '0', '', '0', '0', null, null, null, '0', null, null, null, '10', 'zh_CN');

-- ----------------------------
-- Table structure for `tb_territory_vote_log`
-- ----------------------------
DROP TABLE IF EXISTS `tb_territory_vote_log`;
CREATE TABLE `tb_territory_vote_log` (
  `territory_vote_id` varchar(64) NOT NULL,
  `vote_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `territory_id` int(11) NOT NULL,
  `vote_num` int(11) default '0',
  PRIMARY KEY  (`territory_vote_id`),
  KEY `idx_tv_iid` (`item_id`),
  KEY `idx_tv_tid` (`territory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_territory_vote_log
-- ----------------------------
INSERT INTO `tb_territory_vote_log` VALUES ('10_10', '2', '10', '10', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_11', '2', '10', '11', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_12', '2', '10', '12', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_13', '2', '10', '13', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_14', '2', '10', '14', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_15', '2', '10', '15', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_16', '2', '10', '16', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_17', '2', '10', '17', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_18', '2', '10', '18', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('10_19', '2', '10', '19', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_20', '2', '10', '20', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_21', '2', '10', '21', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_22', '2', '10', '22', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_23', '2', '10', '23', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_24', '2', '10', '24', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_25', '2', '10', '25', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_26', '2', '10', '26', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_27', '2', '10', '27', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_28', '2', '10', '28', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_29', '2', '10', '29', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_30', '2', '10', '30', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_31', '2', '10', '31', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_32', '2', '10', '32', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_33', '2', '10', '33', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_34', '2', '10', '34', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_35', '2', '10', '35', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_36', '2', '10', '36', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_37', '2', '10', '37', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_38', '2', '10', '38', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_5', '2', '10', '5', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_6', '2', '10', '6', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_7', '2', '10', '7', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_8', '2', '10', '8', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('10_9', '2', '10', '9', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('11_73', '3', '11', '73', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('11_74', '3', '11', '74', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('12_73', '3', '12', '73', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('12_74', '3', '12', '74', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('13_100', '5', '13', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_101', '5', '13', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_102', '5', '13', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_103', '5', '13', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_104', '5', '13', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_105', '5', '13', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_106', '5', '13', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_107', '5', '13', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_108', '5', '13', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_109', '5', '13', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_110', '5', '13', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_111', '5', '13', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_75', '5', '13', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_76', '5', '13', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_77', '5', '13', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_78', '5', '13', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_79', '5', '13', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_80', '5', '13', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_81', '5', '13', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_82', '5', '13', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_83', '5', '13', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_84', '5', '13', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_85', '5', '13', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_86', '5', '13', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_87', '5', '13', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_88', '5', '13', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_89', '5', '13', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_90', '5', '13', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_91', '5', '13', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_92', '5', '13', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_93', '5', '13', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_94', '5', '13', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_95', '5', '13', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_96', '5', '13', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_97', '5', '13', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_98', '5', '13', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('13_99', '5', '13', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_100', '5', '14', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_101', '5', '14', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_102', '5', '14', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_103', '5', '14', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_104', '5', '14', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_105', '5', '14', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_106', '5', '14', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_107', '5', '14', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_108', '5', '14', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_109', '5', '14', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_110', '5', '14', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_111', '5', '14', '111', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('14_75', '5', '14', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_76', '5', '14', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_77', '5', '14', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_78', '5', '14', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_79', '5', '14', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_80', '5', '14', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_81', '5', '14', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_82', '5', '14', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_83', '5', '14', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_84', '5', '14', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_85', '5', '14', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_86', '5', '14', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_87', '5', '14', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_88', '5', '14', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_89', '5', '14', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_90', '5', '14', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_91', '5', '14', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_92', '5', '14', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_93', '5', '14', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_94', '5', '14', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_95', '5', '14', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_96', '5', '14', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_97', '5', '14', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_98', '5', '14', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('14_99', '5', '14', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_100', '5', '15', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_101', '5', '15', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_102', '5', '15', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_103', '5', '15', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_104', '5', '15', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_105', '5', '15', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_106', '5', '15', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_107', '5', '15', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_108', '5', '15', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_109', '5', '15', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_110', '5', '15', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_111', '5', '15', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_75', '5', '15', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_76', '5', '15', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_77', '5', '15', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_78', '5', '15', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_79', '5', '15', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_80', '5', '15', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_81', '5', '15', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_82', '5', '15', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_83', '5', '15', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_84', '5', '15', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_85', '5', '15', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_86', '5', '15', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_87', '5', '15', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_88', '5', '15', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_89', '5', '15', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_90', '5', '15', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_91', '5', '15', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_92', '5', '15', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_93', '5', '15', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_94', '5', '15', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_95', '5', '15', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_96', '5', '15', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_97', '5', '15', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_98', '5', '15', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('15_99', '5', '15', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_100', '5', '16', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_101', '5', '16', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_102', '5', '16', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_103', '5', '16', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_104', '5', '16', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_105', '5', '16', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_106', '5', '16', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_107', '5', '16', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_108', '5', '16', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_109', '5', '16', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_110', '5', '16', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_111', '5', '16', '111', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('16_75', '5', '16', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_76', '5', '16', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_77', '5', '16', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_78', '5', '16', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_79', '5', '16', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_80', '5', '16', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_81', '5', '16', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_82', '5', '16', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_83', '5', '16', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_84', '5', '16', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_85', '5', '16', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_86', '5', '16', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_87', '5', '16', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_88', '5', '16', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_89', '5', '16', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_90', '5', '16', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_91', '5', '16', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_92', '5', '16', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_93', '5', '16', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_94', '5', '16', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_95', '5', '16', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_96', '5', '16', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_97', '5', '16', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_98', '5', '16', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('16_99', '5', '16', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_112', '6', '17', '112', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_113', '6', '17', '113', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_114', '6', '17', '114', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_115', '6', '17', '115', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_116', '6', '17', '116', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_117', '6', '17', '117', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_118', '6', '17', '118', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_119', '6', '17', '119', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_120', '6', '17', '120', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_121', '6', '17', '121', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_122', '6', '17', '122', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_123', '6', '17', '123', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_124', '6', '17', '124', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_125', '6', '17', '125', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_126', '6', '17', '126', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_127', '6', '17', '127', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_128', '6', '17', '128', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_129', '6', '17', '129', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_130', '6', '17', '130', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_131', '6', '17', '131', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_132', '6', '17', '132', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_133', '6', '17', '133', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_134', '6', '17', '134', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_135', '6', '17', '135', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_136', '6', '17', '136', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_137', '6', '17', '137', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_138', '6', '17', '138', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_139', '6', '17', '139', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_140', '6', '17', '140', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_141', '6', '17', '141', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_142', '6', '17', '142', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_143', '6', '17', '143', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_144', '6', '17', '144', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_145', '6', '17', '145', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_146', '6', '17', '146', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_147', '6', '17', '147', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_148', '6', '17', '148', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_226', '10', '17', '226', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_227', '10', '17', '227', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_228', '10', '17', '228', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_229', '10', '17', '229', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_230', '10', '17', '230', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_231', '10', '17', '231', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_232', '10', '17', '232', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_233', '10', '17', '233', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_234', '10', '17', '234', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_235', '10', '17', '235', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_236', '10', '17', '236', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_237', '10', '17', '237', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_238', '10', '17', '238', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_239', '10', '17', '239', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_240', '10', '17', '240', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_241', '10', '17', '241', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_242', '10', '17', '242', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_243', '10', '17', '243', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_244', '10', '17', '244', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_245', '10', '17', '245', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_246', '10', '17', '246', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_247', '10', '17', '247', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_248', '10', '17', '248', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_249', '10', '17', '249', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_250', '10', '17', '250', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_251', '10', '17', '251', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_252', '10', '17', '252', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_253', '10', '17', '253', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_254', '10', '17', '254', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_255', '10', '17', '255', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_256', '10', '17', '256', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_257', '10', '17', '257', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_258', '10', '17', '258', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_259', '10', '17', '259', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_260', '10', '17', '260', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_261', '10', '17', '261', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('17_262', '10', '17', '262', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_112', '6', '18', '112', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_113', '6', '18', '113', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_114', '6', '18', '114', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_115', '6', '18', '115', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_116', '6', '18', '116', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_117', '6', '18', '117', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_118', '6', '18', '118', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_119', '6', '18', '119', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_120', '6', '18', '120', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_121', '6', '18', '121', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_122', '6', '18', '122', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_123', '6', '18', '123', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_124', '6', '18', '124', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_125', '6', '18', '125', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_126', '6', '18', '126', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_127', '6', '18', '127', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_128', '6', '18', '128', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_129', '6', '18', '129', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_130', '6', '18', '130', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_131', '6', '18', '131', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_132', '6', '18', '132', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_133', '6', '18', '133', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_134', '6', '18', '134', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_135', '6', '18', '135', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_136', '6', '18', '136', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_137', '6', '18', '137', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_138', '6', '18', '138', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_139', '6', '18', '139', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_140', '6', '18', '140', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_141', '6', '18', '141', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_142', '6', '18', '142', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_143', '6', '18', '143', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_144', '6', '18', '144', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_145', '6', '18', '145', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_146', '6', '18', '146', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_147', '6', '18', '147', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('18_148', '6', '18', '148', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_263', '11', '19', '263', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('19_264', '11', '19', '264', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_265', '11', '19', '265', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_266', '11', '19', '266', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_267', '11', '19', '267', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_268', '11', '19', '268', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_269', '11', '19', '269', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_270', '11', '19', '270', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_271', '11', '19', '271', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_272', '11', '19', '272', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_273', '11', '19', '273', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_274', '11', '19', '274', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_275', '11', '19', '275', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_276', '11', '19', '276', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_277', '11', '19', '277', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_278', '11', '19', '278', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_279', '11', '19', '279', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_280', '11', '19', '280', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_281', '11', '19', '281', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_282', '11', '19', '282', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_283', '11', '19', '283', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_284', '11', '19', '284', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_285', '11', '19', '285', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_286', '11', '19', '286', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_287', '11', '19', '287', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_288', '11', '19', '288', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_289', '11', '19', '289', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_290', '11', '19', '290', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_291', '11', '19', '291', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_292', '11', '19', '292', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_293', '11', '19', '293', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_294', '11', '19', '294', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_295', '11', '19', '295', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_296', '11', '19', '296', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_297', '11', '19', '297', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_298', '11', '19', '298', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('19_299', '11', '19', '299', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('1_1', '1', '1', '1', '55');
INSERT INTO `tb_territory_vote_log` VALUES ('1_2', '1', '1', '2', '20');
INSERT INTO `tb_territory_vote_log` VALUES ('1_225', '1', '1', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('1_3', '1', '1', '3', '6');
INSERT INTO `tb_territory_vote_log` VALUES ('1_4', '1', '1', '4', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('20_263', '11', '20', '263', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('20_264', '11', '20', '264', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_265', '11', '20', '265', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_266', '11', '20', '266', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_267', '11', '20', '267', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_268', '11', '20', '268', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_269', '11', '20', '269', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_270', '11', '20', '270', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_271', '11', '20', '271', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_272', '11', '20', '272', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_273', '11', '20', '273', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_274', '11', '20', '274', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_275', '11', '20', '275', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_276', '11', '20', '276', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_277', '11', '20', '277', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_278', '11', '20', '278', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_279', '11', '20', '279', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_280', '11', '20', '280', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_281', '11', '20', '281', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_282', '11', '20', '282', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_283', '11', '20', '283', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_284', '11', '20', '284', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_285', '11', '20', '285', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_286', '11', '20', '286', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_287', '11', '20', '287', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_288', '11', '20', '288', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_289', '11', '20', '289', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_290', '11', '20', '290', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_291', '11', '20', '291', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_292', '11', '20', '292', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_293', '11', '20', '293', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_294', '11', '20', '294', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_295', '11', '20', '295', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_296', '11', '20', '296', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_297', '11', '20', '297', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_298', '11', '20', '298', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('20_299', '11', '20', '299', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_100', '5', '21', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_101', '5', '21', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_102', '5', '21', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_103', '5', '21', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_104', '5', '21', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_105', '5', '21', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_106', '5', '21', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_107', '5', '21', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_108', '5', '21', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_109', '5', '21', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_110', '5', '21', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_111', '5', '21', '111', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('21_75', '5', '21', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_76', '5', '21', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_77', '5', '21', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_78', '5', '21', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_79', '5', '21', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_80', '5', '21', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_81', '5', '21', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_82', '5', '21', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_83', '5', '21', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_84', '5', '21', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_85', '5', '21', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_86', '5', '21', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_87', '5', '21', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_88', '5', '21', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_89', '5', '21', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_90', '5', '21', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_91', '5', '21', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_92', '5', '21', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_93', '5', '21', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_94', '5', '21', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_95', '5', '21', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_96', '5', '21', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_97', '5', '21', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_98', '5', '21', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('21_99', '5', '21', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_100', '5', '22', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_101', '5', '22', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_102', '5', '22', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_103', '5', '22', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_104', '5', '22', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_105', '5', '22', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_106', '5', '22', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_107', '5', '22', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_108', '5', '22', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_109', '5', '22', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_110', '5', '22', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_111', '5', '22', '111', '3');
INSERT INTO `tb_territory_vote_log` VALUES ('22_75', '5', '22', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_76', '5', '22', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_77', '5', '22', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_78', '5', '22', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_79', '5', '22', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_80', '5', '22', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_81', '5', '22', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_82', '5', '22', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_83', '5', '22', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_84', '5', '22', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_85', '5', '22', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_86', '5', '22', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_87', '5', '22', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_88', '5', '22', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_89', '5', '22', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_90', '5', '22', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_91', '5', '22', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_92', '5', '22', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_93', '5', '22', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_94', '5', '22', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_95', '5', '22', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_96', '5', '22', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_97', '5', '22', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_98', '5', '22', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('22_99', '5', '22', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_100', '5', '23', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_101', '5', '23', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_102', '5', '23', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_103', '5', '23', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_104', '5', '23', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_105', '5', '23', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_106', '5', '23', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_107', '5', '23', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_108', '5', '23', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_109', '5', '23', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_110', '5', '23', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_111', '5', '23', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_75', '5', '23', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_76', '5', '23', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_77', '5', '23', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_78', '5', '23', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_79', '5', '23', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_80', '5', '23', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_81', '5', '23', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_82', '5', '23', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_83', '5', '23', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_84', '5', '23', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_85', '5', '23', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_86', '5', '23', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_87', '5', '23', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_88', '5', '23', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_89', '5', '23', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_90', '5', '23', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_91', '5', '23', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_92', '5', '23', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_93', '5', '23', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_94', '5', '23', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_95', '5', '23', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_96', '5', '23', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_97', '5', '23', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_98', '5', '23', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('23_99', '5', '23', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_100', '5', '24', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_101', '5', '24', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_102', '5', '24', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_103', '5', '24', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_104', '5', '24', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_105', '5', '24', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_106', '5', '24', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_107', '5', '24', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_108', '5', '24', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_109', '5', '24', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_110', '5', '24', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_111', '5', '24', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_75', '5', '24', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_76', '5', '24', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_77', '5', '24', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_78', '5', '24', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_79', '5', '24', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_80', '5', '24', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_81', '5', '24', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_82', '5', '24', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_83', '5', '24', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_84', '5', '24', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_85', '5', '24', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_86', '5', '24', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_87', '5', '24', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_88', '5', '24', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_89', '5', '24', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_90', '5', '24', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_91', '5', '24', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_92', '5', '24', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_93', '5', '24', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_94', '5', '24', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_95', '5', '24', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_96', '5', '24', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_97', '5', '24', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_98', '5', '24', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('24_99', '5', '24', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_100', '5', '25', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_101', '5', '25', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_102', '5', '25', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_103', '5', '25', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_104', '5', '25', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_105', '5', '25', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_106', '5', '25', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_107', '5', '25', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_108', '5', '25', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_109', '5', '25', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_110', '5', '25', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_111', '5', '25', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_75', '5', '25', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_76', '5', '25', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_77', '5', '25', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_78', '5', '25', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_79', '5', '25', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_80', '5', '25', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_81', '5', '25', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_82', '5', '25', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_83', '5', '25', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_84', '5', '25', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_85', '5', '25', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_86', '5', '25', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_87', '5', '25', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_88', '5', '25', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_89', '5', '25', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_90', '5', '25', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_91', '5', '25', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_92', '5', '25', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_93', '5', '25', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_94', '5', '25', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_95', '5', '25', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_96', '5', '25', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_97', '5', '25', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_98', '5', '25', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('25_99', '5', '25', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_100', '5', '26', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_101', '5', '26', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_102', '5', '26', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_103', '5', '26', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_104', '5', '26', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_105', '5', '26', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_106', '5', '26', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_107', '5', '26', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_108', '5', '26', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_109', '5', '26', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_110', '5', '26', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_111', '5', '26', '111', '5');
INSERT INTO `tb_territory_vote_log` VALUES ('26_75', '5', '26', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_76', '5', '26', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_77', '5', '26', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_78', '5', '26', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_79', '5', '26', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_80', '5', '26', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_81', '5', '26', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_82', '5', '26', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_83', '5', '26', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_84', '5', '26', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_85', '5', '26', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_86', '5', '26', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_87', '5', '26', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_88', '5', '26', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_89', '5', '26', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_90', '5', '26', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_91', '5', '26', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_92', '5', '26', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_93', '5', '26', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_94', '5', '26', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_95', '5', '26', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_96', '5', '26', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_97', '5', '26', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_98', '5', '26', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('26_99', '5', '26', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_100', '5', '27', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_101', '5', '27', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_102', '5', '27', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_103', '5', '27', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_104', '5', '27', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_105', '5', '27', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_106', '5', '27', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_107', '5', '27', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_108', '5', '27', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_109', '5', '27', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_110', '5', '27', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_111', '5', '27', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_75', '5', '27', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_76', '5', '27', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_77', '5', '27', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_78', '5', '27', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_79', '5', '27', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_80', '5', '27', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_81', '5', '27', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_82', '5', '27', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_83', '5', '27', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_84', '5', '27', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_85', '5', '27', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_86', '5', '27', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_87', '5', '27', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_88', '5', '27', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_89', '5', '27', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_90', '5', '27', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_91', '5', '27', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_92', '5', '27', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_93', '5', '27', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_94', '5', '27', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_95', '5', '27', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_96', '5', '27', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_97', '5', '27', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_98', '5', '27', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('27_99', '5', '27', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_100', '5', '28', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_101', '5', '28', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_102', '5', '28', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_103', '5', '28', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_104', '5', '28', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_105', '5', '28', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_106', '5', '28', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_107', '5', '28', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_108', '5', '28', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_109', '5', '28', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_110', '5', '28', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_111', '5', '28', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_75', '5', '28', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_76', '5', '28', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_77', '5', '28', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_78', '5', '28', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_79', '5', '28', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_80', '5', '28', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_81', '5', '28', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_82', '5', '28', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_83', '5', '28', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_84', '5', '28', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_85', '5', '28', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_86', '5', '28', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_87', '5', '28', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_88', '5', '28', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_89', '5', '28', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_90', '5', '28', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_91', '5', '28', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_92', '5', '28', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_93', '5', '28', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_94', '5', '28', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_95', '5', '28', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_96', '5', '28', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_97', '5', '28', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_98', '5', '28', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('28_99', '5', '28', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_100', '5', '29', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_101', '5', '29', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_102', '5', '29', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_103', '5', '29', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_104', '5', '29', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_105', '5', '29', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_106', '5', '29', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_107', '5', '29', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_108', '5', '29', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_109', '5', '29', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_110', '5', '29', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_111', '5', '29', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_75', '5', '29', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_76', '5', '29', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_77', '5', '29', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_78', '5', '29', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_79', '5', '29', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_80', '5', '29', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_81', '5', '29', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_82', '5', '29', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_83', '5', '29', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_84', '5', '29', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_85', '5', '29', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_86', '5', '29', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_87', '5', '29', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_88', '5', '29', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_89', '5', '29', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_90', '5', '29', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_91', '5', '29', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_92', '5', '29', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_93', '5', '29', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_94', '5', '29', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_95', '5', '29', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_96', '5', '29', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_97', '5', '29', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_98', '5', '29', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('29_99', '5', '29', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('2_1', '1', '2', '1', '1000039');
INSERT INTO `tb_territory_vote_log` VALUES ('2_2', '1', '2', '2', '4');
INSERT INTO `tb_territory_vote_log` VALUES ('2_225', '1', '2', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('2_3', '1', '2', '3', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('2_4', '1', '2', '4', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_100', '5', '30', '100', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_101', '5', '30', '101', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_102', '5', '30', '102', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_103', '5', '30', '103', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_104', '5', '30', '104', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_105', '5', '30', '105', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_106', '5', '30', '106', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_107', '5', '30', '107', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_108', '5', '30', '108', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_109', '5', '30', '109', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_110', '5', '30', '110', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_111', '5', '30', '111', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_75', '5', '30', '75', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_76', '5', '30', '76', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_77', '5', '30', '77', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_78', '5', '30', '78', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_79', '5', '30', '79', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_80', '5', '30', '80', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_81', '5', '30', '81', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_82', '5', '30', '82', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_83', '5', '30', '83', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_84', '5', '30', '84', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_85', '5', '30', '85', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_86', '5', '30', '86', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_87', '5', '30', '87', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_88', '5', '30', '88', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_89', '5', '30', '89', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_90', '5', '30', '90', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_91', '5', '30', '91', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_92', '5', '30', '92', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_93', '5', '30', '93', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_94', '5', '30', '94', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_95', '5', '30', '95', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_96', '5', '30', '96', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_97', '5', '30', '97', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_98', '5', '30', '98', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('30_99', '5', '30', '99', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_337', '13', '31', '337', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('31_338', '13', '31', '338', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_339', '13', '31', '339', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_340', '13', '31', '340', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_341', '13', '31', '341', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_342', '13', '31', '342', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_343', '13', '31', '343', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_344', '13', '31', '344', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_345', '13', '31', '345', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_346', '13', '31', '346', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_347', '13', '31', '347', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_348', '13', '31', '348', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_349', '13', '31', '349', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_350', '13', '31', '350', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_351', '13', '31', '351', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_352', '13', '31', '352', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_353', '13', '31', '353', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_354', '13', '31', '354', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_355', '13', '31', '355', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_356', '13', '31', '356', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_357', '13', '31', '357', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_358', '13', '31', '358', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_359', '13', '31', '359', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_360', '13', '31', '360', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_361', '13', '31', '361', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_362', '13', '31', '362', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_363', '13', '31', '363', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_364', '13', '31', '364', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_365', '13', '31', '365', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_366', '13', '31', '366', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_367', '13', '31', '367', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_368', '13', '31', '368', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_369', '13', '31', '369', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_370', '13', '31', '370', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('31_371', '13', '31', '371', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_226', '10', '32', '226', '3');
INSERT INTO `tb_territory_vote_log` VALUES ('32_228', '10', '32', '228', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_229', '10', '32', '229', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_230', '10', '32', '230', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_231', '10', '32', '231', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_232', '10', '32', '232', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_233', '10', '32', '233', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_234', '10', '32', '234', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_235', '10', '32', '235', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_236', '10', '32', '236', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_237', '10', '32', '237', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_238', '10', '32', '238', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_239', '10', '32', '239', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_240', '10', '32', '240', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_241', '10', '32', '241', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_242', '10', '32', '242', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_243', '10', '32', '243', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_244', '10', '32', '244', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_245', '10', '32', '245', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_246', '10', '32', '246', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_247', '10', '32', '247', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_248', '10', '32', '248', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_249', '10', '32', '249', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_250', '10', '32', '250', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_251', '10', '32', '251', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_252', '10', '32', '252', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_253', '10', '32', '253', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_254', '10', '32', '254', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_255', '10', '32', '255', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_256', '10', '32', '256', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_257', '10', '32', '257', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_258', '10', '32', '258', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_259', '10', '32', '259', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_260', '10', '32', '260', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_261', '10', '32', '261', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('32_262', '10', '32', '262', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('3_1', '1', '3', '1', '19');
INSERT INTO `tb_territory_vote_log` VALUES ('3_2', '1', '3', '2', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('3_225', '1', '3', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('3_3', '1', '3', '3', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('3_4', '1', '3', '4', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('4_1', '1', '4', '1', '67');
INSERT INTO `tb_territory_vote_log` VALUES ('4_2', '1', '4', '2', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('4_3', '1', '4', '3', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('4_4', '1', '4', '4', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('5_1', '1', '5', '1', '13');
INSERT INTO `tb_territory_vote_log` VALUES ('5_2', '1', '5', '2', '20');
INSERT INTO `tb_territory_vote_log` VALUES ('5_225', '1', '5', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('5_3', '1', '5', '3', '6');
INSERT INTO `tb_territory_vote_log` VALUES ('5_4', '1', '5', '4', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('6_1', '1', '6', '1', '66');
INSERT INTO `tb_territory_vote_log` VALUES ('6_2', '1', '6', '2', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('6_3', '1', '6', '3', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('6_4', '1', '6', '4', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('7_1', '1', '7', '1', '1000011');
INSERT INTO `tb_territory_vote_log` VALUES ('7_2', '1', '7', '2', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('7_225', '1', '7', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('7_3', '1', '7', '3', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('7_4', '1', '7', '4', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('8_1', '1', '8', '1', '9');
INSERT INTO `tb_territory_vote_log` VALUES ('8_2', '1', '8', '2', '1');
INSERT INTO `tb_territory_vote_log` VALUES ('8_225', '1', '8', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('8_3', '1', '8', '3', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('8_4', '1', '8', '4', '10');
INSERT INTO `tb_territory_vote_log` VALUES ('9_1', '1', '9', '1', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('9_2', '1', '9', '2', '2');
INSERT INTO `tb_territory_vote_log` VALUES ('9_225', '1', '9', '225', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('9_3', '1', '9', '3', '0');
INSERT INTO `tb_territory_vote_log` VALUES ('9_4', '1', '9', '4', '0');

-- ----------------------------
-- Table structure for `tb_vote_channel`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_channel`;
CREATE TABLE `tb_vote_channel` (
  `channel_id` int(11) NOT NULL auto_increment,
  `parent_id` int(11) default '0',
  `channel_name` varchar(255) NOT NULL,
  `channel_authGroup` varchar(32) NOT NULL,
  `channel_createdTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`channel_id`),
  KEY `idx_ch_authGroup` (`channel_authGroup`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_channel
-- ----------------------------
INSERT INTO `tb_vote_channel` VALUES ('1', '0', '公益文明频道', 'g_wenming_editor', '2009-10-27 15:27:49');
INSERT INTO `tb_vote_channel` VALUES ('2', '0', '娱乐频道', 'g_yule_editor', '2009-11-05 13:49:57');
INSERT INTO `tb_vote_channel` VALUES ('3', '0', 'test', 'middleAdmin', '2009-11-26 17:44:36');

-- ----------------------------
-- Table structure for `tb_vote_code`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_code`;
CREATE TABLE `tb_vote_code` (
  `vote_code_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `is_active` bit(1) default NULL,
  `vote_code` text,
  PRIMARY KEY  (`vote_code_id`),
  KEY `seq_vc_id` (`vote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_code
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_vote_extra_property`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_extra_property`;
CREATE TABLE `tb_vote_extra_property` (
  `prop_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `prop_paramName` varchar(16) NOT NULL,
  `prop_showName` varchar(16) NOT NULL,
  `prop_mustProp` bit(1) default NULL,
  `prop_validValues` varchar(255) default NULL,
  `prop_defaultValue` varchar(32) default NULL,
  `prop_validRuleName` varchar(32) default NULL,
  `prop_ruleParamValue` varchar(255) default NULL,
  `prop_dataLength` int(4) default NULL,
  PRIMARY KEY  (`prop_id`),
  KEY `idx_vote_ep_voteId` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_extra_property
-- ----------------------------
INSERT INTO `tb_vote_extra_property` VALUES ('4', '1', 'trueName', '姓名', '\0', null, '', '', '', null);
INSERT INTO `tb_vote_extra_property` VALUES ('5', '1', 'address', '学校名称', '\0', '', '', '', '', null);
INSERT INTO `tb_vote_extra_property` VALUES ('6', '5', '111', '住址', '', null, '', '', '', null);
INSERT INTO `tb_vote_extra_property` VALUES ('7', '5', '222', '手机号', '', null, '', '', '', null);
INSERT INTO `tb_vote_extra_property` VALUES ('8', '11', 'name', '名称', '\0', null, '', '', '', '64');

-- ----------------------------
-- Table structure for `tb_vote_item`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_item`;
CREATE TABLE `tb_vote_item` (
  `item_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `vote_num` int(11) default '0',
  `item_name` varchar(255) NOT NULL,
  `item_showName` varchar(256) default NULL,
  `vote_addedNum` int(11) default '0',
  `group_id` int(11) default '0',
  `item_nameLink` varchar(255) default NULL,
  `item_imageURL` varchar(255) default NULL,
  `item_imageLink` varchar(255) default NULL,
  `item_shouldChoose` bit(1) default b'0',
  PRIMARY KEY  (`item_id`),
  KEY `idx_v_gid` (`group_id`),
  KEY `idx_v_vid` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_item
-- ----------------------------
INSERT INTO `tb_vote_item` VALUES ('1', '1', '82', '我爱蓝精灵', '', '0', '1', '', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('2', '1', '1000044', '黑猫警长', '', '0', '1', '', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '');
INSERT INTO `tb_vote_item` VALUES ('3', '1', '19', '孝顺的小乌鸦', '', '0', '1', 'http://www.pragprog.com/titles/jaerlang/programming-erlang', 'http://p4.img.cctvpic.com/votepic/2009/1/1259657993629.gif', 'http://code.google.com/appengine/', '\0');
INSERT INTO `tb_vote_item` VALUES ('5', '1', '39', '我是一朵格桑花 ', '', '0', '0', null, null, null, '\0');
INSERT INTO `tb_vote_item` VALUES ('7', '1', '1000014', '风力发电 ', '', '0', '0', null, null, null, '\0');
INSERT INTO `tb_vote_item` VALUES ('8', '1', '23', '牵牛花', '<a href=\"http://museum.cctv.com/special/tongyao/20090911/103778.shtml\" target=\"_blank\">牵牛花</a>', '0', '2', 'http://code.google.com/appengine/images/appengine_lowres.gif', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('9', '1', '2', '', '', '0', '0', null, null, null, '\0');
INSERT INTO `tb_vote_item` VALUES ('10', '2', '1', 'xxxx', '', '0', '0', null, null, null, '\0');
INSERT INTO `tb_vote_item` VALUES ('11', '3', '2', '学习型', '学习型', '0', '0', null, null, null, '\0');
INSERT INTO `tb_vote_item` VALUES ('17', '6', '0', '111', '', '0', '5', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('18', '6', '0', '2222', '', '0', '5', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('19', '11', '2', '1', '', '0', '6', 'http://localhost:8080/vote/console/', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('20', '11', '2', '2', '', '0', '6', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('21', '5', '2', '北京', '', '100', '7', '', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('22', '5', '3', '上海', '', '0', '7', '', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('23', '5', '0', '长春', '', '10', '7', 'http://www.google.com/search?hl=en&safe=off&client=firefox-a&rls=org.mozilla%3Azh-CN%3Aofficial&hs=Pt0&newwindow=1&q=firefox+confirm&aq=f&oq=&aqi=g8', 'http://code.google.com/appengine/images/appengine_lowres.gif', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('24', '5', '0', '天津', '', '0', '7', 'http://www.google.com/images/nav_logo7.png', 'http://www.google.com/images/nav_logo7.png', 'http://www.google.com/images/nav_logo7.png', '\0');
INSERT INTO `tb_vote_item` VALUES ('25', '5', '0', '连云港', '', '0', '7', '', 'http://www.google.com/images/nav_logo7.png', 'http://www.google.com/images/nav_logo7.png', '\0');
INSERT INTO `tb_vote_item` VALUES ('26', '5', '5', '渤海', '', '0', '8', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('27', '5', '0', '黄海', '', '0', '8', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('28', '5', '0', '绿海', '', '0', '8', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('29', '5', '0', '蓝海', '', '0', '8', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('30', '5', '0', '紫海', '', '0', '8', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('31', '13', '4', 'aaaa', '', '0', '9', '', '', '', '\0');
INSERT INTO `tb_vote_item` VALUES ('32', '10', '3', '11111', '', '0', '3', '', 'http://10.64.4.35:8080/testFund/2010/10/1266899614390.gif', '', '\0');

-- ----------------------------
-- Table structure for `tb_vote_item_group`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_item_group`;
CREATE TABLE `tb_vote_item_group` (
  `group_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `group_createdTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `group_linkUrl` varchar(255) default NULL,
  `group_maxItemsPerVote` int(11) default '0',
  `group_minItemsPerVote` int(11) default '1',
  PRIMARY KEY  (`group_id`),
  KEY `idx_vig_voteId` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_item_group
-- ----------------------------
INSERT INTO `tb_vote_item_group` VALUES ('1', '1', '北京分组', '2009-12-10 17:06:02', '', '1', '1');
INSERT INTO `tb_vote_item_group` VALUES ('2', '1', '华南地区', '2009-12-10 17:06:02', null, '0', '1');
INSERT INTO `tb_vote_item_group` VALUES ('3', '10', 'aaaa', '2009-12-10 17:06:02', 'aaa', '0', '1');
INSERT INTO `tb_vote_item_group` VALUES ('4', '10', 'bbb', '2009-12-10 17:06:02', 'bb', '0', '0');
INSERT INTO `tb_vote_item_group` VALUES ('5', '6', '啊啊啊', '2009-12-10 17:06:02', '', '0', '1');
INSERT INTO `tb_vote_item_group` VALUES ('6', '11', '1111', '2009-12-10 17:06:02', '', '0', '1');
INSERT INTO `tb_vote_item_group` VALUES ('7', '5', '你最喜欢的城市？', '2009-12-03 09:56:57', '', '2', '1');
INSERT INTO `tb_vote_item_group` VALUES ('8', '5', '你最喜欢的海洋是什么？', '2009-12-10 17:06:02', '', '1', '1');
INSERT INTO `tb_vote_item_group` VALUES ('9', '13', 'xxxx', '2009-12-24 11:41:12', '', '0', '1');
INSERT INTO `tb_vote_item_group` VALUES ('10', '5', 'zzz', '2010-09-15 19:38:01', null, '0', '1');

-- ----------------------------
-- Table structure for `tb_vote_lottery`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_lottery`;
CREATE TABLE `tb_vote_lottery` (
  `lottery_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `lottery_rank` int(2) NOT NULL,
  `lottery_territory_name` varchar(64) NOT NULL,
  `lottery_voter_IP` varchar(32) default NULL,
  `lottery_vote_time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `lottery_extraPropsXML` text,
  `lottery_isLotted` bit(1) default b'0',
  `log_id` bigint(20) default NULL,
  PRIMARY KEY  (`lottery_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_lottery
-- ----------------------------

-- ----------------------------
-- Table structure for `tb_vote_territory`
-- ----------------------------
DROP TABLE IF EXISTS `tb_vote_territory`;
CREATE TABLE `tb_vote_territory` (
  `territory_id` int(11) NOT NULL auto_increment,
  `vote_id` int(11) NOT NULL,
  `vote_people` int(11) default '0',
  `vote_num` int(11) default '0',
  `vote_addedNum` int(11) default '0',
  `territory_name` varchar(64) NOT NULL,
  PRIMARY KEY  (`territory_id`),
  KEY `idx_v_vid` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=372 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_vote_territory
-- ----------------------------
INSERT INTO `tb_vote_territory` VALUES ('1', '1', '1100126', '3100208', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('2', '1', '24', '48', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('3', '1', '12', '16', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('5', '2', '1', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('6', '2', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('7', '2', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('8', '2', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('9', '2', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('10', '2', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('11', '2', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('12', '2', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('13', '2', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('14', '2', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('15', '2', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('16', '2', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('17', '2', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('18', '2', '1', '1', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('19', '2', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('20', '2', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('21', '2', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('22', '2', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('23', '2', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('24', '2', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('25', '2', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('26', '2', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('27', '2', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('28', '2', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('29', '2', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('30', '2', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('31', '2', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('32', '2', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('33', '2', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('34', '2', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('35', '2', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('36', '2', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('37', '2', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('38', '2', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('39', '4', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('40', '4', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('41', '4', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('42', '4', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('43', '4', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('44', '4', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('45', '4', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('46', '4', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('47', '4', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('48', '4', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('49', '4', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('50', '4', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('51', '4', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('52', '4', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('53', '4', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('54', '4', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('55', '4', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('56', '4', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('57', '4', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('58', '4', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('59', '4', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('60', '4', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('61', '4', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('62', '4', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('63', '4', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('64', '4', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('65', '4', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('66', '4', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('67', '4', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('68', '4', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('69', '4', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('70', '4', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('71', '4', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('72', '4', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('73', '3', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('74', '3', '1', '2', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('75', '5', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('76', '5', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('77', '5', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('78', '5', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('79', '5', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('80', '5', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('81', '5', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('82', '5', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('83', '5', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('84', '5', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('85', '5', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('86', '5', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('87', '5', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('88', '5', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('89', '5', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('90', '5', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('91', '5', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('92', '5', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('93', '5', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('94', '5', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('95', '5', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('96', '5', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('97', '5', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('98', '5', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('99', '5', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('100', '5', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('101', '5', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('102', '5', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('103', '5', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('104', '5', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('105', '5', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('106', '5', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('107', '5', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('108', '5', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('109', '5', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('110', '5', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('111', '5', '6', '12', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('112', '6', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('113', '6', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('114', '6', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('115', '6', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('116', '6', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('117', '6', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('118', '6', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('119', '6', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('120', '6', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('121', '6', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('122', '6', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('123', '6', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('124', '6', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('125', '6', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('126', '6', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('127', '6', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('128', '6', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('129', '6', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('130', '6', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('131', '6', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('132', '6', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('133', '6', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('134', '6', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('135', '6', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('136', '6', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('137', '6', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('138', '6', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('139', '6', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('140', '6', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('141', '6', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('142', '6', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('143', '6', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('144', '6', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('145', '6', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('146', '6', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('147', '6', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('148', '6', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('149', '7', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('150', '7', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('151', '8', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('152', '8', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('153', '8', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('154', '8', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('155', '8', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('156', '8', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('157', '8', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('158', '8', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('159', '8', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('160', '8', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('161', '8', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('162', '8', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('163', '8', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('164', '8', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('165', '8', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('166', '8', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('167', '8', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('168', '8', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('169', '8', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('170', '8', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('171', '8', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('172', '8', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('173', '8', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('174', '8', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('175', '8', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('176', '8', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('177', '8', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('178', '8', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('179', '8', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('180', '8', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('181', '8', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('182', '8', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('183', '8', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('184', '8', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('185', '8', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('186', '8', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('187', '8', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('188', '9', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('189', '9', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('190', '9', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('191', '9', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('192', '9', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('193', '9', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('194', '9', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('195', '9', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('196', '9', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('197', '9', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('198', '9', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('199', '9', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('200', '9', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('201', '9', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('202', '9', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('203', '9', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('204', '9', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('205', '9', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('206', '9', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('207', '9', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('208', '9', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('209', '9', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('210', '9', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('211', '9', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('212', '9', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('213', '9', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('214', '9', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('215', '9', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('216', '9', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('217', '9', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('218', '9', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('219', '9', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('220', '9', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('221', '9', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('222', '9', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('223', '9', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('224', '9', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('226', '10', '3', '3', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('228', '10', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('229', '10', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('230', '10', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('231', '10', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('232', '10', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('233', '10', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('234', '10', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('235', '10', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('236', '10', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('237', '10', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('238', '10', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('239', '10', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('240', '10', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('241', '10', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('242', '10', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('243', '10', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('244', '10', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('245', '10', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('246', '10', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('247', '10', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('248', '10', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('249', '10', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('250', '10', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('251', '10', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('252', '10', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('253', '10', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('254', '10', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('255', '10', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('256', '10', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('257', '10', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('258', '10', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('259', '10', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('260', '10', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('261', '10', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('262', '10', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('263', '11', '4', '4', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('264', '11', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('265', '11', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('266', '11', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('267', '11', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('268', '11', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('269', '11', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('270', '11', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('271', '11', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('272', '11', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('273', '11', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('274', '11', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('275', '11', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('276', '11', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('277', '11', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('278', '11', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('279', '11', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('280', '11', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('281', '11', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('282', '11', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('283', '11', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('284', '11', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('285', '11', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('286', '11', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('287', '11', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('288', '11', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('289', '11', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('290', '11', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('291', '11', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('292', '11', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('293', '11', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('294', '11', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('295', '11', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('296', '11', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('297', '11', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('298', '11', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('299', '11', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('300', '12', '0', '0', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('301', '12', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('302', '12', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('303', '12', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('304', '12', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('305', '12', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('306', '12', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('307', '12', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('308', '12', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('309', '12', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('310', '12', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('311', '12', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('312', '12', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('313', '12', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('314', '12', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('315', '12', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('316', '12', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('317', '12', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('318', '12', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('319', '12', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('320', '12', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('321', '12', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('322', '12', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('323', '12', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('324', '12', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('325', '12', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('326', '12', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('327', '12', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('328', '12', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('329', '12', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('330', '12', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('331', '12', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('332', '12', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('333', '12', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('334', '12', '0', '0', '0', '台湾');
INSERT INTO `tb_vote_territory` VALUES ('335', '12', '0', '0', '0', '海外');
INSERT INTO `tb_vote_territory` VALUES ('336', '12', '0', '0', '0', '其他');
INSERT INTO `tb_vote_territory` VALUES ('337', '13', '2', '2', '0', '北京');
INSERT INTO `tb_vote_territory` VALUES ('338', '13', '0', '0', '0', '天津');
INSERT INTO `tb_vote_territory` VALUES ('339', '13', '0', '0', '0', '河北');
INSERT INTO `tb_vote_territory` VALUES ('340', '13', '0', '0', '0', '山西');
INSERT INTO `tb_vote_territory` VALUES ('341', '13', '0', '0', '0', '内蒙古');
INSERT INTO `tb_vote_territory` VALUES ('342', '13', '0', '0', '0', '辽宁');
INSERT INTO `tb_vote_territory` VALUES ('343', '13', '0', '0', '0', '吉林');
INSERT INTO `tb_vote_territory` VALUES ('344', '13', '0', '0', '0', '黑龙江');
INSERT INTO `tb_vote_territory` VALUES ('345', '13', '0', '0', '0', '上海');
INSERT INTO `tb_vote_territory` VALUES ('346', '13', '0', '0', '0', '江苏');
INSERT INTO `tb_vote_territory` VALUES ('347', '13', '0', '0', '0', '浙江');
INSERT INTO `tb_vote_territory` VALUES ('348', '13', '0', '0', '0', '安徽');
INSERT INTO `tb_vote_territory` VALUES ('349', '13', '0', '0', '0', '福建');
INSERT INTO `tb_vote_territory` VALUES ('350', '13', '0', '0', '0', '江西');
INSERT INTO `tb_vote_territory` VALUES ('351', '13', '0', '0', '0', '山东');
INSERT INTO `tb_vote_territory` VALUES ('352', '13', '0', '0', '0', '河南');
INSERT INTO `tb_vote_territory` VALUES ('353', '13', '0', '0', '0', '湖北');
INSERT INTO `tb_vote_territory` VALUES ('354', '13', '0', '0', '0', '湖南');
INSERT INTO `tb_vote_territory` VALUES ('355', '13', '0', '0', '0', '广东');
INSERT INTO `tb_vote_territory` VALUES ('356', '13', '0', '0', '0', '广西');
INSERT INTO `tb_vote_territory` VALUES ('357', '13', '0', '0', '0', '海南');
INSERT INTO `tb_vote_territory` VALUES ('358', '13', '0', '0', '0', '重庆');
INSERT INTO `tb_vote_territory` VALUES ('359', '13', '0', '0', '0', '四川');
INSERT INTO `tb_vote_territory` VALUES ('360', '13', '0', '0', '0', '贵州');
INSERT INTO `tb_vote_territory` VALUES ('361', '13', '0', '0', '0', '云南');
INSERT INTO `tb_vote_territory` VALUES ('362', '13', '0', '0', '0', '西藏');
INSERT INTO `tb_vote_territory` VALUES ('363', '13', '0', '0', '0', '陕西');
INSERT INTO `tb_vote_territory` VALUES ('364', '13', '0', '0', '0', '甘肃');
INSERT INTO `tb_vote_territory` VALUES ('365', '13', '0', '0', '0', '青海');
INSERT INTO `tb_vote_territory` VALUES ('366', '13', '0', '0', '0', '宁夏');
INSERT INTO `tb_vote_territory` VALUES ('367', '13', '0', '0', '0', '新疆');
INSERT INTO `tb_vote_territory` VALUES ('368', '13', '0', '0', '0', '兵团');
INSERT INTO `tb_vote_territory` VALUES ('369', '13', '0', '0', '0', '香港');
INSERT INTO `tb_vote_territory` VALUES ('370', '13', '0', '0', '0', '澳门');
INSERT INTO `tb_vote_territory` VALUES ('371', '13', '0', '0', '0', '台湾');
