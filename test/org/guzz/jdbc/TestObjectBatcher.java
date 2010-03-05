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
package org.guzz.jdbc;

import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
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
public class TestObjectBatcher extends DBBasedTestCase {

	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate(getDefaultConn(), "insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + ((i%2==0)?1:0) + ", " + i + ", " + getDateFunction() + ")") ;		
		}		
	}
	
	protected int countUser(TransactionManager tm){
		ReadonlyTranSession session = tm.openNoDelayReadonlyTran() ;
		SearchExpression se = SearchExpression.forClass(User.class) ;
		
		int count = (int) session.count(se) ;
		
		session.close() ;
		
		return count ;
	}	
	
	public void testInsert() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		ObjectBatcher batcher = session.createObjectBatcher(User.class) ;
		
		int newCount = 98 ;
		int count = countUser(tm) ;
		
		for(int loop = 0 ; loop < 10 ; loop++){
			for(int i = 0 ; i < newCount; i++){
				User user = new User() ;
				user.setUserName("hello un " + i) ;
				
				batcher.insert(user) ;
			}
			
			batcher.executeUpdate() ;
		}

		session.commit() ;

		session.close() ;
		
		int count2 = countUser(tm) ;
		assertEquals(count2, newCount * 10 + count) ;

		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		ReadonlyTranSession read = tm.openNoDelayReadonlyTran() ;
		
		int countBefore = countUser(tm) ;
		
		SearchExpression se = SearchExpression.forClass(User.class, 1, 345) ;
		List users = read.list(se) ;
		
		ObjectBatcher batcher = session.createObjectBatcher(User.class) ;
		for(int i = 0 ; i < users.size() ; i++){
			User u = (User) users.get(i) ;
			u.setFavCount(3849021) ;
			batcher.update(u) ;
		}
		
		batcher.executeUpdate() ;
		session.commit() ;
		session.close() ;
		
		se = SearchExpression.forLoadAll(User.class) ;
		se.and(Terms.eq("favCount", 3849021)) ;
		int newUserSize = (int) read.count(se) ;
		read.close() ;
		
		assertEquals(users.size(), newUserSize) ;
		
		int countAfter = countUser(tm) ;
		assertEquals(countBefore, countAfter) ;

		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testDelete() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		ObjectBatcher batcher = session.createObjectBatcher(User.class) ;
		
		int dropCount = 19 ;
		int count = countUser(tm) ;
		
		for(int loop = 0 ; loop < 10 ; loop++){
			int countBefore = countUser(tm) ;
			
			ReadonlyTranSession read = tm.openNoDelayReadonlyTran() ;
			SearchExpression se = SearchExpression.forClass(User.class, 1, 345) ;
			List users = read.list(se) ;
			read.close() ;
			
			for(int i = 0 ; i < dropCount; i++){
				User user = (User) users.get(i) ;
				
				batcher.delete(user) ;
			}
			
			batcher.executeUpdate() ;
			session.commit() ;
			
			int countAfter = countUser(tm) ;
			assertEquals(countAfter, countBefore - dropCount) ;
		}

		session.close() ;
		
		int count2 = countUser(tm) ;
		assertEquals(count2, count - 10 * dropCount) ;

		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testOnlyAllowOneOP() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession session = tm.openRWTran(false) ;
		ObjectBatcher batcher = session.createObjectBatcher(User.class) ;
		
		User user = new User() ;
		user.setUserName("hello un a") ;
		
		batcher.insert(user) ;
		
		try{
			batcher.update(user) ;
			fail("operation shouldn't be allowed.") ;
		}catch(Exception e){
		}
		
		try{
			batcher.delete(user) ;
			fail("operation shouldn't be allowed.") ;
		}catch(Exception e){
		}

		session.close() ;
		((GuzzContextImpl) gf).shutdown() ;
	}

}
