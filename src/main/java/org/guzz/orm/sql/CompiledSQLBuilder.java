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

import org.guzz.connection.DBGroup;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CustomCompiledSQL.DynamicSQLProvider;

/**
 * 
 * A builder to create CompiledSQL.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CompiledSQLBuilder {
	
	public DBGroup getDBGroup(String businessName) ;
	public DBGroup getDBGroup(Class domainClass) ;
	

	/**获得compiledSQL，如果没有则新建一个。*/
	public CompiledSQL buildCompiledSQL(String businessName, String markedSQL) ;	
	public CompiledSQL buildCompiledSQL(Class domainClass, String markedSQL) ;
	
	
	public NormalCompiledSQL buildCompiledSQL(ObjectMapping mapping, String markedSQL) ;	
	public NormalCompiledSQL buildCompiledSQL(MarkedSQL sql) ;
	
		
	public TemplatedCompiledSQL buildTemplatedCompiledSQL(ObjectMapping mapping, String markedSQL) ;	
	public TemplatedCompiledSQL buildTemplatedCompiledSQL(String businessName, String markedSQL) ;	
	
	
	public CustomCompiledSQL buildCustomCompiledSQL(String businessName, DynamicSQLProvider sqlProvider) ;	
	public CustomCompiledSQL buildCustomCompiledSQL(Class domainClass, DynamicSQLProvider sqlProvider) ;
			
}
