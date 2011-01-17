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
package org.guzz;

import org.guzz.web.context.ExtendedBeanFactory;
import org.guzz.web.context.ExtendedBeanFactoryAware;
import org.guzz.web.context.GuzzContextAware;


/**
 * 
 * LifeCycle
 * 
 * <p/>
 * startup sequences:
 * <ol>
 * <li>xxxx = XXXClass.newInstance()</li>
 * <li>xxx.setXXX()/configure(...)</li>
 * <li>.....</li>
 * <li>injected {@link GuzzContext} based on implementing {@link GuzzContextAware} or not</li>
 * <li>xxx.startup()</li>
 * <li>...</li>
 * <li>injected {@link ExtendedBeanFactory} based on implementing {@link ExtendedBeanFactoryAware} or not</li>
 * <li>..running...</li>
 * <li>xxx.shutdown() when guzz is shutdown</li>
 * </ol>
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ContextLifeCycle {
	
	public void startup() ;
	
	public void shutdown() throws Exception ;

}
