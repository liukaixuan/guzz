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
package org.guzz.util.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.service.core.DebugService;

/**
 * 
 * A demon thread holding a queue. When the queue is full, new arrived objects override the oldest one.
 * <p />The queue is not thread-safe, and doesn't guarantee that no data will lose. Objects in this queue is processed randomly.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DemonQueuedThread  extends Thread{
	private transient final Log log = LogFactory.getLog(getClass()) ;
	
	private boolean keepRunning = true ;
	
	private String threadName ;
	
	protected Object[] queues ;
	
	private volatile int currentWritePos = 0 ;
	
	private boolean isSleepNow = false ;
	
	private int millSecondsToSleep = 500 ;

	public boolean isSleeping(){
		return isSleepNow ;
	}
	
	public void addToQueue(Object obj){
		int pos = currentWritePos++ ;
		if(pos >= queues.length){
			currentWritePos = 0 ;
			pos = 0 ;
		}
		
		queues[pos] = obj ;	
	}
	
	public DemonQueuedThread(String threadName, int queueSize){
		this.setDaemon(true) ;
		this.threadName = threadName ;
		this.queues = new Object[queueSize] ;
		
		this.setName(DebugService.DEMON_NAME_PREFIX + threadName) ;
	}
	
	public void shutdown(){
		this.keepRunning = false ;
		
		try {
			this.notify() ;
		} catch (Exception e) {
		}
		
		log.info("thread [" + threadName + "] closed.") ;
	}
	
	/**
	 * 
	 * @return should keep the thread running.
	 */
	protected boolean doWithTheQueue() throws Exception{
		return true ;
	}
				
	public void run(){
		while(keepRunning){
			isSleepNow = false ;
			
			boolean shouldSleep = true ;
			
			try{
				shouldSleep = !doWithTheQueue() ;
				
			}catch(Exception e){
				shouldSleep = true ;
				//ignore all errors
				log.error("error whiling updating inc queue.", e) ;
			}
			
			if(shouldSleep){
				try{
					synchronized(this){
						isSleepNow = true ;
						this.wait(getMillSecondsToSleep()) ;
					}
				}catch(Exception e){
					//ignore all errors
				}
			}
		}
	}
	
	protected int getMillSecondsToSleep(){
		return this.millSecondsToSleep ;
	}

	public void setMillSecondsToSleep(int millSecondsToSleep) {
		this.millSecondsToSleep = millSecondsToSleep;
	}

}
