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
package org.guzz.transaction;

import java.util.HashMap;
import java.util.Iterator;

import org.guzz.exception.InvalidConfigurationException;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DBGroupManager extends HashMap {
	
	public DBGroup getGroup(String groupName){
		DBGroup group = (DBGroup) this.get(groupName) ;
		
		//TODO: move this to GuzzContext.
		if(group == null){
			throw new InvalidConfigurationException("unknown database group:" + groupName) ;
		}
		
		return group ;
	}
	
	public void shutdown(){
		Iterator i = this.values().iterator() ;
		
		while(i.hasNext()){
			DBGroup group = (DBGroup) i.next() ;
			
			CloseUtil.close(group.getMasterDB()) ;
			CloseUtil.close(group.getSlaveDB()) ;
		}
	}

}
