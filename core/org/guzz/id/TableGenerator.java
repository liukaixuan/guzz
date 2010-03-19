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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.dialect.Dialect;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.orm.type.SQLDataType;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.PropertyUtil;
import org.guzz.web.context.GuzzContextAware;

/**
 * An <tt>IdentifierGenerator</tt> that uses a database
 * table to store the last generated value. It is not
 * intended that applications use this strategy directly.
 * However, it may be used to build other (efficient)
 * strategies. The returned type is <tt>Integer</tt>.<br>
 * <br>
 * The hi value MUST be fetched in a seperate transaction
 * to the <tt>Session</tt> transaction so the generator must
 * be able to obtain a new connection and commit it. 
 * <br>
 * The returned value is of type <tt>integer</tt>.<br>
 * <br>
 * Mapping parameters supported: table, column, dbGroup
 *
 * @see TableHiLoGenerator
 */
public abstract class TableGenerator implements IdentifierGenerator, Configurable, GuzzContextAware {
	private static final Log log = LogFactory.getLog(TableGenerator.class);
	
	/* COLUMN and TABLE should be renamed but it would break the public API of hibernate */
	/** The column parameter */
	public static final String COLUMN = "column";
	
	/** Default column name */
	public static final String DEFAULT_COLUMN_NAME = "next_hi";
	
	/** The table parameter */
	public static final String TABLE = "table";
	
	/** The dbGroup's name parameter. Default value is null telling guzz to use the same database of the table the id generated for. */
	public static final String DATABASE_GROUP_NAME = "dbGroup";
	
	/** Default table name */	
	public static final String DEFAULT_TABLE_NAME = "guzz_unique_key";

	private String tableName;
	private String columnName;
	private String query;
	private String update;
	
	private POJOBasedObjectMapping mapping ;
	private Table table ;
	private String primaryKeyPropName ;
	protected SQLDataType pkDataType ;
	private Class domainClass ;	
	
	private String dbGroup ;

	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.mapping = mapping ;
		this.table = mapping.getTable() ;
		this.domainClass = this.mapping.getBusiness().getDomainClass() ;
		
		String colName = table.getPKColName().toLowerCase() ;
		primaryKeyPropName = mapping.getPropNameByColName(colName) ;
		this.pkDataType = mapping.getSQLDataTypeOfColumn(colName) ;
		
		
		tableName = PropertyUtil.getString(params, TABLE, DEFAULT_TABLE_NAME);
		columnName = PropertyUtil.getString(params, COLUMN, DEFAULT_COLUMN_NAME);
		dbGroup = PropertyUtil.getString(params, DATABASE_GROUP_NAME, null);

		query = "select " + columnName + " from " + tableName ;
		
		query = dialect.getForUpdateString(query) ;
		
		update = "update " + 
			tableName + 
			" set " + 
			columnName + 
			" = ? where " + 
			columnName + 
			" = ?";
		
		if(log.isDebugEnabled()){
			log.debug("query:[" + query + "], update:[" + update + "]") ;
		}
	}
	
	protected void setPrimaryKey(Object domainObject, Object value){
		mapping.getBeanWrapper().setValue(domainObject, primaryKeyPropName, value) ;
	}

	public Object generatorKey() {
		return tableName;
	}

	public Integer nextValueInTable(WriteTranSession session){		
		int result;
		int rows;
		do {
			// The loop ensures atomicity of the
			// select + update even for no transaction
			// or read committed isolation level
		
			JDBCTemplate t = null ;
			
			if(dbGroup == null){
				t = session.createJDBCTemplate(domainClass) ;
			}else{
				t = session.createJDBCTemplateByDbGroup(this.dbGroup) ;
			}
			
			result = ((Number) t.executeQuery(query, 
					new SQLQueryCallBack(){
						public Object iteratorResultSet(ResultSet rs) throws Exception {
							if(rs.next()){
								return pkDataType.getSQLValue(rs, 1) ;
							}
							
							throw new SQLException("could not read a hi value - you need to populate the table: " + tableName) ;
						}
					}
			)).intValue() ;

			rows = t.executeUpdate(update, new int[]{result+1, result}) ;
		}while (rows==0);
		
		return new Integer(result);
	}
	
	public Serializable postInsert(WriteTranSession session, Object domainObject) {
		return null ;
	}

	public boolean insertWithPKColumn() {
		return true ;
	}
}
