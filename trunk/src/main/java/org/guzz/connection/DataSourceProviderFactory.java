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
package org.guzz.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class DataSourceProviderFactory {
	
	/**
	 * system datasource providers.
	 */
	private final static Map systemProviders = new HashMap() ;
	
	static{
		systemProviders.put("c3p0", C3P0DataSourceProvider.class.getName()) ;
		systemProviders.put("dbcp", DBCPDataSourceProvider.class.getName()) ;
		systemProviders.put("nopool", NoPooledDataSourceProvider.class.getName()) ;
	}
	
	public static DataSourceProvider buildDataSourceProvicer(Properties props, int maxLoad){
		String provider = (String) props.remove("pool") ;
		if(StringUtil.isEmpty(provider)){
			provider = "c3p0" ;
		}
		
		String providerClass = (String) systemProviders.get(provider) ;
		if(providerClass == null){
			providerClass = provider ;
		}
		
		DataSourceProvider ds = (DataSourceProvider) BeanCreator.newBeanInstance(providerClass) ;
		ds.configure(props, maxLoad) ;
		return ds ;
	}

}
