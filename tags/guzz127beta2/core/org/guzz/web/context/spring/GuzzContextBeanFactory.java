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
import org.guzz.GuzzContext;
import org.springframework.core.io.Resource;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzContextBeanFactory {
	
	public static GuzzContext createGuzzContext(Resource springResource) throws Exception{
		File file = springResource.getFile() ;
		
		if(file == null){
			throw new IOException("file is null. spring resource:" + springResource) ;
		}
		
		return new Configuration(file.getAbsolutePath()).newGuzzContext() ;
	}

}
