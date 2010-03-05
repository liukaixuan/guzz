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
package org.guzz.orm.mapping;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.LinkedList;

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestHbmXMLBuilder extends DBBasedTestCase {
	
	public void testParseHbmStream() throws Exception{
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getObjectMappingByName("article") ;
		
		assertNotNull(map) ;
		assertNotNull(map.getBusiness()) ;
		assertNotNull(map.getBusiness().getDomainClass()) ;
		assertEquals(map.getBusiness().getDomainClass().getName(), Article.class.getName()) ;
		assertEquals(map.col2PropsMapping.size(), 4) ;
		assertEquals(map.prop2ColsMapping.size(), 4) ;
		
		//测试可以正确的进行db->object映射
		ResultSet rs = executeQuery(getDefaultConn(), "select * from TB_ARTICLE order by id asc limit 10 " ) ;
		LinkedList obs = new LinkedList() ;
		while(rs.next()){
			obs.add(map.rs2Object(rs)) ;
		}
		
		CloseUtil.close(rs) ;
		
		assertEquals(obs.size(), 4) ;
		Article a = (Article) obs.get(1) ;
		assertEquals(2, a.getId()) ;
		assertEquals("title 2", a.getTitle()) ;
		assertEquals("content 2", a.getContent()) ;
		
		Calendar c = Calendar.getInstance() ;
		c.set(2009, 7, 26, 13, 45, 9) ;
		c.set(Calendar.MILLISECOND, 0) ;
		
		assertEquals(a.getCreatedTime().getTime(), c.getTime().getTime()) ;		

		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testAddUserHxml() throws Exception{
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		ga = f.instanceNewGhost("user", null, null, null) ;
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/User.hbm.xml") ;

		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testIdentifidGeneratorCreate() throws Exception{
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getObjectMappingByName("article") ;
		
		assertNotNull(map) ;
		assertNotNull(map.getTable()) ;
		assertNotNull(map.getTable().getIdentifierGenerator()) ;
		assertNotNull(map.getTable().getIdentifierGenerator()) ;

		((GuzzContextImpl) f).shutdown() ;
	}
	
}
