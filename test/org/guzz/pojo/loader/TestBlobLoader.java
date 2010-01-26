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

import java.io.File;
import java.io.FileInputStream;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.pojo.TranBlob;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.UserInfo;
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
public class TestBlobLoader extends DBBasedTestCase {

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
			UserInfo info = new UserInfo() ;
			info.setUserId("lucy") ;
			tran.insert(info) ;
			
			userId = info.getId() ;
			
			assertTrue(info.getId() > 0) ;
			
			TranBlob blob = (TranBlob) tran.loadLazyPropForUpdate(info, "portraitImg") ;
			assertTrue(blob != null) ;
			
			blob.writeIntoBlob(fis, 1) ;
			
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
			UserInfo info = (UserInfo) tran.findObjectByPK(UserInfo.class, userId) ;
			assertTrue(info != null) ;
			byte[] dataInDB = info.getPortraitImg().getContent() ;
			
			assertEquals(fileData.length, info.getPortraitImg().getContent().length) ;
			assertArrayEquals(fileData, dataInDB) ;
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

		UserInfo info = new UserInfo() ;
		
		try{
			info.setUserId("lucy") ;
			tran.insert(info) ;
			
			userId = info.getId() ;
			
			assertTrue(info.getId() > 0) ;
			
			TranBlob blob = (TranBlob) tran.loadLazyPropForUpdate(info, "portraitImg") ;
			assertTrue(blob != null) ;
			
			blob.writeIntoBlob(fis, 1) ;
			
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
			byte[] dataInDB = info.getPortraitImg().getContent() ;
			
			assertEquals(fileData.length, info.getPortraitImg().getContent().length) ;
			assertArrayEquals(fileData, dataInDB) ;
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
			TranBlob blob = (TranBlob) tran.loadLazyPropForUpdate(info, "portraitImg") ;
			assertTrue(blob != null) ;
			blob.truncate(0) ;
			blob.setBytes(1, fileData) ;
			
			tran.commit() ;
			
			info = (UserInfo) tran.refresh(info, LockMode.READ) ;
			assertArrayEquals(info.getPortraitImg().getContent(), fileData) ;
			info.getPortraitImg().close() ;
			
			//测试流写入
			blob = (TranBlob) tran.loadLazyPropForUpdate(info, "portraitImg") ;
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
