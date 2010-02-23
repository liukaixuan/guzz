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
package org.guzz.service.remote;

import java.util.Properties;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface RemoteRPCProxy {
	
	public void startup(Properties props) ;

	/**
	 * 获取远程对象的调用方法。返回的对象可能会被调用者缓存，并多次重复使用。
	 */
	public Object getRemoteStub(Class serviceInterface) ;
	
	/**关闭此proxy，释放相关的资源。*/
	public void close() ;
	
}
