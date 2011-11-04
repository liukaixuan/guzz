/*
 * Copyright 2008-2011 the original author or authors.
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.connection.ConnectionFetcher;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.exception.DaoException;
import org.guzz.exception.JDBCException;
import org.guzz.util.CloseUtil;

/**
 *
 * Connections' holder for {@link TranSession}.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ConnectionsGroup {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
	
	private final ConnectionFetcher connectionFetcher ;
			
	private IsolationsSavePointer isp ;
	
	private int lastIsolationLevel = 0 ;
	

	/**保存已经打开的连接。针对同一个数据库只打开一个连接（保证事务提交）。*/
	protected final Map opennedConnections = new HashMap() ;
	
	public ConnectionsGroup(ConnectionFetcher connectionFetcher){
		this.connectionFetcher = connectionFetcher ;
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
	
	public void rollback() throws DaoException {
		SQLException ex = null ;
		Iterator i = this.opennedConnections.values().iterator() ;
		StringBuffer sb = null ;
		
		while(i.hasNext()){
			Connection conn = (Connection) i.next() ;
			
			try {
				//所有连接全部忽略错误，并且rollback。
				conn.rollback() ;				
			} catch (SQLException e) {
				if(ex != null){
					if(sb == null){//combine all exceptions before the last one together, and re-throw the last one.
						sb = new StringBuffer() ;
					}
					
					sb.append("[errorCode:").append(ex.getErrorCode()).append(", msg:").append(ex.getMessage()).append("];") ;
				}
				
				ex = e ;
			}
		}
		
		if(ex != null){//find exception
			if(sb == null){ //only one exception throwed.
				throw new DaoException(ex) ;
			}else{
				throw new DaoException(sb.toString(), ex) ;
			}
		}
	}

	public void close() {
		if(this.isIsolationLevelChanged()){
			log.warn("Some connections' transaction isolations have changed but never reset back before closing.") ;
		}
		
		Iterator i = this.opennedConnections.values().iterator() ;
		
		while(i.hasNext()){
			Connection conn = (Connection) i.next() ;
			
			CloseUtil.close(conn) ;
		}
		
		this.opennedConnections.clear() ;
	}

	public Connection getConnection(PhysicsDBGroup fdb) {
		Connection conn = (Connection) this.opennedConnections.get(fdb.getGroupName()) ;
			
		if(conn == null){
			conn = connectionFetcher.getConnection(fdb) ;
			this.opennedConnections.put(fdb.getGroupName(), conn) ;
			
			if(this.isp != null){
				try {
					setTransactionIsolation(conn, this.lastIsolationLevel) ;
				} catch (SQLException e) {
					throw new JDBCException("failed to set isolation to level " + this.lastIsolationLevel, e, null) ;
				}
			}
		}
		
		return conn ;
	}
	
	public void setTransactionIsolation(Connection conn, int level) throws SQLException{
		if(isp != null){
			isp.setIsolation(conn, level) ;
		}else{
			this.setTransactionIsolation(level).setIsolation(conn, level) ;
		}
	}
	
	public IsolationsSavePointer setTransactionIsolation(int isolationLevel) {
		if(lastIsolationLevel == isolationLevel){
			return isp ;
		}
		
		IsolationsSavePointer i = new IsolationsSavePointer(this.isp, isolationLevel) ;
		this.lastIsolationLevel = isolationLevel ;
		this.isp = i ;
		
		return i;
	}

	public boolean isIsolationLevelChanged() {
		return isp != null && (isp.hasChangedConnections() || isp.getParentSavePointer() != null);
	}

	/**
	 * Restore all changed connections' isolation levels to the given savepointer.
	 */
	public void resetTransactionIsolationTo(IsolationsSavePointer savePointer) {
		if(savePointer == null) return ;
		
		try {
			savePointer.restoreIsolationsToParentLevel() ;
		} catch (SQLException e) {
			throw new JDBCException("failed to restore Isolations.", e, null) ;
		}
		
		if(savePointer.getParentSavePointer() != null){
			this.isp = savePointer.getParentSavePointer() ;
			this.lastIsolationLevel = savePointer.getParentSavePointer().getIsolationLevel() ;
		}else{
			this.isp = null ;
			this.lastIsolationLevel = 0 ;
		}
	}

	public void resetTransactionIsolationToLastSavePointer() {
		this.resetTransactionIsolationTo(this.isp) ;
	}

}
