
--master database

create database bigVote default character set utf8 ;
grant all privileges on bigVote.* to root@'%' identified by 'root' ;

use bigVote ;

create table tb_big_vote(
	vote_id int(11) not null auto_increment primary key, 
	vote_channelId int(11) not null,
	vote_name varchar(128) not null, 
	vote_territoryPolicy varchar(32) default 'city',
	maxItemsPerVote int(11) default 0,
	vote_people int(11) default 0,
	vote_num int(11) default 0,
	vote_addedNum int(11) default 0,
	vote_status int(11) default 0,
	vote_beginTime timestamp null default null,
	vote_endTime timestamp null default null,
	vote_createdTime timestamp
)engine=Innodb ;

create table tb_territory_vote_log(
	territory_vote_id varchar(64) not null primary key, 
	vote_id int(11) not null,
	item_id int(11) not null,
	territory_id int(11) not null,
	vote_num int(11) default 0
)engine=Innodb ;

create index idx_tv_iid on tb_territory_vote_log(item_id) ;
create index idx_tv_tid on tb_territory_vote_log(territory_id) ;


create table tb_vote_item(
	item_id int(11) auto_increment not null primary key, 
	vote_id int(11) not null,
	group_id int(11) default 0,
	vote_num int(11) default 0,
	vote_addedNum int(11) default 0,
	item_name varchar(64) not null,	
	item_showName varchar(255)
)engine=Innodb ;

create index idx_v_vid on tb_vote_item(vote_id) ;
create index idx_v_gid on tb_vote_item(group_id) ;


create table tb_vote_territory(
	territory_id int(11) auto_increment not null primary key, 
	vote_id int(11) not null,
	vote_people int(11) default 0,
	vote_num int(11) default 0 ,
	vote_addedNum int(11) default 0,
	territory_name varchar(64) not null
)engine=Innodb ;

create index idx_v_vid on tb_vote_territory(vote_id) ;


create table tb_vote_extra_property(
	prop_id int(11) auto_increment not null primary key,
	vote_id int(11) not null,
	prop_paramName varchar(16) not null,
	prop_showName varchar(16) not null,
	prop_mustProp bit(1),
	prop_validValues varchar(255),
	prop_defaultValue varchar(32),
	prop_validRuleName varchar(32),
	prop_ruleParamValue varchar(255)
)engine=Innodb ;

create index idx_vote_ep_voteId on tb_vote_extra_property(vote_id) ;

create table tb_vote_item_group(
	group_id int(11) auto_increment not null primary key, 
	vote_id int(11) not null,
	group_name varchar(64) not null,	
	group_createdTime timestamp
)engine=Innodb ;

create index idx_vig_voteId on tb_vote_item_group(vote_id) ;


create table tb_anti_cheat_policy(
	ac_id int(11) auto_increment not null primary key, 
	vote_id int(11) not null,
	ac_maxLife int(11) default 600,
	ac_allowedCount int(11) default 1,
	ac_Name varchar(64) not null,	
	ac_policyImpl varchar(64) not null,	
	ac_limitedField varchar(64) default '',
	ac_createdTime timestamp default now()
)engine=Innodb ;

create index idx_acp_vid on tb_anti_cheat_policy(vote_id) ;


create table tb_vote_channel(
	channel_id int(11) auto_increment not null primary key, 
	parent_id int(11) default 0,
	channel_name varchar(255) not null,
	channel_authGroup varchar(32) not null,
	channel_createdTime timestamp
)engine=Innodb ;

create index idx_ch_authGroup on tb_vote_channel(channel_authGroup) ;



--slow update database

create database guzzSlowUpdate default character set utf8 ;
grant all privileges on guzzSlowUpdate.* to slowupdate@'%' identified by 'slowupdate' ;

use guzzSlowUpdate ;

create table tb_guzz_su(
	gu_id bigint not null auto_increment primary key, 
	gu_db_group varchar(32) not null, 
	gu_tab_name varchar(64) not null, 
	gu_inc_col varchar(64) not null ,
	gu_tab_pk_col varchar(64) not null,
	gu_tab_pk_val varchar(64) not null ,
	gu_inc_count int(11) not null
)engine=Innodb ;

create table tb_vote_log(
	vote_log_id int(11) auto_increment not null primary key, 
	vote_id int(11) not null,
	item_name varchar(64) not null,
	territory_name varchar(64) not null,
	voter_IP varchar(32),
	vote_createdTime timestamp,
	voter_extraPropsXML text
)engine=Innodb ;

