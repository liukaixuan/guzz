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

import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.Table;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanWrapper;


/**
 * 
 * 此类自动生成，和域对象一一对应绑定，定义域对象的各种信息。
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Business {
	
	private String name ;
	
	private BusinessInterpreter interpret ;
	
	private Class domainClass ;
	
	private ObjectMapping mapping ;
	
	private String dbGroup ;
	
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

	public void setName(String name) {
		this.name = name;
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

	public void setDbGroup(String dbGroup) {
		this.dbGroup = dbGroup;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}

	public void setBeanWrapper(BeanWrapper beanWrapper) {
		this.beanWrapper = beanWrapper;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(SimpleTable table) {
		this.table = table;
		table.setBusiness(this) ;
	}

}
