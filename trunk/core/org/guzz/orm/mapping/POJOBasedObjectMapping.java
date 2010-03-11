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

import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.bytecode.BusinessDescriptor;
import org.guzz.bytecode.ProxyFactory;
import org.guzz.orm.Business;
import org.guzz.orm.ColumnORM;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.pojo.GuzzProxy;
import org.guzz.transaction.DBGroup;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * POJO ORM Mapping
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public final class POJOBasedObjectMapping extends AbstractObjectMapping {

	private Business business ;
		
	private BeanWrapper beanWrapper ;
	
	private ProxyFactory proxyFactory ;
	
	private GuzzContext guzzContext ;
	
	private BusinessDescriptor businessDescriptor ;
	
	public POJOBasedObjectMapping(GuzzContextImpl guzzContext, DBGroup dbGroup, Business business){
		super(dbGroup, business.getTable()) ;
		
		this.guzzContext = guzzContext ;
		this.proxyFactory = guzzContext.getProxyFactory() ;
		this.business = business ;
		this.business.setMapping(this) ;
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
	public Object rs2Object(ResultSet rs) throws SQLException{
		//rs记录是按照table中的select columns顺序获取到的，我们可以直接根据这个顺序进行绑定。省略metadata查询。
		//按照select columns顺序查找失败：某些指定的查询语句(SearchTerm)，可能按照不同的顺序和字段个数进行查询并通过此处ORM。
//		String[] columnsForSelect = table.getColumnsForSelect() ;
//		Object obj = BeanCreator.newBeanInstance(this.business.getDomainClass()) ;
//		
//		for(int i = 1 ; i <= columnsForSelect.length ; i++){
//			ColumnORM orm = (ColumnORM) col2PropsMapping.get(columnsForSelect[i]) ;
//			
//			//没有对应的映射，不进行映射。TODO: 在debug模式下，发出警告。
//			if(orm != null){
//				Object value = orm.sqlDataType.getSQLValue(rs, i) ;
//				this.beanWrapper.setValue(obj, orm.propName, value) ;
//			}
//		}
		
		ResultSetMetaData  meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		Object obj = proxyDomainObject() ;
		
		if(obj instanceof GuzzProxy){
			((GuzzProxy) obj).markReading() ;
		}
		
		Table t = getTable() ;
		
		for(int i = 1 ; i <= count ; i++){
			String colName = meta.getColumnName(i) ;
			TableColumn col = t.getColumnByColName(colName) ;
			ColumnORM orm = col != null ? col.getOrm() : null ;
			
			//没有对应的映射，不进行映射。TODO: 在debug模式下，发出警告。
			if(orm != null){
				Object value = orm.loadResult(rs, obj, i) ;
				this.beanWrapper.setValue(obj, col.getPropName(), value) ;
			}
		}
		
		if(obj instanceof GuzzProxy){
			((GuzzProxy) obj).unmarkReading() ;
		}
		
		return obj ;
	}
	
	protected String getColDataType(String propName, String colName, String dataType){
		if (StringUtil.isEmpty(dataType)){
			dataType = beanWrapper.getPropertyType(propName).getName() ;
		}
		return dataType ;
	}

	public void setDomainClass(Class domainClass) {
		this.beanWrapper = new BeanWrapper(domainClass) ;
		
		this.business.setDomainClass(domainClass) ;
		this.business.setBeanWrapper(beanWrapper) ;
	}
	
	public String dump(){
		return this.getClass().toString() ;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public String[] getUniqueName() {
		return new String[]{this.business.getDomainClass().getName(), this.business.getName()} ;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}

}


