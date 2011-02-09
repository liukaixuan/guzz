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
 * Interface for mapping dynamic tables with runtime-determinated columns(different tables and different tables' columns mapped to a same domain class).
 * <p>
 * Only {@link POJOBasedObjectMapping} supports this feature.
 * </p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface CustomTableView extends ShadowTableView{
	
	/**
	 * Set ObjectMapping configured in the hbm.xml file. This method only run one time on startup.
	 */
	public void setConfiguredObjectMapping(POJOBasedObjectMapping configuredMapping) ;
	
	/**
	 * Get the runtime real ObjectMapping for the given tableCondition.
	 * <p>The invoker won't cache the returned {@link POJOBasedObjectMapping}, so the implementor should do the cache for performance critical system.
	 * </p>
	 */
	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) ;

}
