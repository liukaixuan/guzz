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

import org.guzz.orm.mapping.POJOBasedObjectMapping;

/**
 * 
 * the interface for mapping dynamic tables with runtime-determinated columns(different tables and differect tables' columns map to the same domain object).
 * <p>
 * only POJOBasedObjectMapping supports this feature.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface CustomTableView {
	
	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) ;
	
	public Object getValue(Object domainObject, String propName) ;
	
	public void setValue(Object domainObject, String propName, Object value) ;

}
