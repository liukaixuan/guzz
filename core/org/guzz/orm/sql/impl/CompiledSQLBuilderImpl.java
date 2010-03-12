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
package org.guzz.orm.sql.impl;

import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CustomCompiledSQL;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.orm.sql.NormalCompiledSQL;
import org.guzz.orm.sql.CustomCompiledSQL.DynamicSQLProvider;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CompiledSQLBuilderImpl implements CompiledSQLBuilder {
	
	protected ObjectMappingManager omm ;
	
	protected SQLCompiler sc ;
	
	public CompiledSQLBuilderImpl(ObjectMappingManager omm){
		this.omm = omm ;
		this.sc = new SQLCompiler(omm, this) ;
	}

	public NormalCompiledSQL buildCompiledSQL(ObjectMapping mapping, String markedSQL) {
		//TODO: add CompiledSQL cache here?
		
		return sc.compileNormalCS(mapping, markedSQL) ;
	}

	public NormalCompiledSQL buildCompiledSQL(MarkedSQL sql) {
		return sc.compileNormalCS(sql.getMapping(), sql.getOrginalSQL()) ;
	}

	public CompiledSQL buildCompiledSQL(String businessName, String markedSQL) {
		return sc.compile(businessName, markedSQL) ;
	}

	public CompiledSQL buildCompiledSQL(Class domainClass, String markedSQL) {
		return sc.compile(domainClass.getName(), markedSQL) ;
	}

	public CustomCompiledSQL buildCustomCompiledSQL(String businessName, DynamicSQLProvider sqlProvider) {
		return sc.compileCustom(businessName, sqlProvider) ;
	}

	public CustomCompiledSQL buildCustomCompiledSQL(Class domainClass, DynamicSQLProvider sqlProvider) {
		return sc.compileCustom(domainClass.getName(), sqlProvider) ;
	}

}
