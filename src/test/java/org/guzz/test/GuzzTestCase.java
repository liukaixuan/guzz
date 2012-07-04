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
package org.guzz.test;

import junit.framework.TestCase;

import org.guzz.Configuration;
import org.guzz.GuzzContextImpl;
import org.guzz.builder.GuzzConfigFileBuilder;
import org.guzz.io.FileResource;
import org.guzz.transaction.TransactionManager;
import org.guzz.util.StringUtil;

/**
 * 
 * 基于guzz的测试用例。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class GuzzTestCase extends TestCase {
	protected GuzzContextImpl gf = null ;
	protected GuzzConfigFileBuilder b = null ;
	protected TransactionManager tm = null ;
	
	public static String configFile = null ;
	
	protected void buildGF() throws Exception {
		String configFile = "classpath:guzzmain_test1.xml" ;
//		String configFile = "classpath:guzzmain_test1_annotation.xml" ;
		
		FileResource fs = new FileResource(configFile) ;
		
		gf = (GuzzContextImpl) new Configuration(configFile).newGuzzContext() ;
		b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		tm = gf.getTransactionManager() ;
	}
			
	/**准备环境。如插入一些测试数据等。每次启动时数据库自动重新创建，不会保存历史数据。*/
	protected void prepareEnv() throws Exception{
		
	}
	
	/**恢复测试前环境，避免对其他测试用例干扰。*/
	protected void rollbackEnv() throws Exception{
	}

	protected void tearDown() throws Exception {
		rollbackEnv() ;
		
		gf.shutdown() ;
		
		super.tearDown();
	}
	
	public void assertEqualsIDWS(String left, String right){
		left = StringUtil.squeezeWhiteSpace(left) ;
		right = StringUtil.squeezeWhiteSpace(right) ;
		assertEquals(left, right) ;
	}
	
	protected void assertBytesEquals(byte[] a, byte[] b){
		assertEquals(a.length, b.length) ;
		
		for(int i = 0 ; i < a.length ; i++){
			assertEquals(a[i], b[i]) ;
		}
	}
	
}
