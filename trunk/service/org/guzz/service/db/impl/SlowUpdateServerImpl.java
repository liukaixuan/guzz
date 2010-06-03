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
package org.guzz.service.db.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContext;
import org.guzz.exception.DataTypeException;
import org.guzz.exception.GuzzException;
import org.guzz.jdbc.JDBCTemplate;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.orm.type.BigIntSQLDataType;
import org.guzz.orm.type.IntegerSQLDataType;
import org.guzz.orm.type.SQLDataType;
import org.guzz.orm.type.ShortSQLDataType;
import org.guzz.orm.type.StringSQLDataType;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.impl.IncUpdateBusiness;
import org.guzz.service.db.SlowUpdateServer;
import org.guzz.transaction.DBGroup;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;
import org.guzz.util.thread.DemonQueuedThread;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SlowUpdateServerImpl extends AbstractService implements SlowUpdateServer, GuzzContextAware {
	private static transient final Log log = LogFactory.getLog(SlowUpdateServerImpl.class) ;
	
	private TransactionManager tm ;
	
	private GuzzContext guzzContext ;
	
	public static final String CONFIG_BATCH_SIZE = "batchSize" ;
	
	public static final String CONFIG_PAGE_SIZE = "pageSize" ;
	
	/**一次性最多读取页数据进行数据合并*/
	public static final String CONFIG_COMBINE_PAGE_COUNT = "combinePageCount" ;
	
	/**Millseconds to wait for the next round of updates checking.*/
	public static final String CONFIG_UPDATE_INTERVAL = "updateInterval" ;
	
	private int batchSize = 100 ;
	
	private int pageSize = 100 ;
	
	private int combinePageCount = 1 ;
		
	protected UpdateToMasterDBThread updateThread ;
	
	public int getLatency() {
		//FIXME: implmemet this
		return 0;
	}

	public boolean configure(ServiceConfig[] scs) {			
		if(scs == null || scs.length == 0){
			//没有配置此项，不启动。
			log.warn("slowUpdateServer is not started. no configuration found.") ;
			return false ;
		}
		
		ServiceConfig sc = scs[0] ;
		
		String m_batchSize = (String) sc.getProps().remove(CONFIG_BATCH_SIZE) ;
		String m_pageSize = (String) sc.getProps().remove(CONFIG_PAGE_SIZE) ;
		String m_combinePageCount = (String) sc.getProps().remove(CONFIG_COMBINE_PAGE_COUNT) ;
		String m_updateInterval = (String) sc.getProps().remove(CONFIG_UPDATE_INTERVAL) ;
		
		this.batchSize = StringUtil.toInt(m_batchSize, this.batchSize) ;
		this.pageSize = StringUtil.toInt(m_pageSize, this.pageSize) ;
		this.combinePageCount = StringUtil.toInt(m_combinePageCount, this.combinePageCount) ;
		
		int updateInterval = StringUtil.toInt(m_updateInterval, -1) ;
		
		//启动更新线程
		if(updateThread == null){
			updateThread = new UpdateToMasterDBThread(10) ;
			updateThread.start() ;
		}
		
		if(updateInterval > 10){
			updateThread.setMillSecondsToSleep(updateInterval) ;
		}
		
		return true ;
	}

	public void shutdown() {
		if(updateThread != null){
			updateThread.shutdown() ;
			updateThread = null ;
		}
	}
	
	class UpdateToMasterDBThread extends DemonQueuedThread{
		
		public UpdateToMasterDBThread(int queueSize){
			super("slowUpdateServerThread", queueSize) ;
		}
		
		protected boolean doWithTheQueue() throws SQLException{
			TransactionManager tm = SlowUpdateServerImpl.this.tm ;
			if(tm == null) return false ;
			
			ReadonlyTranSession readSession = null ;
			WriteTranSession writeSession = null ;
			
			try{
				LinkedList updates = new LinkedList() ;				
				long maxIdNum = 0 ;
				
				readSession = tm.openDelayReadTran() ;
				
				//读取一次批量处理的数据
				for(int i = 1 ; i <= combinePageCount ; i++){
					SearchExpression se = SearchExpression.forClass(IncUpdateBusiness.class, i, pageSize) ;
					se.setOrderBy("id asc") ;
					List m_updates = readSession.list(se) ;
					
					updates.addAll(m_updates) ;
					
					if(m_updates.size() < pageSize){
						break ;
					}
				}
								
				if(updates.isEmpty()){
					return false ;
				}
				
				//记录下最大的id
				maxIdNum = ((IncUpdateBusiness) updates.get(updates.size() - 1)).getId() ;
				
				//合并数据
				List combinedUpdates = combineIncUpdateOperations(updates) ;
				
				//更新到主数据库
				writeSession =tm.openRWTran(false) ;
				for(int i = 0 ; i < combinedUpdates.size() ; i++){
					IncUpdateBusiness obj = (IncUpdateBusiness) combinedUpdates.get(i) ;
					
					if(obj.getCountToInc() == 0) continue ;
					
					MasterIncTableModel tableModel = this.getTableModel(obj) ;
										
					JDBCTemplate masterJDBC = writeSession.createJDBCTemplateByDbGroup(obj.getDbGroup()) ;
					
					masterJDBC.executeUpdate(
							tableModel.sqlToUpdate, 
							new SQLDataType[]{tableModel.incCountDataType, tableModel.pkDataType},
							new Object[]{new Integer(obj.getCountToInc()), obj.getPkValue()}
						) ;
				}
				
				//从临时表删除数据				
				CompiledSQL deleteTempSQL = tm.getCompiledSQLBuilder().buildCompiledSQL(IncUpdateBusiness.class, "delete from @@" + IncUpdateBusiness.class.getName() + " where @id <= :id") ;
				deleteTempSQL.addParamPropMapping("id", "id") ;
				
				writeSession.executeUpdate(deleteTempSQL.bind("id", maxIdNum)) ;
				
				writeSession.commit() ;
			}catch(Exception e){
				if(writeSession != null){
					writeSession.rollback() ;
				}
				
				throw new GuzzException(e) ;
			}finally{
				if(writeSession != null){
					writeSession.close() ;
				}
				
				if(readSession != null){
					readSession.close() ;
				}
			}
			
			return true ;
		}
		
		/**合并对同一表同一字段的更新操作*/
		protected List combineIncUpdateOperations(List updates){
			int orgSize = updates.size() ;
			
			LinkedList combinedUpdates = new LinkedList() ;
			HashMap ups = new HashMap() ;
			
			Iterator i = updates.iterator() ;
			
			while(i.hasNext()){	
				IncUpdateBusiness obj = (IncUpdateBusiness) i.next() ;
				i.remove() ;
				
				String key = obj.getTableName() + "@ " + obj.getColumnToUpdate() + "@ " + obj.getPkValue() ;
				
				IncUpdateBusiness oldObj = (IncUpdateBusiness) ups.get(key) ;
				if(oldObj == null){
					ups.put(key, obj) ;
					combinedUpdates.addLast(obj) ;
				}else{
					oldObj.setCountToInc(oldObj.getCountToInc() + obj.getCountToInc()) ;
				}	
			}
			
			if(log.isDebugEnabled()){
				log.debug("combine inc updates. compress from [" + orgSize + "] sqls to [" + combinedUpdates.size() + "] sqls.") ;
			}
			
			return combinedUpdates ;
		}
		
		private Map tableCache = new HashMap() ;
		
		//数据库数据结构建模
		protected MasterIncTableModel getTableModel(IncUpdateBusiness obj){
			String key = obj.getTableName() + "@ " + obj.getColumnToUpdate() ;
			
			MasterIncTableModel model = (MasterIncTableModel) tableCache.get(key) ;
			
			if(model == null){
				String tableName = obj.getTableName() ;
				String pkColName = obj.getPkColunName() ;
				
				WriteTranSession writeMasterSession =tm.openRWTran(true) ;
				SQLDataType dataType = null ;
				try{
					DBGroup group = guzzContext.getDBGroup(obj.getDbGroup()) ;
					
					JDBCTemplate masterJDBC = writeMasterSession.createJDBCTemplateByDbGroup(obj.getDbGroup()) ;
					
					String sql = "select " + pkColName + " from " + tableName ;
					sql = group.getDialect().getLimitedString(sql, 0, 1) ;
					
					dataType = (SQLDataType) masterJDBC.executeQuery(sql, 
						new SQLQueryCallBack(){
							public Object iteratorResultSet(ResultSet rs) throws Exception {
								ResultSetMetaData rsmd = rs.getMetaData() ;
								
								int pkType = rsmd.getColumnType(1) ;
								
								if(pkType == Types.INTEGER){
									return new IntegerSQLDataType() ;
								}else if(pkType == Types.BIGINT|| pkType == Types.NUMERIC){
									return new BigIntSQLDataType() ;
								}else if(pkType == Types.SMALLINT || pkType == Types.TINYINT){
									return new ShortSQLDataType() ;
								}else if(pkType == Types.CHAR || pkType == Types.VARCHAR){
									return new StringSQLDataType() ;
								}else{
									throw new DataTypeException("unknown primary key column type. only support:int, bigint, smallint, char, varchar, numeric#as_bigint") ;
								}
							}
						}
					) ;
				}finally{
					writeMasterSession.close() ;
				}
								
				String sqlToUpdate = "update " + tableName + " set " + obj.getColumnToUpdate() + " = " + obj.getColumnToUpdate() + " + ? where " + pkColName + " =?" ;
			
				model = new MasterIncTableModel() ;
				model.pkDataType = dataType ;
				model.sqlToUpdate = sqlToUpdate ;
				model.incCountDataType = new IntegerSQLDataType() ; 
				
				tableCache.put(key, model) ;
			}
			
			return model ;
		}
	}

	static class MasterIncTableModel{
		public SQLDataType pkDataType ;
		
		public SQLDataType incCountDataType ;
		
		public String sqlToUpdate ;		
		
	}

	public boolean isAvailable() {
		return updateThread != null ;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext ;
	}

	public void startup() {
		this.tm = guzzContext.getTransactionManager() ;	
	}

}
