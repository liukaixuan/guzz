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

import java.util.HashMap;
import java.util.Map;

import org.guzz.util.Assert;


/**
 * 
 * 支持动态拼接sql的bsql。这是临时对象，需要时生成，用完就消失。
 * 此对象方法不支持多线程操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TemplatedBindedCompiledSQL extends BindedCompiledSQL{
	
	private TemplatedCompiledSQL cs ;
	
	private NormalCompiledSQL currentCompiledSQL ;
	
	HashMap orginalParams = new HashMap() ;
	
	/**the sql ready to execute in database.*/
	private String cachedSql ;
	
	public TemplatedBindedCompiledSQL(TemplatedCompiledSQL cs){
		super(cs.getResultClass()) ;
		this.cs = cs ;
	}

	protected void notifyTableConditionChanged() {
		this.currentCompiledSQL = null ;
		this.cachedSql = null ;
	}

	public NormalCompiledSQL getCompiledSQLToRun() {
		if(currentCompiledSQL == null){
			currentCompiledSQL = cs.getCompiledSQLForParams(getTableCondition(), orginalParams)
								   .bindNoParams().copyUserSettingsFrom(this)
								   .getCompiledSQLToRun() ;
			
			//TODO: 设置本bsql的参数不在允许设置。
		}
		
		return currentCompiledSQL;
	}

	public String getSQLToRun() {
		if(this.cachedSql != null){
			return this.cachedSql ;
		}else{
			this.cachedSql = getCompiledSQLToRun().getSql(getTableCondition()) ;
			
			return this.cachedSql;
		}
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		Assert.assertNull(this.currentCompiledSQL, "bind() methods should be called before getSQLToRun() and getCompiledSQLToRun() for templated sqls.") ;
		
		super.bind(paramName, paramValue) ;
		orginalParams.put(paramName, paramValue) ;
		
		return this ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		Assert.assertNull(this.currentCompiledSQL, "bind() methods should be called before getSQLToRun() and getCompiledSQLToRun() for templated sqls.") ;
		
		super.bind(paramName, paramValue) ;
		orginalParams.put(paramName, paramValue) ;
		
		return this ;
	}
	
	public BindedCompiledSQL bind(Map params){
		Assert.assertNull(this.currentCompiledSQL, "bind() methods should be called before getSQLToRun() and getCompiledSQLToRun() for templated sqls.") ;
		
		if(params == null) return this;
		if(params.isEmpty()) return this ;
		
		super.bind(params) ;
		orginalParams.putAll(params) ;
		
		return this ;
	}
	
	public BindedCompiledSQL clearBindedParams(){
		Assert.assertNull(this.currentCompiledSQL, "clearBindedParams() should be called before getSQLToRun() and getCompiledSQLToRun() for templated sqls.") ;
		
		super.clearBindedParams() ;
		this.orginalParams.clear() ;
		
		return this ;
	}

}
