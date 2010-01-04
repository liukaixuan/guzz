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
package org.guzz.transaction;

import org.guzz.dialect.Dialect;
import org.guzz.service.core.DatabaseService;

/**
 * 
 * 数据源组。同一组数据库组内所有机器必须为同构数据库。一个数据组包含主数据库和从数据库2部分。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DBGroup {
	
	private String groupName ;
	
	private DatabaseService masterDB ;
	
	private DatabaseService slaveDB ;
	
	private Dialect dialect ;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public DatabaseService getMasterDB() {
		return masterDB;
	}

	public void setMasterDB(DatabaseService masterDB) {
		this.masterDB = masterDB;
	}

	public DatabaseService getSlaveDB() {
		return slaveDB;
	}

	public void setSlaveDB(DatabaseService slaveDB) {
		this.slaveDB = slaveDB;
	}

}
