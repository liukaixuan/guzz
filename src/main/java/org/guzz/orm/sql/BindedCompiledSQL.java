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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.lang.NullValue;
import org.guzz.orm.mapping.FormBeanRowDataLoader;
import org.guzz.orm.mapping.RowDataLoader;
import org.guzz.orm.type.SQLDataType;
import org.guzz.transaction.LockMode;

/**
 * 
 * A executive version of {@link CompiledSQL} with binded parameters.
 * <p>
 * BindedCompiledSQL should be used as a temporary object, never try to cache it.
 * All methods in this class are not thread-safe.
 * </p>
 *
 * @see CompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class BindedCompiledSQL {
	private static final Log log = LogFactory.getLog(BindedCompiledSQL.class) ;
	
	/**
	 * A instance of {@link FormBeanRowDataLoader} maps ResultSet to a {@link HashMap}.
	 */
	public static final RowDataLoader MAP_ROW_DATA_LOADER = FormBeanRowDataLoader.newInstanceForClass(HashMap.class) ;
	
	private Map bindedParams = new HashMap() ;
	
	private RowDataLoader rowDataLoader ;
	
	private boolean exceptionOnNoRecordFound ;
	
	private int bindStartIndex = 1 ;
	
	private LockMode lockMode ;
		
	private Object tableCondition ;	
	
	protected Class resultClass ;
	
	public abstract NormalCompiledSQL getCompiledSQLToRun() ;
	
	public abstract String getSQLToRun() ;
	
	public PhysicsDBGroup getPhysicsDBGroup(){
		return getCompiledSQLToRun().getMapping().getDbGroup().getPhysicsDBGroup(getTableCondition()) ;
	}
	
	protected BindedCompiledSQL(Class resultClass){
		this.resultClass = resultClass ;
	}
	
	
	/**
	 * 将命名参数set到PreparedStatement中
	 * 
	 * @param dialect dialect
	 * @param pstm PreparedStatement
	 */
	public void prepareNamedParams(Dialect dialect, PreparedStatement pstm) throws SQLException{
		NormalCompiledSQL cs = getCompiledSQLToRun() ;
		
		String[] orderParams = cs.getOrderedParams() ;
		
		for(int i = 0 ; i < orderParams.length ; i++){
			String orderParam = orderParams[i] ;
			Object value = bindedParams.get(orderParam) ;
			
			if(value == null){
				throw new DaoException("missing parameter:[" + orderParam + "] in sql:" + getSQLToRun()) ;
			}
			
			//NEW Implemention to fix
			if(value instanceof NullValue){
				value = null ;
			}
			
			SQLDataType type = cs.getSQLDataTypeForParam(cs, orderParam) ;
			
			if(type != null){
				type.setSQLValue(pstm, i + bindStartIndex, value) ;
			}else{ //使用jdbc自己的方式绑定。
				if(log.isInfoEnabled()){
					log.info("bind named params without SQLDataType found, try CompiledSQL#addParamPropMapping(,) for better binding. bind param is:[" + orderParam + "], value is :[" + value + "]. sql is:" + getSQLToRun()) ;
				}
				
				pstm.setObject(i + bindStartIndex, value) ;
			}
		}
		
		
//		//The code belowed warning: This IS a BUG!! null object is not supported!!! fix it to use ObjectMapping's SQLDataType for all cases.
//		
//		if(value instanceof NullValue){
//			//this method only works for pojo's insert/update/delete methods
//			SQLDataType type = compiledSQL.getMapping().getSQLDataTypeOfColumn(compiledSQL.getMapping().getColNameByPropName(orderParam)) ;
//			if(type != null){
//				type.setSQLValue(pstm, i + 1, null) ;
//			}else{
//				pstm.setObject(i + 1, null) ;
//			}
//		}else{
//			//this method cann't handle null value. So, we change to detect the ObjectMapping's type
//			SQLDataType type = dialect.getDataType(value.getClass().getName()) ;
//			type.setSQLValue(pstm, i + 1, value) ;
//		}
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

	public Map getBindedParams() {
		return bindedParams;
	}

	public RowDataLoader getRowDataLoader() {
		return rowDataLoader;
	}

	/**
	 * Set a special {@link RowDataLoader} to temporary override the default ORM settings for this query one time.
	 */
	public BindedCompiledSQL setRowDataLoader(RowDataLoader rowDataLoader) {
		this.rowDataLoader = rowDataLoader;
		return this ;
	}
	
	/**
	 * Set the {@link RowDataLoader} to a {@link FormBeanRowDataLoader}.
	 * <br>
	 * Map each row of the {@link ResultSet} to a instance of the @param beanCls.
	 * 
	 * @param beanCls the pojo or Map class to store the value of the ResultSet
	 * @see FormBeanRowDataLoader
	 */
	public BindedCompiledSQL setBeanMapRowDataLoader(Class beanCls) {
		this.rowDataLoader = FormBeanRowDataLoader.newInstanceForClass(beanCls) ;
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

	public final Object getTableCondition() {
		if(this.tableCondition == NullValue.instance){
			return null ;
		}
		
		return this.tableCondition != null ? this.tableCondition : Guzz.getTableCondition() ;
	}

	public final BindedCompiledSQL setTableCondition(Object tableCondition) {
		if(tableCondition == null){
			tableCondition = NullValue.instance ;
		}
		
		this.tableCondition = tableCondition;
		notifyTableConditionChanged() ;
		
		return this ;
	}
	
	protected abstract void notifyTableConditionChanged() ;

	public Class getResultClass() {
		return resultClass;
	}

	/**
	 * 
	 * Set the mapped javabean for the {@link ResultSet} to any given class with set-xxx methods to override the ORM's default business class.
	 * 
	 * <p>
	 * If the resultClass is a subclass of java.util.Map(for example:java.util.HashMap), the queried value will be put to the Map.<br>
	 * </p>
	 *
	 * @param resultClass
	 */
	public BindedCompiledSQL setResultClass(Class resultClass) {
		this.resultClass = resultClass;
		return this ;
	}

}
