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
package org.guzz.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.guzz.config.LocalFileConfigServer;
import org.guzz.io.FileResource;
import org.guzz.service.ServiceConfig;
import org.guzz.util.PropertyUtil;

import junit.framework.TestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestLocalFileConfigServer extends TestCase {
	
	public void testReliedPropertyUtil(){
		FileResource r = new FileResource("classpath:guzz_test1.properties") ;
		
		Map map = PropertyUtil.loadGroupedProps(r) ;
		assertNotNull(map) ;
		assertEquals(map.keySet().size(), 4) ;
		assertNotNull(map.get("masterDB")) ;
		assertNotNull(map.get("slaveDB")) ;
		assertNotNull(map.get("oracleDB")) ;
		assertNotNull(map.get("guzzDebug")) ;
		assertNull(map.get("mailService")) ;
		
		Properties[] ps = (Properties[]) map.get("slaveDB") ;
		assertEquals(ps.length, 2) ;
		
		//check the loading orders
		assertEquals(ps[0].getProperty("guzz.identifer"), "db_45_36_3306") ;
		assertEquals(ps[1].getProperty("guzz.identifer"), "db_45_37_3306") ;
	}
	
	public void testSCSLoad() throws IOException{
		FileResource r = new FileResource("classpath:guzz_test1.properties") ;
		
		LocalFileConfigServer server = new LocalFileConfigServer() ;
		server.setResource(r) ;
		
		ServiceConfig[] scs = server.queryConfig("slaveDB") ;
		assertEquals(scs.length, 2) ;
		
		scs = server.queryConfig("mail") ;
		assertNotNull(scs) ;
		assertEquals(scs.length, 0) ;
	}

}
