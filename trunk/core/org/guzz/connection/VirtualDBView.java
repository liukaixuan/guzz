/*
 * Copyright 2008-2011 the original author or authors.
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
package org.guzz.connection;

import org.guzz.ContextLifeCycle;
import org.guzz.exception.DaoException;

/**
 * 
 * Case for: split a big table into many small ones, and distribute the small tables into different machines/databases.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface VirtualDBView extends ContextLifeCycle {
	
	/**
	 * Set the configured dbgroup.
	 */
	public void setConfiguredVirtualDBGroup(VirtualDBGroup vdb) ;
	
	/**
	 * Retrieve the actually database group for the given condition.
	 * 
	 * <p>Guzz won't cache the returned result.</p>
	 * 
	 * @param tableCondition tableCondition
	 * @exception DaoException Raise a exception when no physics database group matched.
	 */
	public PhysicsDBGroup getDBGroup(Object tableCondition) throws DaoException ;

}
