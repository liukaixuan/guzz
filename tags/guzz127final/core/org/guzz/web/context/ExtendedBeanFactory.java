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
package org.guzz.web.context;

/**
 * 
 * Bean factory to link with the outside Bean Context. eg: spring'a ApplicationContext.
 * 
 * <p>ExtendedBeanFactory is initialized after GuzzContext.</p>
 *
 * @see ExtendedBeanFactoryAware
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ExtendedBeanFactory {

	/**
	 * Get the factory implementation of the ExtendedBeanFactory. <br/>
	 * In spring context, spring's ApplicationContext will be returned.
	 */
	public Object getFactoryImpl() ;

	public Object getBean(String beanName) ;
	
}
