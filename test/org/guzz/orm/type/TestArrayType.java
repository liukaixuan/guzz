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

import java.util.List;

import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;
import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * test blob loader.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestArrayType extends DBBasedTestCase {

	static{
		org.guzz.test.GuzzTestCase.configFile = "classpath:guzzmain_test1.xml" ;
	}
	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate(getDefaultConn(), "insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + ((i%2==0)?1:0) + ", " + i + ", " + getDateFunction() + ")") ;		
		}		
	}
	
	public void testUpdate() throws Exception{
		String sql = "select * from TB_USER where @id in :ids" ;	
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(User.class, sql) ;
		
		ReadonlyTranSession read = tm.openDelayReadTran() ;
		int[] ids = new int[]{1,2,3,4,5} ;
		
		try{
			BindedCompiledSQL bsql = cs.bind("id", ids) ;
			
			List<User> users = read.list(bsql) ;
			
			assertEquals(users.size(), 5) ;
			
		}finally{
			read.close() ;
		}
	}	

}
