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

import org.guzz.GuzzContext;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzWebApplicationContextUtil {
	
	public static final String GUZZ_SERVLET_CONTEXT_KEY = "guzzContext";
	
	public static final String GUZZ_ORGINAL_HTTP_REQUEST = "guzzOrginalHttpRequest";
	
	public static GuzzContext getGuzzContext(ServletContext sc){
		return (GuzzContext) sc.getAttribute(GUZZ_SERVLET_CONTEXT_KEY) ;
	}
	
}
