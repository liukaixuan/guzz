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
package org.guzz.orm.sql;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guzz.orm.ObjectMapping;
import org.guzz.orm.type.SQLDataType;

/**
 * 
 * 编译好的sql语句。包括可以直接在数据库执行的sql语句，sql执行参数（参数值可以替换），以及ORM信息。
 * 
 * <br><br>一般来说，所有的sql都需要转换成CompliedSQL，然后绑定参数后进行实际的执行操作。
 * 
 * @see BindedCompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CompiledSQL {
	
	private String sql ;
	
	private Map paramPropMapping = null ;
	
	private List orderedParams = new ArrayList() ;
	
	private ObjectMapping mapping ;
		
	private String[] cached_orderedParams = null ;
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public void addParamToLast(String paramName){
		orderedParams.add(paramName) ;
		cached_orderedParams = null ;
	}

	/**如果没有参数，返回长度为0的数组。*/
	public String[] getOrderedParams() {
		if(cached_orderedParams == null){
			cached_orderedParams = (String[]) orderedParams.toArray(new String[0]);
		}
		
		return cached_orderedParams ;
	}

	public void setOrderedParams(List orderedParams) {
		this.orderedParams = orderedParams;
		cached_orderedParams = null ;
	}

	public ObjectMapping getMapping() {
		return mapping;
	}

	public void setMapping(ObjectMapping mapping) {
		this.mapping = mapping;
	}
	
	/**
	 * Add mapping between the parameter Name and the corresponding pojo's property name.
	 * <br>Once the link is established, the jdbc operation for the param(eg: {@link PreparedStatement#setObject(int, Object)}) will be handled with the property's {@link SQLDataType} for more precisely operate.
	 */
	public CompiledSQL addParamPropMapping(String paramName, String propName){
		if(paramPropMapping == null){
			paramPropMapping = new HashMap() ;
		}
		
		paramPropMapping.put(paramName, propName) ;
		return this ;
	}
	
	/**
	 * @see #addParamPropMapping(String, String)
	 */
	public CompiledSQL setParamPropMapping(Map paramPropMapping){
		this.paramPropMapping = paramPropMapping ;
		return this ;
	}
	
	/**
	 * get the pojo's property name for the giving paramName.
	 * @return property name. If no mapping found, return null.
	 */
	public String getPropName(String paramName){
		if(paramPropMapping == null) return null ;
		
		return (String) paramPropMapping.get(paramName) ;
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		return new BindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		return new BindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, long paramValue){
		return new BindedCompiledSQL(this).bind(paramName, new Long(paramValue)) ;
	}
	
	public BindedCompiledSQL bind(Map params){
		return new BindedCompiledSQL(this).bind(params) ;
	}
	
	public BindedCompiledSQL bindNoParams(){
		return new BindedCompiledSQL(this) ;
	}

}
