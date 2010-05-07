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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.dao.PageFlip;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.exception.GuzzException;
import org.guzz.exception.ORMException;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.jdbc.JDBCTemplateImpl;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.mapping.RowDataLoader;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.SearchParams;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.orm.sql.NormalCompiledSQL;
import org.guzz.orm.type.SQLDataType;
import org.guzz.pojo.GuzzProxy;
import org.guzz.service.core.DebugService;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AbstractTranSessionImpl {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
		
	protected ObjectMappingManager omm ;
	
	protected CompiledSQLManager compiledSQLManager ;
	
	protected ConnectionFetcher connectionFetcher ;
			
	protected JDBCTemplate jdbcTemplate ;
	
	protected DebugService debugService ;
	
	protected DBGroupManager dbGroupManager ;	
	
	protected boolean isReadonly ;
	
	/**保存已经打开的连接。针对同一个数据库只打开一个连接（保证事务提交）。*/
	protected Map opennedConnections = new HashMap() ;

	protected CompiledSQLBuilder compiledSQLBuilder ;
	
	public AbstractTranSessionImpl(ObjectMappingManager omm, CompiledSQLManager compiledSQLManager, ConnectionFetcher connectionFetcher, DebugService debugService, DBGroupManager dbGroupManager, boolean isReadonly) {
		this.omm = omm ;
		this.compiledSQLManager = compiledSQLManager ;
		this.connectionFetcher = connectionFetcher ;
		this.debugService = debugService ;
		this.dbGroupManager = dbGroupManager ;
		this.isReadonly = isReadonly ;
		this.compiledSQLBuilder = compiledSQLManager.getCompiledSQLBuilder() ;
	}
	
	public Class getRealDomainClass(Object domainObject){
		if(domainObject instanceof GuzzProxy){
			return ((GuzzProxy) domainObject).getProxiedClass() ;
		}else{
			return domainObject.getClass() ;
		}
	}

	public void close() {
		Iterator i = this.opennedConnections.values().iterator() ;
		
		while(i.hasNext()){
			Connection conn = (Connection) i.next() ;
			
			CloseUtil.close(conn) ;
		}
	}
	
	public Connection getConnection(DBGroup group){
		Connection conn = (Connection) this.opennedConnections.get(group.getGroupName()) ;
			
		if(conn == null){
			conn = connectionFetcher.getConnection(group) ;
			this.opennedConnections.put(group.getGroupName(), conn) ;
		}
		
		return conn ;
	}
	
	public List list(String id, Map params, int startPos, int maxSize){
		CompiledSQL sql = compiledSQLManager.getSQL(id) ;
		if(sql == null){
			throw new GuzzException("sql :[" + id + "] not found.") ;
		}
		
		return list(sql.bind(params), startPos, maxSize);
	}

	/**
	 * @param bsql
	 * @param startPos 从1开始
	 * @param maxSize
	 **/
	public List list(BindedCompiledSQL bsql, int startPos, int maxSize) {
		ObjectMapping m = bsql.getCompiledSQLToRun().getMapping() ;
		String rawSQL = bsql.getSQLToRun() ;
		if(m == null){
			throw new ORMException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		RowDataLoader loader = bsql.getRowDataLoader() ;		
		
		DBGroup db = m.getDbGroup() ;		
		Dialect dialect = m.getDbGroup().getDialect() ;

		//强制锁机制
		LockMode lock = bsql.getLockMode() ;
		
		if(lock == LockMode.UPGRADE){
			rawSQL = dialect.getForUpdateString(rawSQL) ;
		}else if(lock == LockMode.UPGRADE_NOWAIT){
			rawSQL = dialect.getForUpdateNoWaitString(rawSQL) ;
		}
		
		//TODO: check if the defaultDialect supports prepared bind in limit clause, and put the limit to compiledSQL
				
		//add limit clause.		
		rawSQL = db.getDialect().getLimitedString(rawSQL, startPos - 1, maxSize) ;
		this.debugService.logSQL(bsql, rawSQL) ;
		
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		
		try{
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL) ;
			
			bsql.prepareNamedParams(db.getDialect(), pstm) ;
			
			rs = pstm.executeQuery() ;
			
			//do ORM
			LinkedList results = new LinkedList() ;
			
			while(rs.next()){
				if(loader == null){
					results.addLast(m.rs2Object(rs)) ;
				}else{
					results.addLast(loader.rs2Object(m, rs)) ;
				}
			}
			
			return results ;
		}catch(SQLException e){
			throw new DaoException(rawSQL, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(pstm) ;
		}
	}
	
	public List list(SearchExpression se) {
		ObjectMapping m = omm.getObjectMapping(se.getFrom(), se.getTableCondition()) ;
		
		if(m == null){
			throw new ORMException("unknow object:" + se.getFrom()) ;
		}
		
		SearchParams sp = new SearchParams() ;
		MarkedSQL ms = se.toLoadRecordsMarkedSQL((POJOBasedObjectMapping) m, sp) ;
		
		CompiledSQL sql = this.compiledSQLBuilder.buildCompiledSQL(ms).setParamPropMapping(sp.getParamPropMapping()) ;
		
		return list(sql.bind(sp.getSearchParams()).setTableCondition(se.getTableCondition()), se.getStartPos(), se.getPageSize()) ;
	}
	
	public long count(SearchExpression se) {
		ObjectMapping m = omm.getObjectMapping(se.getFrom(), se.getTableCondition()) ;
		
		if(m == null){
			throw new ORMException("unknown business:" + se.getFrom()) ;
		}
		
		SearchParams sp = new SearchParams() ;
		
		MarkedSQL ms = se.toComputeRecordNumberSQL((POJOBasedObjectMapping) m, sp) ;
		
		CompiledSQL sql = this.compiledSQLBuilder.buildCompiledSQL(ms).setParamPropMapping(sp.getParamPropMapping()) ;
		
		Object ret = findCell00(sql.bind(sp.getSearchParams()).setTableCondition(se.getTableCondition()), Long.class.getName()) ;
		
		if(ret == null){
			return 0L ;
		}else{
			return ((Long) ret).longValue() ;
		}
	}
	
	public PageFlip page(SearchExpression se) {
		ObjectMapping m = omm.getObjectMapping(se.getFrom(), se.getTableCondition()) ;
		
		if(m == null){
			throw new ORMException("unknow object:" + se.getFrom()) ;
		}
		
		PageFlip pf = null ;
		
		Class m_flip = se.getPageFlipClass() ;
		if(m_flip == null){
			pf = new PageFlip() ;
		}else{
			pf = (PageFlip) BeanCreator.newBeanInstance(m_flip) ;
		}
		
		List records = null ;
		
		if(se.isLoadRecords()){
			records = list(se) ;
		}
		
		int recordCount = 0 ;
		
		if(se.isComputeRecordNumber()){
			SearchParams sp = new SearchParams() ;			
			MarkedSQL ms = se.toComputeRecordNumberSQL((POJOBasedObjectMapping) m, sp) ;
			
			CompiledSQL sql = this.compiledSQLBuilder.buildCompiledSQL(ms).setParamPropMapping(sp.getParamPropMapping()) ;
			
			Integer count = (Integer) findCell00(sql.bind(sp.getSearchParams()).setTableCondition(se.getTableCondition()), "int") ;
			recordCount = count.intValue() ;
		}
		
		pf.setResult(recordCount, se.getPageNo(), se.getPageSize(), records) ;
		
		return pf;
	}

	public Object findCell00(String id, Map params, String returnType){
		CompiledSQL sql = compiledSQLManager.getSQL(id) ;
		if(sql == null){
			throw new GuzzException("sql :[" + id + "] not found.") ;
		}
		
		return findCell00(sql.bind(params), returnType);
	}

	public Object findCell00(BindedCompiledSQL bsql, String returnType){
		ObjectMapping m = bsql.getCompiledSQLToRun().getMapping() ;
		String rawSQL = bsql.getSQLToRun() ;
		if(m == null){
			throw new ORMException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		Dialect dialect = m.getDbGroup().getDialect() ;

		//强制锁机制
		LockMode lock = bsql.getLockMode() ;
		
		if(lock == LockMode.UPGRADE){
			rawSQL = dialect.getForUpdateString(rawSQL) ;
		}else if(lock == LockMode.UPGRADE_NOWAIT){
			rawSQL = dialect.getForUpdateNoWaitString(rawSQL) ;
		}
		
		RowDataLoader loader = bsql.getRowDataLoader() ;
		
		DBGroup db = m.getDbGroup() ;
		
		this.debugService.logSQL(bsql, rawSQL) ;
		
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		
		try{
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL) ;
			bsql.prepareNamedParams(db.getDialect(), pstm) ;
			
			rs = pstm.executeQuery() ;
			
			if(rs.next()){
				if(loader != null){
					return loader.rs2Object(m, rs) ;
				}else if(returnType != null){
					SQLDataType type = db.getDialect().getDataType(returnType) ;
							
					return type.getSQLValue(rs, 1) ;
				}else{
					return rs.getObject(1) ;
				}
			}else{
				if(bsql.isExceptionOnNoRecordFound()){
					throw new DaoException("record not found for the query:[" + rawSQL + "], params:[" + bsql.getBindedParams() + "].") ;
				}else{
					return null ;
				}
			}
		}catch(SQLException e){
			throw new DaoException(rawSQL, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(pstm) ;
		}
	}
	
	/**
	 * @param bsql
	 * @param returnType
	 */
	protected Object findCell00(BindedCompiledSQL bsql, SQLDataType returnType){
		ObjectMapping m = bsql.getCompiledSQLToRun().getMapping() ;
		String rawSQL = bsql.getSQLToRun() ;
		if(m == null){
			throw new ORMException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		Dialect dialect = m.getDbGroup().getDialect() ;

		//强制锁机制
		LockMode lock = bsql.getLockMode() ;
		
		if(lock == LockMode.UPGRADE){
			rawSQL = dialect.getForUpdateString(rawSQL) ;
		}else if(lock == LockMode.UPGRADE_NOWAIT){
			rawSQL = dialect.getForUpdateNoWaitString(rawSQL) ;
		}
		
		RowDataLoader loader = bsql.getRowDataLoader() ;
		
		DBGroup db = m.getDbGroup() ;
		
		this.debugService.logSQL(bsql, rawSQL) ;
		
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		
		try{
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL) ;
			bsql.prepareNamedParams(db.getDialect(), pstm) ;
			
			rs = pstm.executeQuery() ;
			
			if(rs.next()){
				if(loader != null){
					return loader.rs2Object(m, rs) ;
				}else if(returnType != null){
					return returnType.getSQLValue(rs, 1) ;
				}else{
					return rs.getObject(1) ;
				}
			}else{
				if(bsql.isExceptionOnNoRecordFound()){
					throw new DaoException("record not found for the query:[" + rawSQL + "], params:[" + bsql.getBindedParams() + "].") ;
				}else{
					return null ;
				}
			}
		}catch(SQLException e){
			throw new DaoException(rawSQL, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(pstm) ;
		}
	}

	public Object findObject(String id, Map params){
		CompiledSQL sql = compiledSQLManager.getSQL(id) ;
		if(sql == null){
			throw new GuzzException("sql :[" + id + "] not found.") ;
		}
		
		return findObject(sql.bind(params)) ;
	}

	public Object findObject(BindedCompiledSQL bsql) {
		ObjectMapping m = bsql.getCompiledSQLToRun().getMapping() ;
		String rawSQL = bsql.getSQLToRun() ;
		
		if(m == null){
			throw new ORMException("ObjectMapping is null. sql is:" + rawSQL) ;
		}
		
		RowDataLoader loader = bsql.getRowDataLoader() ;
		
		DBGroup db = m.getDbGroup() ;
		Dialect dialect = db.getDialect() ;
		
		//强制锁机制
		LockMode lock = bsql.getLockMode() ;
		
		if(lock == LockMode.UPGRADE){
			rawSQL = dialect.getForUpdateString(rawSQL) ;
		}else if(lock == LockMode.UPGRADE_NOWAIT){
			rawSQL = dialect.getForUpdateNoWaitString(rawSQL) ;
		}
		
		//TODO: check if the defaultDialect supports prepared bind in limit clause, and put the limit to compiledSQL
		rawSQL = db.getDialect().getLimitedString(rawSQL, 0, 1) ;
		this.debugService.logSQL(bsql, rawSQL) ;
		
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		
		try{
			Connection conn = getConnection(db) ;
			pstm = conn.prepareStatement(rawSQL) ;
			
			bsql.prepareNamedParams(db.getDialect(), pstm) ;
			
			rs = pstm.executeQuery() ;
			
			//do ORM		
			if(rs.next()){
				if(loader == null){
					return m.rs2Object(rs) ;
				}else{
					return loader.rs2Object(m, rs) ;
				}
			}else{
				if(bsql.isExceptionOnNoRecordFound()){
					throw new DaoException("record not found for the query:[" + rawSQL + "], params:[" + bsql.getBindedParams() + "].") ;
				}else{
					return null ;
				}
			}
		}catch(SQLException e){
			throw new DaoException(rawSQL, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(pstm) ;
		}
	}

	public Object findObject(SearchExpression se) {
		se.setPageNo(1) ;
		se.setPageSize(1) ;
		
		List l = list(se) ;
		if(l.isEmpty()){
			return null ;
		}else{
			return l.get(0) ;
		}
	}
	
	public Object findObjectByPK(String businessName, Serializable pk){
		CompiledSQL cs = this.compiledSQLManager.getDefinedSelectSQL(businessName) ;
		if(cs == null){
			throw new DaoException("no defined sql found for class:[" + businessName + "]. forget to register it in guzz.xml?") ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams() ;
		String[] orderedParams = bsql.getCompiledSQLToRun().getOrderedParams() ;
		
		if(orderedParams.length != 1){
			throw new DaoException("error orm! too many params in findObjectByPK. class is:" + businessName) ;
		}
		
		return findObject(bsql.bind(orderedParams[0], pk)) ;
	}
	
	public Object findObjectByPK(Class domainClass, Serializable pk){
		return findObjectByPK(domainClass.getName(), pk) ;
	}
	
	public Object findObjectByPK(Class domainClass, int pk){
		return findObjectByPK(domainClass.getName(), new Integer(pk)) ;
	}
	
	public Object refresh(Object domainObject, LockMode lockMode){
		String className = getRealDomainClass(domainObject).getName() ;
		CompiledSQL cs = this.compiledSQLManager.getDefinedSelectSQL(className) ;
		if(cs == null){
			throw new DaoException("no defined sql found for class:[" + className + "]. forget to register it in guzz.xml?") ;
		}
		
		BindedCompiledSQL bsql = cs.bindNoParams() ;
		
		NormalCompiledSQL runtimeCS = bsql.getCompiledSQLToRun() ;
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) runtimeCS.getMapping() ;
		Table table = mapping.getTable() ;
		
		BeanWrapper bw = mapping.getBeanWrapper() ;
		Object pk = bw.getValueUnderProxy(domainObject, table.getPKPropName()) ;
		String[] orderedParams = runtimeCS.getOrderedParams() ;
		
		if(orderedParams.length != 1){
			throw new DaoException("error orm! too many params in findObjectByPK. class is:" + className) ;
		}
		
		bsql.bind(orderedParams[0], pk).setLockMode(lockMode) ;
		
		//record must be exsit on refresh(), or raise a exception.
		bsql.setExceptionOnNoRecordFound(true) ;
		
		return findObject(bsql) ;
	}

	public JDBCTemplate createJDBCTemplate(Class domainClass) {				
		return createJDBCTemplate(domainClass.getName()) ;
	}

	public JDBCTemplate createJDBCTemplateByDbGroup(String groupName) {
		DBGroup group = this.dbGroupManager.getGroup(groupName) ;
		
		Connection conn = getConnection(group) ;
		
		return new JDBCTemplateImpl(group.getDialect(), debugService, conn, isReadonly) ;
	}
	
	public JDBCTemplate createJDBCTemplate(String businessName){
		ObjectMapping map = this.omm.getObjectMapping(businessName, Guzz.getTableCondition()) ;
		
		if(map == null){
			throw new ORMException("unknown business:[" + businessName + "]") ;
		}
		
		DBGroup group = map.getDbGroup() ;
		
		Connection conn = getConnection(group) ;
				
		return new JDBCTemplateImpl(group.getDialect(), debugService, conn, isReadonly) ;
	}
}
