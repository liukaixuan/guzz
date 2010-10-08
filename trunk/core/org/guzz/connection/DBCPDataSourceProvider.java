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

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

/**
 * 
 * Apache commons dbcp datasource provider.
 * 
 * <br>Configuration details: 
 * <a target="_blank" href="http://commons.apache.org/dbcp/configuration.html">http://commons.apache.org/dbcp/configuration.html</a>
 *
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DBCPDataSourceProvider implements DataSourceProvider{
	private static transient final Log log = LogFactory.getLog(DBCPDataSourceProvider.class) ;
	BasicDataSource dataSource = null ;
	
	public void configure(Properties props, int maxLoad){
		if(dataSource == null){
			dataSource = new BasicDataSource() ;
		}
		
		JavaBeanWrapper bw = BeanWrapper.createPOJOWrapper(dataSource.getClass()) ;
		Enumeration e = props.keys() ;
		while(e.hasMoreElements()){
			String key = (String) e.nextElement() ;
			String value = props.getProperty(key) ;
			
			try{
				bw.setValueAutoConvert(dataSource, key, value) ;
			}catch(Exception e1){
				log.error("unkown property:[" + key + "=" + value + "]", e1) ;
			}
		}
		
		//数据库最大连接500
		if(maxLoad > 1000 || maxLoad < 1){
			maxLoad = 500 ;
		}
		
		dataSource.setMaxActive(maxLoad) ;
		
		//fetch a connection to force the datasource building the pool
		Connection c = null ;
		try {
			c = dataSource.getConnection() ;
		} catch (SQLException e1) {
			log.error(props, e1) ;
		}finally{
			CloseUtil.close(c) ;
		}
	}

	public DataSource getDataSource() {
		return dataSource ;
	}

	public void shutdown() {
		if(dataSource != null){
			try {
				dataSource.close() ;
			} catch (SQLException e) {
				log.error("fail to shutdown the DBCPDataSource", e) ;
			}
			
			dataSource = null ;
		}
	}
	
	

}
