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

import java.util.HashMap;
import java.util.Map;

import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;

/**
 * 
 * 管理ObjectMapping，管理所有表和对象的映射关系。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ObjectMappingManager {
	
	private Map ghostVSTables = new HashMap() ;
	
	private Map ghostVSOMs = new HashMap() ; 
	
	/**通过域对象的名称或者完整的类名，获取对应的数据库表。*/
	public Table getTableByGhostName(String name) {
		return (Table) ghostVSTables.get(name) ;
	}
	
	public ObjectMapping getObjectMappingByName(String name) {
		return (ObjectMapping) ghostVSOMs.get(name) ;
	}	
	
	public void registerObjectMapping(ObjectMapping map){
		String[] names = map.getUniqueName() ;
		
		for(int i = 0 ; i < names.length ; i++){
			ghostVSOMs.put(names[i], map) ;
		}
		
		Table table = map.getTable() ;
		
		if(table != null){
			String[] ids = map.getUniqueName() ;
			for(int i = 0 ; i < ids.length ; i++){
				ghostVSTables.put(ids[i], table) ;
			}
		}
		
//		if(map instanceof POJOBasedObjectMapping){
//			POJOBasedObjectMapping pm = (POJOBasedObjectMapping) map ;
//			Business business = pm.getBusiness() ;
//			
//			ghostVSTables.put(business.getDomainClass().getName(), pm.getTable()) ;
//			
//		}
	}
	

}
