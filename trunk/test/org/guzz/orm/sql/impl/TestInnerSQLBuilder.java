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
package org.guzz.orm.sql.impl;

import java.util.Arrays;

import junit.framework.TestCase;

import org.guzz.Configuration;
import org.guzz.GuzzContextImpl;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.impl.CompiledSQLManagerImpl;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestInnerSQLBuilder extends TestCase {

	public void testTranslateSQLMark() throws Exception{
		GuzzContextImpl f = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) f.getObjectMappingManager().getObjectMappingByName("user") ;
		CompiledSQLManagerImpl csm = new CompiledSQLManagerImpl(f.getCompiledSQLBuilder()) ;
		
		//test insert
		CompiledSQL cs = csm.buildInsertSQLWithPK(map) ;
		assertEquals(cs.getSql(), "insert into TB_USER(pk, userName, password, VIP_USER, FAV_COUNT, createdTime) values(?, ?, ?, ?, ?, ?)") ;
		assertEquals(cs.getOrderedParams().length, 6) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[id, userName, password, vip, favCount, createdTime]") ;
		
		cs = csm.buildInsertSQLWithoutPK(map) ;
		assertEquals(cs.getSql(), "insert into TB_USER(userName, password, VIP_USER, FAV_COUNT, createdTime) values(?, ?, ?, ?, ?)") ;
		assertEquals(cs.getOrderedParams().length, 5) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[userName, password, vip, favCount, createdTime]") ;
		
		//update
		cs = csm.buildUpdateSQL(map) ; 
		assertEquals(cs.getSql(), "update TB_USER set userName=?, password=?, VIP_USER=?, FAV_COUNT=?, createdTime=? where pk=?") ;
		assertEquals(cs.getOrderedParams().length, 6) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[userName, password, vip, favCount, createdTime, id]") ;
				
		//delete
		cs = csm.buildDeleteSQL(map) ;
		assertEquals(cs.getSql(), "delete from TB_USER where pk=?") ;
		assertEquals(cs.getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[id]") ;
		
		//select
		cs = csm.buildSelectSQL(map) ;
		assertEquals(cs.getSql(), "select pk, userName, password, VIP_USER, FAV_COUNT, createdTime from TB_USER where pk=?") ;
		assertEquals(cs.getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[id]") ;
		
	}
	
	public void testInsertUpdateIgnoreParam() throws Exception{
		GuzzContextImpl gf = (GuzzContextImpl) new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		
		Business ga = gf.instanceNewGhost("articleCount", null, null, null) ;
		
		gf.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/ArticleCount.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) gf.getObjectMappingManager().getObjectMappingByName("articleCount") ;
		CompiledSQLManagerImpl csm = new CompiledSQLManagerImpl(gf.getCompiledSQLBuilder()) ;
		
		//test insert
		CompiledSQL cs = csm.buildInsertSQLWithPK(map) ;
		assertEquals(cs.getSql(), "insert into TB_ARTICLE_COUNT(ARTICLE_ID, readCount, createdTime) values(?, ?, ?)") ;
		assertEquals(cs.getOrderedParams().length, 3) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[articleId, readCount, createdTime]") ;
		
		cs = csm.buildInsertSQLWithoutPK(map) ;
		assertEquals(cs.getSql(), "insert into TB_ARTICLE_COUNT(readCount, createdTime) values(?, ?)") ;
		assertEquals(cs.getOrderedParams().length, 2) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[readCount, createdTime]") ;
		
		//update
		cs = csm.buildUpdateSQL(map) ;
		assertEquals(cs.getSql(), "update TB_ARTICLE_COUNT set supportCount=?, opposeCount=?, createdTime=? where ARTICLE_ID=?") ;
		assertEquals(cs.getOrderedParams().length, 4) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[supportCount, opposeCount, createdTime, articleId]") ;
				
		//delete
		cs = csm.buildDeleteSQL(map) ;
		assertEquals(cs.getSql(), "delete from TB_ARTICLE_COUNT where ARTICLE_ID=?") ;
		assertEquals(cs.getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[articleId]") ;
		
		//select
		cs = csm.buildSelectSQL(map) ;
		assertEquals(cs.getSql(), "select ARTICLE_ID, readCount, supportCount, opposeCount, createdTime from TB_ARTICLE_COUNT where ARTICLE_ID=?") ;
		assertEquals(cs.getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.getOrderedParams()).toString(), "[articleId]") ;
	}

}
