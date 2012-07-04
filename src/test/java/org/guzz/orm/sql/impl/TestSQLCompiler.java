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

import org.guzz.GuzzContextImpl;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.MarkedSQL;
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
		ObjectMapping map = gf.getObjectMappingManager().getStaticObjectMapping("article") ;
		
		//测试POJO字段映射
		String sql = "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE order by id asc limit 10 " ;
		SQLCompiler sc = new SQLCompiler(gf.getObjectMappingManager(), gf.getCompiledSQLBuilder()) ;
		
		MarkedSQL ms = new MarkedSQL(map, sql) ;
		
		sql = sc.translateMark(null, ms.getMapping(), ms.getOrginalSQL()) ;
		assertEquals(sql, "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 ") ;
		
		//测试@prop在sql最后的情况。
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		//测试对表转义的支持。
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@article order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@" + Article.class.getName() +" order by id asc limit 10 @title") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 NAME") ;
		
		ms = new MarkedSQL(map, "select @id, @title, DESCRIPTION, @createdTime from @@" + Article.class.getName() +" order by id asc limit 10 (@title)") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10 (NAME)") ;
		
		//pure sql
		ms = new MarkedSQL(map, "select * ,  id, title, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select * ,  id, title, DESCRIPTION, createdTime from TB_ARTICLE order by id asc limit 10") ;	
		
		//insert. bug on '('
		ms = new MarkedSQL(map, "insert into @@article(@id, @title, DESCRIPTION, @createdTime) values(:id, :title, :d, :c)") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "insert into TB_ARTICLE(id, NAME, DESCRIPTION, createdTime) values(:id, :title, :d, :c)") ;		
	
		//sql with HH:mm:ss time string
		ms = new MarkedSQL(map, "select * from @@article where @title=:title and @createdTime > \\@to_date('20110101 0\\:0\\:0', 'YYYYMMDD HH24\\:MI\\:SS')") ;
		assertEquals(sc.translateMark(null, map, ms.getOrginalSQL()), "select * from TB_ARTICLE where NAME=:title and createdTime > @to_date('20110101 0\\:0\\:0', 'YYYYMMDD HH24\\:MI\\:SS')") ;	
	
	}
	
	public void testSQLCompile() throws Exception{
		ObjectMapping map = gf.getObjectMappingManager().getStaticObjectMapping("article") ;
		
		//测试POJO字段映射
		String sql = "select @id, @title, DESCRIPTION, @createdTime from TB_ARTICLE where id=:id and title=:m_title order by id asc limit 10 " ;
		SQLCompiler sc = new SQLCompiler(gf.getObjectMappingManager(), ((GuzzContextImpl) gf).getCompiledSQLBuilder()) ;
		
		CompiledSQL cs = sc.compileNormalCS(map, sql) ;
		
		assertEquals(cs.bindNoParams().getSQLToRun(), "select id, NAME, DESCRIPTION, createdTime from TB_ARTICLE where id=? and title=? order by id asc limit 10 ") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 2) ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[0], "id") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[1], "m_title") ;		
		

		cs = sc.compileNormalCS(map, "update @@article set * = ? , title = :title,var=:var,checked=1 where id=:id") ;
		
		assertEquals(cs.bindNoParams().getSQLToRun(), "update TB_ARTICLE set * = ? , title = ?,var=?,checked=1 where id=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 3) ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[0], "title") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[1], "var") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[2], "id") ;
		
		cs = sc.compileNormalCS(map, "update @@article set * = ? , title = :title,var=:var,checked=1 where id=(:id)") ;
		
		assertEquals(cs.bindNoParams().getSQLToRun(), "update TB_ARTICLE set * = ? , title = ?,var=?,checked=1 where id=(?)") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 3) ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[0], "title") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[1], "var") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[2], "id") ;
		
		//测试pure sql
		cs = sc.compileNormalCS(map, "select * from TB_TABLE where id = 1") ;
		
		assertEquals(cs.bindNoParams().getSQLToRun(), "select * from TB_TABLE where id = 1") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 0) ;
		
		//sql with HH:mm:ss time string
		cs = sc.compileNormalCS(map, "select * from @@article where @title=:title and @createdTime > \\@to_date('20110101 0\\:0\\:0', 'YYYYMMDD HH24\\:MI\\:SS')") ;
		assertEquals(cs.bindNoParams().getSQLToRun(), "select * from TB_ARTICLE where NAME=? and createdTime > @to_date('20110101 0:0:0', 'YYYYMMDD HH24:MI:SS')") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 1) ;
		
		try{
			cs = sc.compileNormalCS(map, "select * from @@article where @title=:title and @createdTime > \\@to_date('\\20110101 0\\:0\\:0', 'YYYYMMDD HH24\\:MI\\:SS')") ;
			fail("should fail.") ;
		}catch(Exception e){
			
		}
	}
	
	public void testSpecial() throws Exception{
		ObjectMapping map = gf.getObjectMappingManager().getStaticObjectMapping("book") ;
		
		//测试POJO字段映射
		String sql = "update TB_BOOK set @title=:title where id=:id" ;
		SQLCompiler sc = new SQLCompiler(gf.getObjectMappingManager(), ((GuzzContextImpl) gf).getCompiledSQLBuilder()) ;
		
		CompiledSQL cs = sc.compileNormalCS(map, sql) ;
		
		assertEquals(cs.bindNoParams().getSQLToRun(), "update TB_BOOK set NAME=? where id=?") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams().length, 2) ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[0], "title") ;
		assertEquals(cs.bindNoParams().getCompiledSQLToRun().getOrderedParams()[1], "id") ;
	}

}
