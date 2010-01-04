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
import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestDaoWrite extends DBBasedTestCase {

	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate("insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + (i%2==0) + ", " + i + ", now())") ;		
		}		
	}
	
	public void testInsert() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		
		Article a = new Article() ;
		a.setContent("my content") ;
		Date now = new Date() ;
		a.setCreatedTime(now) ;
		a.setKeywords("a, b, c, d") ;
		a.setTitle("标题党！") ;
		
		Integer pk = (Integer) session.insert(a) ;
		
		session.commit() ;
		
		assertEquals(pk.intValue(), 5) ;
		assertEquals(a.getId(), 5) ;
		
		Article b = (Article) session.findObjectByPK(Article.class, new Integer(5)) ;
		assertNotNull(b) ;
		assertEquals(a.getContent(), b.getContent()) ;
		assertEquals(a.getTitle(), b.getTitle()) ;
		assertEquals(a.getCreatedTime(), b.getCreatedTime()) ;
		assertEquals(b.getCreatedTime(), now) ;
				
		session.close() ;
	}
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		
		User user = (User) session.findObjectByPK(User.class, 100) ;
		assertNotNull(user) ;
		assertEquals(user.getUserName(), "name 100") ;
		
		Date now = new Date() ;
		user.setUserName("new name of me") ;
		user.setCreatedTime(now) ;
		
		session.update(user) ;
		session.commit() ;
		
		User user2 = (User) session.findObjectByPK(User.class, 100) ;
		
		assertEquals(user2.getId(), 100) ;
		assertEquals(user2.getUserName(), "new name of me") ;
		assertEquals(user2.getCreatedTime(), now) ;
				
		session.close() ;
	}
	
	public void testDelete() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		
		User user = (User) session.findObjectByPK(User.class, 10) ;
		assertNotNull(user) ;
		
		session.delete(user) ;
		session.commit() ;
		
		User user2 = (User) session.findObjectByPK(User.class, 10) ;
		assertNull(user2) ;
				
		session.close() ;
		
		ReadonlyTranSession rts = tm.openDelayReadTran() ;
		
		SearchExpression se = SearchExpression.forClass(User.class) ;
		se.and(Terms.smaller("id", 15)) ;
		List users = rts.list(se) ;
		assertEquals(users.size(), 13) ;
	}

}
