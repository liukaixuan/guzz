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

import java.util.HashMap;
import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.dao.PageFlip;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestDaoRead extends DBBasedTestCase {
	
	public void testFind() throws Exception{
		
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		assertNotNull(gf.getDialect("default")) ;
		
		Business ga = gf.instanceNewGhost("article", null, null, null) ;
		gf.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		TransactionManager tm = gf.getTransactionManager() ;
		
		SearchExpression se = SearchExpression.forClass(Article.class) ;
		se.and(Terms.eq("title", "title 1")) ;
				
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		List articles = session.list(se) ;
		assertEquals(articles.size(), 1) ;		
		
		session.close() ;
	}
	
	public void testFindObjectById() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		TransactionManager tm = gf.getTransactionManager() ;
		
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		for(int i = 1 ; i < 1000 ; i++){
			HashMap params = new HashMap() ;
			params.put("id", new Integer(i)) ;
			
			User user = (User) session.findObject("selectUser", params) ;
			assertNotNull(user) ;
			assertEquals(user.getUserName(), "name " + i) ;	
			assertEquals(user.getPassword(), "psw " + i) ;	
			assertEquals(user.getFavCount(), i) ;		
			assertEquals(user.isVip(), (i%2 == 0)) ;		
		}
		
		for(int i = 1000 ; i < 1050 ; i++){
			HashMap params = new HashMap() ;
			params.put("id", new Integer(i)) ;
			
			User user = (User) session.findObject("selectUser", params) ;
			assertNull(user) ;
		}
		
	}
	
	public void testFindListById() throws Exception{
		FileResource fr = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContext gf = new Configuration(fr).newGuzzContext() ;
		
		TransactionManager tm = gf.getTransactionManager() ;		
		ReadonlyTranSession session = tm.openDelayReadTran() ;		
			
		List users = session.list("selectUsers", null, 1, 10) ;
		assertNotNull(users) ;
		assertEquals(users.size(), 10) ;		
	}
	
	public void testFindListBySE() throws Exception{
		FileResource fr = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContext gf = new Configuration(fr).newGuzzContext() ;
		
		
		SearchExpression se = SearchExpression.forClass(User.class) ;
		se.and(Terms.eq("id", 58)) ;
		
		TransactionManager tm = gf.getTransactionManager() ;		
		ReadonlyTranSession session = tm.openDelayReadTran() ;		
		
		assertEquals(session.list(se).size(), 1) ;
		
		se = SearchExpression.forClass(User.class) ;
		se.and(Terms.eq("id", 58000)) ;
		assertEquals(session.list(se).size(), 0) ;
		
		se = SearchExpression.forClass(User.class) ;		
		assertEquals(session.list(se).size(), SearchExpression.DEFAULT_PAGE_SIZE) ;
		
		se = SearchExpression.forClass(User.class) ;
		se.setPageNo(1) ;
		se.setPageSize(9) ;
		assertEquals(session.list(se).size(), 9) ;		
		

		se = SearchExpression.forClass(User.class) ;
		se.setPageNo(34) ;
		se.setPageSize(30) ;
		assertEquals(session.list(se).size(), 9) ;
		
		
		se = SearchExpression.forClass(User.class) ;
		se.setPageNo(35) ;
		se.setPageSize(30) ;
		assertEquals(session.list(se).size(), 0) ;
	}
	
	public void testFindPageBySE() throws Exception{
		FileResource fr = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContext gf = new Configuration(fr).newGuzzContext() ;		
		TransactionManager tm = gf.getTransactionManager() ;		
		ReadonlyTranSession session = tm.openDelayReadTran() ;		
		
		//normal
		SearchExpression se = SearchExpression.forClass(User.class) ;
		se.setPageNo(2) ;
		se.setPageSize(10) ;
		se.setOrderBy("id asc") ;
		PageFlip page = session.page(se) ;
		assertEquals(page.getElements().size(), 10) ;
		
		for(int i = 0 ; i < page.getElements().size() ; i++){
			User u = (User) page.getElements().get(i) ;
			assertEquals(u.getId(), 11 + i) ;
			assertEquals(u.getUserName(), "name " + (i + 11)) ;
		}
		
		//count
		assertEquals(page.getTotalCount(), 999) ;
		
		//with skipCount
		se = SearchExpression.forClass(User.class) ;
		se.setPageNo(2) ;
		se.setPageSize(10) ;
		se.setSkipCount(5) ;
		se.setOrderBy("id asc") ;
		page = session.page(se) ;
		assertEquals(page.getElements().size(), 10) ;
		
		for(int i = 0 ; i < page.getElements().size() ; i++){
			User u = (User) page.getElements().get(i) ;
			assertEquals(u.getId(), 11 + 5 + i) ;
			assertEquals(u.getUserName(), "name " + (i + 11 + 5)) ;
		}
		
		//count
		assertEquals(page.getTotalCount(), 999) ;
	}

	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate("insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + ((i%2==0)?1:0) + ", " + i + ", " + getDateFunction() + ")") ;		
		}
		
	}

}
