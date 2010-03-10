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
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.guzz.orm.rdms.Table;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.transaction.DBGroup;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ResultMapBasedObjectMapping extends AbstractObjectMapping {
	private final String id ;
	
	private final Class domainClass ;
	
	private final BeanWrapper beanWrapper ;
	
	private final List props = new LinkedList() ;
	
	private final Table table ;
		
	public ResultMapBasedObjectMapping(DBGroup dbGroup, String id, Class domainClass, Table table){
		super(dbGroup) ;
		this.id = id ;
		this.domainClass = domainClass ;
		beanWrapper = new BeanWrapper(domainClass) ;
		this.table = table ;
	}
	
	public x$ORM addPropertyMap(String propName, String colName, String dataType, String nullValue, ColumnDataLoader columnDataLoader){
		super.addPropertyMap(propName, colName, dataType, nullValue, columnDataLoader) ;
		
		x$ORM orm = (x$ORM) this.prop2ColsMapping.get(propName) ;
		props.add(orm) ;
		
		return orm ;
	}

	protected String getColDataType(String propName, String colName, String dataType) {
		if (StringUtil.isEmpty(dataType)){
			dataType = beanWrapper.getPropertyType(propName).getName() ;
		}
		return dataType ;
	}

	public String[] getUniqueName() {
		return new String[]{id} ;
	}

	public Object rs2Object(ResultSet rs) throws SQLException {
		//result map 采用ibatis模式，以ORM为准，如果不存在select的字段报错。
		
		Object obj = BeanCreator.newBeanInstance(this.domainClass) ;
		
		for(int i = 0 ; i < props.size() ; i++){
			x$ORM orm = (x$ORM) props.get(i) ;
			
			int index = rs.findColumn(orm.colName) ;
			Object value = orm.loadResult(rs, obj, index) ;
			this.beanWrapper.setValue(obj, orm.propName, value) ;
		}
		
		return obj ;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}
	
	/**
	 * 获取ResultMap的table对象。此对象一般仅包含tableName和shadow支持，也可能为null。
	 */
	public Table getTable(){
		return table ;
	}

}
