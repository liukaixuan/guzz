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
import java.sql.SQLException;
import java.util.Properties;

import org.guzz.dialect.Dialect;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.orm.type.SQLDataType;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.PropertyUtil;

/**
 * 
 * Mapping parameters supported: sequence, db_group
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SequenceIdGenerator implements IdentifierGenerator, Configurable {

	/** the seqence parameter */
	public static final String PARAM_SEQUENCE = "sequence" ;
	
	/** default seqence name */
	public static final String DEFAULT_SEQUENCE_NAME = "guzz_sequence" ;
	
	/** The dbGroup's name parameter. Default value is null telling guzz to use the same database of the table the id generated for. */
	public static final String DATABASE_GROUP_NAME = "db_group";
	
	private POJOBasedObjectMapping mapping ;
	private Table table ;
	private String primaryKeyPropName ;
	protected SQLDataType pkDataType ;
	private Class domainClass ;
	private String dbGroup ;
	
	private String selectSequenceClause = null ;
	
	protected void setPrimaryKey(Object domainObject, Object value){
		mapping.getBeanWrapper().setValue(domainObject, primaryKeyPropName, value) ;
	}
	
	protected Number nextSequenceValue(WriteTranSession session){
		JDBCTemplate t = null ;
		
		if(this.dbGroup == null){
			t = session.createJDBCTemplate(domainClass) ;
		}else{
			t = session.createJDBCTemplateByDbGroup(this.dbGroup) ;
		}
		
		return (Number) t.executeQueryWithoutPrepare(selectSequenceClause, 
				new SQLQueryCallBack(){
					public Object iteratorResultSet(ResultSet rs) throws Exception {
						if(rs.next()){
							return pkDataType.getSQLValue(rs, 1) ;
						}
						
						throw new SQLException("unknown sequenceCause:" + selectSequenceClause) ;
					}
				}
		) ;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject) {
		Serializable value = nextSequenceValue(session) ;
		
		setPrimaryKey(domainObject, value) ;
		
		return (Serializable) value ;
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
		this.domainClass = this.mapping.getBusiness().getDomainClass() ;
		
		String colName = table.getPKColName().toLowerCase() ;
		primaryKeyPropName = mapping.getPropNameByColName(colName) ;
		this.pkDataType = mapping.getSQLDataTypeOfColumn(colName) ;
		
		this.selectSequenceClause = dialect.getSelectSequenceClause(PropertyUtil.getString(params, PARAM_SEQUENCE, DEFAULT_SEQUENCE_NAME)) ;
		this.dbGroup = PropertyUtil.getString(params, DATABASE_GROUP_NAME, null) ;
	}

}
