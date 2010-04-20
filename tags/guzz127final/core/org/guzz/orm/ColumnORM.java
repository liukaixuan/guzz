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

import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.type.SQLDataType;

public class ColumnORM{
	public final SQLDataType sqlDataType ;
	public final ColumnDataLoader columnDataLoader ;
	public final TableColumn tableColumn ;
	
	public ColumnORM(TableColumn tableColumn, SQLDataType type){
		this.tableColumn = tableColumn ;
		this.sqlDataType = type ;
		this.columnDataLoader = null ;
	}
	
	public ColumnORM(TableColumn tableColumn, ColumnDataLoader columnDataLoader){
		this.tableColumn = tableColumn ;
		this.sqlDataType = null ;
		this.columnDataLoader = columnDataLoader ;
	}
	
	public Object loadResult(ResultSet rs, Object objectFetching, int index) throws SQLException{
		if(columnDataLoader != null){
			return columnDataLoader.loadData(rs, objectFetching, index) ;
		}else{
			return sqlDataType.getSQLValue(rs, index) ;
		}
	}
	
}