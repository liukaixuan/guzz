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

/**
 * 
 * Shadow table means table partition, the table name will change on the giving condition.
 * <p>
 * Builded sqls should change itself on the condition changes.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ShadowTableView {

	/**
	 * retrieve the table name on the condition
	 * @param tableCondition shadow seed conditon
	 */
	public String toTableName(Object tableCondition) ;
	
	/**
	 * set the table name configured in the hbm.xml file. 
	 */
	public void setConfiguredTableName(String tableName) ;
	
	public void startup() ;
	
	public void shutdown() throws Exception ;
	
}
