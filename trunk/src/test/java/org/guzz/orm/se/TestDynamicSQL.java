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
import java.util.Map;

import org.guzz.test.DBBasedTestCase;
import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestDynamicSQL extends DBBasedTestCase {
	
	public void testDynamicSQLService() throws Exception{
		assertNotNull(gf.getDialect("default")) ;
						
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		HashMap params = new HashMap() ;
		params.put("isVip", true) ;
		
		List result = session.list("9a3f8376-beb9-437b-8928-290eb0ba4420", params) ;
		assertEquals(result.size(), 1) ;
		
		Map group = (Map) result.get(0) ;

		assertEquals(group.size(), 2) ;
				
		assertEquals(group.get("M_COUNT"), 499L) ;
		assertEquals(group.get("vip"), true) ;
		
		session.close() ;
	}

	protected void prepareEnv() throws Exception{
		for(int i = 1 ; i < 1000 ; i++){
			executeUpdate(getDefaultConn(), "insert into TB_USER values(" + i + ", 'name " + i + "', 'psw " + i + "', " + ((i%2==0)?1:0) + ", " + i + ", " + getDateFunction() + ")") ;		
		}
		
	}

}
