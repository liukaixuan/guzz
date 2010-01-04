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
package org.guzz.service.core.impl;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.connection.DataSourceProvicer;
import org.guzz.connection.DataSourceProviderFactory;
import org.guzz.exception.GuzzException;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.DatabaseService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SingleMachineDatabaseServiceImpl extends AbstractService implements DatabaseService {
	private static transient final Log log = LogFactory.getLog(SingleMachineDatabaseServiceImpl.class) ;
	
	DataSourceProvicer dsp = null ;
	
	private String serviceIdentifer ;
	
	public boolean isAvailable(){
		return dsp != null ;
	}
	
	public DataSource getDataSource() {
		return dsp.getDataSource() ;
	}
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			throw new GuzzException("no serviceConfig found for single database.") ;
		}
		
		configure(scs[0]) ;
		
		return true ;
	}
	
	public void configure(ServiceConfig sc) {
		try {			
			//检测已经初始化过
			if(this.serviceIdentifer != null){
				if(this.serviceIdentifer.equals(sc.getUniqueIdentifer())){
					//同一个连接池更新属性
					this.dsp.configure(sc.getProps(), sc.getMaxLoad()) ;
				}else{
					//销毁上一个连接池，并应用新的连接池。
					DataSourceProvicer oldDsp = this.dsp ;
					
					DataSourceProvicer dsp = DataSourceProviderFactory.buildDataSourceProvicer(sc.getProps(), sc.getMaxLoad()) ;
					this.dsp = dsp ;
					this.serviceIdentifer = sc.getUniqueIdentifer() ;
					
					if(oldDsp != null){
						oldDsp.shutdown() ;
					}
				}
			}else{ //第一次初始化
				DataSourceProvicer dsp = DataSourceProviderFactory.buildDataSourceProvicer(sc.getProps(), sc.getMaxLoad()) ;
				this.dsp = dsp ;
				this.serviceIdentifer = sc.getUniqueIdentifer() ;
			}			
		} catch (Exception e) {
			throw new GuzzException("fail to set up  single database service.", e) ;
		}
	}

	public void shutdown() {
		dsp.shutdown() ;
	}

	public void startup() {
		
	}

}
