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
package org.guzz.transaction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestTranSession extends DBBasedTestCase{

	public void testOpenTranSession() throws Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		
		ts.close() ;
		
		ts = gf.getTransactionManager().openNoDelayReadonlyTran() ;
		ts.close() ;
		
		WriteTranSession ts2 = gf.getTransactionManager().openRWTran(true) ;
		ts2.close() ;

		ts2 = gf.getTransactionManager().openRWTran(false) ;
		ts2.close() ;
	}
	
	public void testSEReadDB() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		
		//query a object
		SearchExpression se = SearchExpression.forClass(Article.class) ;
		se.and(Terms.eq("id", new Integer(1))) ;
		
		Article a = (Article) ts.findObject(se) ;
		
		assertNotNull(a) ;
		assertEquals(a.getTitle(), "title 1") ;
		
		//query a list
		se = SearchExpression.forClass(Article.class) ;
		se.and(Terms.smaller("id", new Integer(4))) ;
		List articles = ts.list(se) ;
		assertNotNull(articles) ;
		assertEquals(articles.size(), 3) ;
		assertEquals(((Article) articles.get(2)).getTitle(), "title 3") ;
				
		ts.close() ;
	}
	
	public void testReadDBCell00() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		
		//query a object
		CompiledSQL sql = gf.getTransactionManager().getCompiledSQLBuilder().buildCompiledSQL("article", "select count(*) from @@article") ;
						
		assertEquals((Long)ts.findCell00(sql.bindNoParams(), "bigint"), new Long(4)) ;
		
		ts.close() ;
	}
	
	public void testJDBCTemplate() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		
		JDBCTemplate jt = ts.createJDBCTemplate(Article.class, null) ;
		String name2 = (String) jt.executeQuery("select * from TB_ARTICLE where id = 2", new SQLQueryCallBack(){

			public Object iteratorResultSet(ResultSet rs) throws Exception {
				if(rs.next()){
					return rs.getString("NAME");
				}
				return null ;
			}
			
		}) ;
		
		assertEquals(name2, "title 2") ;
		
		ts.close() ;
	}
		
	public void testTimeout() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		try{
			//timeout in 2 seconds
			ts.setQueryTimeoutInSeconds(2) ;
			
			JDBCTemplate jdbc = ts.createJDBCTemplateByDbGroup("mysql") ;
			jdbc.executeQueryWithoutPrepare("select benchmark(3000000, md5('iteratorResultSet') ) as a", 
					new SQLQueryCallBack() {
						
						public Object iteratorResultSet(ResultSet rs) throws Exception {
							if(rs.next()){
								return rs.getString("a") ;
							}
							
							return null ;
						}
					}
			) ;
			
			fail("Should be timed out.") ;
			
		}catch(Exception e){
			e.printStackTrace() ;
		}finally{
			ts.close() ;
		}
	}
		
	public void testTimeout2() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		try{
			//timeout in 2 seconds
			ts.setQueryTimeoutInSeconds(2) ;
			
			JDBCTemplate jdbc = ts.createJDBCTemplateByDbGroup("mysql") ;
			
			Connection conn = jdbc.getConnection() ;
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE) ;
			
			Statement st = conn.createStatement() ;
			st.execute("select benchmark(3000000, md5('iteratorResultSet') ) as a") ;
			
			st.close() ;
			fail("Should be timed out.") ;
		}catch(Exception e){
			e.printStackTrace() ;
		}finally{
			ts.resetTransactionIsolationToLastSavePointer() ;
			
			ts.close() ;
		}
	}
		
	public void testResetTransactionIsolation() throws SQLException, Exception{
		ReadonlyTranSession ts = gf.getTransactionManager().openDelayReadTran() ;
		try{			
			JDBCTemplate jdbc = ts.createJDBCTemplateByDbGroup("mysql") ;
			
			Connection conn = jdbc.getConnection() ;
			
			int old = conn.getTransactionIsolation() ;
			
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE) ;
			
			Statement st = conn.createStatement() ;
			st.execute("select benchmark(300000, md5('iteratorResultSet') ) as a") ;
			
			st.close() ;
			
			assertTrue(ts.isIsolationLevelChanged()) ;
			
			ts.resetTransactionIsolationToLastSavePointer() ;
			assertFalse(ts.isIsolationLevelChanged()) ;
			
			assertEquals(old, conn.getTransactionIsolation()) ;
			assertTrue(Connection.TRANSACTION_SERIALIZABLE != conn.getTransactionIsolation()) ;
		}finally{
			ts.close() ;
		}
	}
	
}
