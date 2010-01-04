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

import org.guzz.exception.ORMException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.MarkedSQL;

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
		this.sc = new SQLCompiler(omm) ;
	}
	
	public CompiledSQL buildCompiledSQLWithoutMapping(String markedSQL){
		MarkedSQL ms = new MarkedSQL(null, markedSQL) ;
		CompiledSQL cs = sc.compile(ms) ;		
		
		return cs;
	}

	public CompiledSQL buildCompiledSQL(ObjectMapping mapping, String markedSQL) {
		//TODO: add CompiledSQL cache here?
		
		MarkedSQL ms = new MarkedSQL(mapping, markedSQL) ;
		CompiledSQL cs = sc.compile(ms) ;		
		
		return cs;
	}

	public CompiledSQL buildCompiledSQL(String ghostName, String markedSQL) {
		ObjectMapping m = omm.getObjectMappingByName(ghostName) ;
		if(m == null){
			throw new ORMException("no ObjectMapping found. name:" + ghostName) ;
		}
		
		return buildCompiledSQL(m, markedSQL);
	}

	public CompiledSQL buildCompiledSQL(Class domainClass, String markedSQL) {
		ObjectMapping m = omm.getObjectMappingByName(domainClass.getName()) ;
		if(m == null){
			throw new ORMException("no ObjectMapping found. " + domainClass) ;
		}
		
		return buildCompiledSQL(m, markedSQL);
	}

}
