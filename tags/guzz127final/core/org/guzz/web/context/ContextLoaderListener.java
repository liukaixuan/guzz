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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.GuzzException;

/**
 * 
 * load guzz framework
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ContextLoaderListener implements ServletContextListener {
	
	private transient static final Log log = LogFactory.getLog(ContextLoaderListener.class) ;
		
	private ContextLoader context ;

	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc= event.getServletContext() ;
		if(context == null){
			context = new ContextLoader() ;
			try {
				context.initGuzzContext(sc) ;
			} catch (Exception e) {
				log.error(e) ;
				context = null ;
				onLoadError(e) ;
			}
		}

	}
	
	protected void onLoadError(Exception e){
		//默认情况下打印出错误信息（防止用户没有配置log4j等而忽略了错误），并且停止应用启动。
		e.printStackTrace() ;
		throw new GuzzException(e) ;
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		if(context != null){
			context.shutdown(event.getServletContext()) ;
			
			context = null ;
		}
	}	

}
