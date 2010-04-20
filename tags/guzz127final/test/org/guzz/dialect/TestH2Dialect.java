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
package org.guzz.dialect;

import java.util.Date;
import java.util.List;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestH2Dialect extends DBBasedTestCase {

	public void testUpdateNoWait() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
				
		WriteTranSession tran = tm.openRWTran(true) ;
		
		Article a = (Article) tran.findObjectByPK("article", new Integer(1)) ;
		assertNotNull(a) ;
		
		String content = (String) tran.loadPropForUpdate(a, "content") ;
		
		String newContent = "new Content of " + new Date() ;
		
		a.setContent(newContent) ;
		tran.update(a) ;
		
		a = (Article) tran.refresh(a, LockMode.NONE) ;
		assertEquals(a.getContent(), newContent) ;
		
		tran.close() ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testUpdateNoWaitWithLimit() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
		
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL("article", "select * from @@article where id > :id") ;
		cs.addParamPropMapping("id", "id") ;
		
		ReadonlyTranSession read = tm.openNoDelayReadonlyTran() ;
		
		//验证生成的sql可以执行
		List comments = read.list(cs.bind("id", "1").setLockMode(LockMode.UPGRADE_NOWAIT), 3, 10) ;
		
		comments = read.list(cs.bind("id", "1").setLockMode(LockMode.READ), 3, 10) ;
		
		comments = read.list(cs.bind("id", "1").setLockMode(LockMode.UPGRADE), 3, 10) ;		

		comments = read.list(cs.bind("id", "1").setLockMode(LockMode.NONE), 3, 10) ;
		
		read.close() ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
}
