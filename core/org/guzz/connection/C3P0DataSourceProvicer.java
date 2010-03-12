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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class C3P0DataSourceProvicer implements DataSourceProvicer{
	private static transient final Log log = LogFactory.getLog(C3P0DataSourceProvicer.class) ;
	ComboPooledDataSource c3p0 = null ;
	
	public void configure(Properties props, int maxLoad){
		if(c3p0 == null){
			c3p0 = new ComboPooledDataSource() ;
		}
		
		JavaBeanWrapper bw = BeanWrapper.createPOJOWrapper(c3p0.getClass()) ;
		Enumeration e = props.keys() ;
		while(e.hasMoreElements()){
			String key = (String) e.nextElement() ;
			String value = props.getProperty(key) ;
			
			try{
				bw.setValueAutoConvert(c3p0, key, value) ;
			}catch(Exception e1){
				log.error("unkown property:[" + key + "=" + value + "]", e1) ;
			}
		}
		
		//数据库最大连接500
		if(maxLoad > 1000 || maxLoad < 1){
			maxLoad = 500 ;
		}
		
		c3p0.setMaxPoolSize(maxLoad) ;
		
		//fetch a connection to force c3p0 building the pool
		Connection c = null ;
		try {
			c = c3p0.getConnection() ;
		} catch (SQLException e1) {
			log.error(props, e1) ;
		}finally{
			CloseUtil.close(c) ;
		}
	}

	public DataSource getDataSource() {
		return c3p0 ;
	}

	public void shutdown() {
		if(c3p0 != null){
			c3p0.close() ;
			c3p0 = null ;
		}
	}
	
	

}
