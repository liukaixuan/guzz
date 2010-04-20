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
package org.guzz.orm;

import org.guzz.orm.rdms.Table;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;


/**
 * 
 * 此类自动生成，和域对象一一对应绑定，定义域对象的各种信息。
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Business {
	
	private final String name ;
	
	private BusinessInterpreter interpret ;
	
	private Class domainClass ;
	
	private ObjectMapping mapping ;
	
	private final String dbGroup ;

	/**基础的BeanWrapper*/
	private JavaBeanWrapper configuredBeanWrapper ;
	
	/**
	 * 使用运行时需要使用的beanwrapper。一般与configuredBeanWrapper相同，但在CustomTableView情况下可能存在差异。
	 */
	private BeanWrapper beanWrapper ;
	
	
	private Table table ;
	
	public Business(String name, String dbGroup){
		this.name = name ;
		if(StringUtil.isEmpty(dbGroup)){
			dbGroup = "default" ;
		}
		this.dbGroup = dbGroup ;
	}

	public String getName() {
		return name;
	}

	public BusinessInterpreter getInterpret() {
		return interpret;
	}

	public void setInterpret(BusinessInterpreter interpret) {
		this.interpret = interpret;
	}

	public Class getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(Class domainClass) {
		this.domainClass = domainClass;
	}

	public ObjectMapping getMapping() {
		return mapping;
	}

	public void setMapping(ObjectMapping mapping) {
		this.mapping = mapping;
	}

	public String getDbGroup() {
		return dbGroup;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper == null ? configuredBeanWrapper : this.beanWrapper;
	}

	public void setBeanWrapper(BeanWrapper beanWrapper) {
		this.beanWrapper = beanWrapper;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	public Business newCopy(){
		Business b = new Business(this.name, this.dbGroup) ;
		
		b.beanWrapper = this.beanWrapper ;
		b.configuredBeanWrapper = this.configuredBeanWrapper ;
		b.domainClass = this.domainClass ;
		b.interpret = this.interpret ;
		b.mapping = this.mapping ;
		b.table = this.table ;
		
		return b ;
	}

	public JavaBeanWrapper getConfiguredBeanWrapper() {
		return configuredBeanWrapper;
	}

	public void setConfiguredBeanWrapper(JavaBeanWrapper configuredBeanWrapper) {
		this.configuredBeanWrapper = configuredBeanWrapper;
	}

}
