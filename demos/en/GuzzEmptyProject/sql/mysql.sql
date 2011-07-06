
create database fms_main default character set utf8 ;

create database fms_temp default character set utf8 ;

create database fms_log default character set utf8 ;


use fms_temp ;

create table tb_guzz_su(
	gu_id bigint not null auto_increment primary key, 
	gu_db_group varchar(32) not null, 
	gu_tab_name varchar(64) not null, 
	gu_inc_col varchar(64) not null ,
	gu_tab_pk_col varchar(64) not null,
	gu_tab_pk_val varchar(64) not null ,
	gu_inc_count int(11) not null
)engine=Innodb ;


grant all privileges on fms_main.* to root@'%' identified by 'root' ;

grant all privileges on fms_temp.* to slowupdate@'%' identified by 'slowupdate' ;

grant all privileges on fms_log.* to log@'%' identified by 'log' ;


