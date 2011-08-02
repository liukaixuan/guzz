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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.connection.DataSourceProvider;
import org.guzz.connection.DataSourceProviderFactory;
import org.guzz.exception.GuzzException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.DatabaseService;
import org.guzz.util.ArrayUtil;
import org.guzz.util.lb.LBRound;
import org.guzz.util.lb.RoundCard;

/**
 * 
 * 从数据库服务。从数据库服务器中可能存在多个从数据库，{@link MultiMachinesDatabaseServiceImpl} 负责管理按照负载进行调度使用。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MultiMachinesDatabaseServiceImpl extends AbstractService implements DatabaseService {
	private static transient final Log log = LogFactory.getLog(MultiMachinesDatabaseServiceImpl.class) ;
		
	DataSourceProvider[] uniqueProviders = null ;
	
	private RoundCard card ;	
	
	/**
	 * @link ServiceConfig.uniqueIdentifer vs DataSourceProvicer
	 */
	private Map configProviders = new HashMap() ;
		
	public boolean isAvailable(){
		return card != null ;
	}
	
	public DataSource getDataSource() {
		DataSourceProvider p = (DataSourceProvider) card.getCard() ;
		if(p == null){
			return null ;
		}
		return p.getDataSource() ;
	}
	
	public synchronized boolean configure(ServiceConfig[] scs) {
		try {
			if(scs == null || scs.length == 0){
				this.card = null ;
				
				return false;
			}
			
			DataSourceProvider[] uniqueProviders = new DataSourceProvider[scs.length] ;
			
			LBRound lr = new LBRound() ;
			
			//创建数据源。如果以前申请过，仅仅调整配置参数，不再新建。
			Map newConfigProviders = new HashMap() ;
			
			for(int i = 0 ; i < scs.length ; i++){
				ServiceConfig sc = scs[i] ;
				int maxLoad = sc.getMaxLoad() ;
				
				//如果没有指定，或者超过1000，按照500算。
				if(maxLoad > 1000 || maxLoad < 1){
					maxLoad = 500 ;
					sc.setMaxLoad(maxLoad) ;
				}
				
				DataSourceProvider oldProvider = (DataSourceProvider) configProviders.get(sc.getUniqueIdentifer()) ;
				
				if(oldProvider == null){
					oldProvider = DataSourceProviderFactory.buildDataSourceProvicer(sc.getProps(), maxLoad) ;
				}else{
					oldProvider.configure(sc.getProps(), maxLoad) ;
				}
				
				uniqueProviders[i] = oldProvider ;
				newConfigProviders.put(sc.getUniqueIdentifer(), oldProvider) ;
				
				lr.addToPool(oldProvider, maxLoad) ;
			}
			
			lr.applyNewPool() ;
			//更新到当前使用中
			this.card = lr ;
			
			DataSourceProvider[] oldProviders = this.uniqueProviders ;			
			this.uniqueProviders = uniqueProviders ;
			
			//重新创建现有的configProviders
			configProviders.clear() ;
			this.configProviders = newConfigProviders ;
			
			//作废以前的连接池
			if(oldProviders != null){
				for(int i = 0 ; i < oldProviders.length ; i++){
					DataSourceProvider oldDsp = oldProviders[i] ;
					
					if(ArrayUtil.inArray(this.uniqueProviders, oldDsp)){ //新的连接中已经不再使用
						try{
							oldDsp.shutdown() ;
						}catch(Exception e){
							log.error("error whiling closing dataSourceProvider:" + oldDsp, e) ;
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new GuzzException("fail to set up  mulit database service.", e) ;
		}
		
		return true ;
	}
	
	public synchronized void shutdown() {
		if(this.uniqueProviders != null){
			if(log.isInfoEnabled()){
				log.info("shutting down service:[" + this.getServiceInfo().getServiceName() + "->" + this.configProviders.keySet() + "]...") ;
			}
			
			for(int i = 0 ; i < this.uniqueProviders.length ; i++){
				try{					
					this.uniqueProviders[i].shutdown() ;
				}catch(Exception e){
					log.error("fail to shutdown datasource provider:" + i, e) ;
				}
			}
		}
	}

	public void startup() {
		
	}

}
