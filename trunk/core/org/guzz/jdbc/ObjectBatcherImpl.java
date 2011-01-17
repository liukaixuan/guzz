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

import org.guzz.Guzz;
import org.guzz.connection.DBGroup;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.id.IdentifierGenerator;
import org.guzz.lang.NullValue;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.orm.sql.NormalCompiledSQL;
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

	private PreparedStatement ps ;
	private Dialect dialect ;

	//inited in the construct method to protect add/delete/update passing wrong instances.
	private BeanWrapper bw ;
	private String[] props ;

	private Class domainCls ;
	
	private Object tableCondition ;
	
	private NormalCompiledSQL runtimeCS ;

	/**
	 * add:1
	 * update:2
	 * delelte:3
	 */
	private int mark = 0 ;

	private String[] markMsg = new String[]{"", "add", "update", "delete"} ;

	public ObjectBatcherImpl(CompiledSQLManager compiledSQLManager, WriteTranSessionImpl sessionImpl, DebugService debugService){
		this.compiledSQLManager = compiledSQLManager ;
		this.sessionImpl = sessionImpl ;
		this.debugService = debugService ;
	}

	protected void preparePS(Object domainObject, int operation){
		CompiledSQL cs = null ;
		this.domainCls = this.sessionImpl.getRealDomainClass(domainObject) ;
		
		switch (operation) {
			case 1:
				cs = this.compiledSQLManager.getDefinedInsertSQL(this.domainCls.getName()) ;
				break;
			case 2:
				cs = this.compiledSQLManager.getDefinedUpdateSQL(this.domainCls.getName()) ;
				break;
			case 3:
				cs = this.compiledSQLManager.getDefinedDeleteSQL(this.domainCls.getName()) ;
				break;
			default:
				break;
		}

		if(cs == null){
			throw new DaoException("no defined sql found for class:[" + this.domainCls.getName() + "]. forget to register it in guzz.xml?") ;
		}

		//be carefule: createObjectBatcher first, then set tableCondition through Guzz.setTableCondition.
		//tableCondition won't change again even if Guzz.setTableCondition() invoked again unless #clearBatch() is called.
		if(this.tableCondition == null){
			this.tableCondition = Guzz.getTableCondition() ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams().setTableCondition(this.tableCondition) ;
		this.runtimeCS = bsql.getCompiledSQLToRun() ;
		
		ObjectMapping mapping  = runtimeCS.getMapping() ;
		this.bw = mapping.getBeanWrapper() ;
		this.props = runtimeCS.getOrderedParams() ;
		
		DBGroup dbGroup = mapping.getDbGroup() ;
		this.dialect = dbGroup.getDialect() ;
		Connection conn = this.sessionImpl.getConnection(dbGroup, bsql.getTableCondition()) ;

		String rawSQL = bsql.getSQLToRun() ;
		this.debugService.logSQL("batch:" + rawSQL) ;

		try {
			this.ps = conn.prepareStatement(rawSQL) ;
		} catch (SQLException e) {
			throw new DaoException("error prepare sql:[" + rawSQL + "], domainObject is:" + domainObject.getClass()) ;
		}
	}

	public void insert(Object domainObject) {
		if(mark == 0){
			mark = 1 ;
			preparePS(domainObject, 1) ;
		}else if(mark != 1){
			throw new DaoException("duplicate operations. the batch has already been started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already been prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = runtimeCS.bindNoParams() ;
		IdentifierGenerator ig = runtimeCS.getMapping().getTable().getIdentifierGenerator() ;

		ig.preInsert(this.sessionImpl, domainObject, this.tableCondition) ;

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
			throw new DaoException("duplicate operations. the batch has already been started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already been prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = runtimeCS.bindNoParams() ;

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
			throw new DaoException("duplicate operations. the batch has already been started for:" + markMsg[mark]) ;
		}

		if(!this.domainCls.isInstance(domainObject)){
			throw new DaoException("duplicate domain object. the batch has already been prepared for:" + this.domainCls) ;
		}

		BindedCompiledSQL bsql = runtimeCS.bindNoParams() ;

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
		//not initialized.
		if(mark == 0){
			return ;
		}
		
		try {
			ps.clearBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute clearBatch. CompiledSQL is:" + runtimeCS, e) ;
		}
		
		this.mark = 0 ;
	}

	public int[] executeUpdate() {
		//not initialized.
		if(mark == 0){
			return new int[0] ;
		}
		
		try {
			return ps.executeBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute batch update. CompiledSQL is:" + runtimeCS, e) ;
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
			throw new DaoException("batch has already been started. Call setTableCondtion before invoking insert/update/delete method.") ;
		}
		
		this.tableCondition = tableCondition == null ? NullValue.instance : tableCondition;
	}

}
