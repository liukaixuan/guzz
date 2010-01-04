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

import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.service.core.DebugService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DataSourceTransactionManager implements TransactionManager {
	
	private CompiledSQLManager compiledSQLManager ;
	
	//static? no. allow mulit instances of web apps under one container.
	protected CompiledSQLBuilder compiledSQLBuilder ;
			
	private ObjectMappingManager omm ;
	
	private DebugService debugService ;
	
	private DBGroupManager dbGroupManager ;
	
	public DataSourceTransactionManager(ObjectMappingManager omm, CompiledSQLManager compiledSQLManager,
			CompiledSQLBuilder compiledSQLBuilder, DebugService debugService, DBGroupManager dbGroupManager) {
		this.omm = omm ;
		this.compiledSQLManager = compiledSQLManager ;
		this.compiledSQLBuilder = compiledSQLBuilder ;
		this.debugService = debugService ;
		this.dbGroupManager = dbGroupManager ;
	}
	
	public ReadonlyTranSession openDelayReadTran() {
		return new ReadonlyTranSessionImpl(omm, compiledSQLManager, debugService, dbGroupManager, true) ;
	}
	
//	public ReadonlyTranSession openDelayReadTran(DataSourceProvicer dsp){
//		Connection conn;
//		try {
//			conn = dsp.getDataSource().getConnection();
//		} catch (SQLException e) {
//			//TODO: add a check job to diagnose the datasource. refetch the connection from another slave datasource.
//			throw new DaoException(e) ;
//		}
//
//		try {
//			//不设置标记
////			conn.setReadOnly(true) ;
//			return new ReadonlyTranSessionImpl(dialect, omm, compiledSQLManager, serviceManager, conn, true);
//		} catch (SQLException e) {
//			//be careful of conn leak.
//			CloseUtil.close(conn) ;
//			
//			throw new DaoException("fail to open readonly tran.", e) ;
//		}		
//	}

	public WriteTranSession openRWTran(boolean autoCommit)  {
		return new WriteTranSessionImpl(omm, compiledSQLManager, debugService, dbGroupManager, autoCommit) ;
	}
	
//	public WriteTranSession openRWTran(Connection conn, boolean autoCommit){
//		try {
//			conn.setAutoCommit(autoCommit) ;
//			return new WriteTranSessionImpl(dialect, omm, compiledSQLManager, serviceManager, conn);
//		} catch (SQLException e) {
//			//谁申请谁释放
////			CloseUtil.close(conn) ;
//			
//			throw new DaoException("fail to open rw tran.", e) ;
//		}
//	}
//	
//	public WriteTranSession openRWTran(DataSourceProvicer dsp, boolean autoCommit){
//		Connection conn;
//		try {
//			conn = dsp.getDataSource().getConnection();
//		} catch (SQLException e) {
//			throw new DaoException("fail to get rw connection.", e) ;
//		}
//		
//		try {
//			conn.setAutoCommit(autoCommit) ;
//			return new WriteTranSessionImpl(dialect, omm, compiledSQLManager, serviceManager, conn);
//		} catch (SQLException e) {
//			CloseUtil.close(conn) ;
//			throw new DaoException("fail to open rw tran.", e) ;
//		}
//	}

	public ReadonlyTranSession openNoDelayReadonlyTran() {
		return new ReadonlyTranSessionImpl(omm, compiledSQLManager, debugService, dbGroupManager, false) ;
	}
	
	public CompiledSQLBuilder getCompiledSQLBuilder() {
		return compiledSQLBuilder;
	}

}
