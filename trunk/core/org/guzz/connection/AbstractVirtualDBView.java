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

import org.guzz.GuzzContext;
import org.guzz.exception.DaoException;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * Shadow databases for dbgroups all configured in guzz.xml.
 * <p/>If you need to create dbgroups dynamically, implement {@link VirtualDBView} directly.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractVirtualDBView implements VirtualDBView, GuzzContextAware {
	
	protected GuzzContext gc ;
	
	protected VirtualDBGroup configuredVirtualDBGroup ;
	
	/**
	 * Return the physics dbgroup name configured in guzz.xml for the given table condition.
	 * 
	 * @param tableCondition table condition for shadowing
	 */
	public abstract String getPhysicsDBGroupName(Object tableCondition) ;

	public PhysicsDBGroup getDBGroup(Object tableCondition) throws DaoException {
		String fname = getPhysicsDBGroupName(tableCondition) ;
		
		return this.gc.getPhysicsDBGroup(fname) ;
	}

	public void setConfiguredVirtualDBGroup(VirtualDBGroup vdb) {
		this.configuredVirtualDBGroup = vdb ;
	}

	public VirtualDBGroup getConfiguredVirtualDBGroup() {
		return configuredVirtualDBGroup;
	}

	public void shutdown() throws Exception {		
	}

	public void startup() {		
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.gc = guzzContext ;
	}

}
