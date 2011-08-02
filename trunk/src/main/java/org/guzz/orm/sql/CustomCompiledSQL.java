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

import java.util.Map;

import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;

/**
 * 
 * 编译好的sql语句。包括可以直接在数据库执行的sql语句，sql执行参数（参数值可以替换），以及ORM信息。
 * 
 * <br><br>一般来说，所有的sql都需要转换成CompliedSQL，然后绑定参数后进行实际的执行操作。
 * 
 * @see BindedCompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CustomCompiledSQL extends CompiledSQL {
	
	public static interface DynamicSQLProvider{		
		public NormalCompiledSQL getSql(POJOBasedObjectMapping mapping) ;
	}
	
	private final ObjectMappingManager omm ;
	
	private final CompiledSQLBuilder sqlBuilder ;
	
	private final String businessName ;
	
	private String sql ;
	
	private DynamicSQLProvider sqlProvider ;
	
	public CustomCompiledSQL(ObjectMappingManager omm, CompiledSQLBuilder sqlBuilder, String businessName){
		this.omm = omm ;
		this.businessName = businessName ;
		this.sqlBuilder = sqlBuilder ;
	}
	
	public POJOBasedObjectMapping getObjectMapping(Object tableCondition){
		return omm.getCustomObjectMapping(businessName, tableCondition) ;
	}
	
	/**
	 * 根据tableCondition获取完成shadow映射后的sql语句。
	 */
	public NormalCompiledSQL getSql(POJOBasedObjectMapping mapping) {
		NormalCompiledSQL m_sql = null ;
		
		if(this.sqlProvider != null){
			m_sql = sqlProvider.getSql(mapping) ;
		}else{
			m_sql = this.sqlBuilder.buildCompiledSQL(mapping, this.sql) ;
		}
		
		return m_sql ;
	}
	
	
	/**
	 * 设置查询sql，如果sql中涉及shadow表，表名可以用@@businessName替代；
	 * 替代后，调用 {@link CompiledSQL#addShadowMapping(String, Table)} 声明映射。
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSqlProvider(DynamicSQLProvider sqlProvider) {
		this.sqlProvider = sqlProvider;
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		return new CustomBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		return new CustomBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, long paramValue){
		return new CustomBindedCompiledSQL(this).bind(paramName, new Long(paramValue)) ;
	}
	
	public BindedCompiledSQL bind(Map params){
		return new CustomBindedCompiledSQL(this).bind(params) ;
	}
	
	public BindedCompiledSQL bindNoParams(){
		return new CustomBindedCompiledSQL(this) ;
	}

}
