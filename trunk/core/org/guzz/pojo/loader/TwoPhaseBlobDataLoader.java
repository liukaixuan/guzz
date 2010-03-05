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
package org.guzz.pojo.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.GuzzContext;
import org.guzz.dao.PersistListener;
import org.guzz.dao.PersistListenerAdapter;
import org.guzz.exception.DaoException;
import org.guzz.exception.ORMException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.RowDataLoader;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.pojo.lob.TranBlob;
import org.guzz.service.core.DebugService;
import org.guzz.transaction.LockMode;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * used for very large blob, or for reducing the memory usage, or for streamed data.
 * 
 * <p>
 * for simple manipulation of not very large byte[], use 'bytes' dataType to do object mapping is much easier.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TwoPhaseBlobDataLoader extends PersistListenerAdapter implements ColumnDataLoader, PersistListener, GuzzContextAware {
	
	private TransactionManager tm ;
	private DebugService debugService ;
	
	private ObjectMapping mapping ;
	private Table table ;
	private String columnName ;
	
	private CompiledSQL sqlToLoadLazily ;
	private CompiledSQL sqlInsertCallback ;
	private BeanWrapper wrap ;
	
	private final InputStream threadSafeInputStream = new InputStream(){
		public int read() throws IOException {
			return 1;
		}

		public int available() throws IOException {
			return 1 ;
		}

		public int read(byte[] b, int off, int len) throws IOException {
			for(int i = 0 ; i < len ; i++){
				b[off + i] = 1 ;
			}
			
			return len ;
		}
	} ;
	
	private final RowDataLoader blobDataLoader = new RowDataLoader(){
		public Object rs2Object(ObjectMapping mapping, ResultSet rs) throws SQLException {
			return rs.getBlob(1) ;
		}
	} ;
	
	public void configure(ObjectMapping mapping, Table table, String propName, String columnName) {
		this.mapping = mapping ;
		this.wrap = mapping.getBeanWrapper() ;
		
		this.table = table ;
		this.columnName = columnName ;
	}
	
	public void postInsert(WriteTranSession tran, Connection conn, Object domainObject, Serializable pk) {
		//设置blob为空blob。
		BindedCompiledSQL bsql = this.sqlInsertCallback.bind("pkValue", pk) ;
		bsql.setBindStartIndex(2) ;
		
		PreparedStatement pstm = null ;
		
		this.debugService.logSQL(bsql) ;
		
		try{
			pstm = conn.prepareStatement(bsql.getSql()) ;
			bsql.prepareNamedParams(mapping.getDbGroup().getDialect(), pstm) ;
			pstm.setBinaryStream(1, threadSafeInputStream, 1) ;
			
			pstm.executeUpdate() ;
		}catch(SQLException e){
			throw new DaoException(e) ;
		}finally{
			CloseUtil.close(pstm) ;
		}
	}

	/**
	 * @return {@link TranBlob}
	 */
	public Object loadData(ResultSet rs, Object objectFetching, int indexToLoad) throws SQLException{
		Blob b = rs.getBlob(indexToLoad) ;
		
		if(b == null){
			return null ;
		}else{
			return new TranBlob(b) ;
		}
	}

	/**
	 * @return {@link TranBlob}. The invoker should close the blob returned. see also: {@link BranClob#close()}. return null if the column's value is null.
	 */
	public Object loadLazyData(Object fetchedObject) {
		Object pk = this.wrap.getValue(fetchedObject, table.getPKPropName()) ;
		if(pk == null){
			throw new ORMException("primary value is not setted. object to fecth is:" + fetchedObject) ;
		}
		
		BindedCompiledSQL bsql = this.sqlToLoadLazily.bind("pkValue", pk) ;
		bsql.setRowDataLoader(blobDataLoader) ;
	
		ReadonlyTranSession tran = tm.openDelayReadTran() ;
		
		try{
			Blob blob = (Blob) tran.findCell00(bsql, null) ;
			if(blob == null){
				//force closing the connection, as the user would never get the chance to do that.
				tran.close() ;
				
				return null ;
			}else{
				//the user should close the connection.
				return new TranBlob(tran, blob) ;
			}
		}catch(RuntimeException e){
			//close the connection , and re-throw the exception.
			tran.close() ;
			throw e ;
		}
	}

	/**
	 * @return {@link TranBlob}
	 */
	public Object loadLazyDataForWrite(WriteTranSession tran, Object fetchedObject) {
		Object pk = this.wrap.getValue(fetchedObject, table.getPKPropName()) ;
		if(pk == null){
			throw new ORMException("primary value is not setted. object to fecth is:" + fetchedObject) ;
		}
		
		BindedCompiledSQL bsql = this.sqlToLoadLazily.bind("pkValue", pk) ;
		bsql.setRowDataLoader(blobDataLoader).setExceptionOnNoRecordFound(true) ;
		bsql.setLockMode(LockMode.UPGRADE) ;
		
		Blob blob = (Blob) tran.findCell00(bsql, null) ;
		
		if(blob == null){
			return null ;
		}else{
			return new TranBlob(blob) ;
		}
	}

	public void shutdown() throws Exception {
	}

	public void startup() {
		String sqlForLazyLoad = "select " + columnName + " from " + table.getBusinessShape() + " where " + table.getPKColName() + " = :pkValue" ;
		this.sqlToLoadLazily = tm.getCompiledSQLBuilder().buildCompiledSQL(mapping, sqlForLazyLoad) ;
		this.sqlToLoadLazily.addParamPropMapping("pkValue", table.getPKPropName()) ;

		String sql = "update " + table.getBusinessShape() + " set " + this.columnName + " = ? where " + table.getPKColName() + " = :pkValue" ;
		this.sqlInsertCallback = tm.getCompiledSQLBuilder().buildCompiledSQL(mapping, sql) ;
		this.sqlInsertCallback.addParamPropMapping("pkValue", table.getPKPropName()) ;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
		this.debugService = guzzContext.getDebugService() ;
	}

}
