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
package org.guzz.orm.rdms;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.guzz.dao.PersistListener;
import org.guzz.dialect.Dialect;
import org.guzz.id.IdentifierGenerator;
import org.guzz.orm.CustomTableView;

/**
 * 
 * 数据库表。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface Table {
	
	/**
	 * get the table name. if the table is not a shadow one, pass null as the parameter.
	 */
	public String getTableName(Object tableCondition) ;
	
	public boolean isShadow() ;
	
	public CustomTableView getCustomTableView() ;
	
	public boolean isCustomTable() ;
	
	/**
	 * return the configured tabled name 
	 * 
	 */
	public String getConfigTableName() ;
	
	public TableColumn getPKColumn() ;
	
//	public String getPKColName() ;
//	
//	public String getPKPropName() ;
	
	public IdentifierGenerator getIdentifierGenerator() ;	

	public TableColumn[] getColumnsForInsert() ;
	
	public TableColumn[] getColumnsForUpdate() ;
	
	/**
	 * 不包含lazy字段。
	 */
	public String[] getPropsForUpdate() ;
	
	public TableColumn[] getColumnsForSelect() ;
	
	/**
	 * props can be updated to db, and is lazily loaded.
	 */
	public String[] getLazyUpdateProps() ;
	
	public String[] getLazyProps() ;
	
	public boolean hasLazy() ;
	
	public boolean isDynamicUpdateEnable() ;
	
	public TableColumn getColumnByPropName(String propName) ;
	
	public TableColumn getColumnByColNameInSQL(String colName) ;
	
	/**
	 * Get the {@link TableColumn} by the meta name({@link ResultSetMetaData#getColumnLabel(int)}) in the {@link ResultSet}.
	 * <br>
	 * 
	 * @param colName column meta name
	 * @return return null if not found.
	 */
	public TableColumn getColumnByColNameInRS(String colName) ;

	public PersistListener[] getPersistListeners() ;
	
	public void addPersistListener(PersistListener listener) ;
	
	/**
	 * 获得对象形式的表名称。如：@@user
	 */
	public String getBusinessShape() ;
	
	public void addColumn(TableColumn column) ;
	
	/**
	 * Remove a column from the current table mapping.
	 */
	public void removeColumn(TableColumn column) ;
	
	public Dialect getDialect() ;
	
	public Table newCopy() ;

	public String getPKPropName() ;
}
