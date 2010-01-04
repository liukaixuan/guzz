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
package org.guzz.transaction;

import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.service.core.DebugService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TransactionManagerFactory {
	
	public static TransactionManager buildTransactionFactory(
			ObjectMappingManager omm, 
			CompiledSQLManager compiledSQLManager, 
			CompiledSQLBuilder compiledSQLBuilder, 
			DebugService debugService, 
			DBGroupManager dbGroupManager){
				
		return new DataSourceTransactionManager(omm, compiledSQLManager, compiledSQLBuilder, debugService, dbGroupManager) ;
	}

}
