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
package org.guzz.service.core.impl;

import java.io.Serializable;

/**
 * 
 * 用于进行异步更新的领域对象。不支持shadow。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IncUpdateBusiness implements Serializable{

	private long id ;

	/**要更新表所在的数据库分组*/
	private String dbGroup ;
	
	private String tableName ;
	
	private String columnToUpdate ;
	
	private String pkColunName ;
	
	private String pkValue ;
	
	private int countToInc ;
	
	public IncUpdateBusiness(){
	}
	
	public IncUpdateBusiness(String dbGroup){
		this.dbGroup = dbGroup ;
	}
	
	/**
	 * 对当前更新操作，增加给定的值。线程安全方法。
	 */
	public synchronized void incCount(int incValue){
		countToInc += incValue ;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnToUpdate() {
		return columnToUpdate;
	}

	public void setColumnToUpdate(String columnToUpdate) {
		this.columnToUpdate = columnToUpdate;
	}

	public String getPkColunName() {
		return pkColunName;
	}

	public void setPkColunName(String pkColunName) {
		this.pkColunName = pkColunName;
	}

	public String getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	public int getCountToInc() {
		return countToInc;
	}

	public void setCountToInc(int countToInc) {
		this.countToInc = countToInc;
	}

	public String getDbGroup() {
		return dbGroup;
	}

	public void setDbGroup(String dbGroup) {
		this.dbGroup = dbGroup;
	}	
	
}
