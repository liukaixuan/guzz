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
package org.guzz.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.ContextLifeCycle;
import org.guzz.GuzzContext;
import org.guzz.dao.PersistListener;
import org.guzz.exception.DaoException;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
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
 * <li>loader.configure(ObjectMapping mapping, Table table, TableColumn tableColumn)</li>
 * <li>.....</li>
 * <li>injected {@link GuzzContext} based on implementing {@link GuzzContextAware} or not</li>
 * <li>loader.startup()</li>
 * <li>....</li>
 * <li>loader.shutdown()</li>
 * </ol>
 * 
 * <p>The ColumnDataLoader can implement the interface of {@link PersistListener} to performing more advanced operations.<p>
 * 
 * @see PersistListener
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ColumnDataLoader extends ContextLifeCycle {
	
	/**
	 * configure the loader.
	 * 
	 * @param mapping
	 * @param table fetch Object stored table.
	 * @param tableColumn the column to be loaded. The passed tableColumn is not fully inited, the orm and dataType will not be available.
	 */
	public void configure(ObjectMapping mapping, Table table, TableColumn tableColumn) ;
	
	/**
	 * load the data immediately during other properties reading from the database.
	 * 
	 * @param rs The current ResultSet. the ResultSet(and connection) will be closed after all properties are loaded. Your returning value cann't rely on this for future usage.
	 * @param objectFetching The object being orm. the property before this property in the hbm.xml config file is already set, so you can use it here. this param could be null on loading with something like org.guzz.orm.mapping.FirstColumnDataLoader.
	 * @param indexToLoad the propName index in the ResultSet. The first column is 1, the second column is 2, ...
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
	 * @return the loaded object. the object will be set to the fetchedObject automatically.
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
	 * @return the loaded object. the object will <b>NOT</b> be set to the fetchedObject automatically.
	 * @exception DaoException throw exception on @param fetchedObject doesn't exist in the database.
	 */
	public Object loadLazyDataForWrite(WriteTranSession tran, Object fetchedObject) throws DaoException ;
			
}
