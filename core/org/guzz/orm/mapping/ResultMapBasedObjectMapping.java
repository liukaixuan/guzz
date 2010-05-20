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

import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.transaction.DBGroup;
import org.guzz.util.JRTInfo;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public final class ResultMapBasedObjectMapping extends AbstractObjectMapping {
	private final String id ;
	
	private final Class domainClass ;
	
	private final JavaBeanWrapper beanWrapper ;	
		
	public ResultMapBasedObjectMapping(DBGroup dbGroup, String id, Class domainClass, Table table){
		super(dbGroup, table) ;
		this.id = id ;
		this.domainClass = domainClass ;
		beanWrapper = BeanWrapper.createPOJOWrapper(domainClass) ;
	}	

	protected String getColDataType(String propName, String colName, String dataType) {
		if (StringUtil.isEmpty(dataType)){
			Class type = beanWrapper.getPropertyType(propName) ;
			
			if(JRTInfo.isJDK50OrHigher()){
				if(type.isEnum()){
					return  "enum.ordinal|" + type.getName() ;
				}
			}
			
			dataType = type.getName() ;
		}
		
		return dataType ;
	}

	public String[] getUniqueName() {
		return new String[]{id} ;
	}

	public Object rs2Object(ResultSet rs) throws SQLException {
		//result map 采用ibatis模式，以ORM为准，如果不存在select的字段报错。
		
		Object obj = BeanCreator.newBeanInstance(this.domainClass) ;
		
		TableColumn[] cols = getTable().getColumnsForSelect() ;
		
		for(int i = 0 ; i < cols.length ; i++){
			TableColumn col = cols[i] ;
			
			int index = rs.findColumn(col.getColNameForRS()) ;
			Object value = col.getOrm().loadResult(rs, obj, index) ;
			
			this.beanWrapper.setValue(obj, col.getPropName(), value) ;
		}
		
		return obj ;
	}

	public BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}	

}
