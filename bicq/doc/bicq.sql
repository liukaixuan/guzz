# MySQL-Front Dump 2.5
#
# Host: localhost   Database: bicq
# --------------------------------------------------------
# Server version 4.0.0-alpha-nt


#
# Table structure for table 'friend'
#

CREATE TABLE friend (
  ID bigint(10) unsigned NOT NULL auto_increment,
  friendnumber int(11) default NULL,
  belongnumber int(11) NOT NULL default '0',
  addtime bigint(20) default NULL,
  PRIMARY KEY  (ID),
  KEY ID (ID)
) TYPE=MyISAM;



#
# Table structure for table 'permit'
#

CREATE TABLE permit (
  ID int(10) unsigned NOT NULL auto_increment,
  fromnumber int(11) NOT NULL default '0',
  tonumber int(11) NOT NULL default '0',
  type int(6) default NULL,
  content blob,
  PRIMARY KEY  (ID),
  KEY tonumber (tonumber)
) TYPE=MyISAM;


#
# Table structure for table 'textmessage'
#

CREATE TABLE textmessage (
  ID int(10) unsigned NOT NULL auto_increment,
  fromnumber int(11) default NULL,
  tonumber int(11) NOT NULL default '0',
  type int(11) default NULL,
  content blob,
  PRIMARY KEY  (ID),
  KEY ID (ID)
) TYPE=MyISAM;


#
# Table structure for table 'user'
#

CREATE TABLE user (
  ID int(10) unsigned NOT NULL auto_increment,
  number int(11) NOT NULL default '0',
  password varchar(50) NOT NULL default '',
  nickname varchar(50) default NULL,
  gender smallint(6) default NULL,
  portrait smallint(6) default NULL,
  address varchar(200) default NULL,
  zip int(11) default NULL,
  country varchar(30) default NULL,
  province varchar(50) default NULL,
  myexplain varchar(255) default NULL,
  birthday bigint(20) default NULL,
  telephone varchar(20) default NULL,
  homepage varchar(255) default NULL,
  realname varchar(50) default NULL,
  mail varchar(50) default NULL,
  auth smallint(6) default NULL,
  lastloginIP varchar(30) default NULL,
  lastlogintime bigint(20) default NULL,
  registerIP varchar(30) default NULL,
  registertime bigint(20) default NULL,
  totalonlinetime bigint(20) default NULL,
  PRIMARY KEY  (ID),
  UNIQUE KEY number (number)
) TYPE=MyISAM;


