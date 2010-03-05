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

import java.io.File;
import java.io.FileInputStream;

import org.guzz.Configuration;
import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.dialect.H2Dialect;
import org.guzz.pojo.lob.TranBlob;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.UserInfo;
import org.guzz.test.UserInfoH2;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.FileUtil;

/**
 * 
 * test blob loader.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestBlobType extends DBBasedTestCase {

	protected void prepareEnv() throws Exception{
	}
	
	protected void assertArrayEquals(byte[] a, byte[] b){
		assertEquals(a.length, b.length) ;
		
		for(int i = 0 ; i < a.length ; i++){
			assertEquals(a[i], b[i]) ;
		}
	}
	
	public void testInsert() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
		
		String classPath = this.getClass().getClassLoader().getResource(".").getFile() ;
		File lib = new File(classPath, "../lib/xercesImpl.jar") ;
		assertTrue(lib.exists()) ;
		
		FileInputStream fis = new FileInputStream(lib) ;
		
		WriteTranSession tran = tm.openRWTran(false) ;		
		int userId = 0 ;
		
		try{
			UserInfoH2 info = new UserInfoH2() ;
			info.setUserId("lucy") ;
			info.setPortraitImg(Guzz.createBlob(fis)) ;
			tran.insert(info) ;
			
			userId = info.getId() ;
			assertTrue(info.getId() > 0) ;
			tran.commit() ;	
			
		}catch(Exception e){
			tran.rollback() ;
			tran.close() ;
			
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}
		
		fis.close() ;
		fis = new FileInputStream(lib) ;
		
		byte[] fileData = FileUtil.readBytes(fis) ;
		
		//test lazy load
		try{
			UserInfoH2 info = (UserInfoH2) tran.findObjectByPK(UserInfoH2.class, userId) ;
			assertTrue(info != null) ;
			byte[] dataInDB = info.getPortraitImg().getContent() ;
			
			assertEquals(fileData.length, info.getPortraitImg().getContent().length) ;
			assertArrayEquals(fileData, dataInDB) ;
			
			//blob不是lazy的，我们测试blob能够正常读取到。
			info = (UserInfoH2) tran.findObjectByPK(UserInfoH2.class, userId) ;
			assertTrue(info.getPortraitImg() != null) ;
			
			
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
			fis.close() ;
		}
		
	}
	
	public void testUpdate() throws Exception{
		GuzzContext gf = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		TransactionManager tm = gf.getTransactionManager() ;
		
		String classPath = this.getClass().getClassLoader().getResource(".").getFile() ;
		File lib = new File(classPath, "../lib/xercesImpl.jar") ;
		assertTrue(lib.exists()) ;
		
		FileInputStream fis = new FileInputStream(lib) ;
				
		WriteTranSession tran = tm.openRWTran(false) ;		
		int userId = 0 ;

		//UserInfo located in oracle
		UserInfo info = new UserInfo() ;
		
		try{
			info.setUserId("lucy") ;
			info.setPortraitImg(Guzz.createBlob(fis)) ;
			
			tran.insert(info) ;
			
			userId = info.getId() ;
			assertTrue(info.getId() > 0) ;
			tran.commit() ;		
		}catch(Exception e){
			tran.rollback() ;
			tran.close() ;
			
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}
		
		fis.close() ;
		fis = new FileInputStream(lib) ;
		
		byte[] fileData = FileUtil.readBytes(fis) ;
		
		//test lazy load
		try{
			info = (UserInfo) tran.findObjectByPK(UserInfo.class, userId) ;
			assertTrue(info != null) ;
			
			//oracle必须先插入空的，在更新。
			assertEquals(1, info.getPortraitImg().getContent().length) ;
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
			fis.close() ;
		}
		
		//读取要进行更新的数据
		File lib2Update = new File(classPath, "../lib/xalan.jar") ;
		fis = new FileInputStream(lib2Update) ;
		fileData = FileUtil.readBytes(fis) ;
		fis.close() ;
		
		fis = new FileInputStream(lib2Update) ;
		
		tran = tm.openRWTran(false) ;
		//test lazy load
		try{
			
			//测试byte[] update
			TranBlob blob = (TranBlob) tran.loadPropForUpdate(info, "portraitImg") ;
			assertTrue(blob != null) ;
			blob.truncate(0) ;
			blob.setBytes(1, fileData) ;
			
			tran.commit() ;
			
			info = (UserInfo) tran.refresh(info, LockMode.READ) ;
			assertArrayEquals(info.getPortraitImg().getContent(), fileData) ;
			info.getPortraitImg().close() ;
			
			//测试流写入
			blob = (TranBlob) tran.loadPropForUpdate(info, "portraitImg") ;
			assertTrue(blob != null) ;
			blob.truncate(0) ;
			blob.writeIntoBlob(fis, 1) ;
			
			tran.commit() ;
			info = (UserInfo) tran.refresh(info, LockMode.READ) ;
			assertArrayEquals(info.getPortraitImg().getContent(), fileData) ;
			info.getPortraitImg().close() ;
			
		}catch(Exception e){
			e.printStackTrace() ;
			fail(e.getMessage()) ;
		}finally{
			tran.close() ;
		}
		
	}	

}
