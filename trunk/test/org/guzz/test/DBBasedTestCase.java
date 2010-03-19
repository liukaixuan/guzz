/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;

import junit.framework.TestCase;

/**
 * 
 * 基于数据库的测试用例。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class DBBasedTestCase extends TestCase {
	protected Connection H2Conn = null ;
	protected Connection oracleConn = null ;
	
	public void assertEqualsIDWS(String left, String right){
		left = StringUtil.squeezeWhiteSpace(left) ;
		right = StringUtil.squeezeWhiteSpace(right) ;
		assertEquals(left, right) ;
	}	
	
	protected int executeUpdate(Connection conn, String sql) throws SQLException{
		Statement st = conn.createStatement() ;
		
		try{
			return st.executeUpdate(sql) ;
		}finally{
			CloseUtil.close(st) ;
		}
	}
	
	protected int executeUpdateNoException(Connection conn, String sql){
		Statement st = null ;
		
		try{
			st = conn.createStatement() ;
			return st.executeUpdate(sql) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}finally{
			CloseUtil.close(st) ;
		}
		
		return 0 ;
	}
	
	protected ResultSet executeQuery(Connection conn, String sql) throws SQLException{
		Statement st = conn.createStatement() ;
		
		return st.executeQuery(sql) ;
	}
	
	/**准备环境。如插入一些测试数据等。每次启动时数据库自动重新创建，不会保存历史数据。*/
	protected void prepareEnv() throws Exception{
		
	}
	
	/**恢复测试前环境，避免对其他测试用例干扰。*/
	protected void rollbackEnv() throws Exception{
		
	}
	
	protected String getDateFunction(){
//		return "sysdate" ;
		return "now()" ;
	}
	
	protected Connection getDefaultConn(){
		return this.H2Conn ;
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		setUpForOracle10G() ;
		setUpForH2() ;
		
		prepareEnv() ;
	}
	
	protected void setUpForH2() throws Exception {
		Class.forName("org.h2.Driver");
		this.H2Conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
		
		//创建一个表，插入一些测试数据。
		executeUpdate(H2Conn, "drop table if exists TB_ARTICLE") ;
		
		executeUpdate(H2Conn, "create table TB_ARTICLE(id int not null AUTO_INCREMENT primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP)") ;
		executeUpdate(H2Conn, "insert into TB_ARTICLE values(1, 'title 1', 'content 1', now())") ;
		executeUpdate(H2Conn, "insert into TB_ARTICLE values(2, 'title 2', 'content 2', '2009-08-26 13:45:09')") ;
		executeUpdate(H2Conn, "insert into TB_ARTICLE values(3, 'title 3', 'content 3', now())") ;
		executeUpdate(H2Conn, "insert into TB_ARTICLE values(4, 'title 4', 'content 4', now())") ;
		
		executeUpdate(H2Conn, "drop table if exists TB_USER") ;
		executeUpdate(H2Conn, "create table TB_USER(pk int not null auto_increment primary key , userName varchar(128), MyPSW varchar(255), VIP_USER bit, FAV_COUNT int, createdTime TIMESTAMP)") ;
		
		executeUpdate(H2Conn, "drop table if exists TB_BOOK") ;
		executeUpdate(H2Conn, "create table TB_BOOK(id int not null AUTO_INCREMENT primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP, ISDN varchar(64))") ;
		executeUpdate(H2Conn, "insert into TB_BOOK values(1, 'book title 1', 'book content 1', now(), 'isdn-b1')") ;
		
		//prepare for clob/blob
		executeUpdate(H2Conn, "drop table if exists TB_USER_INFO2") ;
		executeUpdate(H2Conn, "create table TB_USER_INFO2(pk int not null AUTO_INCREMENT primary key , userId varchar(64), aboutMe CLOB, portraitImg BLOB)") ;
		
		//comment shadow table
		executeUpdate(H2Conn, "drop table if exists TB_COMMENT1") ;
		executeUpdate(H2Conn, "drop table if exists TB_COMMENT2") ;

    	String sql = "create table TB_COMMENT(id int not null AUTO_INCREMENT primary key ,userId int(11), userName varchar(64), DESCRIPTION text, createdTime TIMESTAMP)" ;
    	executeUpdate(H2Conn, StringUtil.replaceString(sql, "TB_COMMENT", "TB_COMMENT1")) ;
		executeUpdate(H2Conn, StringUtil.replaceString(sql, "TB_COMMENT", "TB_COMMENT2")) ;
	}
	
	protected void setUpForOracle10G() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		this.oracleConn = DriverManager.getConnection("jdbc:oracle:thin:@10.64.4.31:1521:orcl", "vote", "vote");
		
		//创建seq
		executeUpdateNoException(oracleConn, "drop SEQUENCE guzz_sequence") ;
		executeUpdateNoException(oracleConn, "CREATE SEQUENCE guzz_sequence INCREMENT BY 1 START WITH 100000") ;		
		
		//创建一个表，插入一些测试数据。
		executeUpdateNoException(oracleConn, "drop table TB_ARTICLE") ;
		executeUpdate(oracleConn, "create table TB_ARTICLE(id number(10) not null primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP)") ;
		executeUpdate(oracleConn, "insert into TB_ARTICLE values(1, 'title 1', 'content 1', sysdate)") ;
		executeUpdate(oracleConn, "insert into TB_ARTICLE values(2, 'title 2', 'content 2', to_date('2009-08-26 13:45:09', 'yyyy-MM-DD HH24:MI:SS'))") ;
		executeUpdate(oracleConn, "insert into TB_ARTICLE values(3, 'title 3', 'content 3', sysdate)") ;
		executeUpdate(oracleConn, "insert into TB_ARTICLE values(4, 'title 4', 'content 4', sysdate)") ;
		
		executeUpdateNoException(oracleConn, "drop table TB_USER") ;
		executeUpdate(oracleConn, "create table TB_USER(pk number(10) not null primary key , userName varchar(128), MyPSW varchar(255), VIP_USER number(1), FAV_COUNT number(10), createdTime TIMESTAMP)") ;
		
		executeUpdateNoException(oracleConn, "drop table TB_BOOK") ;
		executeUpdate(oracleConn, "create table TB_BOOK(id number(10) not null primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP, ISDN varchar(64))") ;
		executeUpdate(oracleConn, "insert into TB_BOOK values(1, 'book title 1', 'book content 1', sysdate, 'isdn-b1')") ;
		
		executeUpdateNoException(oracleConn, "drop table TB_USER_INFO") ;
		executeUpdate(oracleConn, "create table TB_USER_INFO(pk number(10) not null primary key , userId varchar(64), aboutMe CLOB, portraitImg BLOB)") ;
	}

	protected void tearDown() throws Exception {
		rollbackEnv() ;
		
		CloseUtil.close(oracleConn) ;
		CloseUtil.close(H2Conn) ;
		
		super.tearDown();
	}
	
}
