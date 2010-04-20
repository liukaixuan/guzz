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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.guzz.exception.ServiceExecutionException;

/**
 *
 * 用于以后获取未来结果。应用场景如下：<p/>
 * 假设程序需要跨域网络读取3个服务器数据，按照传统模式，需要1个1个的进行网络读取，所有操作为线性执行，操作总时间为3个网络服务需要时间之和；这样网络操作的性能将对系统性能造成重要影响。<p/>
 * 引入FutureResult之后，应用依旧顺序的调用网络服务，但返回的不是直接结果，而是FutureResult对象，在用户从FutureResult中读取实际结果时才能真正的获取数据。<br/>
 * 在应用返回FutureResult的同时，后台进程将会并行的读取3个网络服务资源，并存储到FutureResult中，这样3个网络服务就完成了并行操作，实际网络操作时间为3个网络服务中最慢的1个。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FutureResult<ReturnType> {

	protected FutureDataFetcher<ReturnType> fecther ;

	protected Future<ReturnType> f ;

	public FutureResult(ExecutorService executor, FutureDataFetcher<ReturnType> fecther){
		this.fecther = fecther ;
		f = executor.submit(fecther) ;
	}

	protected FutureResult(){
	}

	//
	public ReturnType get() throws Exception{
		return f.get() ;
	}

	/**
	 * 如果出现异常，忽略异常并返回{@link FutureDataFetcher#getDefaultData()}
	 */
	public ReturnType getIgnoreException(){
		try {
			return f.get() ;
		} catch (Exception e) {
		}

		return this.fecther.getDefaultData() ;
	}

	public ReturnType get(long timeout, TimeUnit unit) throws Exception{
		return f.get(timeout, unit) ;
	}

	/**
	 * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     * <p/>
     * If the result is not available, or a exception raised, return the default value({@link FutureDataFetcher#getDefaultData()}), and cancel the task.
     * <p>
     * This method ignore any exceptions.
     * </p>
	 */
	public ReturnType getOrCancel(long timeout, TimeUnit unit){
		try {
			return f.get(timeout, unit) ;
		} catch (InterruptedException e) {
			f.cancel(true) ;
		} catch (ExecutionException e) {
			f.cancel(true) ;
		} catch (TimeoutException e) {
			f.cancel(true) ;
		}catch(Throwable t){
			//ignore any exceptions
		}

		return this.fecther.getDefaultData() ;
	}

	/**
	 * 立即获取结果。如果任务还没有执行，或者正在执行，撤销任务。
	 *
	 * @param suppressException 是否忽略异常信息。
	 */
	public ReturnType getNoWait(boolean suppressException){
		if(!f.isDone()){
			f.cancel(true) ;
		}

		try {
			return f.get() ;
		} catch (Exception e) {
			if(!suppressException){
				throw new ServiceExecutionException(e) ;
			}
		}

		return fecther.getDefaultData() ;
	}

	/**
	 * 立即获取结果。如果任务还没有执行，撤销任务并返回。如果任务已经开始执行，等待任务完成并返回。
	 *
	 * @param suppressException 是否忽略异常信息。
	 */
	public ReturnType getNoQueue(boolean suppressException){
		if(!f.isDone()){
			f.cancel(false) ;
		}

		try {
			return f.get() ;
		} catch (Exception e) {
			if(!suppressException){
				throw new ServiceExecutionException(e) ;
			}
		}

		return fecther.getDefaultData() ;
	}

	/**
	 * 立即获取结果。如果任务还没有执行，撤销任务并返回。如果任务已经开始执行，等待指定的时间并返回。
	 *
	 * @param timeout 时间
	 * @param unit 时间单位
	 * @param suppressException 是否忽略异常信息。
	 */
	public ReturnType getNoQueue(long timeout, TimeUnit unit, boolean suppressException){
		if(!f.isDone()){
			f.cancel(false) ;
		}

		try {
			return f.get(timeout, unit) ;
		} catch (Exception e) {
			if(!suppressException){
				throw new ServiceExecutionException(e) ;
			}
		}

		return fecther.getDefaultData() ;
	}

}
