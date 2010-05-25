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
package org.guzz.orm.se;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.guzz.test.shop.SpecialProperty;
import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestBeanMap extends DBBasedTestCase{
	
	protected void prepareEnv() throws Exception {
		super.prepareEnv();
		
		//create special property table in H2 database.
//		executeUpdate(H2Conn, "drop table if exists tb_cargo") ;
//		executeUpdate(H2Conn, "create table tb_cargo(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, storeCount int(11), price double, onlineTime datetime)") ;
        
		executeUpdate(H2Conn, "drop table if exists tb_s_property") ;
		executeUpdate(H2Conn, "create table tb_s_property(id int not null AUTO_INCREMENT primary key , cargoName varchar(32), propName varchar(32), colName varchar(32), dataType varchar(32))") ;
				
		//add book and cross-stitch's special properties' declarations into tb_s_property.
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'ISBN','ISBN','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'author','author','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'publisher','publisher','string')") ;
		
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'gridNum','gridNum','int')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'backColor','backColor','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'size','size','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'brand','brand','string')") ;
		
		
		//create table for cargo book and cargo cross-stitch.
		//we know the rule is : return "tb_cargo_" + cargoName;
		//cargo book:
		executeUpdate(H2Conn, "drop table if exists tb_cargo_book") ;
		executeUpdate(H2Conn, "create table tb_cargo_book(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, storeCount int(11), price double, onlineTime datetime" +
				", ISBN varchar(64) not null" +
				", author varchar(64)" +
				", publisher varchar(64)" +
				")") ;
		//cargo cross-stitch:
		executeUpdate(H2Conn, "drop table if exists tb_cargo_crossStitch") ;
		executeUpdate(H2Conn, "create table tb_cargo_crossStitch(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, storeCount int(11), price double, onlineTime datetime" +
				", gridNum int(11) not null" +
				", backColor varchar(64)" +
				", size varchar(64)" +
				", brand varchar(64)" +
				")") ;
	}
	
	public void testToMap() throws Exception{
		String sql = "select id as a, NAME as b, DESCRIPTION as c, createdTime as d from TB_ARTICLE" ;
		
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(Article.class, sql) ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		List<HashMap> resuts = session.list(cs.bindNoParams().setBeanMapRowDataLoader(HashMap.class), 1, 2) ;
		assertEquals(resuts.size(), 2) ;
		assertEquals(resuts.get(0).size(), 4) ;
	}
	
	public void testToBean() throws Exception{
		User u = new User(1) ;
		u.setPassword("psw_124") ;
		
		Date now = new Date() ;
		
		u.setCreatedTime(now) ;
		tm.openRWTran(true).insert(u) ;
		
		String sql = "select a.@id as id, a.@cargoName, c.MyPSW, c.createdTime as createdTime from @@sp as a left join @@user as c on a.@id = pk where a.@id > 0 order by a.@id asc" ;
		
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(SpecialProperty.class, sql) ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		List<ArticleModel> resuts = session.list(cs.bindNoParams().setBeanMapRowDataLoader(ArticleModel.class), 1, 2) ;
		assertEquals(resuts.size(), 2) ;
		assertEquals(resuts.get(0).getCargoName(), "book") ;
		assertEquals(resuts.get(0).getMyPSW(), "psw_124") ;
		assertEquals(resuts.get(0).getCreatedTime().getTime(), now.getTime()) ;
	}
	
	public static class ArticleModel{
		private int id ;
		
		/**which cargo this property belongs.*/
		private String cargoName ;
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getCargoName() {
			return cargoName;
		}

		public void setCargoName(String cargoName) {
			this.cargoName = cargoName;
		}

		public String getPropName() {
			return propName;
		}

		public void setPropName(String propName) {
			this.propName = propName;
		}

		public String getColName() {
			return colName;
		}

		public void setColName(String colName) {
			this.colName = colName;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getMyPSW() {
			return MyPSW;
		}

		public void setMyPSW(String myPSW) {
			MyPSW = myPSW;
		}

		public Date getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(Date createdTime) {
			this.createdTime = createdTime;
		}

		public boolean isEdited() {
			return edited;
		}

		public void setEdited(boolean edited) {
			this.edited = edited;
		}

		/**property name of the cargo (used in java).*/
		private String propName ;
		
		/**the column name in the table of database to store the propety.*/
		private String colName ;
		
		private String userName ;
		
		private String MyPSW ;

		private Date createdTime ;
		
		private boolean edited ;
		
	}

	
}

