/*
Navicat MySQL Data Transfer

Source Server         : iadrian.pe
Source Server Version : 50169
Source Host           : iadrian.pe:3306
Source Database       : paisaconnect

Target Server Type    : MYSQL
Target Server Version : 50169
File Encoding         : 65001

Date: 2013-12-05 11:07:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `resources`
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `src` longtext,
  `mime_type` varchar(100) DEFAULT NULL,
  `filesize` varchar(100) DEFAULT NULL,
  `post_date` datetime DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of resources
-- ----------------------------

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fb_id` varchar(100) NOT NULL,
  `register_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;