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
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.guzz.orm.type.SQLDataType;

/**
 * 
 * Compiled (detached) Sql, including parsed sql statement that can be executed directly in the database,
 *  the ordered parameters in the sql, and the default ORM settings.
 * <p>
 * In Guzz, almost every sql should be converted to a CompiledSQL, then bind its parameters, and then execute.
 * </p>
 * 
 * The CompiledSQL is thread-safe, and is recommended to be cached in all cases for higher performance and cleaner codes.
 * 
 * @see BindedCompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class CompiledSQL {
		
	protected Map paramPropMapping = null ;
	
	protected Map paramTypes = null ;
	
	protected Class resultClass ;
	
	/**
	 * Register the dataType for a sql param.
	 */
	public CompiledSQL registerParamType(String paramName, String dataType){
		if(paramTypes == null){
			paramTypes = new HashMap() ;
		}
		
		paramTypes.put(paramName, dataType) ;
		return this ;
	}
	
	/**
	 * @see #registerParamType(String, String)
	 */
	public CompiledSQL addParamType(String paramName, String dataType){
		return registerParamType(paramName, dataType) ;
	}
	
	/**
	 * Register the dataType for a sql param.
	 */
	public CompiledSQL registerParamTypes(Map paramTypes){
		if(paramTypes == null){
			return this ;
		}
		
		if(this.paramTypes == null){
			this.paramTypes = new HashMap() ;
		}
		
		this.paramTypes.putAll(paramTypes) ;
		
		return this ;
	}
	
	/**
	 * Add a mapping between the parameter name and the corresponding pojo's property name.
	 * <br>Once the link is established, the jdbc operation for the param(eg: {@link PreparedStatement#setObject(int, Object)}) will be replaced with the property's {@link SQLDataType} for more precisely binding.
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
	 * <br>Once the link is established, the jdbc operation for the param(eg: {@link PreparedStatement#setObject(int, Object)}) will be replaced with the property's {@link SQLDataType} for more precisely binding.
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
	 * Query the data type for the giving paramName.
	 * 
	 * @param cs The runtime CompiledSQL.
	 * @param paramName
	 * @return Return null if no mapping found.
	 */
	public SQLDataType getSQLDataTypeForParam(NormalCompiledSQL cs, String paramName){
		//check type first, then prop name.
		if(this.paramTypes != null){
			String typeName = (String) this.paramTypes.get(paramName) ;
 			
			if(typeName != null){
				return cs.getMapping().getDbGroup().getDialect().getDataType(typeName) ;
			}
		}
		
		if(paramPropMapping != null){
			String propName = (String) this.paramPropMapping.get(paramName) ;
			
			if(propName != null){
				return cs.getMapping().getSQLDataTypeOfProperty(propName) ;
			}
		}
		
		return null ;
	}
	
	/**绑定sql执行需要的参数*/
	public abstract BindedCompiledSQL bind(String paramName, Object paramValue) ;
	
	public abstract BindedCompiledSQL bind(String paramName, int paramValue) ;
	
	public abstract BindedCompiledSQL bind(String paramName, long paramValue) ;
	
	public abstract BindedCompiledSQL bind(Map params) ;
	
	public abstract BindedCompiledSQL bindNoParams() ;

	public Class getResultClass() {
		return resultClass;
	}

	/**
	 * 
	 * Set the mapped javabean for the {@link ResultSet} to any given class with set-xxx methods to override the ORM's default business class.
	 * 
	 * <p>
	 * If the resultClass is a subclass of java.util.Map(for example:java.util.HashMap), the queried values will be put into the Map.<br>
	 * </p>
	 *
	 * @param resultClass
	 */
	public CompiledSQL setResultClass(Class resultClass) {
		this.resultClass = resultClass;
		
		return this ;
	}

}
