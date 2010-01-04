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

import org.guzz.exception.DataTypeException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.ObjectMapping.x$ORM;

/**
 * 
 * 以Object方式读取查询请求的第1列。使得ORM的结果为List<ColumnType>，如：List<Integer>等。
 *
 * TODO: support "null" attribute in hbm.xml, and finsih clob/blob loader, finish smallblob/smallclob SQLDataType
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FirstColumnDataLoader implements RowDataLoader {

	public Object rs2Object(ObjectMapping mapping, ResultSet rs) throws SQLException {
		ResultSetMetaData  meta = rs.getMetaData() ;
		String colName = meta.getColumnName(1) ;
		
		if(colName != null){
			try{
				x$ORM orm = mapping.getORMByColumn(colName) ;
				return orm.loadResult(rs, null, 1) ;
			}catch(DataTypeException e){
				//ignore and continue
			}
		}
		
		//TODO: using meta.getType() and dialect to find the SQLDataType.
		//including ObjectMapping implmentments.
		return rs.getObject(1) ;
	}

}
