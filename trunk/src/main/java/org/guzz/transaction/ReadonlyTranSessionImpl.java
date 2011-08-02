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

import java.sql.Connection;
import java.sql.SQLException;

import org.guzz.connection.ConnectionFetcher;
import org.guzz.connection.DBGroupManager;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.exception.DaoException;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.service.core.DatabaseService;
import org.guzz.service.core.DebugService;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ReadonlyTranSessionImpl extends AbstractTranSessionImpl implements ReadonlyTranSession {
//	private static transient final Log log = LogFactory.getLog(ReadonlyTranSessionImpl.class) ;	
	
	private boolean allowDelay ;
	
	public ReadonlyTranSessionImpl(ObjectMappingManager omm, CompiledSQLManager compiledSQLManager, DebugService debugService, DBGroupManager dbGroupManager, boolean allowDelay){
		super(omm, compiledSQLManager, new ReadonlyConnectionFetcher(allowDelay), debugService, dbGroupManager, true) ;
		this.allowDelay = allowDelay ;
	}

	public boolean allowDelayRead() {
		return allowDelay ;
	}
	
}

class ReadonlyConnectionFetcher implements ConnectionFetcher{
	
	private boolean allowDelay ;
	
	public ReadonlyConnectionFetcher(boolean allowDelay){
		this.allowDelay = allowDelay ;
	}
	
	public Connection getConnection(PhysicsDBGroup dbGroup) {
		if(allowDelay){
			return openDelayReadConn(dbGroup) ;
		}else{
			return openNoDelayReadonlyConn(dbGroup) ;
		}
	}
	
	public Connection openDelayReadConn(PhysicsDBGroup dbGroup) {
		DatabaseService slaveDatabaseService = dbGroup.getSlaveDB() ;
		
		if(slaveDatabaseService != null && slaveDatabaseService.isAvailable()){
			Connection conn = null;
			try {
				conn = slaveDatabaseService.getDataSource().getConnection();
			} catch (SQLException e) {
				//be careful of conn leak.
				CloseUtil.close(conn) ;
				
				//TODO: add a check job to diagnose the datasource. refetch the connection from another slave datasource.
				throw new DaoException("fail to acquire readonly conn.", e) ;
			}

			try {
				conn.setReadOnly(true) ;
				
				return conn ;
			} catch (SQLException e) {
				//be careful of conn leak.
				CloseUtil.close(conn) ;
				
				throw new DaoException("fail to open readonly conn.", e) ;
			}
		}
		
		return openNoDelayReadonlyConn(dbGroup) ;
	}
	
	public Connection openNoDelayReadonlyConn(PhysicsDBGroup dbGroup){
		DatabaseService masterDatabaseService = dbGroup.getMasterDB() ;
		
		if(masterDatabaseService != null && masterDatabaseService.isAvailable()){
			Connection conn = null;
			try {
				conn = masterDatabaseService.getDataSource().getConnection();
				
				//were not make master database's connection to readonly
				return conn ;
			} catch (SQLException e) {
				//be careful of conn leak.
				CloseUtil.close(conn) ;
				
				//TODO: add a check job to diagnose the datasource. refetch the connection from another slave datasource.
				throw new DaoException("fail to acquire no-delay readonly conn.", e) ;
			}
		}
		
		throw new DaoException("no datasource is available.") ;
	}

	public boolean isAllowDelay() {
		return allowDelay;
	}

	public void setAllowDelay(boolean allowDelay) {
		this.allowDelay = allowDelay;
	}
	
}
