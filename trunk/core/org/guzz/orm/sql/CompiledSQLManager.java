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

import org.guzz.orm.mapping.POJOBasedObjectMapping;

/**
 * 
 * 用来管理预设的SQL，如通过xml文件配置的复杂查询语句等。这些sql语句拥有唯一的id标识，系统在加载时自动将其编译为 {@link CompiledSQL} 
 * 减少编译过程。
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CompiledSQLManager {
	
	public static final INNER_CS_MARK CS_PREFIX = new INNER_CS_MARK() ;
	
	
	public CompiledSQL getSQL(String id) ;
	
	/**
	 * 获得领域对象的select语句。
	 * @param className 域对象完成的类名或者ghost name
	 */
	public CompiledSQL getDefinedSelectSQL(String className) ;
	
	/**
	 * 获得领域对象的insert语句。
	 * @param className 域对象完成的类名或者ghost name
	 */
	public CompiledSQL getDefinedInsertSQL(String className) ;
	
	public CompiledSQL getDefinedUpdateSQL(String className) ;
	
	public CompiledSQL getDefinedDeleteSQL(String className) ;

	public CompiledSQL buildUpdateSQL(POJOBasedObjectMapping mapping, String[] propsToUpdate) ;
	
	/**
	 * build a sql to load cell00 by primary key.<br> named param for pk is:guzz_pk
	 * 
	 * @param object mapping 
	 * @param columnName the column name of the database.
	 */
	public CompiledSQL buildLoadColumnByPkSQL(POJOBasedObjectMapping mapping, String columnName) ;
	
	public void addCompliedSQL(String id, CompiledSQL cs) ;
	
	/**
	 * 添加一个域对象，自动生成域对象insert/update/delete的CompiledSQL，并进行注册。
	 */
	public void addDomainBusiness(POJOBasedObjectMapping mapping) ;

	
	static class INNER_CS_MARK{
		protected INNER_CS_MARK(){}
		
		public final String BUSINESS_INSERT_PREFIX = "__bi__insert__" ;
		public final String BUSINESS_UPDATE_PREFIX = "__bi__update__" ;
		public final String BUSINESS_DELETE_PREFIX = "__bi__delete__" ;
		public final String BUSINESS_SELECT_PREFIX = "__bi__select__" ;
		
	}

}


