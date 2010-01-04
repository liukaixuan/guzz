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

import java.util.HashMap;
import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.CompiledSQL;
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
public class TestSQLBatcher extends DBBasedTestCase {

	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate("insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + (i%2==0) + ", " + i + ", now())") ;		
		}		
	}
	
	protected int countUser(TransactionManager tm){
		ReadonlyTranSession session = tm.openNoDelayReadonlyTran() ;
		SearchExpression se = SearchExpression.forClass(User.class) ;
		
		int count = (int) session.count(se) ;
		
		session.close() ;
		
		return count ;
	}
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(User.class, "update @@" + User.class.getName() + " set @favCount = 3849021 where @id = :id") ;
		
		WriteTranSession session = tm.openRWTran(false) ;
		ReadonlyTranSession read = tm.openNoDelayReadonlyTran() ;
		
		int countBefore = countUser(tm) ;
		
		SearchExpression se = SearchExpression.forClass(User.class, 1, 345) ;
		List users = read.list(se) ;
		
		SQLBatcher batcher = session.createCompiledSQLBatcher(cs) ;
		
		for(int i = 0 ; i < users.size() ; i++){
			User user = (User) users.get(i) ;
			
			HashMap params = new HashMap() ;
			params.put("id", new Integer(user.getId())) ;
			
			batcher.addNewBatchParams(params) ;
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
	}
	
	public void testDelete() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(User.class, "delete from @@" + User.class.getName() + " where @id = :id") ;
		
		WriteTranSession session = tm.openRWTran(false) ;
		SQLBatcher batcher = session.createCompiledSQLBatcher(cs) ;
		
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
				
				batcher.addNewBatchParams("id", user.getId()) ;
			}
			
			batcher.executeUpdate() ;
			session.commit() ;
			
			int countAfter = countUser(tm) ;
			assertEquals(countAfter, countBefore - dropCount) ;
		}

		session.close() ;
		
		int count2 = countUser(tm) ;
		assertEquals(count2, count - 10 * dropCount) ;
	}

}
