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
package org.guzz.connection;



/**
 * 
 * Virtual Database Group. Query {@link PhysicsDBGroup} by tableCondition.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VirtualDBGroup extends DBGroup {
	
	private VirtualDBView virtualDBView ;
	
	public VirtualDBGroup(){
	}

	public PhysicsDBGroup getPhysicsDBGroup(Object tableCondition) {
		return this.virtualDBView.getDBGroup(tableCondition) ;
	}

	public String getPhysicsGroupName(Object tableCondition) {
		return getPhysicsDBGroup(tableCondition).getGroupName() ;
	}

	public final boolean isPhysics() {
		return false;
	}

	public VirtualDBView getVirtualDBGroupView() {
		return virtualDBView;
	}

	public void setVirtualDBGroupView(VirtualDBView virtualDBView) {
		this.virtualDBView = virtualDBView;
	}

}
