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
package org.guzz.pojo;

/**
 * 
 * The POJO Object that can tell which properties have been changed inside it?
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface DynamicUpdatable {
	
	/**
	 * List changed attributes comparing with the database. Including lazied ones.
	 * 
	 * <p>Only props holding mappings to database should be returned. TODO:check for this.<p>
	 * 
	 * @return attributea to update. return null to save all props, return String[0] to ignore update operation.
	 */
	public String[] getChangedProps() ;
	
	/**
	 * Reset the counter of the changed properties.
	 */
	public void resetChangeCounter() ;

}
