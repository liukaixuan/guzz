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

import org.guzz.orm.rdms.TableColumn;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * test blob loader.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestNullValue extends DBBasedTestCase {

	static{
//		org.guzz.test.GuzzTestCase.configFile = "classpath:guzzmain_test1.xml" ;
	}
	protected void prepareEnv() throws Exception{		
	}
	
	public void testNullValueAffects() throws Exception{
		WriteTranSession session = tm.openRWTran(true) ;
		
		User u = new User() ;
		u.setUserName("xxxxxx") ;
		u.setPassword("xxxxxx") ;
		
		session.insert(u) ;
		
		u = (User) session.refresh(u, LockMode.READ) ;
		session.close() ;
		
		assertEquals(u.getFavCount().intValue(), 999) ;
	}
	
	public void testChangeNullValue() throws Exception{
		//set new nullValue
		this.gf.getBusiness(User.class.getName()).getTable().getColumnByPropName("favCount")
		.setNullValue("12345") ;
		
		WriteTranSession session = tm.openRWTran(true) ;	
		
		User u = new User() ;
		u.setUserName("xxxxxx") ;
		u.setPassword("xxxxxx") ;
		
		session.insert(u) ;
		
		u = (User) session.refresh(u, LockMode.READ) ;		
		session.close() ;
		
		assertEquals(u.getFavCount().intValue(), 12345) ;
	}
	
	public void testValueCanBeSet() throws Exception{
		//set new nullValue
		this.gf.getBusiness(User.class.getName()).getTable().getColumnByPropName("favCount")
		.setNullValue("null") ;
		
		WriteTranSession session = tm.openRWTran(true) ;	
		
		User u = new User() ;
		u.setUserName("xxxxxx") ;
		u.setPassword("xxxxxx") ;
		u.setFavCount(new Integer(21)) ;
		
		session.insert(u) ;
		
		u = (User) session.refresh(u, LockMode.READ) ;
		session.close() ;
		
		assertEquals(u.getFavCount().intValue(), 21) ;
	}
	
	public void testValueCanBeNullInDB() throws Exception{
		WriteTranSession session = tm.openRWTran(true) ;
		
		//set new nullValue
		TableColumn col = this.gf.getBusiness(User.class.getName()).getTable().getColumnByPropName("favCount") ;
		col.setNullValue("null") ;
			
		
		User u = new User() ;
		u.setUserName("xxxxxx") ;
		u.setPassword("xxxxxx") ;
		
		session.insert(u) ;
		
		u = (User) session.refresh(u, LockMode.READ) ;		
		session.close() ;
		
		assertEquals(u.getFavCount(), null) ;
	}
	
}
