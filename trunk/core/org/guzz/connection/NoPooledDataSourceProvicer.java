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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.guzz.exception.InvalidConfigurationException;

/**
 * 
 * Datasource without database connections pool. Fetch connection on demand, and release it on close.
 * <p/>
 * This Provider accepts 4 parameters: driverClass, jdbcUrl, user and password.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NoPooledDataSourceProvicer implements DataSourceProvicer{
	protected DirectConnDataSource ds = null ;
	
	public void configure(Properties props, int maxLoad){
		ds = new DirectConnDataSource(props) ;
	}

	public DataSource getDataSource() {
		return ds ;
	}

	public void shutdown() {
	}
	
	static class DirectConnDataSource implements DataSource{		
		private final String jdbcUrl ;
		private final String user ;
		private final String password ;
						
		private int loginTimeout ;
		private PrintWriter pw ;
		
		public DirectConnDataSource(Properties props){
			String driverClass = props.getProperty("driverClass") ;
			
			try{
				Class.forName(driverClass).newInstance() ;
			}catch(Exception e){
				throw new InvalidConfigurationException("unknown driverClass:[" + driverClass + "] for nopool datasource.", e) ;
			}
			
			this.jdbcUrl = props.getProperty("jdbcUrl") ;
			this.user = props.getProperty("user") ;
			this.password = props.getProperty("password") ;
		}

		public Connection getConnection() throws SQLException {
			return DriverManager.getConnection(jdbcUrl, user, password) ;
		}

		public Connection getConnection(String username, String password) throws SQLException {
			return DriverManager.getConnection(jdbcUrl, username, password) ;
		}

		public PrintWriter getLogWriter() throws SQLException {
			return pw;
		}

		public int getLoginTimeout() throws SQLException {
			return loginTimeout ;
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			this.pw = out ;
		}

		public void setLoginTimeout(int seconds) throws SQLException {
			this.loginTimeout = seconds ;
		}

		/**
		 * not supported.
		 * @since 1.6
		 */
		public boolean isWrapperFor(Class iface) throws SQLException {
			return false;
		}

		/**
		 * not supported.
		 * @since 1.6
		 */
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}		
	}

}
