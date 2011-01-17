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
package org.guzz.builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.guzz.Configuration;
import org.guzz.GuzzContextImpl;
import org.guzz.config.ConfigServer;
import org.guzz.connection.DBGroup;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.dialect.Dialect;
import org.guzz.dialect.H2Dialect;
import org.guzz.dialect.Mysql5Dialect;
import org.guzz.io.FileResource;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ResultMapBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.test.sample.SampleTestService;
import org.guzz.test.sample.SampleTestService2;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestMainConfigBuilder extends TestCase {
	
	public void testLoadFullConfigFile() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(null, fs, "UTF-8") ;
		
		Document md = b.rootDoc ;
		assertNotNull(md) ;
//		System.out.println(md.asXML()) ;
		List elements = md.selectNodes("//business") ;
		assertEquals(elements.size(), 8) ;
		assertEquals(((Element) elements.get(0)).attributeValue("name"), "article") ;
		assertEquals(((Element) elements.get(1)).attributeValue("name"), "user") ;
	}
	
	public void testLoadDialect() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		Map ds = b.getConfiguredDialect() ;
		assertNotNull(ds) ;
		assertEquals(ds.size(), 3) ;
		
		Dialect d = (Dialect) ds.get("default") ;		
		assertEquals(d.getClass().getName(), H2Dialect.class.getName()) ;
		
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadObjectMapping() throws Exception{		
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		Document md = b.rootDoc ;
		assertNotNull(md) ;
		
		List es = md.selectNodes("//sqlMap/orm") ;
		
		assertEquals(es.size(), 2) ;
		
		ResultMapBasedObjectMapping map = b.loadORM(null, (Element) es.get(0)) ;
		assertNotNull(map) ;
	    
		assertEquals(map.getColNameByPropNameForSQL("id"), "pk") ;
		assertEquals(map.getColNameByPropNameForSQL("name"), "userName") ;
		assertEquals(map.getColNameByPropNameForSQL("favCount"), "FAV_COUNT") ;
		assertEquals(map.getColNameByPropNameForSQL("vip"), "VIP_USER") ;		
		
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadSelectSQL() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		Document md = b.rootDoc ;
		assertNotNull(md) ;
		
		List maps = b.listGlobalORMs() ;
		for(int i = 0 ; i < maps.size() ; i++){
			gf.getObjectMappingManager().registerObjectMapping((ObjectMapping) maps.get(i)) ;
		}		
		
		Map sqls = b.listConfiguedCompiledSQLs() ;
		assertEquals(sqls.size(), 5) ;
		
		CompiledSQL cs = (CompiledSQL) sqls.get("selectUsers") ;
		assertNotNull(cs) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "select pk, userName, VIP_USER, FAV_COUNT from TB_USER") ;
		
		cs = (CompiledSQL) sqls.get("selectUser") ;
		assertNotNull(cs) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "select * from TB_USER where pk = ?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[0], "id") ;
		
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadGhosts() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List objects = b.listBusinessObjectMappings() ;
		
		assertEquals(objects.size(), 8) ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadConfigServer() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		ConfigServer server = b.loadConfigServer() ;
		
		assertNotNull(server) ;
		assertTrue(server.queryConfig("masterDB").length > 0) ;
		assertTrue(server.queryConfig("guzzDebug").length > 0) ;
		assertEquals(server.queryConfig("slaveDB").length, 0) ;
		
		ServiceConfig sc = server.queryConfig("masterDB")[0] ;
		assertEquals(sc.getUniqueIdentifer(),"testmaster") ;
		assertEquals(sc.getIP(),"localhost") ;
		assertEquals(sc.getConfigName(),"masterDB") ;
		assertEquals(sc.getMaxLoad(),80) ;
		assertEquals(sc.getProps().getProperty("driverClass"),"org.h2.Driver") ;
		assertEquals(sc.getProps().getProperty("jdbcUrl"),"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1") ;
		assertEquals(sc.getProps().getProperty("user"),"sa") ;
		assertEquals(sc.getProps().getProperty("password"),"") ;
		assertEquals(sc.getProps().getProperty("acquireIncrement"),"10") ;
		assertEquals(sc.getProps().getProperty("idleConnectionTestPeriod"),"60") ;
		assertEquals(sc.getProps().getProperty("something.else"),null) ;
		
		CloseUtil.close(fs) ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadServices() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List ss = Collections.list(Collections.enumeration(b.loadServices().values())) ;
		
		assertNotNull(ss) ;
		assertEquals(ss.size(),2) ;
		
		//test load order
		ServiceInfo s = (ServiceInfo) ss.get(0) ;
		assertEquals(s.getServiceName(), "onlyForTest") ;
		assertEquals(s.getConfigName(), "onlyForTestConfig") ;
		
		s = (ServiceInfo) ss.get(1) ;
		assertEquals(s.getServiceName(), "onlyForTest2") ;
		assertEquals(s.getConfigName(), "onlyForTest2Config") ;
		
		//test service dependencies.
		SampleTestService ts1 = (SampleTestService) gf.getService("onlyForTest") ;
		SampleTestService2 ts2 = (SampleTestService2) gf.getService("onlyForTest2") ;
		
		assertNotNull(ts1) ;
		assertNotNull(ts2) ;
		assertSame(ts1, ts2.sampleTestService1) ;
		
		CloseUtil.close(fs) ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadTrans() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List gs = b.listDBGroups() ;
		
		assertNotNull(gs) ;
		assertEquals(gs.size(),6) ;
		
		//test load order
		PhysicsDBGroup g1 = (PhysicsDBGroup) gs.get(0) ;
		assertEquals(g1.getGroupName(), "default") ;
		assertNotNull(g1.getMasterDB()) ;
		assertNull(g1.getSlaveDB()) ;
		assertEquals(g1.getDialect().getClass(), H2Dialect.class) ;
		
		DBGroup g2 = (DBGroup) gs.get(1) ;
		assertEquals(g2.getGroupName(), "mysql") ;
		assertEquals(g2.getDialect().getClass(), Mysql5Dialect.class) ;
		
		CloseUtil.close(fs) ;
		((GuzzContextImpl) gf).shutdown() ;
	}
	
	public void testLoadPropertyFile() throws Exception{
//		fail("implement this") ;	
	}

}
