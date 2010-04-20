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
 * Listener for ExtendedBeanFactory. setExtendedBeanFactory is called both after {@link GuzzContextAware#setGuzzContext(org.guzz.GuzzContext)} and startup() methods in plugins.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface ExtendedBeanFactoryAware {

	/**在GuzzContext初始化完成后调用*/
	public void setExtendedBeanFactory(ExtendedBeanFactory extendedBeanFactory) ;

}
