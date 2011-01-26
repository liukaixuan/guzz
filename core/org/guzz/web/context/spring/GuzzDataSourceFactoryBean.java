/*
 * Copyright 2008-2011 the original author or authors.
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
package org.guzz.web.context.spring;

import javax.sql.DataSource;

import org.guzz.GuzzContext;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.exception.GuzzException;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 
 * Helper class to retrieve guzz's dataSource in the spring bean.
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GuzzDataSourceFactoryBean extends AbstractFactoryBean {
	
	private GuzzContext guzzContext ;
		
	private String dbGroup = "default" ;
	
	private Object tableCondition ;
	
	private boolean masterDB ;
	
	private boolean slaveDB ;

	protected Object createInstance() throws Exception {
		if(guzzContext == null){
			guzzContext = (GuzzContext) this.getBeanFactory().getBean("guzzContext") ;
		}
		
		if(guzzContext == null){
			throw new GuzzException("guzzContext not found. put guzzContext bean in front of this bean.") ;
		}
		
		PhysicsDBGroup group = guzzContext.getDBGroup(dbGroup).getPhysicsDBGroup(tableCondition) ;
		
		if(masterDB){ //force master
			if(group.getMasterDB() == null){
				throw new GuzzException("dbgroup [" + this.dbGroup + "] has no masterDB.") ;
			}else{
				return group.getMasterDB().getDataSource() ;
			}
		}else if(slaveDB){ //force slave
			if(group.getSlaveDB() == null){
				throw new GuzzException("dbgroup [" + this.dbGroup + "] has no slaveDB.") ;
			}else{
				return group.getSlaveDB().getDataSource() ;
			}
		}else{ //auto. master first, then slave, or raise a exception.
			if(group.getMasterDB() != null){
				return group.getMasterDB().getDataSource() ;
			}else if(group.getSlaveDB() != null){
				return group.getSlaveDB().getDataSource() ;
			}else{
				throw new GuzzException("dbgroup [" + this.dbGroup + "] has no dataSource.") ;
			}
		}
	}

	public Class getObjectType() {
		return DataSource.class ;
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

	public String getDbGroup() {
		return dbGroup;
	}

	public void setDbGroup(String dbGroup) {
		this.dbGroup = dbGroup;
	}

	public Object getTableCondition() {
		return tableCondition;
	}

	public void setTableCondition(Object tableCondition) {
		this.tableCondition = tableCondition;
	}

	public boolean isMasterDB() {
		return masterDB;
	}

	public void setMasterDB(boolean masterDB) {
		this.masterDB = masterDB;
	}

	public boolean isSlaveDB() {
		return slaveDB;
	}

	public void setSlaveDB(boolean slaveDB) {
		this.slaveDB = slaveDB;
	}

}
