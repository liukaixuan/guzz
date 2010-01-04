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
package org.guzz.transaction;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guzz.bytecode.LazyPropChangeDetector;
import org.guzz.exception.DaoException;
import org.guzz.exception.GuzzException;
import org.guzz.exception.ORMException;
import org.guzz.id.IdentifierGenerator;
import org.guzz.jdbc.ObjectBatcher;
import org.guzz.jdbc.ObjectBatcherImpl;
import org.guzz.jdbc.SQLBatcher;
import org.guzz.jdbc.SQLBatcherImpl;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.pojo.DynamicUpdatable;
import org.guzz.pojo.GuzzProxy;
import org.guzz.service.core.DatabaseService;
import org.guzz.service.core.DebugService;
import org.guzz.util.ArrayUtil;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class WriteTranSessionImpl extends AbstractTranSessionImpl implements WriteTranSession {
	private List psForBatch = null ;
	private List objectBatchers = null ;
	
	public WriteTranSessionImpl(ObjectMappingManager omm, CompiledSQLManager compiledSQLManager, DebugService debugService, DBGroupManager dbGroupManager, boolean autoCommit) {
		super(omm, compiledSQLManager, new WriteConnectionFetcher(autoCommit), debugService, dbGroupManager, false);
	}
	
	protected String getDomainClassName(Object domainObject){
		if(domainObject instanceof GuzzProxy){
			return ((GuzzProxy) domainObject).getProxiedClassName() ;
		}else{
			return domainObject.getClass().getName() ;
		}
	}
	
	public boolean delete(Object domainObject) {
		CompiledSQL cs = this.compiledSQLManager.getDefinedDeleteSQL(getDomainClassName(domainObject)) ;
		if(cs == null){
			throw new DaoException("no defined sql found for class:[" + getDomainClassName(domainObject) + "]. forget to register it in guzz.xml?") ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams() ;
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) cs.getMapping() ;
		
		BeanWrapper bw = mapping.getBeanWrapper() ;
		String[] props = cs.getOrderedParams() ;
		for(int i = 0 ; i < props.length ; i++){
			Object value = bw.getValueUnderProxy(domainObject, props[i]) ;
			bsql.bind(props[i], value) ;
		}
		
		return executeUpdate(bsql) == 1 ;
	}

	/* @see org.guzz.transaction.WriteTranSession#insert(java.lang.Object) */
	public Serializable insert(Object domainObject) {
		//inserted domainObject must be orginal(not proxied).
		CompiledSQL cs = this.compiledSQLManager.getDefinedInsertSQL(domainObject.getClass().getName()) ;
		if(cs == null){
			throw new DaoException("no defined sql found for class:[" + domainObject.getClass().getName() + "]. forget to register it in guzz.xml?") ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams() ;
		
		Serializable pk = null ;
		
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) cs.getMapping() ;
		IdentifierGenerator ig = mapping.getTable().getIdentifierGenerator() ;		
		
		pk = ig.preInsert(this, domainObject) ;
		
		BeanWrapper bw = mapping.getBeanWrapper() ;
		
		String[] props = cs.getOrderedParams() ;
		for(int i = 0 ; i < props.length ; i++){
			Object value = bw.getValue(domainObject, props[i]) ;
			bsql.bind(props[i], value) ;
		}
		
		executeUpdate(bsql) ;
		
		if(pk == null){
			pk = ig.postInsert(this, domainObject) ;
		}else{
			ig.postInsert(this, domainObject) ;
		}
		
		return pk ;
	}

	public boolean update(Object domainObject) {
		/*
		 * 1. 所有含有loader字段的属性不进行保存。
		 * 2. 所有lazy属性，只有在显式的调用setxxx方法后才进行保存。
		 * 3. 如果设置了dynamic-update=true，只更新显式调用setXXX的属性（lazy属性的变化也包含在此，因此dynamic-update检测到的更改字段包含lazy的属性的更改）。
		 */
		String domainClassName = getDomainClassName(domainObject) ;
		
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) this.omm.getObjectMappingByName(domainClassName) ;
		if(mapping == null){
			throw new DaoException("ObjectMapping is null. class is:" + domainClassName) ;
		}
		
		BeanWrapper bw = mapping.getBeanWrapper() ;
		Table table = mapping.getBusiness().getTable() ;
		CompiledSQL cs = null ;
		String[] updatedProps = null ;
				
		boolean dynamicUpdateEnable = domainObject instanceof DynamicUpdatable ;
		
		//TODO:设计错误！为了让项目更灵活的控制字段更新，lazy的字段应该和非lazy字段分开计数。DynamicUpdatable应该 只 统计非lazy字段的修改情况。
		
		//dynamic-update=true
		if(dynamicUpdateEnable){
			String[] changedProps = ((DynamicUpdatable) domainObject).getChangedProps() ;
			
			if(changedProps == null){
				//null means: ignore dynamic-update=true.
				updatedProps = table.getPropsForUpdate() ;
			}else if(changedProps.length == 0){
				if(log.isDebugEnabled()){
					log.debug("changedProps is empty. the update operation is ignored. object is:" + domainObject) ;
				}
				
				return false ;
			}else{//update the changed properties.
				updatedProps = changedProps ;
				cs = this.compiledSQLManager.buildUpdateSQL(mapping, updatedProps) ;
			}
		}else{
			updatedProps = table.getPropsForUpdate() ;
		}
		
		//has lazy properties
		if(!dynamicUpdateEnable && domainObject instanceof LazyPropChangeDetector){
			String[] changedProps = ((LazyPropChangeDetector) domainObject).getChangedLazyProps() ;
			
			if(changedProps != null && changedProps.length > 0){
				//add the changed props to update list.
				updatedProps = (String[]) ArrayUtil.addToArray(updatedProps, changedProps) ;
				cs = this.compiledSQLManager.buildUpdateSQL(mapping, updatedProps) ;
			}
		}
		
		//update as normal(no dynamic, no lazy load)
		if(cs == null){
			cs = this.compiledSQLManager.getDefinedUpdateSQL(domainClassName) ;
			if(cs == null){
				throw new DaoException("no defined sql found for class:[" + domainClassName + "]. forget to register it in guzz.xml?") ;
			}
			//updateProps is already setted.
		}else{ //add pk property for where clause binding.
			updatedProps = (String[]) ArrayUtil.addToArray(updatedProps, table.getPKPropName()) ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams() ;
				
		for(int i = 0 ; i < updatedProps.length ; i++){
			Object value = bw.getValueUnderProxy(domainObject, updatedProps[i]) ;
			bsql.bind(updatedProps[i], value) ;
		}
		
		return executeUpdate(bsql) == 1 ;
	}	
	
	public int executeUpdate(String id, Map params){
		CompiledSQL cs = compiledSQLManager.getSQL(id) ;
		if(cs == null){
			throw new DaoException("configured sql not found. id is:" + id) ;
		}
		
		return executeUpdate(cs.bind(params)) ;
	}
	
	public int executeUpdate(BindedCompiledSQL bsql){
		CompiledSQL sql = bsql.getCompiledSQL() ;
		String rawSQL = sql.getSql() ;
		ObjectMapping m = sql.getMapping() ;
		if(m == null){
			throw new DaoException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		DBGroup db = m.getDbGroup() ;
		
		this.debugService.logSQL(bsql) ;
		
		PreparedStatement pstm = null;
		
		try {
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL);			
			bsql.prepareNamedParams(db.getDialect(), pstm) ;
		
			return pstm.executeUpdate() ;
		}catch(SQLException e){
			throw new DaoException(rawSQL, e) ;
		}finally{
			CloseUtil.close(pstm) ;
		}
	}
	
	public SQLBatcher createCompiledSQLBatcher(CompiledSQL sql) {
		String rawSQL = sql.getSql() ;
		ObjectMapping m = sql.getMapping() ;
		if(m == null){
			throw new ORMException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		DBGroup db = m.getDbGroup() ;
		
		this.debugService.logSQL("batch:" + rawSQL, null) ;
		
		PreparedStatement pstm = null;
		
		try {
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL);
		}catch(SQLException e){
			CloseUtil.close(pstm) ;
			throw new DaoException(rawSQL, e) ;
		}
		
		if(this.psForBatch == null){
			this.psForBatch = new LinkedList() ;
		}
		
		this.psForBatch.add(pstm) ;
		
		SQLBatcherImpl b = new SQLBatcherImpl(pstm, db.getDialect(), sql) ;
		
		return b ;
	}
	
	public ObjectBatcher createObjectBatcher(Class domainClass) {
		String className = domainClass.getName() ;
		ObjectMapping m = this.omm.getObjectMappingByName(className) ;
		if(m == null){
			throw new ORMException("unknown domainClass:" + className) ;
		}
		
		DBGroup db = m.getDbGroup() ;
		Connection conn = getConnection(db) ;
		
		ObjectBatcherImpl b = new ObjectBatcherImpl(compiledSQLManager, this, this.debugService, db.getDialect(), conn, className) ;
		
		if(this.objectBatchers == null){
			this.objectBatchers = new LinkedList() ;
		}
		
		this.objectBatchers.add(b) ;
		
		return b ;
	}
	
	public void commit(){
		try {
			Iterator i = this.opennedConnections.values().iterator() ;
			
			while(i.hasNext()){
				Connection conn = (Connection) i.next() ;
				
				conn.commit() ;
			}
			
		} catch (SQLException e) {
			throw new DaoException(e) ;
		}
	}
	
	public void rollback(){
		SQLException ex = null ;
		Iterator i = this.opennedConnections.values().iterator() ;
		StringBuffer sb = null ;
		
		while(i.hasNext()){
			Connection conn = (Connection) i.next() ;
			
			try {
				//所有连接全部忽略错误，并且rollback。
				conn.rollback() ;				
			} catch (SQLException e) {
				if(sb == null){
					sb = new StringBuffer() ;
				}
				
				sb.append("[errorCode:").append(ex.getErrorCode()).append(", msg:").append(ex.getMessage()).append("];") ;
				
				ex = e ;
			}
		}
		
		if(ex != null){
			throw new DaoException(sb.toString(), ex) ;
		}
	}
		
	public void commitAndClose(){
		try {
			commit() ;
		} catch (Exception e) {
			throw new DaoException(e) ;
		}finally{
			close() ;
		}
	}
	
	public void rollbackAndClose(){
		try {
			rollback() ;
		} catch (Exception e) {
			throw new DaoException(e) ;
		}finally{
			close() ;
		}
	}

	public void close() {
		if(this.psForBatch != null){
			Iterator i = this.psForBatch.iterator() ;
			while(i.hasNext()){
				PreparedStatement ps = (PreparedStatement) i.next() ;
				CloseUtil.close(ps) ;
			}
		}
		
		if(this.objectBatchers != null){
			Iterator i = objectBatchers.iterator() ;
			while(i.hasNext()){
				ObjectBatcherImpl b = (ObjectBatcherImpl) i.next() ;
				CloseUtil.close(b.getPreparedStatement()) ;
			}
		}
		
		//close connections.
		super.close();
	}

}


class WriteConnectionFetcher implements ConnectionFetcher{
	
	private boolean autoCommit ;
	
	public WriteConnectionFetcher(boolean autoCommit){
		this.autoCommit = autoCommit ;
	}
	
	public Connection getConnection(DBGroup dbGroup) {
		return openRWConn(dbGroup) ;
	}
	
	public Connection openRWConn(DBGroup dbGroup) {
		DatabaseService masterDatabaseService = dbGroup.getMasterDB() ;
		
		if(masterDatabaseService != null && masterDatabaseService.isAvailable()){
			Connection conn;
			try {
				conn = masterDatabaseService.getDataSource().getConnection();				
			} catch (SQLException e) {
				throw new DaoException("master datasource failed.", e) ;
			}
			
			try {
				conn.setAutoCommit(autoCommit) ;
				
				return conn ;
			} catch (Exception e) {
				//be careful of conn leak.
				CloseUtil.close(conn) ;
				
				throw new DaoException("fail to set autoCommit to:[" + autoCommit + "]", e) ;
			}
		}else{
			throw new GuzzException("master database is not available.") ;
		}
	}
	
	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean allowDelay) {
		this.autoCommit = allowDelay;
	}
	
}
