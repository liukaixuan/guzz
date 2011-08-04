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
package org.guzz.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Service;
import org.guzz.exception.GuzzException;
import org.guzz.io.Resource;
import org.guzz.service.ServiceConfig;
import org.guzz.util.PropertyUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * 通过读取配置文件完成配置服务的ConfigServer。
 * 配置文件为一个properties文件，类似mysql的my.cnf文件，通过[xxx]进行分组。<br>
 * 例如：<pre>
 * [masterDB]
 * guzz.identifer=masterDB_localhost_3306
 * guzz.IP=localhost
 * guzz.maxLoad=100
 * driverClass=org.h2.Driver
 * jdbcUrl=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
 * user=sa
 * password=
 * acquireIncrement=10
 * idleConnectionTestPeriod=60
 * 
 * [slaveDB]
 * guzz.identifer=db_45_36_3306
 * guzz.IP=192.168.45.36
 * guzz.maxLoad=100
 * driverClass=org.h2.Driver
 * jdbcUrl=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
 * user=sa
 * password=testsa
 * acquireIncrement=10
 * idleConnectionTestPeriod=60
 * 
 * 
 * [slaveDB]
 * guzz.identifer=db_45_37_3306
 * guzz.IP=192.168.45.37
 * guzz.maxLoad=80
 * driverClass=org.h2.Driver
 * jdbcUrl=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
 * user=sa
 * password=sa
 * acquireIncrement=10
 * idleConnectionTestPeriod=60
 * 
 * </pre>
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LocalFileConfigServer implements ConfigServer {
	private transient static final Log log = LogFactory.getLog(LocalFileConfigServer.class) ;
	
	//serviceName vs SeriviceConfig[]
	private Map configs = new HashMap() ;
		
	public void setResource(Resource r){
		this.addResource(r, true) ;
	}
		
	public void setResource1(Resource r){
		this.addResource(r, true) ;
	}
		
	public void setResource2(Resource r){
		this.addResource(r, true) ;
	}
		
	public void setResource3(Resource r){
		this.addResource(r, true) ;
	}
	
	/**
	 * Optional resource. The resource can be available or not.
	 */
	public void setOptionalResource(Resource r){
		this.addResource(r, false) ;
	}
	
	public void setOptionalResource1(Resource r){
		this.addResource(r, false) ;
	}
	
	public void setOptionalResource2(Resource r){
		this.addResource(r, false) ;
	}
	
	public void setOptionalResource3(Resource r){
		this.addResource(r, false) ;
	}
	
	protected void addResource(Resource r, boolean resourceMustBeValid){
		Map props = PropertyUtil.loadGroupedProps(r) ;
		
		if(props == null){
			if(resourceMustBeValid){
				throw new GuzzException("cann't load resource:" + r) ;
			}else{
				log.info("optional resource not found[ignore it]. resource is [" + r + ']') ;
				return ;
			}
		}
		
		Iterator keys = props.entrySet().iterator() ;
		
		while(keys.hasNext()){
			Map.Entry entry = (Entry) keys.next() ;
			
			String configGroupName = (String) entry.getKey() ;
			Properties[] ps = (Properties[]) entry.getValue() ;
			
			ServiceConfig[] scs = new ServiceConfig[ps.length] ;
			for(int i = 0 ; i < ps.length ; i++){
				Properties p = ps[i] ;
				ServiceConfig sc = new ServiceConfig() ;
				scs[i] = sc ;
				
				sc.setConfigName(configGroupName) ;
				
				//读取系统配置项uniqueIdentifer appName serviceName IP maxLoad
				//剩下作为附件参数
				
				sc.setUniqueIdentifer(p.getProperty("guzz.identifer")) ;
				sc.setIP(p.getProperty("guzz.IP")) ;
				sc.setMaxLoad(StringUtil.toInt(p.getProperty("guzz.maxLoad"), -1)) ;
				sc.setAppName(p.getProperty("guzz.appName")) ;
				
				//删除系统配置项
				Iterator pe = p.keySet().iterator() ;
				while(pe.hasNext()){
					String pe_key = (String) pe.next() ;
					if(pe_key.startsWith("guzz.")){
						pe.remove() ;
					}
				}
				
				sc.setProps(p) ;
			}
			
			if(this.configs.containsKey(configGroupName)){
				log.warn("config:[" + configGroupName + "] overrided with resource :" + r) ;
			}
			
			configs.put(configGroupName, scs) ;
		}
	}

	public ServiceConfig[] queryConfig(String configName) throws IOException {
		ServiceConfig[] scs = (ServiceConfig[]) configs.get(configName) ;
		
		if(scs == null){
			scs = new ServiceConfig[0] ;
			if(log.isWarnEnabled()){
				log.warn("no configuration found for config:" + configName) ;
			}
		}
		
		//Must return a cloned copy to support duplicated queries.
		ServiceConfig[] newscs = new ServiceConfig[scs.length] ;
		for(int i = 0 ; i < scs.length ; i++){
			ServiceConfig ns = (ServiceConfig) scs[i].clone() ;
			newscs[i] = ns ;
		}
		
		return newscs ;
	}

	public void registerService(String configName, Service service) {
		//never notify others
	}
	
	public void shutdown(){
		configs.clear() ;
	}

	public void startup() {
		
	}

}

















