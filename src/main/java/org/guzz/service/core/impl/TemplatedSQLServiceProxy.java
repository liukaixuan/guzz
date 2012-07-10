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
package org.guzz.service.core.impl;

import java.util.LinkedList;
import java.util.Map;

import org.guzz.Service;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.AbstractService;
import org.guzz.service.ProxyService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.service.core.TemplatedSQLService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TemplatedSQLServiceProxy extends ProxyService implements TemplatedSQLService {
	
	public TemplatedSQLServiceProxy(){
		NotAvailableTemplatedSQLServiceImpl s = new NotAvailableTemplatedSQLServiceImpl() ;
		s.setServiceInfo(new ServiceInfo(Service.FAMOUSE_SERVICE.TEMPLATED_SQL, null, NotAvailableTemplatedSQLServiceImpl.class)) ;
		
		this.templatedSQLServiceImpl = s ;
	}
	
	private TemplatedSQLService templatedSQLServiceImpl ;
	
	public Object setServiceImpl(Service service){
		Object oldService = this.templatedSQLServiceImpl ;
		TemplatedSQLService newService = (TemplatedSQLService) service ;
		
		if(oldService instanceof NotAvailableTemplatedSQLServiceImpl){
			LinkedList<Temp_TSQL_Holder> sqls = ((NotAvailableTemplatedSQLServiceImpl) oldService).getSqlsToPrepare() ;
			
			for(Temp_TSQL_Holder sql : sqls){
				if(sql.mapping == null){
					newService.addImutableSql(sql.id, sql.ormName, sql.sqlStatement) ;
				}else{
					newService.addImutableSql(sql.id, sql.mapping, sql.sqlStatement) ;
				}
			}
		}
		
		this.templatedSQLServiceImpl = newService ;
		
		return oldService ;
	}

	public Service getServiceImpl() {
		return (Service) templatedSQLServiceImpl ;
	}

	public CompiledSQL getSqlById(String id, Object tableCondition, Map params) {
		return templatedSQLServiceImpl.getSqlById(id, tableCondition, params) ;
	}

	public void addImutableSql(String id, ObjectMapping mapping, String sqlStatement) {
		templatedSQLServiceImpl.addImutableSql(id, mapping, sqlStatement) ;
	}

	public void addImutableSql(String id, String ormName, String sqlStatement) {
		templatedSQLServiceImpl.addImutableSql(id, ormName, sqlStatement) ;
	}

	public CompiledSQL getSqlByStatement(ObjectMapping mapping, Object tableCondition, String sqlStatement, Map params) {
		return templatedSQLServiceImpl.getSqlByStatement(mapping, tableCondition, sqlStatement, params) ;
	}

	public CompiledSQL getSqlByStatement(String ormName, Object tableCondition, String sqlStatement, Map params) {
		return templatedSQLServiceImpl.getSqlByStatement(ormName, tableCondition, sqlStatement, params) ;
	}
	
	
	static class NotAvailableTemplatedSQLServiceImpl extends AbstractService implements TemplatedSQLService{

		private LinkedList<Temp_TSQL_Holder> sqls = new LinkedList<Temp_TSQL_Holder>() ;
		
		public LinkedList<Temp_TSQL_Holder> getSqlsToPrepare(){
			return sqls ;
		}
		
		public CompiledSQL getSqlById(String id, Object tableCondition, Map params) {
			throw new InvalidConfigurationException(Service.FAMOUSE_SERVICE.TEMPLATED_SQL + " service is required for templated sqls!") ;
		}

		//由于真正的templatedSQLService还未加载或是初始化，这里先保存下要初始化的配置。
		public void addImutableSql(String id, ObjectMapping mapping, String sqlStatement) {
			Temp_TSQL_Holder h = new Temp_TSQL_Holder() ;
			h.id = id ;
			h.mapping = mapping ;
			h.sqlStatement = sqlStatement ;
			
			sqls.addLast(h) ;
		}

		public void addImutableSql(String id, String ormName, String sqlStatement) {
			Temp_TSQL_Holder h = new Temp_TSQL_Holder() ;
			h.id = id ;
			h.ormName = ormName ;
			h.sqlStatement = sqlStatement ;
			
			sqls.addLast(h) ;
		}

		public CompiledSQL getSqlByStatement(ObjectMapping mapping, Object tableCondition, String sqlStatement, Map params) {
			throw new InvalidConfigurationException(Service.FAMOUSE_SERVICE.TEMPLATED_SQL + " service is required for templated sqls!") ;
		}

		public CompiledSQL getSqlByStatement(String ormName, Object tableCondition, String sqlStatement, Map params) {
			throw new InvalidConfigurationException(Service.FAMOUSE_SERVICE.TEMPLATED_SQL + " service is required for templated sqls!") ;
		}

		public boolean configure(ServiceConfig[] scs) {
			return false;
		}

		public boolean isAvailable() {
			return false;
		}

		public void startup() {			
		}

		public void shutdown() {
		}
		
	}
	
	private static class Temp_TSQL_Holder{
		
		public String id ;
		
		public ObjectMapping mapping ;
		
		public String ormName ;
		
		public String sqlStatement ;
		
	}

}
