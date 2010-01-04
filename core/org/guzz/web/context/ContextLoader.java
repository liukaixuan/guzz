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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Configuration;
import org.guzz.GuzzContext;
import org.guzz.io.FileResource;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ContextLoader {
	
	private transient static final Log log = LogFactory.getLog(ContextLoader.class) ;
	
	/**
	 * Config param for the root WebApplicationContext implementation class to
	 * use: "<code>contextClass</code>"
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";
	
	/**
	* Name of servlet context parameter (i.e., "<code>guzzConfigLocation</code>")
	* that can specify the config location for the root context, falling back
	* to the implementation's default otherwise.
	*/
	public static final String CONFIG_LOCATION_PARAM = "guzzConfigLocation";
	

	private GuzzContext guzzContext ;
	
	public GuzzContext initGuzzContext(ServletContext sc) throws Exception{
		String configFile = sc.getInitParameter(CONFIG_LOCATION_PARAM) ;
		if(configFile == null){
			configFile = "/WEB-INF/guzz.xml" ;
		}
		
		String name = sc.getRealPath(configFile) ;
		
		FileResource fr = new FileResource(name) ;
		
		try{
			GuzzContext gf = new Configuration(fr).newGuzzContext() ;
			
			return initGuzzContext(sc, gf) ;
		}finally{
			CloseUtil.close(fr) ;
		}
	}
	
	public GuzzContext initGuzzContext(ServletContext sc, GuzzContext guzzContext) throws Exception{
		sc.setAttribute(GuzzWebApplicationContextUtil.GUZZ_SERVLET_CONTEXT_KEY, guzzContext) ;
		this.guzzContext = guzzContext ;
			
		return guzzContext ;
	}
	
	public void shutdown(ServletContext sc){
		if(guzzContext != null){
			try{
				guzzContext.shutdown() ;
			}catch(Exception e){
				log.error("error whiling shutting down guzzContext", e) ;
			}
			
			sc.removeAttribute(GuzzWebApplicationContextUtil.GUZZ_SERVLET_CONTEXT_KEY) ;
			guzzContext = null ;
		}
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

}
