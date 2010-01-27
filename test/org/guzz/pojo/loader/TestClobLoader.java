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
package org.guzz.pojo.loader;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.pojo.lob.TranClob;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.UserInfo;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestClobLoader extends DBBasedTestCase {

	protected void prepareEnv() throws Exception{
	}
	
	public void testInsert() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession tran = tm.openRWTran(false) ;
		
		StringBuffer sb = new StringBuffer() ;
		for(int i = 0 ; i < 1000000 ; i++){ //100万字符
			sb.append('a') ;
		}
		
		String bigString = sb.toString() ;
		sb.setLength(0) ;
		
		int userId = 0 ;
		
		try{
			UserInfo info = new UserInfo() ;
			info.setUserId("lily") ;
			tran.insert(info) ;
			
			userId = info.getId() ;
			
			assertTrue(info.getId() > 0) ;
			
			TranClob clob = (TranClob) tran.loadPropForUpdate(info, "aboutMe") ;
			
			assertTrue(clob != null) ;
			
			int count = clob.setString(1, bigString) ;
			
			assertEquals(bigString.length(), count) ;
			
			tran.commit() ;			
		}catch(Exception e){
			tran.rollback() ;
			tran.close() ;
			
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}
		
		//test lazy load
		try{
			UserInfo info = (UserInfo) tran.findObjectByPK(UserInfo.class, userId) ;
			assertTrue(info != null) ;
			
			assertEquals(bigString.length(), info.getAboutMe().length()) ;
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
		}
		
	}
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession tran = tm.openRWTran(false) ;
		
		StringBuffer sb = new StringBuffer() ;
		for(int i = 0 ; i < 1000000 ; i++){ //100万字符
			sb.append('a') ;
		}
		
		String bigString = sb.toString() ;
		sb.setLength(0) ;
		
		int userId = 0 ;
		
		UserInfo info = new UserInfo() ;
		
		try{
			info.setUserId("lily") ;
			tran.insert(info) ;
			
			userId = info.getId() ;
			
			assertTrue(info.getId() > 0) ;
			
			TranClob clob = (TranClob) tran.loadPropForUpdate(info, "aboutMe") ;
			
			assertTrue(clob != null) ;
			
			int count = clob.setString(1, bigString) ;
			
			assertEquals(bigString.length(), count) ;
			
			tran.commit() ;
			
			info = (UserInfo) tran.refresh(info, LockMode.READ) ;
			
			assertEquals(info.getAboutMe().getContent(), bigString) ;
			
			
		}catch(Exception e){
			tran.rollback() ;
			
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
		}
		
		tran = tm.openRWTran(false) ;
		//test lazy load
		try{
			TranClob clob = (TranClob) tran.loadPropForUpdate(info, "aboutMe") ;
			clob.truncate(0) ;
			
			clob.setString(1, "hello world!") ;
			
			tran.commit() ;
			
			info = (UserInfo) tran.refresh(info, LockMode.READ) ;
			
			assertEquals(info.getAboutMe().getContent(), "hello world!") ;
			
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
		}
		
	}	

}
