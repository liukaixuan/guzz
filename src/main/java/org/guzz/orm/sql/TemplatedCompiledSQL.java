/*
 * Copyright 2008-2012 the original author or authors.
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
package org.guzz.orm.sql;

import java.util.Map;

import org.guzz.orm.ObjectMapping;
import org.guzz.service.core.TemplatedSQLService;

/**
 * 
 * 支持动态拼接sql的编译好的sql语句。包原始模板id，ORM信息。
 * 
 * <br><br>一般来说，所有的sql都需要转换成CompliedSQL，然后绑定参数后进行实际的执行操作。
 * 
 * @see BindedCompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TemplatedCompiledSQL extends CompiledSQL {
	
	private String sql ;

	private String id ;
	
	private ObjectMapping mapping ;

	private final TemplatedSQLService templatedSqlService; 
	
	private String ormName ;
	
	private TemplatedCompiledSQL(TemplatedSQLService templatedSqlService){
		this.templatedSqlService = templatedSqlService ;
	}
	
	public static TemplatedCompiledSQL buildAndRegisterById(TemplatedSQLService templatedSqlService, ObjectMapping mapping, String id, String sql){
		TemplatedCompiledSQL t = new TemplatedCompiledSQL(templatedSqlService) ;
		t.id = id ;
		
		templatedSqlService.addImutableSql(id, mapping, sql) ;
		return t ;
	}
	
	public static TemplatedCompiledSQL buildAndRegisterById(TemplatedSQLService templatedSqlService, String ormName, String id, String sql){
		TemplatedCompiledSQL t = new TemplatedCompiledSQL(templatedSqlService) ;
		t.id = id ;
		
		templatedSqlService.addImutableSql(id, ormName, sql) ;
		return t ;
	}
	
	public static TemplatedCompiledSQL buildBySql(TemplatedSQLService templatedSqlService, ObjectMapping mapping, String sql){		
		TemplatedCompiledSQL t = new TemplatedCompiledSQL(templatedSqlService) ;
		t.mapping = mapping ;
		t.sql = sql ;
		
		return t ;
	}
	
	public static TemplatedCompiledSQL buildBySql(TemplatedSQLService templatedSqlService, String ormName, String sql){		
		TemplatedCompiledSQL t = new TemplatedCompiledSQL(templatedSqlService) ;
		t.ormName = ormName ;
		t.sql = sql ;
		
		return t ;
	}
	
	public final String getTemplatedSql(){
		return this.sql ;
	}
	
	public CompiledSQL getCompiledSQLForParams(Object tableCondition, Map params){
		CompiledSQL cs = null ;
		if(id != null){
			cs = this.templatedSqlService.getSqlById(id, tableCondition, params) ;
		}else if(mapping != null){
			cs = this.templatedSqlService.getSqlByStatement(mapping, tableCondition, sql, params) ;			
		}else{
			cs = this.templatedSqlService.getSqlByStatement(ormName, tableCondition, sql, params) ;
		}
		
		//复制orm等设置到新的cs中
		cs.paramPropMapping = this.paramPropMapping ;
		cs.paramTypes = this.paramTypes ;
		cs.resultClass = this.resultClass ;
		
		return cs ;
	}
	
	public final ObjectMapping getMapping() {
		return mapping;
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		return new TemplatedBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		return new TemplatedBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, long paramValue){
		return new TemplatedBindedCompiledSQL(this).bind(paramName, new Long(paramValue)) ;
	}
	
	public BindedCompiledSQL bind(Map params){
		return new TemplatedBindedCompiledSQL(this).bind(params) ;
	}
	
	public BindedCompiledSQL bindNoParams(){
		return new TemplatedBindedCompiledSQL(this) ;
	}

}
