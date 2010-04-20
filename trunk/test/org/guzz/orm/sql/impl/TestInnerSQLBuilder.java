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

import org.guzz.GuzzContextImpl;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.GuzzTestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestInnerSQLBuilder extends GuzzTestCase {

	public void testTranslateSQLMark() throws Exception{
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) gf.getObjectMappingManager().getStaticObjectMapping("user") ;
		CompiledSQLManagerImpl csm = new CompiledSQLManagerImpl(((GuzzContextImpl) gf).getCompiledSQLBuilder()) ;
		
		//test insert
		CompiledSQL cs = csm.buildNormalInsertSQLWithPK(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "insert into TB_USER(pk, userName, MyPSW, VIP_USER, FAV_COUNT, createdTime) values(?, ?, ?, ?, ?, ?)") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 6) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[id, userName, password, vip, favCount, createdTime]") ;
		
		cs = csm.buildNormalInsertSQLWithoutPK(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "insert into TB_USER(userName, MyPSW, VIP_USER, FAV_COUNT, createdTime) values(?, ?, ?, ?, ?)") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 5) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[userName, password, vip, favCount, createdTime]") ;
		
		//update
		cs = csm.buildNormalUpdateSQL(map) ; 
		assertEquals(cs.bindNoParams().getSQLToRun(), "update TB_USER set userName=?, MyPSW=?, VIP_USER=?, FAV_COUNT=?, createdTime=? where pk=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 6) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[userName, password, vip, favCount, createdTime, id]") ;
				
		//delete
		cs = csm.buildNormalDeleteSQL(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "delete from TB_USER where pk=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[id]") ;
		
		//select
		cs = csm.buildNormalSelectSQL(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "select pk, userName, MyPSW, VIP_USER, FAV_COUNT, createdTime from TB_USER where pk=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[id]") ;
	}
	
	public void testInsertUpdateIgnoreParam() throws Exception{
		Business ga = gf.instanceNewGhost("articleCount", null, null, null) ;
		
		gf.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/ArticleCount.hbm.xml") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) gf.getObjectMappingManager().getStaticObjectMapping("articleCount") ;
		CompiledSQLManagerImpl csm = new CompiledSQLManagerImpl(((GuzzContextImpl) gf).getCompiledSQLBuilder()) ;
		
		//test insert
		CompiledSQL cs = csm.buildNormalInsertSQLWithPK(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "insert into TB_ARTICLE_COUNT(ARTICLE_ID, readCount, createdTime) values(?, ?, ?)") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 3) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[articleId, readCount, createdTime]") ;
		
		cs = csm.buildNormalInsertSQLWithoutPK(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "insert into TB_ARTICLE_COUNT(readCount, createdTime) values(?, ?)") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 2) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[readCount, createdTime]") ;
		
		//update
		cs = csm.buildNormalUpdateSQL(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "update TB_ARTICLE_COUNT set supportCount=?, opposeCount=?, createdTime=? where ARTICLE_ID=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 4) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[supportCount, opposeCount, createdTime, articleId]") ;
				
		//delete
		cs = csm.buildNormalDeleteSQL(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "delete from TB_ARTICLE_COUNT where ARTICLE_ID=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[articleId]") ;
		
		//select
		cs = csm.buildNormalSelectSQL(map) ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "select ARTICLE_ID, readCount, supportCount, opposeCount, createdTime from TB_ARTICLE_COUNT where ARTICLE_ID=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		assertEquals(Arrays.asList(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()).toString(), "[articleId]") ;
	}

}
