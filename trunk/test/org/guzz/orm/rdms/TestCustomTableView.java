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
package org.guzz.orm.rdms;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.guzz.Guzz;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.jdbc.ObjectBatcher;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.shop.Cargo;
import org.guzz.test.shop.CargoStatus;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * Assume we have two cargo items, book and cross-stitch.
 * <p>
 * The 'book' contains special properties of ISBN, author and publisher.<br>
 * The 'cross-stitch' contains special properties of gridNum, backColor, size and brand.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestCustomTableView extends DBBasedTestCase {
	
	protected Connection H2CrossStitchConn = null ;
	
	protected void prepareEnv() throws Exception {
		super.prepareEnv();
		
		this.H2CrossStitchConn = ((PhysicsDBGroup) this.gf.getDBGroup("cargoDB.cargo2")).getMasterDB().getDataSource().getConnection() ;
		
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
		executeUpdate(H2Conn, "drop table if exists tb_cargo_crossStitch") ;
		executeUpdate(H2Conn, "create table tb_cargo_book(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, storeCount int(11), price double, onlineTime datetime, statusThisWeek int(11), statusNextWeek varchar(32)" +
				", ISBN varchar(64) not null" +
				", author varchar(64)" +
				", publisher varchar(64)" +
				")") ;
		
		//cargo cross-stitch:
		executeUpdate(H2CrossStitchConn, "drop table if exists tb_cargo_book") ;
		executeUpdate(H2CrossStitchConn, "drop table if exists tb_cargo_crossStitch") ;
		executeUpdate(H2CrossStitchConn, "create table tb_cargo_crossStitch(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, storeCount int(11), price double, onlineTime datetime, statusThisWeek int(11), statusNextWeek varchar(32)" +
				", gridNum int(11) not null" +
				", backColor varchar(64)" +
				", size varchar(64)" +
				", brand varchar(64)" +
				")") ;
	}
		
	public void testInsert() throws Exception{
		WriteTranSession session = tm.openRWTran(true) ;
		
		for(int i = 1 ; i < 1001 ; i++){
			Cargo book = new Cargo() ;
			
			book.setName("book" + i) ;
			book.setDescription("nice book " + i) ;
			book.setPrice(33.56) ;
			book.setStoreCount(i % 10) ;
			book.setStatusThisWeek(CargoStatus.NORMAL) ;
			book.setStatusNextWeek(CargoStatus.LIMITED) ;
			
			Date now = new Date() ;
			book.setOnlineTime(now) ;
			
			//ISBN, author and publisher
			book.getSpecialProps().put("ISBN", "isbn-bbb-" + i) ;
			book.getSpecialProps().put("author", "not me") ;
			if(i % 2 == 0){
				book.getSpecialProps().put("publisher", "wolf") ;
			}else{
				book.getSpecialProps().put("publisher", "sheep") ;
			}
			
			//add a trash property.
			book.getSpecialProps().put("xxx", "raise a exception?") ;
			
			Guzz.setTableCondition("book") ;
			
			Integer pk = (Integer) session.insert(book) ;
			
			assertEquals(pk.intValue(), i) ;
		}
		
		//cross-stitch
		for(int i = 1 ; i < 1001 ; i++){
			Cargo cs = new Cargo() ;
			
			cs.setName("cs" + i) ;
			cs.setDescription("good cross-stitch " + i) ;
			cs.setPrice(86.56) ;
			cs.setStoreCount(i % 30) ;
			cs.setStatusThisWeek(CargoStatus.ON_SALE) ;
			cs.setStatusNextWeek(null) ;
			
			Date now = new Date() ;
			cs.setOnlineTime(now) ;
			
			//gridNum, backColor, size and brand
			cs.getSpecialProps().put("gridNum", new Integer(i)) ;
			cs.getSpecialProps().put("backColor", "white") ;
			cs.getSpecialProps().put("size", "56x84") ;
			if(i % 2 == 0){
				cs.getSpecialProps().put("brand", "cherry") ;
			}else{
				cs.getSpecialProps().put("brand", "湘湘绣铺") ;
			}
			
			Guzz.setTableCondition("crossStitch") ;
			
			Integer pk = (Integer) session.insert(cs) ;
			
			assertEquals(pk.intValue(), i) ;
		}
		
		session.close() ;
	}
	
	public void testUpdate() throws Exception{
		testInsert() ;
		
		Guzz.setTableCondition("book") ;
		WriteTranSession session = tm.openRWTran(true) ;
		
		int pk = 10 ;
		
		//check read is ok.
		Cargo book = (Cargo) session.findObjectByPK(Cargo.class, pk) ;
		
		assertNotNull(book) ;
		assertEquals(book.getName(), "book" + pk) ;
		assertEquals(book.getDescription(), "nice book " + pk) ;
		assertEquals(book.getPrice(), 33.56d, 0.1) ;
		assertEquals(book.getStoreCount(), pk % 10) ;
		assertEquals(book.getSpecialProps().get("ISBN"), "isbn-bbb-" + pk) ;
		assertEquals(book.getSpecialProps().get("author"), "not me") ;
		assertEquals(book.getSpecialProps().get("publisher"), "wolf") ;
		assertEquals(book.getStatusThisWeek(), CargoStatus.NORMAL) ;
		assertEquals(book.getStatusNextWeek(), CargoStatus.LIMITED) ;
		
		book.setName("new book" + pk) ;
		book.getSpecialProps().put("ISBN", "new-ISBN-xxxxxxxxxxxxxxxxxx-" + pk) ;
		session.update(book) ;
		
		//read again
		book = (Cargo) session.findObjectByPK(Cargo.class, pk) ;
		assertNotNull(book) ;
		assertEquals(book.getName(), "new book" + pk) ;
		assertEquals(book.getDescription(), "nice book " + pk) ;
		assertEquals(book.getPrice(), 33.56d, 0.1) ;
		assertEquals(book.getStoreCount(), pk % 10) ;
		assertEquals(book.getSpecialProps().get("ISBN"), "new-ISBN-xxxxxxxxxxxxxxxxxx-" + pk) ;
		assertEquals(book.getSpecialProps().get("author"), "not me") ;
		assertEquals(book.getSpecialProps().get("publisher"), "wolf") ;
		
		//test refresh
		book.setPrice(65d) ;
		book.getSpecialProps().put("publisher", "egg") ;
		book.setStatusThisWeek(CargoStatus.SHORTAGE) ;
		book.setStatusNextWeek(CargoStatus.SHORTAGE) ;
		session.update(book) ;
		
		session.refresh(book, LockMode.UPGRADE) ;
		assertEquals(book.getName(), "new book" + pk) ;
		assertEquals(book.getDescription(), "nice book " + pk) ;
		assertEquals(book.getPrice(), 65d, 0.1) ;
		assertEquals(book.getStoreCount(), pk % 10) ;
		assertEquals(book.getSpecialProps().get("ISBN"), "new-ISBN-xxxxxxxxxxxxxxxxxx-" + pk) ;
		assertEquals(book.getSpecialProps().get("author"), "not me") ;
		assertEquals(book.getSpecialProps().get("publisher"), "egg") ;
		assertEquals(book.getStatusThisWeek(), CargoStatus.SHORTAGE) ;
		assertEquals(book.getStatusNextWeek(), CargoStatus.SHORTAGE) ;
		
		session.close() ;
	}
	
	public void testDelete() throws Exception{
		testInsert() ;
		
		Guzz.setTableCondition("book") ;
		WriteTranSession session = tm.openRWTran(true) ;
		
		int pk = 10 ;
		
		//check read is ok.
		Cargo book = (Cargo) session.findObjectByPK(Cargo.class, pk) ;
		session.delete(book) ;
		
		try{
			session.refresh(book, LockMode.UPGRADE) ;
			
			fail("book is deleted, so refresh() should fail.") ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		book = (Cargo) session.findObjectByPK(Cargo.class, pk) ;
		assertNull(book) ;
		
		session.close() ;
	}
	
	public void testFindBySE() throws Exception{
		testInsert() ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		Guzz.setTableCondition("book") ;
		SearchExpression se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.bigger("id", 0)) ;
		assertEquals(session.count(se), 1000) ;
		
		//add conditon for special property
		se.and(Terms.eq("publisher", "wolf")) ;
		assertEquals(session.count(se), 500) ;
		
		se = SearchExpression.forClass(Cargo.class) ;
		assertEquals(session.count(se), 1000) ;
				
		//SearchExpression.setTableCondition条件优先级高于Guzz.setTableCondition。
		se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.eq("brand", "湘湘绣铺")).setTableCondition("crossStitch") ;
		assertEquals(session.count(se), 500) ;
		
		//list 
		Guzz.setTableCondition(null) ;
		se = SearchExpression.forClass(Cargo.class).setPageSize(1000) ;
		se.and(Terms.eq("publisher", "wolf")) ;
		se.and(Terms.eq("price", new Double(33.56))) ;
		se.setOrderBy("id asc") ;
		
		try{
			session.list(se) ;
			fail("null tableCondition should raise a exception.") ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		se.setTableCondition("book") ;
		List books = session.list(se) ;
		assertEquals(books.size(), 500) ;
		
		for(int i = 0 ; i < books.size() ; i++){
			Cargo book = (Cargo) books.get(i) ;
			
			int pk = book.getId() ;
			
			assertEquals(book.getName(), "book" + pk) ;
			assertEquals(book.getDescription(), "nice book " + pk) ;
			assertEquals(book.getPrice(), 33.56d, 0.1) ;
			assertEquals(book.getStoreCount(), pk % 10) ;
			assertEquals(book.getSpecialProps().get("ISBN"), "isbn-bbb-" + pk) ;
			assertEquals(book.getSpecialProps().get("author"), "not me") ;
			assertEquals(book.getSpecialProps().get("publisher"), "wolf") ;
		}
		
		session.close() ;
	}
	
	public void testEnum() throws Exception{
		testInsert() ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		Guzz.setTableCondition("book") ;
		
		//find by enum condition
		SearchExpression se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.eq("statusThisWeek", CargoStatus.NORMAL)) ;
		se.and(Terms.eq("statusNextWeek", CargoStatus.LIMITED)) ;
		assertEquals(session.count(se), 1000) ;

		se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.eq("statusThisWeek", CargoStatus.NORMAL)) ;
		se.and(Terms.eq("statusNextWeek", CargoStatus.SHORTAGE)) ;
		assertEquals(session.count(se), 0) ;
		
		//test read null enum
		se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.eq("brand", "湘湘绣铺")).setTableCondition("crossStitch") ;
		assertEquals(session.count(se), 500) ;
		
		List cts = session.list(se) ;
		for(int i = 0 ; i < cts.size() ; i++){
			Cargo ct = (Cargo) cts.get(i) ;

			assertEquals(ct.getStatusThisWeek(), CargoStatus.ON_SALE) ;
			assertEquals(ct.getStatusNextWeek(), null) ;
		}
		
		//test enum value changed in the database.
		WriteTranSession write = tm.openRWTran(true) ;
		JDBCTemplate jdbc = write.createJDBCTemplate(Cargo.class, "crossStitch") ;
		jdbc.executeUpdate("update tb_cargo_crossStitch set statusThisWeek = null, statusNextWeek='LIMITED'") ;
		write.close() ;
		
		cts = session.list(se) ;
		for(int i = 0 ; i < cts.size() ; i++){
			Cargo ct = (Cargo) cts.get(i) ;

			//
			assertEquals(ct.getStatusThisWeek(), null) ;
			assertEquals(ct.getStatusNextWeek(), CargoStatus.LIMITED) ;
		}
		
		session.close() ;
	}
	
	public void testCompiledSQL() throws Exception{
		testInsert() ;
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		String sql = "select * from @@cargo where @publisher = :publisher2" ;
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(Cargo.class, sql) ;
		cs.addParamPropMapping("publisher2", "publisher") ;
		
		Guzz.setTableCondition("book") ;
		assertEquals(session.list(cs.bind("publisher2", "sheep"), 1, 1000).size(), 500) ;
		assertEquals(session.list(cs.bind("publisher2", "else"), 1, 1000).size(), 0) ;
		
		session.close() ;
	}
	
	public void testIBatisQuery() throws Exception{
		testInsert() ;
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		HashMap params = new HashMap() ;
		params.put("id", 100) ;
		
		Guzz.setTableCondition("crossStitch") ;
		List cargoes = session.list("selectCrossSize", params) ;
		
		assertEquals(cargoes.size(), 900) ;
		
		session.close() ;
	}
	
	public void testQueryThroughTables() throws Exception{
		testInsert() ;
		
		WriteTranSession write = tm.openRWTran(true) ;
		
		int pk = 10 ;
		Guzz.setTableCondition("book") ;
		Cargo book = (Cargo) write.findObjectByPK(Cargo.class, pk) ;
		book.setPrice(100.00) ;
		write.update(book) ;
		
		Guzz.setTableCondition("crossStitch") ;
		Cargo crossStitch = (Cargo) write.findObjectByPK(Cargo.class, pk) ;
		crossStitch.setPrice(100.00) ;
		write.update(crossStitch) ;
		
		write.close() ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ; 
		
		Guzz.setTableCondition("all") ;
		
		//list all cargoes priced above 100.00
		String sql="select c.* from (select @id, @name, @storeCount from tb_cargo_book where @price>=:param_price union all select @id, @name, @storeCount from tb_cargo_crossstitch where @price>=:param_price) as c ";
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(Cargo.class, sql) ; 
		cs.addParamPropMapping("param_price", "price") ;
		assertEquals(session.list(cs.bind("param_price", 100.00), 1, 1000).size(), 2) ;
		
		//result as HashMap -- set bsql
		assertEquals(session.list(cs.bind("param_price", 100.00).setResultClass(java.util.HashMap.class), 1, 1000).size(), 2) ;
		
		//result as HashMap -- set cs
		cs.setResultClass(java.util.HashMap.class) ;
		assertEquals(session.list(cs.bind("param_price", 100.00), 1, 1000).size(), 2) ;
		
		session.close() ;
	}
	
	public void testObjectBatcher() throws Exception{
		WriteTranSession session = tm.openRWTran(false) ;
		ObjectBatcher batcher = session.createObjectBatcher() ;
		
		Guzz.setTableCondition("book") ;
		for(int i = 1 ; i < 1001 ; i++){
			Cargo book = new Cargo() ;
			
			book.setName("book" + i) ;
			book.setDescription("nice book " + i) ;
			book.setPrice(33.56) ;
			book.setStoreCount(i % 10) ;
			
			Date now = new Date() ;
			book.setOnlineTime(now) ;
			
			//ISBN, author and publisher
			book.getSpecialProps().put("ISBN", "isbn-bbb-" + i) ;
			book.getSpecialProps().put("author", "not me") ;
			if(i % 2 == 0){
				book.getSpecialProps().put("publisher", "wolf") ;
			}else{
				book.getSpecialProps().put("publisher", "sheep") ;
			}
			
			//add a trash property.
			book.getSpecialProps().put("xxx", "raise a exception?") ;
			
			batcher.insert(book) ;
		}
		
		batcher.executeUpdate() ;
		session.commit() ;
		
		session.close() ;
		
		ReadonlyTranSession read = tm.openNoDelayReadonlyTran() ;
		
		SearchExpression se = SearchExpression.forClass(Cargo.class) ;
		assertEquals(read.count(se), 1000) ;
		assertEquals(read.count(se.setTableCondition("crossStitch")), 0) ;
		
		read.close() ;
	}
	
	protected void setUpForOracle10G() throws Exception {
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
		this.H2CrossStitchConn.close() ;
	}

}
