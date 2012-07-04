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
package org.guzz.orm.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContextImpl;
import org.guzz.bytecode.BusinessDescriptor;
import org.guzz.bytecode.ProxyFactory;
import org.guzz.connection.DBGroup;
import org.guzz.orm.Business;
import org.guzz.orm.ColumnORM;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.type.SQLDataType;
import org.guzz.pojo.GuzzProxy;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

/**
 * 
 * POJO ORM Mapping
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public final class POJOBasedObjectMapping extends AbstractObjectMapping{
	private static final Log log = LogFactory.getLog(POJOBasedObjectMapping.class) ;

	private Business business ;
		
	private BeanWrapper beanWrapper ;
	
	private ProxyFactory proxyFactory ;
	
	private GuzzContextImpl guzzContext ;
	
	private BusinessDescriptor businessDescriptor ;
	
	public POJOBasedObjectMapping(GuzzContextImpl guzzContext, DBGroup dbGroup, Table table){
		super(dbGroup, table) ;
		
		this.guzzContext = guzzContext ;
		this.proxyFactory = guzzContext.getProxyFactory() ;
	}
	
	public Object proxyDomainObject(){
		Table table = this.business.getTable() ;
		Object obj = null ;
		
		if(table.hasLazy() || table.isDynamicUpdateEnable()){
			obj = proxyFactory.proxy(getBusinessDescriptor()) ;
		}else{
			obj = BeanCreator.newBeanInstance(this.business.getDomainClass()) ;
		}
		
		return obj ;
	}

	public BusinessDescriptor getBusinessDescriptor() {
		if(businessDescriptor == null){
			Table table = this.business.getTable() ;
			
			BusinessDescriptor ld = new BusinessDescriptor(guzzContext.getTransactionManager(), business) ;
			String[] lazyProps = table.getLazyProps() ;
			for(int i = 0 ; i < lazyProps.length ; i++){
				ld.addLazyColumn((ColumnORM) getTable().getColumnByPropName(lazyProps[i]).getOrm()) ;
			}
			
			this.businessDescriptor = ld ;
		}
		
		return this.businessDescriptor;
	}
	
	/**将当前@rs行的记录转换成Object对象并返回
	 * @throws SQLException */
	public Object rs2Object(ResultSet rs, Class resultClass) throws SQLException{
		boolean isMap = false ;
		BeanWrapper bw = this.beanWrapper ;
		
		Object instance = resultClass == null ? proxyDomainObject() : BeanCreator.newBeanInstance(resultClass) ;
		
		if(instance instanceof Map){
			isMap = true ;
		}else{
			bw = resultClass == null ? this.beanWrapper : BeanWrapper.createPOJOWrapper(resultClass) ;
		}
		
		if(instance instanceof GuzzProxy){
			((GuzzProxy) instance).markReading() ;
		}
		
		Table t = getTable() ;
		ResultSetMetaData  meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		
		for(int i = 1 ; i <= count ; i++){
			String colName = meta.getColumnLabel(i) ;
			TableColumn col = t.getColumnByColNameInRS(colName) ;
			ColumnORM orm = col != null ? col.getOrm() : null ;
			
			if(orm != null){
				//进行映射。
				Object value = orm.loadResult(rs, instance, i) ;
				
				if(isMap){
					((Map) instance).put(col.getPropName(), value) ;
				}else{
					bw.setValue(instance, col.getPropName(), value) ;
				}
			}else if(resultClass != null){
				//如果设置了resultClass，尽可能多的赋值给resultClass；如果某个属性resultClass不接受，直接报错！
				//原则：指定resultClass后，resultClass不允许忽略任何结果集。【这是IBatis的策略，直接借用】
				
				Object value = rs.getObject(i) ;
				
				if(isMap){
					((Map) instance).put(colName, value) ;
				}else{
					bw.setValue(instance, colName, value) ;
				}
			}else{
				//有时SQL中有一些计算出的字段，数据库中没有这个字段，但Bean中有这个字段. -by 波波
				String propName = getPropName(colName, bw);
				
				if(isMap){
					((Map) instance).put(propName == null ? colName : propName, rs.getObject(i)) ;
				}else{
					if (propName != null) {
						String typeName = bw.getPropertyTypeName(propName);
						SQLDataType sqlType = getDbGroup().getDialect().getDataType(typeName);
						Object value = sqlType.getSQLValue(rs, i);
						
						bw.setValue(instance, propName, value) ;
					} else {
						//TODO: business忽略某些结果集。在debug模式下，DebugService发出警告！
						if(log.isDebugEnabled()){
							log.debug("warning:ignore ResultSet column:[" + colName + "] in POJOBasedObjectMapping for business:[" + this.business.getName() + "].") ;
						}
					}
				}
			}
		}
		
		if(instance instanceof GuzzProxy){
			((GuzzProxy) instance).unmarkReading() ;
		}
		
		return instance ;
	}
	
	protected String getPropName(String colName, BeanWrapper bw) {
		if (bw instanceof JavaBeanWrapper) {
			List<String> props = ((JavaBeanWrapper)bw).getAllWritabeProps();
			for (String prop : props) {
				if (colName.equalsIgnoreCase(prop)) {
					return prop;
				}
			}
		}
		return null;
	}
	
	
	protected String getColDataType(String propName, String colName, String dataType){
		if (StringUtil.isEmpty(dataType)){
			return beanWrapper.getPropertyTypeName(propName) ;
		}
		
		return dataType ;
	}
	
	public String dump(){
		return this.getClass().toString() ;
	}

	public POJOBasedObjectMapping replicate(BeanWrapper newBeanWrapper){
		Business newBusiness = getBusiness().newCopy() ;
		Table newTable = newBusiness.getTable().newCopy() ;
		
		newBusiness.setTable(newTable) ;
		newBusiness.setConfiguredBeanWrapper(newBusiness.getConfiguredBeanWrapper()) ;
		newBusiness.setBeanWrapper(newBeanWrapper) ;
		
		POJOBasedObjectMapping newMap = new POJOBasedObjectMapping((GuzzContextImpl) this.guzzContext, this.dbGroup, newTable) ;
		newBusiness.setMapping(newMap) ;
		newMap.setBusiness(newBusiness) ;
		
		return newMap ;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
		this.beanWrapper = business.getBeanWrapper() ;
	}

	public String[] getUniqueName() {
		return new String[]{this.business.getDomainClass().getName(), this.business.getName()} ;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}

}


