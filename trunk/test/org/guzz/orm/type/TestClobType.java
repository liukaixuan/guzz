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
package org.guzz.orm.type;

import org.guzz.Configuration;
import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.dialect.H2Dialect;
import org.guzz.pojo.lob.TranClob;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.UserInfoH2;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.transaction.WriteTranSessionImpl;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestClobType extends DBBasedTestCase {

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
		
		try{
			UserInfoH2 info = new UserInfoH2() ;
			info.setUserId("lily") ;
			info.setAboutMe(Guzz.createClob(bigString)) ;
			
			tran.insert(info) ;
			tran.commit() ;
		
			assertTrue(info.getId() > 0) ;
			
			info = (UserInfoH2) tran.refresh(info, LockMode.NONE) ;
			TranClob clob = info.getAboutMe() ;
			
			assertTrue(clob != null) ;
			assertEquals(clob.length(), bigString.length()) ;
			assertEquals(clob.getContent(), bigString) ;
			
			clob.close() ;
			
		}catch(Exception e){
			tran.rollback() ;
			tran.close() ;
			
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}
	}	
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		//H2数据库不支lob字段update。如果使用H2进行测试，此用例无法通过为正常。
		if( gf.getDBGroup("default").getDialect() instanceof H2Dialect){
			return ;
		}
		
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession tran = tm.openRWTran(false) ;
		
		StringBuffer sb = new StringBuffer() ;
		for(int i = 0 ; i < 1000000 ; i++){ //100万字符
			sb.append('a') ;
		}
		
		String bigString = sb.toString() ;
		sb.setLength(0) ;
	
		UserInfoH2 info = new UserInfoH2() ;
		
		try{
			info.setUserId("lily") ;
			info.setAboutMe(Guzz.createClob(bigString)) ;
			
			tran.insert(info) ;
			tran.commit() ;
		
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
			
			info = (UserInfoH2) tran.refresh(info, LockMode.READ) ;
			
			assertEquals(info.getAboutMe().getContent(), "hello world!") ;
			
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
		}
		
	}	

}
