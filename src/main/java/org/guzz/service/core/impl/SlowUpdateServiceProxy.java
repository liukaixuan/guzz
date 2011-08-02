/*
 * Copyright 2008-2010 the original author or authors.
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
package org.guzz.service.core.impl;

import java.io.Serializable;

import org.guzz.Service;
import org.guzz.service.ProxyService;
import org.guzz.service.core.SlowUpdateService;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class SlowUpdateServiceProxy extends ProxyService implements SlowUpdateService {

	private SlowUpdateService slowUpdateService ;
	
	public SlowUpdateServiceProxy(SlowUpdateService slowUpdateService){
		this.slowUpdateService = slowUpdateService ;
	}
	
	public Service getServiceImpl() {
		return (Service) slowUpdateService ;
	}

	public void setServiceImpl(Service service) {
		this.slowUpdateService = (SlowUpdateService) service ;
	}

	public void updateCount(String dbGroup, String tableName, String columnToUpdate, String pkColName, Serializable pkValue, int countToInc) {
		slowUpdateService.updateCount(dbGroup, tableName, columnToUpdate, pkColName, pkValue, countToInc) ;
	}

	public void updateCount(String businessName, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) {
		slowUpdateService.updateCount(businessName, tableCondition, propToUpdate, pkValue, countToInc) ;
	}

	public void updateCount(Class domainClass, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) {
		slowUpdateService.updateCount(domainClass, tableCondition, propToUpdate, pkValue, countToInc) ;
	}

}
