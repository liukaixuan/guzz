/*
 * Copyright 2008-2010 the original author or authors.
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

import org.guzz.orm.sql.CompiledSQL;

/**
 * 
 * Provide sqls dynamically.
 * 
 * <p>
 * In guzz, complicated sqls are suggested to be stored in the guzz.xml file like IBatis.
 * The advantage is good for management, but once you have to change it, you must restart your apps to take effects.
 * That is a big issue for any really serious online systems.
 * </p>
 * <p>
 * To solve this problem, guzz introduced {@link DynamicSQLService}, with which you can add/remove/update(tune) complicated and/or
 * performance critica sqls online without restartings;  and you can also design a new architecture to retrieve data for (ajax based) show out without writing a single line of server-side code. 
 * </p>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface DynamicSQLService {
	
	/**
	 * 
	 * Get sql by the id. 
	 * 
	 * <p>
	 * If the sql has been changed and should take effects, this method should return the new one.<br><br>
	 * The implementation is responsible for moniting the change of sqls, making decisions whether it should take effects now or not, and caching(flushing cache) it for performance.
	 * </p>
	 * 
	 * @param id The id used to identify sqls.
	 * @return CompiledSQL to be executed. Return null if no sql found for the given id.
	 */
	public CompiledSQL getSql(String id) ;
	
	/**
	 * When both this service and the guzz.xml have defined a sql for a same id, which one takes a priority?
	 * 
	 * @return true: use sql from this service.<br>false: use sql in the guzz.xml.
	 */
	public boolean overrideSqlInGuzzXML() ;

}
