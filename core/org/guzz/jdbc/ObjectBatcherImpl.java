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
package org.guzz.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.id.IdentifierGenerator;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.service.core.DebugService;
import org.guzz.transaction.WriteTranSessionImpl;
import org.guzz.util.javabean.BeanWrapper;

/**
 *
 * batcher to perform batch operations for insert/update/delete ONE domain object's instances.
 * <p>a batcher can only be used for one single domain object's one single operation(insert, or update, or delete.</p>
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ObjectBatcherImpl implements ObjectBatcher {
	protected CompiledSQLManager compiledSQLManager ;
	private WriteTranSessionImpl sessionImpl ;
	private DebugService debugService ;
	protected String domainClassName ;

	private PreparedStatement ps ;
	private Dialect dialect ;
	private CompiledSQL cs ;
	private Connection conn ;

	//inited in the construct method to protect add/delete/update passing wrong instances.
	private BeanWrapper bw ;
	private String[] props ;

	private Class domainCls ;
	
	private Object tableCondition ;

	/**
	 * add:1
	 * update:2
	 * delelte:3
	 */
	private int mark = 0 ;

	private String[] markMsg = new String[]{"", "add", "update", "delete"} ;

	public ObjectBatcherImpl(CompiledSQLManager compiledSQLManager, WriteTranSessionImpl sessionImpl, DebugService debugService, Dialect dialect, Connection conn, String domainClassName){
		this.compiledSQLManager = compiledSQLManager ;
		this.sessionImpl = sessionImpl ;
		this.debugService = debugService ;
		this.dialect = dialect ;
		this.conn = conn ;
		this.domainClassName = domainClassName ;
	}

	protected void preparePS(Object domainObject, int operation){
		switch (operation) {
			case 1:
				this.cs = this.compiledSQLManager.getDefinedInsertSQL(domainClassName) ;
				break;
			case 2:
				this.cs = this.compiledSQLManager.getDefinedUpdateSQL(domainClassName) ;
				break;
			case 3:
				this.cs = this.compiledSQLManager.getDefinedDeleteSQL(domainClassName) ;
				break;
			default:
				break;
		}

		if(this.cs == null){
			throw new DaoException("no defined sql found for class:[" + domainClassName + "]. forget to register it in guzz.xml?") ;
		}

		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) cs.getMapping() ;
		this.bw = mapping.getBeanWrapper() ;
		this.props = cs.getOrderedParams() ;

		//? get the real class under (maybe) proxy ?
		this.domainCls = domainObject.getClass() ;

		String rawSQL = cs.bindNoParams().setTableCondition(this.tableCondition).getSql() ;
		this.debugService.logSQL("batch:" + rawSQL, null) ;

		try {
			this.ps = this.conn.prepareStatement(rawSQL) ;
		} catch (SQLException e) {
			throw new DaoException("error prepare sql:[" + rawSQL + "], domainObject is:" + domainObject.getClass()) ;
		}
	}

	public void insert(Object domainObject) {
		if(mark == 0){
			mark = 1 ;
			preparePS(domainObject, 1) ;
		}else if(mark != 1){
			throw new DaoException("duplicate operations. the batch has already started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = cs.bindNoParams() ;

		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) cs.getMapping() ;
		IdentifierGenerator ig = mapping.getTable().getIdentifierGenerator() ;

		ig.preInsert(this.sessionImpl, domainObject) ;

		BeanWrapper bw = new BeanWrapper(domainObject.getClass()) ;

		for(int i = 0 ; i < props.length ; i++){
			Object value = bw.getValue(domainObject, props[i]) ;
			bsql.bind(props[i], value) ;
		}

		try {
			bsql.prepareNamedParams(dialect, ps) ;
			this.ps.addBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute add. param type is:" + domainObject.getClass(), e) ;
		}

		//POST ID is not supported
//		if(pk == null){
//			pk = ig.postInsert(this, domainObject) ;
//		}else{
//			ig.postInsert(this, domainObject) ;
//		}
	}

	public void update(Object domainObject) {
		if(mark == 0){
			mark = 2 ;
			preparePS(domainObject, 2) ;
		}else if(mark != 2){
			throw new DaoException("duplicate operations. the batch has already started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = cs.bindNoParams() ;

		for(int i = 0 ; i < props.length ; i++){
			Object value = bw.getValue(domainObject, props[i]) ;
			bsql.bind(props[i], value) ;
		}

		try {
			bsql.prepareNamedParams(dialect, ps) ;
			this.ps.addBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute add. param type is:" + domainObject.getClass(), e) ;
		}
	}

	public void delete(Object domainObject) {
		if(mark == 0){
			mark = 3 ;
			preparePS(domainObject, 3) ;
		}else if(mark != 3){
			throw new DaoException("duplicate operations. the batch has already started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = cs.bindNoParams() ;

		for(int i = 0 ; i < props.length ; i++){
			Object value = bw.getValue(domainObject, props[i]) ;
			bsql.bind(props[i], value) ;
		}

		try {
			bsql.prepareNamedParams(dialect, ps) ;
			this.ps.addBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute add. param type is:" + domainObject.getClass(), e) ;
		}
	}

	public void clearBatch() {
		try {
			ps.clearBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute clearBatch. CompiledSQL is:" + cs, e) ;
		}
	}

	public int[] executeUpdate() {
		try {
			return ps.executeBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute batch update. CompiledSQL is:" + cs, e) ;
		}
	}

	public PreparedStatement getPreparedStatement(){
		return ps ;
	}

	public Object getTableCondition() {
		return tableCondition;
	}

	public void setTableCondition(Object tableCondition) {
		if(mark != 0){ //sql已经准备完毕了，不允许在更改。避免同一个对象映射到多个表在1个batch中执行。
			throw new DaoException("batch has already started. please setTableCondtion before invoking insert/update/delete method.") ;
		}
		
		this.tableCondition = tableCondition;
	}

}
