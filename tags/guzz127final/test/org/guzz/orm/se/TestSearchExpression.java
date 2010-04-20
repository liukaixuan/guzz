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

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.se.InTerm;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.SearchParams;
import org.guzz.orm.se.Terms;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestSearchExpression extends DBBasedTestCase{
	
	public void testLoadClass() throws Exception{
		SearchExpression se = SearchExpression.forClass(Article.class) ;
		assertNotNull(se) ;
		
		se.and(Terms.eq("title", "48")) ;
		
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
//		Business ga = f.instanceNewGhost("article", null, null) ;
//		
//		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getStaticObjectMapping("article") ;
		SearchParams sp = new SearchParams() ;		
		
		assertEquals(se.toComputeRecordNumberSQL(map, sp).getOrginalSQL(), "select count(*) from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(sp.getSearchParams().size(), 1) ;
		assertEquals(se.toLoadRecordsMarkedSQL(map, new SearchParams()).getOrginalSQL(), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(se.toDeleteRecordString(map, new SearchParams()).getOrginalSQL(), "delete from TB_ARTICLE where NAME = :title_0") ;
		
		se = SearchExpression.forClass(Article.class) ;
		se.and(Terms.eq("title", "48")) ;
		se.setSelectedProps("title, createdTime") ;
		se.setOrderBy("title desc, id asc") ;
		
		assertEquals(se.toComputeRecordNumberSQL(map, new SearchParams()).getOrginalSQL(), "select count(*) from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(se.toDeleteRecordString(map, new SearchParams()).getOrginalSQL(), "delete from TB_ARTICLE where NAME = :title_0") ;
		
		sp = new SearchParams() ;
		se.and(Terms.biggerOrEq("title", 10)) ;
		assertEqualsIDWS(se.toLoadRecordsMarkedSQL(map, sp).getOrginalSQL(), "select NAME , createdTime from TB_ARTICLE where  (NAME = :title_0) and (NAME >= :title_1) order by NAME desc, id asc") ;
		assertEquals(sp.getSearchParams().size(), 2) ;
		
		
		se = SearchExpression.forClass(Article.class) ;
		se.and(new InTerm("title", new int[]{1, 45, 79, 955, 46})) ;
		
		assertEqualsIDWS(se.toLoadRecordsMarkedSQL(map, new SearchParams()).getOrginalSQL(), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE where NAME in( :title_0, :title_1, :title_2, :title_3, :title_4)") ;
		
		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testLoadGhost() throws Exception{
		SearchExpression se = SearchExpression.forBusiness("article") ;
		assertNotNull(se) ;
		
		se.and(Terms.eq("title", "48")) ;
		
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getStaticObjectMapping("article") ;
		SearchParams sp = new SearchParams() ;		
		
		assertEquals(se.toComputeRecordNumberSQL(map, sp).getOrginalSQL(), "select count(*) from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(sp.getSearchParams().size(), 1) ;
		assertEquals(se.toLoadRecordsMarkedSQL(map, new SearchParams()).getOrginalSQL(), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(se.toDeleteRecordString(map, new SearchParams()).getOrginalSQL(), "delete from TB_ARTICLE where NAME = :title_0") ;
		
		se = SearchExpression.forBusiness("article") ;
		se.and(Terms.eq("title", "48")) ;
		se.setSelectedProps("title, createdTime") ;
		se.setOrderBy("title desc, id asc") ;
		
		assertEquals(se.toComputeRecordNumberSQL(map, new SearchParams()).getOrginalSQL(), "select count(*) from TB_ARTICLE where NAME = :title_0") ;
		assertEquals(se.toDeleteRecordString(map, new SearchParams()).getOrginalSQL(), "delete from TB_ARTICLE where NAME = :title_0") ;
		
		sp = new SearchParams() ;
		se.and(Terms.biggerOrEq("title", 10)) ;
		assertEqualsIDWS(se.toLoadRecordsMarkedSQL(map, sp).getOrginalSQL(), "select NAME , createdTime from TB_ARTICLE where  (NAME = :title_0) and (NAME >= :title_1) order by NAME desc, id asc") ;
		assertEquals(sp.getSearchParams().size(), 2) ;

		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testCountGhost() throws Exception{
		SearchExpression se = SearchExpression.forBusiness("article") ;
		assertNotNull(se) ;
		
		se.and(Terms.eq("title", "48")) ;
		
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getStaticObjectMapping("article") ;
		SearchParams sp = new SearchParams() ;		
		
		se.setCountSelectPhrase("max(title)") ;
		assertEquals(se.toComputeRecordNumberSQL(map, sp).getOrginalSQL(), "select max(NAME) from TB_ARTICLE where NAME = :title_0") ;

		((GuzzContextImpl) f).shutdown() ;		
	}
	
}
