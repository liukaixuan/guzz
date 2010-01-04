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

import java.io.Serializable;

/**
 * 
 * 用于以后获取未来结果。应用场景如下：<p/>
 * 假设程序需要跨域网络读取3个服务器数据，按照传统模式，需要1个1个的进行网络读取，所有操作为线性执行，操作总时间为3个网络服务需要时间之和；这样网络操作的性能将对系统性能造成重要影响。<p/>
 * 引入FutureResult之后，应用依旧顺序的调用网络服务，但返回的不是直接结果，而是FutureResult对象，在用户从FutureResult中读取实际结果时才能真正的获取数据。<br/>
 * 在应用返回FutureResult的同时，后台进程将会并行的读取3个网络服务资源，并存储到FutureResult中，这样3个网络服务就完成了并行操作，实际网络操作时间为3个网络服务中最慢的1个。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FutureResult implements Serializable {
	
	private Object result ;
	
	private IFutureDataFetcher fecther ;
	
	public FutureResult(IFutureDataFetcher fecther){
		this.fecther = fecther ;
	}
	
	//TODO: add result waiting code, and ThreadPool/maybe SocketPool here.

	public Object getResult() throws Exception{
		return fecther.fetchData();
	}
	
	/**
	 * 如果出现异常，忽略异常并返回null
	 */
	public Object getResultIgnoreException(){
		try {
			return fecther.fetchData();
		} catch (Exception e) {
			//TODO: log errors
			return null ;
		}
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
