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

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.guzz.Configuration;
import org.guzz.GuzzContextImpl;
import org.guzz.config.ConfigServer;
import org.guzz.dialect.Dialect;
import org.guzz.dialect.H2Dialect;
import org.guzz.dialect.Mysql5Dialect;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ResultMapBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.transaction.DBGroup;
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
		assertEquals(elements.size(), 3) ;
		assertEquals(((Element) elements.get(0)).attributeValue("name"), "article") ;
		assertEquals(((Element) elements.get(1)).attributeValue("name"), "user") ;		
		
	}
	
	public void testLoadDialect() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		
		Map ds = b.getConfiguredDialect() ;
		assertNotNull(ds) ;
		assertEquals(ds.size(), 2) ;
		
		Dialect d = (Dialect) ds.get("default") ;		
		assertEquals(d.getClass().getName(), H2Dialect.class.getName()) ;
	}
	
	public void testLoadObjectMapping() throws Exception{		
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		Document md = b.rootDoc ;
		assertNotNull(md) ;
		
		List es = md.selectNodes("//sqlMap/orm") ;
		
		assertEquals(es.size(), 1) ;
		
		ResultMapBasedObjectMapping map = b.loadORM(null, (Element) es.get(0)) ;
		assertNotNull(map) ;
	    
		assertEquals(map.getColNameByPropName("id"), "pk") ;
		assertEquals(map.getColNameByPropName("name"), "userName") ;
		assertEquals(map.getColNameByPropName("favCount"), "FAV_COUNT") ;
		assertEquals(map.getColNameByPropName("vip"), "VIP_USER") ;		
		
	}
	
	public void testLoadSelectSQL() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;	
		
		Business ga = gf.instanceNewGhost("article", null, null, null) ;
		
		gf.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		ga = gf.instanceNewGhost("user", null, null, null) ;
		gf.addHbmConfigFile(ga , FileResource.CLASS_PATH_PREFIX + "org/guzz/test/User.hbm.xml") ;
		
		Document md = b.rootDoc ;
		assertNotNull(md) ;
		
		List maps = b.listGlobalORMs() ;
		for(int i = 0 ; i < maps.size() ; i++){
			gf.getObjectMappingManager().registerObjectMapping((ObjectMapping) maps.get(i)) ;
		}		
		
		Map sqls = b.listConfiguedCompiledSQLs() ;
		assertEquals(sqls.size(), 3) ;
		
		CompiledSQL cs = (CompiledSQL) sqls.get("selectUsers") ;
		assertNotNull(cs) ;
		assertEquals(cs.getSql(), "select pk, userName, VIP_USER, FAV_COUNT from TB_USER") ;
		
		cs = (CompiledSQL) sqls.get("selectUser") ;
		assertNotNull(cs) ;
		assertEquals(cs.getSql(), "select * from TB_USER where pk = ?") ;
		assertEquals(cs.getOrderedParams().length, 1) ;
		assertEquals(cs.getOrderedParams()[0], "id") ;
		
	}
	
	public void testLoadGhosts() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List objects = b.listBusinessObjectMappings() ;
		
		assertEquals(objects.size(), 3) ;		
	}
	
	public void testLoadConfigServer() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		ConfigServer server = b.loadConfigServer() ;
		
		assertNotNull(server) ;
		assertNotNull(server.queryConfig("masterDB")) ;
		assertNotNull(server.queryConfig("guzzDebug")) ;
		assertNull(server.queryConfig("slaveDB")) ;
		
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
	}
	
	public void testLoadServices() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl)new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List ss = b.loadServices() ;
		
		assertNotNull(ss) ;
		assertEquals(ss.size(),2) ;
		
		//test load order
		ServiceInfo s = (ServiceInfo) ss.get(0) ;
		assertEquals(s.getServiceName(), "onlyForTest") ;
		assertEquals(s.getConfigName(), "onlyForTestConfig") ;
		
		s = (ServiceInfo) ss.get(1) ;
		assertEquals(s.getServiceName(), "onlyForTest2") ;
		assertEquals(s.getConfigName(), "onlyForTest2Config") ;
		
		CloseUtil.close(fs) ;
	}
	
	public void testLoadTrans() throws Exception{
		FileResource fs = new FileResource("classpath:guzzmain_test1.xml") ;
		GuzzContextImpl gf = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		GuzzConfigFileBuilder b = GuzzConfigFileBuilder.build(gf, fs, "UTF-8") ;
		
		List gs = b.listDBGroups() ;
		
		assertNotNull(gs) ;
		assertEquals(gs.size(),2) ;
		
		//test load order
		DBGroup g1 = (DBGroup) gs.get(0) ;
		assertEquals(g1.getGroupName(), "default") ;
		assertNotNull(g1.getMasterDB()) ;
		assertNull(g1.getSlaveDB()) ;
		assertEquals(g1.getDialect().getClass(), H2Dialect.class) ;
		
		DBGroup g2 = (DBGroup) gs.get(1) ;
		assertEquals(g2.getGroupName(), "log") ;
		assertEquals(g2.getDialect().getClass(), Mysql5Dialect.class) ;
		
		CloseUtil.close(fs) ;
	}
	
	public void testLoadPropertyFile() throws Exception{
		fail("implement this") ;	
	}

}
