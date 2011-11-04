/*
 * Copyright 2008-2011 the original author or authors.
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
package org.guzz.dao;

import java.sql.SQLException;

import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * Base class for testing the interaction between Spring and Guzz.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SoloDaoTestCase extends DBBasedTestCase{
	
	ClassPathXmlApplicationContext ctx ; 
	
	IUserDao userDao ;
	
	protected void prepareEnv() throws Exception {
		super.prepareEnv();
		
		ctx = new ClassPathXmlApplicationContext("applicationContext_solo.xml");
		
		userDao = (IUserDao) ctx.getBean("userDao") ;
	}
	
	public void testTranRolledBackOnException(){
		this.userDao.clearTable() ;
		
		User u = new User() ;
		u.setUserName("zzzzzzzzzzzzz") ;
		try{
			userDao.insertFailed(u) ;
		}catch(Exception e){
			
		}
		
		assertEquals(userDao.countAll(), 1) ;
		
		u = userDao.getById(u.getId()) ;
		assertNotNull(u) ;
	}
	
	public void testNoConnLeak(){
		this.userDao.clearTable() ;
		
		assertEquals(userDao.countAll(), 0) ;
		
		for(int i = 0 ; i < 10000 ; i++){
			User u = new User() ;
			u.setUserName("hahaha" + i) ;
			userDao.insert(u) ;

			assertTrue(u.getId() > 0) ;
			
			User uuu = userDao.getById(u.getId()) ;
			assertNotNull(uuu) ;
			assertEquals(uuu.getUserName(), u.getUserName()) ;
			
			userDao.delete(u) ;
		}

		assertEquals(userDao.countAll(), 0) ;
		
		for(int i = 0 ; i < 10000 ; i++){
			User u = new User() ;
			u.setUserName("hahaha" + i) ;
			
			try{
				userDao.insertFailed(u) ;
			}catch(Exception e){}
		}

		assertEquals(userDao.countAll(), 10000) ;

		for(int i = 0 ; i < 10000 ; i++){
			userDao.getById(i) ;
		}
	}
	
	public void testTranOK(){
		this.userDao.clearTable() ;
		
		for(int i = 0 ; i < 10000 ; i++){
			User u = new User() ;
			u.setUserName("commit" + i) ;
			userDao.insert(u) ;
		}
		
		assertEquals(userDao.countAll(), 10000) ;
	}	
	
	public void testTimeout() throws SQLException{
		this.userDao.clearTable() ;
		
		User u = new User() ;
		u.setUserName("testTimeout") ;
		
		try{
			userDao.insertTimeout(u) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		//timeout won't take effects
		assertEquals(userDao.countAll(), 1) ;
	}
	
	public void testFindOK() throws SQLException{
		this.userDao.clearTable() ;
		
		User u = new User() ;
		u.setUserName("testFindOK") ;
		userDao.insert(u) ;
		
		User u2 = userDao.findByUserName("testFindOK") ;
		assertNotNull(u2) ;
		assertEquals(u.getId(), u2.getId()) ;
	}
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		ctx.destroy() ;
	}
	
}

