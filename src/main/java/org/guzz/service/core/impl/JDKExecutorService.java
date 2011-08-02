/**
 * 
 */
package org.guzz.service.core.impl;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class JDKExecutorService extends AbstractService implements ExecutorService{
	
	protected ExecutorService executorService ;
	
	private Properties config ;
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			log.info("no configuration found for the ThreadPool in service:" + getClass().getName()) ;
			this.config = new Properties() ;
		}else{
			this.config = scs[0].getProps() ;
		}
		
		return true ;
	}
	
	protected ThreadFactory createThreadFactory(Properties config){
		return Executors.defaultThreadFactory() ;
	}
	
	/**
	 * 默认使用ArrayBlockingQueue
	 */
	protected BlockingQueue<Runnable> createBlockingQueue(Properties config){
		int queueSize = StringUtil.toInt((String) config.remove("queueSize"), 2048) ;
		
		return new ArrayBlockingQueue<Runnable>(queueSize) ;
	}
	
	protected ExecutorService createExecutorService(Properties config){
		int corePoolSize = StringUtil.toInt((String) config.remove("corePoolSize"), 5) ;
		int maximumPoolSize = StringUtil.toInt((String) config.remove("maxPoolSize"), 50) ;
		int keepAliveMilSeconds = StringUtil.toInt((String) config.remove("keepAliveMilSeconds"), 60000) ;
		
		ThreadPoolExecutor e = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveMilSeconds, TimeUnit.MILLISECONDS, createBlockingQueue(config), createThreadFactory(config)) ;

		return e ;
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

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executorService.awaitTermination(timeout, unit);
	}

	public void execute(Runnable command) {
		executorService.execute(command);
	}

	public boolean isShutdown() {
		return executorService.isShutdown();
	}

	public boolean isTerminated() {
		return executorService.isTerminated();
	}

	public List<Runnable> shutdownNow() {
		return executorService.shutdownNow();
	}

	public <T> Future<T> submit(Callable<T> task) {
		return executorService.submit(task);
	}

	public <T> Future<T> submit(Runnable task, T result) {
		return executorService.submit(task, result);
	}

	public Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}

}
