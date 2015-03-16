## IncUpdateBusiness.hbm.xml ##

```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
    <class name="org.guzz.service.core.impl.IncUpdateBusiness" table="tb_guzz_su">
        <id name="id" type="bigint" column="gu_id">
        	<generator class="native">
        		<param name="sequence">seq_iub_id</param>
        	</generator>
        </id>
        <property name="dbGroup" type="string" column="gu_db_group" />
        <property name="tableName" type="string" column="gu_tab_name" />
        <property name="columnToUpdate" type="string" column="gu_inc_col" />
        <property name="pkColunName" type="string" column="gu_tab_pk_col" />
        <property name="pkValue" type="string" column="gu_tab_pk_val" />
        <property name="countToInc" type="int" column="gu_inc_count" />
    </class>
</hibernate-mapping>


<!--
mysql:
create table tb_guzz_su(
	gu_id bigint not null auto_increment primary key, 
	gu_db_group varchar(32) not null, 
	gu_tab_name varchar(64) not null, 
	gu_inc_col varchar(64) not null ,
	gu_tab_pk_col varchar(64) not null,
	gu_tab_pk_val varchar(64) not null ,
	gu_inc_count int(11) not null
)engine=Innodb ;


oracle 10g:

CREATE SEQUENCE seq_iub_id INCREMENT BY 1 START WITH 1 ;

create table tb_guzz_su(
	gu_id number(20) not null primary key, 
	gu_db_group varchar(32) not null, 
	gu_tab_name varchar(64) not null, 
	gu_inc_col varchar(64) not null ,
	gu_tab_pk_col varchar(64) not null,
	gu_tab_pk_val varchar(64) not null ,
	gu_inc_count number(10, 0) not null
) ;
-->

```