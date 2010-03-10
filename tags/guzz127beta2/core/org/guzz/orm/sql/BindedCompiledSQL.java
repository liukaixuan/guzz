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
package org.guzz.orm.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.lang.NullValue;
import org.guzz.orm.mapping.MapDataLoader;
import org.guzz.orm.mapping.RowDataLoader;
import org.guzz.orm.type.SQLDataType;
import org.guzz.transaction.LockMode;

/**
 * 
 * 绑定完参数的 {@link CompiledSQL} ，这是临时对象，需要时生成，用完就消失。
 * 此对象方法不支持多线程操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BindedCompiledSQL {
	private static final Log log = LogFactory.getLog(BindedCompiledSQL.class) ;
	
	public static final RowDataLoader MAP_ROW_DATA_LOADER = new MapDataLoader() ;
	
	private CompiledSQL compiledSQL ;
	
	private Map bindedParams = new HashMap() ;
	
	private RowDataLoader rowDataLoader ;
	
	private boolean exceptionOnNoRecordFound ;
	
	private int bindStartIndex = 1 ;
	
	private LockMode lockMode ;
	
	/**the sql ready to execute in database.*/
	private String cachedSql ;
	
	private Object tableCondition ;
	
	public BindedCompiledSQL(CompiledSQL cs){
		this.compiledSQL = cs ;
	}
	
	public String getSql(){
		if(this.cachedSql != null){
			return this.cachedSql ;
		}else{
			if(tableCondition != null){
				this.cachedSql = this.compiledSQL.getSql(tableCondition) ;
			}else{
				this.cachedSql = this.compiledSQL.getSql(Guzz.getTableCondition()) ;
			}
			
			return this.cachedSql;
		}
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		if(paramValue == null){
			this.bindedParams.put(paramName, NullValue.instance) ;
		}else{
			bindedParams.put(paramName, paramValue) ;
		}
		
		return this ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		bindedParams.put(paramName, new Integer(paramValue)) ;
		return this ;
	}
	
	public BindedCompiledSQL bind(Map params){
		if(params == null) return this;
		if(params.isEmpty()) return this ;
		
		for (Iterator i = params.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            bind(e.getKey().toString(), e.getValue());
        }
		
		return this ;
	}
	
	public BindedCompiledSQL clearBindedParams(){
		bindedParams.clear() ;
		return this ;
	}
	
	/**
	 * 将命名参数set到PreparedStatement中
	 * 
	 * @param defaultDialect
	 * @param pstm PreparedStatement
	 */
	public void prepareNamedParams(Dialect dialect, PreparedStatement pstm) throws SQLException{
		String[] orderParams = compiledSQL.getOrderedParams() ;
		
		for(int i = 0 ; i < orderParams.length ; i++){
			String orderParam = orderParams[i] ;
			Object value = bindedParams.get(orderParam) ;
			
			if(value == null){
				throw new DaoException("missing paramter:[" + orderParam + "] in sql:" + getSql()) ;
			}
			
			//NEW Implemention to fix
			if(value instanceof NullValue){
				value = null ;
			}
			
			String propName = this.compiledSQL.getPropName(orderParam) ;
			
			if(propName != null){
				SQLDataType type = compiledSQL.getMapping().getSQLDataTypeOfProperty(propName) ;
				type.setSQLValue(pstm, i + bindStartIndex, value) ;
			}else{ //使用jdbc自己的方式绑定。
				if(log.isInfoEnabled()){
					log.info("bind named params without SQLDataType found, try CompiledSQL#addParamPropMapping(,) for better binding. bind param is:[" + orderParam + "], value is :[" + value + "]. sql is:" + getSql()) ;
				}
				
				pstm.setObject(i + bindStartIndex, value) ;
			}
			
			
//			//The code belowed warning: This IS a BUG!! null object is not supported!!! fix it to use ObjectMapping's SQLDataType for all cases.
//			
//			if(value instanceof NullValue){
//				//this method only works for pojo's insert/update/delete methods
//				SQLDataType type = compiledSQL.getMapping().getSQLDataTypeOfColumn(compiledSQL.getMapping().getColNameByPropName(orderParam)) ;
//				if(type != null){
//					type.setSQLValue(pstm, i + 1, null) ;
//				}else{
//					pstm.setObject(i + 1, null) ;
//				}
//			}else{
//				//this method cann't handle null value. So, we change to detect the ObjectMapping's type
//				SQLDataType type = dialect.getDataType(value.getClass().getName()) ;
//				type.setSQLValue(pstm, i + 1, value) ;
//			}
		}
	}

	public Map getBindedParams() {
		return bindedParams;
	}

	public CompiledSQL getCompiledSQL() {
		return compiledSQL;
	}

	public RowDataLoader getRowDataLoader() {
		return rowDataLoader;
	}

	/**
	 * 指定特殊的ORM策略，将临时覆盖在配置文件中配置默认ORM。
	 */
	public BindedCompiledSQL setRowDataLoader(RowDataLoader rowDataLoader) {
		this.rowDataLoader = rowDataLoader;
		return this ;
	}

	/**
	 * throw exception if record doesn't exsit in the database. the TranSession should return null if this value is false.
	 * <p>
	 * affect the findCell/findObject methods.
	 * </p>
	 */
	public boolean isExceptionOnNoRecordFound() {
		return exceptionOnNoRecordFound;
	}

	/**
	 * throw exception if record doesn't exsit in the database. the TranSession should return null if this value is false.
	 * <p>
	 * affect the findCell/findObject methods.
	 * </p>
	 */
	public BindedCompiledSQL setExceptionOnNoRecordFound(boolean exceptionOnNoRecordFound) {
		this.exceptionOnNoRecordFound = exceptionOnNoRecordFound;
		return this ;
	}

	/**
	 * set the first named param's index to bind. default is 1
	 */
	public void setBindStartIndex(int bindStartIndex) {
		this.bindStartIndex = bindStartIndex;
	}

	public LockMode getLockMode() {
		return lockMode;
	}

	public BindedCompiledSQL setLockMode(LockMode lockMode) {
		this.lockMode = lockMode;
		return this ;
	}

	public Object getTableCondition() {
		return tableCondition;
	}

	public BindedCompiledSQL setTableCondition(Object tableCondition) {
		this.tableCondition = tableCondition;
		this.cachedSql = null ;
		
		return this ;
	}

}
