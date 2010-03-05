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
package org.guzz.bytecode;

import java.util.HashMap;

import org.guzz.lang.NullValue;
import org.guzz.orm.Business;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.pojo.DynamicUpdatable;
import org.guzz.pojo.GuzzProxy;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.ArrayUtil;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * 存放proxy需要的域对象信息。
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class BusinessDescriptor {
	
	HashMap props = new HashMap() ;
		
	private TransactionManager transactionManager ;
	
	private Business business ;
	
	private String[] updatableProps ;
	private String[] lazyProps ;
		
	//Map<setMethod().getName(), index in updatableProps>
	private HashMap setPropsMap = new HashMap() ;
	
	//Map<setMethod().getName(), index in lazyProps>
	private HashMap setLazyPropsMap = new HashMap() ;	
	
	public BusinessDescriptor(TransactionManager transactionManager, Business business){
		this.transactionManager = transactionManager ;
		this.business = business ;
		
		init() ;
	}
	
	protected void init(){
		updatableProps = business.getTable().getPropsForUpdate() ;
		lazyProps = business.getTable().getLazyUpdateProps() ;
		
		//updatableProps includes lazyProps
		if(lazyProps.length > 0){
			updatableProps = (String[]) ArrayUtil.addToArray(updatableProps, lazyProps) ;
		}
			
		for(int i = 0 ; i < updatableProps.length ; i++){
			String propName = updatableProps[i] ;
			setPropsMap.put(business.getBeanWrapper().getWriteMethod(propName).getName(), new Integer(i)) ;
		}
		
		for(int i = 0 ; i < lazyProps.length ; i++){
			String propName = lazyProps[i] ;
			setLazyPropsMap.put(business.getBeanWrapper().getWriteMethod(propName).getName(), new Integer(i)) ;
		}
	}
	
	public void addLazyColumn(ObjectMapping.x$ORM orm){
		BeanWrapper wrapper = business.getBeanWrapper() ;
		
		LazyColumn lc = new LazyColumn(transactionManager, business.getTable(), business.getName(), wrapper, orm) ;
		
		props.put(wrapper.getReadMethod(orm.propName).getName(), lc) ;
	}
	
	public LazyColumn match(String propName){
		return (LazyColumn) props.get(propName) ;
	}
	
	/**
	 * proxy实现必须实现的接口。这些接口将在guzz其他地方使用。
	 */
	public Class[] getMustProxiedInterfaces(){
		Table table = business.getTable() ;
		Class businessClass = business.getDomainClass() ;
		
		if(table.hasLazy() && table.isDynamicUpdateEnable()){
			if(DynamicUpdatable.class.isAssignableFrom(businessClass)){
				return (new Class[]{GuzzProxy.class}) ;
			}else{
				return (new Class[]{DynamicUpdatable.class, GuzzProxy.class}) ;
			}
		}else if(table.hasLazy()){ //包含lazy属性，但不需要dynamic update.
			return (new Class[]{LazyPropChangeDetector.class, GuzzProxy.class}) ;
		}else if(table.isDynamicUpdateEnable()){ //需要dynamic update，但是没有lazy属性。
			if(!DynamicUpdatable.class.isAssignableFrom(businessClass)){
				return (new Class[]{DynamicUpdatable.class, GuzzProxy.class}) ;
			}else{
				return (new Class[]{GuzzProxy.class}) ;
			}
		}
		
		//shouldn't be here.
		return (new Class[]{GuzzProxy.class}) ;
	}
	
	public String[] getOrderedAllUpdatableProps(){
		return this.updatableProps ;
	}
	
	public String[] getOrderedAllUpdatableLazyProps(){
		return this.lazyProps ;
	}
	
	public Integer getIndexOfWritedProp(String methodName){
		return (Integer) this.setPropsMap.get(methodName) ;
	}
	
	public Integer getIndexOfWritedLazyProp(String methodName){
		return (Integer) this.setLazyPropsMap.get(methodName) ;
	}
	
	public static class LazyColumn{
		private CompiledSQL sqlForLoadLazy ;
		private ObjectMapping.x$ORM orm ;
		private Table table ;
		private BeanWrapper wrap ;
		
		private TransactionManager tm ;
		
		public LazyColumn(TransactionManager tm, Table table, String businessName, BeanWrapper wrap, ObjectMapping.x$ORM orm){
			this.tm = tm ;
			this.table = table ;
			this.wrap = wrap ;
			this.orm = orm ;
			
			String sql = "select " + orm.colName + " from " + MarkedSQL.TABLE_START_TAG_IN_MARKED_SQL + businessName + " where " + table.getPKColName() + "=:id" ;
			sqlForLoadLazy = tm.getCompiledSQLBuilder().buildCompiledSQL(businessName, sql) ;
		}
		
		public Object loadProperty(Object fetchedObject){
			String propToLoad = orm.propName ;
			Object value = wrap.getValueUnderProxy(fetchedObject, propToLoad) ;
			
			//return the cached value
			if(value != null){
				if(value instanceof NullValue){
					return null ;
				}else{
					return value ;
				}
			}
			
			//load the value from db
			if(orm.columnDataLoader != null){
				value = orm.columnDataLoader.loadLazyData(fetchedObject) ;
			}else{
				Object pkValue = wrap.getValue(fetchedObject, table.getPKPropName()) ;
				
				ReadonlyTranSession session = tm.openDelayReadTran() ;
				try{
					value = session.findCell00(sqlForLoadLazy.bind("id", pkValue), orm.dataTypeName) ;
				}finally{
					session.close() ;
				}
			}
			
			//set to cache
			wrap.setValueUnderProxy(fetchedObject, propToLoad, value) ;
			
			return value ;
		}
		
		/**
		 * 从主数据库读取，不进行缓存。供写操作使用。属性读取后，不会自动set到 @param fetchedObject 中。
		 * <p/>
		 * 数据库异常由调用者处理。
		 */
		public Object getPropertyForWrite(WriteTranSession tran, Object fetchedObject){
			Object value = null ;
			
			//load the value from db
			if(orm.columnDataLoader != null){
				value = orm.columnDataLoader.loadLazyDataForWrite(tran, fetchedObject) ;
			}else{
				Object pkValue = wrap.getValue(fetchedObject, table.getPKPropName()) ;
				
				value = tran.findCell00(sqlForLoadLazy.bind("id", pkValue), orm.dataTypeName) ;
			}
			
			return value ;
		}
	}
	
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public Business getBusiness() {
		return business;
	}
	
	public Class getDomainClass(){
		return business.getDomainClass() ;
	}
	
	public Table getTable(){
		return business.getTable() ;
	}

}
