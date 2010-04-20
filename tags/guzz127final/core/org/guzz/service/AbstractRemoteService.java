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
package org.guzz.service;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.guzz.util.StringUtil;

/**
 * 
 * prepare {@link ExecutorService}
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractRemoteService<ServiceReturnType> extends AbstractService {
	
	protected ExecutorService executorService ;
	
	private Properties config ;
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			log.warn("remoteRPCProxy proxy not started. no configuration found.") ;
			return false;
		}
		
		this.config = scs[0].getProps() ;
		
		return true ;
	}
	
	protected ThreadFactory createThreadFactory(Properties config){
		return Executors.defaultThreadFactory() ;
	}
	
	/**
	 * 默认使用ArrayBlockingQueue
	 */
	protected BlockingQueue<Runnable> createBlockingQueue(Properties config){
		int queueSize = StringUtil.toInt(config.getProperty("queueSize"), 2048) ;
		
		return new ArrayBlockingQueue<Runnable>(queueSize) ;
	}
	
	protected ExecutorService createExecutorService(Properties config){
		int corePoolSize = StringUtil.toInt(config.getProperty("corePoolSize"), 5) ;
		int maximumPoolSize = StringUtil.toInt(config.getProperty("maxPoolSize"), 50) ;
		int keepAliveMilSeconds = StringUtil.toInt(config.getProperty("keepAliveMilSeconds"), 60000) ;
		
		ThreadPoolExecutor e = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveMilSeconds, TimeUnit.MILLISECONDS, createBlockingQueue(config), createThreadFactory(config)) ;

		return e ;
	}
	
	/**
	 * 创建一个新的任务
	 */
	public FutureResult<ServiceReturnType> sumbitTask(FutureDataFetcher<ServiceReturnType> fetcher){
		return new FutureResult<ServiceReturnType>(executorService, fetcher) ;
	}

	public void startup() {
		this.executorService = createExecutorService(config) ;
	}

	public boolean isAvailable() {
		return executorService != null ;
	}

	public void shutdown() {
		if(executorService != null){
			executorService.shutdown() ;
			executorService = null ;
		}
	}

}
