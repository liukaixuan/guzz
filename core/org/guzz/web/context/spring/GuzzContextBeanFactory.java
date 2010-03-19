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
package org.guzz.web.context.spring;

import java.io.File;
import java.io.IOException;

import org.guzz.Configuration;
import org.guzz.GuzzContextImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzContextBeanFactory extends AbstractFactoryBean implements ApplicationContextAware {

	private Resource configFile ;
	
	private GuzzContextImpl guzzContext ;
	
	private ApplicationContext applicationContext ;
	
	protected final GuzzContextImpl createGuzzContext(Resource springResource) throws Exception{
		File file = springResource.getFile() ;
		
		if(file == null){
			throw new IOException("file is null. spring resource:" + springResource) ;
		}
		
		return (GuzzContextImpl) new Configuration(file.getAbsolutePath()).newGuzzContext() ;
	}

	protected Object createInstance() throws Exception {
		if(this.guzzContext == null){
			this.guzzContext = createGuzzContext(this.configFile) ;
			
			if(this.applicationContext != null){
				SpringExtendedBeanFactory ebf = new SpringExtendedBeanFactory(applicationContext) ;
				this.guzzContext.setExtendedBeanFactory(ebf) ;
			}
		}
		
		return guzzContext;
	}

	public Class getObjectType() {
		return GuzzContextImpl.class ;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext ;
		
		if(this.guzzContext != null){
			SpringExtendedBeanFactory ebf = new SpringExtendedBeanFactory(applicationContext) ;
			this.guzzContext.setExtendedBeanFactory(ebf) ;
		}
	}

	public Resource getConfigFile() {
		return configFile;
	}

	public void setConfigFile(Resource configFile) {
		this.configFile = configFile;
	}

}
