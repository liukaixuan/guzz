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
package org.guzz.test.shop;

import org.guzz.connection.AbstractVirtualDBView;
import org.guzz.util.Assert;

/**
 * 
 * Key class. Mapping different cargo to different table and different properties.
 * 
 * <p>We define cargo's name to be the table condition.</p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CargoVirtualDBView extends AbstractVirtualDBView {
	
	public String getPhysicsDBGroupName(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default db to store un-categoried cargoes.") ;
	
		//store crossStitch in cargo2 db.
		if("crossStitch".equals(cargoName)){
			return "cargoDB.cargo2" ;
		}
		
		//store others in the default database.
		return "default";
	}

}
