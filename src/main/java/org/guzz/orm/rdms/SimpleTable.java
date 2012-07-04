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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guzz.dao.PersistListener;
import org.guzz.dialect.Dialect;
import org.guzz.id.IdentifierGenerator;
import org.guzz.orm.CustomTableView;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.util.ArrayUtil;

/**
 * 
 * 基本表。定义一般数据库表的结构，和具体使用的数据库如Oracle/Mysql等无关。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SimpleTable implements Table {
	
	private final Dialect dialect ;
	
	public SimpleTable(Dialect dialect){
		this.dialect = dialect ;
	}
	
	public Dialect getDialect(){
		return this.dialect ;
	}

	public String getTableName(Object tableCondition) {
		if(shadowTableView == null){
			return tableName ;
		}else{
			return shadowTableView.toTableName(tableCondition) ;
		}
	}

	public boolean isShadow() {
		return shadowTableView != null ;
	}
	
	public CustomTableView getCustomTableView(){
		return this.customTableView ;
	}
	
	public boolean isCustomTable(){
		return this.customTableView != null ;
	}
	
	public String getConfigTableName(){
		return tableName ;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		if(this.shadowTableView != null){
			this.shadowTableView.setConfiguredTableName(tableName) ;
		}
	}

	public IdentifierGenerator getIdentifierGenerator() {
		return ig;
	}
	
	public void setIdentifierGenerator(IdentifierGenerator ig){
		this.ig = ig ;
	}

	public TableColumn getPKColumn() {
		return pkColumn;
	}

	public String getPKPropName() {
		return pkColumn.getPropName() ;
	}

	public void addPKColumn(TableColumn pkColumn){
		this.pkColumn = pkColumn ;
		addColumn(pkColumn) ;
	}
	
	public TableColumn getVersionColumn() {
		return versionColumn;
	}

	public String getVersionPropName() {
		return versionColumn.getPropName() ;
	}

	public void addVersionColumn(TableColumn versionColumn){
		this.versionColumn = versionColumn ;
		addColumn(versionColumn) ;
	}
	
	public void addColumn(TableColumn column){
		synchronized(lock){
			this.columns.add(column) ;
			this.propColumns.put(column.getPropName(), column) ;
			
			//数据库的column名称不区分大小写。检索时全部按照小写检索。
			this.sqlColColumns.put(column.getColNameForSQL().toLowerCase(), column) ;
			this.rsColColumns.put(column.getColNameForRS().toLowerCase(), column) ;
			
			cache_columnsForUpdate = null ;
			cache_propsForUpdate = null ;
			cache_columnsForInsert = null ;
			cache_lazyUpdatableProps = null ;
			cache_lazyProps = null ;
		}
	}
	
	public void removeColumn(TableColumn column){
		synchronized(lock){
			this.columns.remove(column) ;
			this.propColumns.remove(column.getPropName()) ;
			
			//数据库的column名称不区分大小写。检索时全部按照小写检索。
			this.sqlColColumns.remove(column.getColNameForSQL().toLowerCase()) ;
			this.rsColColumns.remove(column.getColNameForRS().toLowerCase()) ;
			
			cache_columnsForUpdate = null ;
			cache_propsForUpdate = null ;
			cache_columnsForInsert = null ;
			cache_lazyUpdatableProps = null ;
			cache_lazyProps = null ;
		}
	}
	
	public TableColumn getColumnByPropName(String propName){
		return (TableColumn) this.propColumns.get(propName) ;
	}
	
	public TableColumn[] getColumnsForUpdate() {
		if(cache_columnsForUpdate == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isAllowUpdate() && col.getDataLoader() == null && !col.isLazy()){
						temp.addLast(col) ;
					}
				}
				
				this.cache_columnsForUpdate = (TableColumn[]) temp.toArray(new TableColumn[0]) ;
			}
		}
		
		return cache_columnsForUpdate;
	}
	
	public String[] getPropsForUpdate() {
		if(cache_propsForUpdate == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isAllowUpdate() && col.getDataLoader() == null && !col.isLazy()){
						temp.addLast(col.getPropName()) ;
					}
				}
				
				this.cache_propsForUpdate = (String[]) temp.toArray(new String[0]) ;
			}
		}
		
		return cache_propsForUpdate;
	}

	public TableColumn[] getColumnsForInsert() {
		if(cache_columnsForInsert == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isAllowInsert() && col.getDataLoader() == null){
						temp.addLast(col) ;
					}
				}
				
				this.cache_columnsForInsert = (TableColumn[]) temp.toArray(new TableColumn[0]) ;
			}
		}
		
		return cache_columnsForInsert;
	}
	
	public TableColumn[] getColumnsForSelect() {
		if(cache_columnsForSelect == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					//TODO: 在配置文件中增加select属性，如果select=false，则也不加入选择。用于
					//某些自定义加载数据源中根本没有对应数据库字段的属性。
					//如用户头像存储在文件中，数据库并不会设计一个portraitImgData空字段与之对应。	
					
					//反对观点：在这种情形下，自定义属性只能是lazy模式，否则无法加载到
					//（guzz没有办法知道那些属性需要加载，因为guzz执行的是sql语句，而不是hql属性查询语句。）
					//这是guzz的一个限制。除非设计类似hql的属性查询，否则并不能解决问题。
					
					//should not load lazy column.
					if(!col.isLazy()){
						temp.addLast(col) ;
					}
				}
				
				this.cache_columnsForSelect = (TableColumn[]) temp.toArray(new TableColumn[0]) ;
			}
		}
		
		return cache_columnsForSelect;
	}
	
	public String[] getLazyUpdateProps() {
		if(cache_lazyUpdatableProps == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isLazy() && col.isAllowUpdate()){
						temp.addLast(col.getPropName()) ;
					}
				}
				
				this.cache_lazyUpdatableProps = (String[]) temp.toArray(new String[0]) ;
			}
		}
		
		return cache_lazyUpdatableProps;
	}
	
	public String[] getLazyProps() {
		if(cache_lazyProps == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isLazy()){
						temp.addLast(col.getPropName()) ;
					}
				}
				
				this.cache_lazyProps = (String[]) temp.toArray(new String[0]) ;
			}
		}
		
		return cache_lazyProps;
	}

	public boolean hasLazy() {
		return getLazyUpdateProps().length > 0 ;
	}

	public boolean isDynamicUpdateEnable() {
		return dynamicUpdate;
	}

	public boolean isDynamicUpdate() {
		return dynamicUpdate;
	}

	public void setDynamicUpdate(boolean dynamicUpdate) {
		this.dynamicUpdate = dynamicUpdate;
	}
	
	public void addPersistListener(PersistListener listener){
		this.persistListeners = (PersistListener[]) ArrayUtil.addToArray(this.persistListeners, listener) ;
	}

	public PersistListener[] getPersistListeners() {
		return persistListeners;
	}

	public ShadowTableView getShadowTableView() {
		return shadowTableView;
	}

	public void setShadowTableView(ShadowTableView shadowTableView) {
		this.shadowTableView = shadowTableView;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public String getBusinessShape(){
		return MarkedSQL.TABLE_START_TAG_IN_MARKED_SQL + businessName ;
	}

	public void setCustomTableView(CustomTableView customTableView) {
		this.customTableView = customTableView;
	}
	

	public TableColumn getColumnByColNameInSQL(String colName) {
		return (TableColumn) this.sqlColColumns.get(colName.toLowerCase()) ;
	}
		
	public TableColumn getColumnByColNameInRS(String colName) {
		return (TableColumn) this.rsColColumns.get(colName.toLowerCase()) ;
	}
	
	private Object lock = new Object() ;
	
	private String tableName ;
	
	private boolean dynamicUpdate ;
	
//	private String primaryKey ;
	
//	private String PKPropName ;
	
	private TableColumn pkColumn ;
	
	private TableColumn versionColumn ;
	
	private IdentifierGenerator ig ;
	
	private List columns = new LinkedList() ;
	private Map propColumns = new HashMap() ;
	private Map rsColColumns = new HashMap() ;
	private Map sqlColColumns = new HashMap() ;
	
	/** 用于guzz执行领域对象select操作的属性名称 */
	private TableColumn[] cache_columnsForSelect ;
	
	/** 用于guzz执行领域对象update操作的属性名称 */
	private TableColumn[] cache_columnsForUpdate ;
	
	private String[] cache_propsForUpdate ;
	
	/** 用于guzz执行领域对象insert操作的属性名称 */
	private TableColumn[] cache_columnsForInsert ;
	
	private String[] cache_lazyUpdatableProps ;
	
	private String[] cache_lazyProps ;
	
	private PersistListener[] persistListeners = new PersistListener[0] ;
	
	private ShadowTableView shadowTableView ;
	
	private CustomTableView customTableView ;
	
	private String businessName ;
	
	public Table newCopy(){
		SimpleTable t = new SimpleTable(this.dialect) ;
		
		t.businessName = this.businessName ;
		
		t.rsColColumns = new HashMap() ;
		t.rsColColumns.putAll(this.rsColColumns) ;
		
		t.sqlColColumns = new HashMap() ;
		t.sqlColColumns.putAll(this.sqlColColumns) ;
		
		t.columns = new LinkedList() ;
		t.columns.addAll(this.columns) ;
		
		t.customTableView = this.customTableView ;
		t.dynamicUpdate = this.dynamicUpdate ;
		t.ig = this.ig ;
		t.lock = new Object() ;
		t.persistListeners = this.persistListeners ;
//		t.PKPropName = this.PKPropName ;
		t.pkColumn = this.pkColumn ;
		
		t.propColumns = new HashMap() ;
		t.propColumns.putAll(this.propColumns) ;
		
		t.shadowTableView = this.shadowTableView ;
		t.tableName = this.tableName ;
		
		return t ;
	}
	
}
