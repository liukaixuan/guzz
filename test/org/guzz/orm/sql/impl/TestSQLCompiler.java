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

import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.io.FileResource;
import org.guzz.orm.Business;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.orm.sql.impl.SQLCompiler;
import org.guzz.test.Article;
import org.guzz.test.DBBasedTestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestSQLCompiler extends DBBasedTestCase {
	
	public void testTranslateSQLMark() throws Exception{
		
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		ObjectMapping map = f.getObjectMappingManager().getObjectMappingByName("article") ;
		
		//测试POJO字段映射
		String sql = "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE order by id asc limit 10 " ;
		SQLCompiler sc = new SQLCompiler(f.getObjectMappingManager()) ;
		
		MarkedSQL ms = new MarkedSQL(map, sql) ;
		
		sql = sc.translateMark(null, ms) ;
		assertEquals(sql, "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 ") ;
		
		//测试@prop在sql最后的情况。
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, ms), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		//测试对表转义的支持。
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@article order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, ms), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@" + Article.class.getName() +" order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, ms), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@" + Article.class.getName() +" order by id asc limit 10 (@title)") ;
		assertEquals(sc.translateMark(null, ms), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 (NAME)") ;
		
		//pure sql
		ms = new MarkedSQL(map, "select * ,  id, title, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10") ;
		assertEquals(sc.translateMark(null, ms), "select * ,  id, title, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10") ;	
		
		//insert. bug on '('
		ms = new MarkedSQL(map, "insert into @@article(@id, @title, DESCRIPTION, @createdTime) values(:id, :title, :d, :c)") ;
		assertEquals(sc.translateMark(null, ms), "insert into TB_ARTICLE(id, NAME, DESCRIPTION, createdTime) values(:id, :title, :d, :c)") ;		

		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testSQLCompile() throws Exception{
		
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		Business ga = f.instanceNewGhost("article", null, null, null) ;
		
		f.addHbmConfigFile(ga, FileResource.CLASS_PATH_PREFIX + "org/guzz/test/Article.hbm.xml") ;
		
		ObjectMapping map = f.getObjectMappingManager().getObjectMappingByName("article") ;
		
		//测试POJO字段映射
		String sql = "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE where id=:id and title=:m_title order by id asc limit 10 " ;
		SQLCompiler sc = new SQLCompiler(f.getObjectMappingManager()) ;
		
		MarkedSQL ms = new MarkedSQL(map, sql) ;		
		CompiledSQL cs = sc.compile(ms) ;
		
		assertEquals(cs.getSql(null), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE where id=? and title=? order by id asc limit 10 ") ;
		assertEquals(cs.getOrderedParams().length, 2) ;
		assertEquals(cs.getOrderedParams()[0], "id") ;
		assertEquals(cs.getOrderedParams()[1], "m_title") ;		
		

		cs = sc.compile(new MarkedSQL(map, "update @@article set * = ? , title = :title,var=:var,checked=1 where id=:id")) ;
		
		assertEquals(cs.getSql(null), "update TB_ARTICLE set * = ? , title = ?,var=?,checked=1 where id=?") ;
		assertEquals(cs.getOrderedParams().length, 3) ;
		assertEquals(cs.getOrderedParams()[0], "title") ;
		assertEquals(cs.getOrderedParams()[1], "var") ;
		assertEquals(cs.getOrderedParams()[2], "id") ;
		
		cs = sc.compile(new MarkedSQL(map, "update @@article set * = ? , title = :title,var=:var,checked=1 where id=(:id)")) ;
		
		assertEquals(cs.getSql(null), "update TB_ARTICLE set * = ? , title = ?,var=?,checked=1 where id=(?)") ;
		assertEquals(cs.getOrderedParams().length, 3) ;
		assertEquals(cs.getOrderedParams()[0], "title") ;
		assertEquals(cs.getOrderedParams()[1], "var") ;
		assertEquals(cs.getOrderedParams()[2], "id") ;
		
		//测试pure sql
		cs = sc.compile(new MarkedSQL(map, "select * from TB_TABLE where id = 1")) ;
		
		assertEquals(cs.getSql(null), "select * from TB_TABLE where id = 1") ;
		assertEquals(cs.getOrderedParams().length, 0) ;

		((GuzzContextImpl) f).shutdown() ;
	}
	
	public void testSpecial() throws Exception{
		GuzzContext f = new Configuration("classpath:guzzmain_test1.xml").newGuzzContext() ;
		
		ObjectMapping map = f.getObjectMappingManager().getObjectMappingByName("book") ;
		
		//测试POJO字段映射
		String sql = "update TB_BOOK set @title=:title where id=:id" ;
		SQLCompiler sc = new SQLCompiler(f.getObjectMappingManager()) ;
		
		MarkedSQL ms = new MarkedSQL(map, sql) ;		
		CompiledSQL cs = sc.compile(ms) ;
		
		assertEquals(cs.getSql(null), "update TB_BOOK set NAME=? where id=?") ;
		assertEquals(cs.getOrderedParams().length, 2) ;
		assertEquals(cs.getOrderedParams()[0], "title") ;
		assertEquals(cs.getOrderedParams()[1], "id") ;

		((GuzzContextImpl) f).shutdown() ;
	}

}
