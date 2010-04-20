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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContextImpl;
import org.guzz.orm.ColumnDataLoader;
import org.guzz.web.context.ExtendedBeanFactoryAware;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * TODO: refact to interface.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ColumnDataLoaderManager {
	private transient static Log log = LogFactory.getLog(ColumnDataLoaderManager.class.getName()) ;
	
	private List loaders = new LinkedList() ;
	
	private final GuzzContextImpl gc ;
	
	public ColumnDataLoaderManager(GuzzContextImpl guzzContextImpl){
		this.gc = guzzContextImpl ;
	}
	
	public void addDataLoader(ColumnDataLoader loader){
		this.loaders.add(loader) ;
		
		if(loader instanceof GuzzContextAware){
			gc.registerContextStartedAware((GuzzContextAware) loader) ;
		}
		
		if(loader instanceof ExtendedBeanFactoryAware){
			gc.registerExtendedBeanFactoryAware((ExtendedBeanFactoryAware) loader) ;
		}
		
		if(gc.isFullStarted()){
			loader.startup() ;
		}
	}
	
	public void onGuzzFullStarted(){
		for(int i = 0 ; i < loaders.size() ; i++){
			ColumnDataLoader loader = (ColumnDataLoader) loaders.get(i) ;
			
			loader.startup() ;
		}
	}
	
	public void shutdown(){
		for(int i = 0 ; i < loaders.size() ; i++){
			ColumnDataLoader loader = (ColumnDataLoader) loaders.get(i) ;
			
			try {
				loader.shutdown() ;
			} catch (Exception e) {
				log.error("error while shutting down ColumnDataLoader:" + loader.getClass(), e) ;
			}
		}
		
		this.loaders.clear() ;
	}

}
