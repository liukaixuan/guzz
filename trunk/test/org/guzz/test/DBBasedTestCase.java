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
	protected Connection conn = null ;
	
	public void assertEqualsIDWS(String left, String right){
		left = StringUtil.squeezeWhiteSpace(left) ;
		right = StringUtil.squeezeWhiteSpace(right) ;
		assertEquals(left, right) ;
	}	
	
	protected int executeUpdate(String sql) throws SQLException{
		Statement st = conn.createStatement() ;
		
		try{
			return st.executeUpdate(sql) ;
		}finally{
			CloseUtil.close(st) ;
		}
	}
	
	protected ResultSet executeQuery(String sql) throws SQLException{
		Statement st = conn.createStatement() ;
		
		return st.executeQuery(sql) ;
	}
	
	/**准备环境。如插入一些测试数据等。每次启动时数据库自动重新创建，不会保存历史数据。*/
	protected void prepareEnv() throws Exception{
		
	}
	
	/**恢复测试前环境，避免对其他测试用例干扰。*/
	protected void rollbackEnv() throws Exception{
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		Class.forName("org.h2.Driver");
		this.conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
		
		//创建一个表，插入一些测试数据。
		executeUpdate("drop table if exists TB_ARTICLE") ;
		
		executeUpdate("create table TB_ARTICLE(id int not null AUTO_INCREMENT primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP)") ;
		executeUpdate("insert into TB_ARTICLE values(1, 'title 1', 'content 1', now())") ;
		executeUpdate("insert into TB_ARTICLE values(2, 'title 2', 'content 2', '2009-08-26 13:45:09')") ;
		executeUpdate("insert into TB_ARTICLE values(3, 'title 3', 'content 3', now())") ;
		executeUpdate("insert into TB_ARTICLE values(4, 'title 4', 'content 4', now())") ;
		
		executeUpdate("drop table if exists TB_USER") ;
		executeUpdate("create table TB_USER(pk int not null auto_increment primary key , userName varchar(128), password varchar(255), VIP_USER bit, FAV_COUNT int, createdTime TIMESTAMP)") ;
		
		executeUpdate("drop table if exists TB_BOOK") ;
		executeUpdate("create table TB_BOOK(id int not null AUTO_INCREMENT primary key , NAME varchar(128), DESCRIPTION varchar(255), createdTime TIMESTAMP, ISDN varchar(64))") ;
		executeUpdate("insert into TB_BOOK values(1, 'book title 1', 'book content 1', now(), 'isdn-b1')") ;
				
		prepareEnv() ;
	}

	protected void tearDown() throws Exception {
		rollbackEnv() ;
		
		CloseUtil.close(conn) ;
		
		super.tearDown();
	}
	
}
