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
package org.guzz.transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.guzz.dao.PageFlip;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.sql.BindedCompiledSQL;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ReadonlyTranSession {
	
	public JDBCTemplate createJDBCTemplate(Class domainClass) ;
	
	/**
	 * @param id
	 * @param params
	 * @param startPos the first is 1, the second is 2...
	 * @param maxSize
	 **/
	public List list(String id, Map params, int startPos, int maxSize) ;
	
	/**
	 * @param bsql
	 * @param startPos the first is 1, the second is 2...
	 * @param maxSize
	 **/
	public List list(BindedCompiledSQL bsql, int startPos, int maxSize) ;

	public List list(SearchExpression se) ;
	
	/**分页查询*/
	public PageFlip page(SearchExpression se) ;
	
	
	public Object findObject(String id, Map params) ;
	
	public Object findObject(BindedCompiledSQL bsql) ;
	
	public Object findObject(SearchExpression se) ;	
	
	
	public Object findObjectByPK(String businessName, Serializable pk) ;
	
	public Object findObjectByPK(Class domainClass, Serializable pk) ;
	
	public Object findObjectByPK(Class domainClass, int pk) ;

	
	public Object findCell00(String id, Map params, String returnType) ;
	
	public Object findCell00(BindedCompiledSQL bsql, String returnType) ;
	
	/**执行se中的count操作，返回long类型的数据。*/
	public long count(SearchExpression se) ;	
	
	public void close() ;
	
	public boolean allowDelayRead() ;
	
}
