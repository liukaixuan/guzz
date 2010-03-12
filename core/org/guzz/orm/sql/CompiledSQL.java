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
import java.util.HashMap;
import java.util.Map;

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
public abstract class CompiledSQL {
		
	protected Map paramPropMapping = null ;
	
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
	 * Add mapping between the parameter Name and the corresponding pojo's property name.
	 * <br>Once the link is established, the jdbc operation for the param(eg: {@link PreparedStatement#setObject(int, Object)}) will be handled with the property's {@link SQLDataType} for more precisely operate.
	 */
	public CompiledSQL addParamPropMappings(Map paramPropMapping){
		if(paramPropMapping == null){
			return this ;
		}
		
		if(this.paramPropMapping == null){
			this.paramPropMapping = new HashMap() ;
		}
		
		this.paramPropMapping.putAll(paramPropMapping) ;
		
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
	public abstract BindedCompiledSQL bind(String paramName, Object paramValue) ;
	
	public abstract BindedCompiledSQL bind(String paramName, int paramValue) ;
	
	public abstract BindedCompiledSQL bind(String paramName, long paramValue) ;
	
	public abstract BindedCompiledSQL bind(Map params) ;
	
	public abstract BindedCompiledSQL bindNoParams() ;

}
