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
package org.guzz.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.GuzzContext;
import org.guzz.exception.DaoException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.transaction.WriteTranSession;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * user-defined loader to load specified property of a domain object. eg: load a property value from the file system.
 * <p/>
 * per column mapping per instance.
 * <p/>
 * startup sequences:
 * <ol>
 * <li>loader = XXXClass.newInstance()</li>
 * <li>loader.configure(Table table, String propName, String columnName)</li>
 * <li>.....</li>
 * <li>injected {@link GuzzContext} based on implmenting {@link GuzzContextAware} or not</li>
 * <li>loader.startup()</li>
 * <li>....</li>
 * <li>loader.shutdown()</li>
 * </ol>
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ColumnDataLoader {
	
	/**
	 * config the property info.
	 * 
	 * @param table fetch Object stored table.
	 * @param propName property name
	 * @param columnName the corrsponding columnName of the property.
	 */
	public void configure(ObjectMapping mapping, Table table, String propName, String columnName) ;
	
	/**
	 * load the data instancely during other properties reading from the dabase.
	 * 
	 * @param rs The current resultset. the resultset(and connection) will be closed after all properties are loaded. Your returning value cann't rely on this for future usage.
	 * @param objectFetching The object being orm. the property before this property in the hbm.xml config file is already setted, so you can use it here. this param could be null on loading with something like org.guzz.orm.mapping.FirstColumnDataLoader.
	 * @param indexToLoad the propName index in the resultset.
	 * @return the returned object will be set to the pojo property.
	 */
	public Object loadData(ResultSet rs, Object objectFetching, int indexToLoad) throws SQLException ;
	
	/**
	 * eagerly load the lazied property for read. invoked by pojo.getXXX()
	 * <p/>
	 * guzz would never know what you have done to fetch the property, so it could <b>NOT</b> help you release any related resources acquired. 
	 * 
	 * @param fetchedObject the already loaded pojo.
	 * 
	 * @return the loaded object. the object will be setted to the fetchedObject automatically.
	 */
	public Object loadLazyData(Object fetchedObject) ;

	/**
	 * eagerly load the lazied property for write inside a read-write transaction.
	 * <p/>
	 * guzz would never know what you have done to fetch the property, so it could <b>NOT</b> help you release any related resources acquired. 
	 * 
	 * @param tran the current opened read-write database transactional environment.
	 * @param fetchedObject the already loaded pojo.
	 * 
	 * @return the loaded object. the object will <b>NOT</b> be setted to the fetchedObject automatically.
	 * @exception DaoException throw exception on @param fetchedObject doesn't exsit in the database.
	 */
	public Object loadLazyDataForWrite(WriteTranSession tran, Object fetchedObject) throws DaoException ;
	
	public void startup() ;
	
	public void shutdown() throws Exception ;
		
}
