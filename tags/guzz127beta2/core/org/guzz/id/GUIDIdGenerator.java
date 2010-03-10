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
package org.guzz.id;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Properties;

import org.guzz.dialect.Dialect;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * Generate <b>string</b> primary key using the database's uuid() or similar function.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GUIDIdGenerator implements IdentifierGenerator, Configurable {
	private POJOBasedObjectMapping mapping ;
	private Table table ;
	private String primaryKeyPropName ;
	
	private String selectSequenceClause = null ;
	
	protected void setPrimaryKey(Object domainObject, Object value){
		mapping.getBeanWrapper().setValue(domainObject, primaryKeyPropName, value) ;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject) {
		JDBCTemplate t = session.createJDBCTemplate(domainObject.getClass()) ;
		
		String guid = (String) t.executeQueryWithoutPrepare(selectSequenceClause, 
				new SQLQueryCallBack(){
					public Object iteratorResultSet(ResultSet rs) throws Exception {
						rs.next() ;
						return rs.getString(1) ;
					}
				}
		) ;
		
		setPrimaryKey(domainObject, guid) ;
		
		return (Serializable) guid ;
	}
	
	public Serializable postInsert(WriteTranSession session, Object domainObject) {
		return null ;
	}

	public boolean insertWithPKColumn() {
		return true;
	}
	
	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.mapping = mapping ;
		this.table = mapping.getTable() ;
		
		String colName = table.getPKColName().toLowerCase() ;
		primaryKeyPropName = mapping.getPropNameByColName(colName) ;
		
		this.selectSequenceClause = dialect.getSelectGUIDClause() ;
	}

}
