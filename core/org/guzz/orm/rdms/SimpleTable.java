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
import org.guzz.id.IdentifierGenerator;
import org.guzz.util.ArrayUtil;

/**
 * 
 * 基本表。定义一般数据库表的结构，和具体使用的数据库如Oracle/Mysql等无关。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SimpleTable implements Table {
	
	private Object lock = new Object() ;
	
	private String tableName ;
	
	private boolean dynamicUpdate ;
	
	private String primaryKey ;
	
	private String PKPropName ;
	
	private IdentifierGenerator ig ;
	
	private List columns = new LinkedList() ;
	private Map propColumns = new HashMap() ;
	
	/** 用于guzz执行领域对象select操作的属性名称 */
	private String[] cache_columnsForSelect ;
	
	/** 用于guzz执行领域对象update操作的属性名称 */
	private String[] cache_columnsForUpdate ;
	
	private String[] cache_propsForUpdate ;
	
	/** 用于guzz执行领域对象insert操作的属性名称 */
	private String[] cache_columnsForInsert ;
	
	private String[] cache_lazyUpdatableProps ;
	
	private String[] cache_lazyProps ;
	
	private PersistListener[] persistListeners = new PersistListener[0] ;
	
	public SimpleTable(){
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPKColName() {
		return primaryKey;
	}

	public void setPKColName(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public IdentifierGenerator getIdentifierGenerator() {
		return ig;
	}
	
	public void setIdentifierGenerator(IdentifierGenerator ig){
		this.ig = ig ;
	}

	public String getPKPropName() {
		return PKPropName;
	}

	public void setPKPropName(String propName) {
		PKPropName = propName;
	}
		
	public void addColumn(TableColumn column){
		synchronized(lock){
			this.columns.add(column) ;
			this.propColumns.put(column.getPropName(), column) ;
			
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
	
	public String[] getColumnsForUpdate() {
		if(cache_columnsForUpdate == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isAllowUpdate() && col.getDataLoader() == null && !col.isLazy()){
						temp.addLast(col.getColName()) ;
					}
				}
				
				this.cache_columnsForUpdate = (String[]) temp.toArray(new String[0]) ;
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

	public String[] getColumnsForInsert() {
		if(cache_columnsForInsert == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					if(col.isAllowInsert() && col.getDataLoader() == null){
						temp.addLast(col.getColName()) ;
					}
				}
				
				this.cache_columnsForInsert = (String[]) temp.toArray(new String[0]) ;
			}
		}
		
		return cache_columnsForInsert;
	}
	
	public String[] getColumnsForSelect() {
		if(cache_columnsForSelect == null){
			synchronized(lock){
				LinkedList temp = new LinkedList() ;
				for(int i = 0 ; i < this.columns.size() ; i++){
					TableColumn col = (TableColumn) this.columns.get(i) ;
					
					//TODO: 在配置文件中增加select属性，如果select=false，则也不加入选择。用于
					//某些自定义加载数据源中根本没有对应数据库字段的属性。
					//如用户头像存储在文件中，数据库并不会设计一个portraitImgData空字段与之对应。	
					
					//反对观点：在这种情形下，自定义属性只能是lazy模式，否则无法加载到
					//（guzz没有办法知道那些属性需要加载，因为guzz执行的是sql语句，而不是属性查询语句。）
					//这是guzz的一个限制。除非设计类似hql的属性查询，否则并不能解决问题。
					
					//should not load lazy column.
					if(!col.isLazy()){
						temp.addLast(col.getColName()) ;
					}
				}
				
				this.cache_columnsForSelect = (String[]) temp.toArray(new String[0]) ;
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

}
