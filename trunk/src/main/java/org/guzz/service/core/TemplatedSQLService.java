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
package org.guzz.service.core;

import java.util.Map;

import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CustomCompiledSQL;
import org.guzz.orm.sql.NormalCompiledSQL;

/**
 * 
 * Translate sqls in (Velocity) templates to sqls that can be recognized by guzz.
 * 
 * <p>Don't use this interface in your application directly. Use {@link CompiledSQLBuilder} instead.</p>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface TemplatedSQLService {
	
	/**
	 * Get sql by the id. 
	 * 
	 * @param id The id used to identify sqls.
	 * @param params passed by the invoker.
	 * @return CompiledSQL to be executed. Return null if no sql found for the given id. The returned {@link CompiledSQL} should be a subclass of {@link NormalCompiledSQL} or {@link CustomCompiledSQL}.
	 */
	public CompiledSQL getSqlById(String id, Object tableCondition, Map params) ;
	
	
	public void addImutableSql(String id, ObjectMapping mapping, String sqlStatement) ;
	
	public void addImutableSql(String id, String ormName, String sqlStatement) ;

	
	public CompiledSQL getSqlByStatement(ObjectMapping mapping, Object tableCondition, String sqlStatement, Map params) ;
	
	public CompiledSQL getSqlByStatement(String ormName, Object tableCondition, String sqlStatement, Map params) ;
	
	
}
