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
package org.guzz.service.core.impl;

import org.guzz.Service;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.AbstractService;
import org.guzz.service.ProxyService;
import org.guzz.service.ServiceChangeCommand;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.DynamicSQLService;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class DynamicSQLServiceProxy extends ProxyService implements DynamicSQLService {
	
	private DynamicSQLService dynamicSQLServiceImpl = new DummyDynamicSQLService() ;

	public CompiledSQL getSql(String id) {
		return dynamicSQLServiceImpl.getSql(id) ;
	}

	public boolean overrideSqlInGuzzXML() {
		return dynamicSQLServiceImpl.overrideSqlInGuzzXML() ;
	}
	
	public Object setServiceImpl(Service service){
		Object old = this.dynamicSQLServiceImpl ;
		this.dynamicSQLServiceImpl = (DynamicSQLService) service ;
		
		return old ;
	}

	public Service getServiceImpl() {
		return (Service) dynamicSQLServiceImpl ;
	}
	
}

class DummyDynamicSQLService extends AbstractService implements DynamicSQLService, Service{

	public CompiledSQL getSql(String id) {
		return null;
	}

	public boolean overrideSqlInGuzzXML() {
		return false;
	}

	public boolean configure(ServiceConfig[] scs) {
		return false;
	}

	public void executeCommand(ServiceChangeCommand cmd) {
	}

	public boolean isAvailable() {
		return false;
	}

	public void shutdown() {
	}

	public void startup() {
	}
}

