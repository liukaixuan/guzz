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

import org.guzz.orm.ObjectMapping;

/**
 * 
 * 用于生成CompiledSQL.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CompiledSQLBuilder {
		
	public CompiledSQL buildCompiledSQL(ObjectMapping mapping, String markedSQL) ;
	
	/**获得compiledSQL，如果没有则新建一个。*/
	public CompiledSQL buildCompiledSQL(String ghostName, String markedSQL) ;
	
	public CompiledSQL buildCompiledSQL(Class domainClass, String markedSQL) ;
		
}
