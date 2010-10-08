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

import java.util.Properties;

import javax.sql.DataSource;

/**
 * 
 * Implementation to provide {@link DataSource} for guzz TranSession.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface DataSourceProvider {
	
	/**
	 * init or re-config the underly datasource.
	 * <p/>
	 * this method will be invoked at the startup, and may also be called by the configServer to re-config the settings(eg: reduce maxLoad as new db servers installed.).
	 * 
	 * @param props  the config properties from the configServer
	 * @param maxLoad usually means max database connections suggested.
	 */
	public void configure(Properties props, int maxLoad) ;
	
	/**
	 * fetch the datasource. This maybe called on every connection requiring, so make it fast! 
	 */
	public DataSource getDataSource() ;
	
	/**
	 * shutdown the pool.
	 */
	public void shutdown() ;

}
