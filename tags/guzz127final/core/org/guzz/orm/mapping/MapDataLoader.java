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
package org.guzz.orm.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import org.guzz.orm.ObjectMapping;

/**
 * 
 * 将JDBC返回的数据直接做成Map，JDBC的列名称为key，每一行的值为value。
 * <p>
 * Map&lt;columnName, ResultSet.getObject()&gt;
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MapDataLoader implements RowDataLoader {

	public Object rs2Object(ObjectMapping mapping, ResultSet rs) throws SQLException {
		ResultSetMetaData  meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		
		HashMap map = new HashMap() ;
		
		for(int i = 1 ; i <= count ; i++){
			String colName = meta.getColumnName(i) ;
			Object value = rs.getObject(i) ;
			
			map.put(colName, value) ;
		}		
		
		return map ;
	}
	
}
