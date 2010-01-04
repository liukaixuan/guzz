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
package org.guzz;

import org.guzz.io.FileResource;
import org.guzz.io.Resource;
import org.guzz.util.CloseUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Configuration {
	
	private Resource config ;
	
	private boolean autoCloseResource ;
	
	public Configuration(Resource config){
		this.config = config ;
	}
	
	public Configuration(String fileName){
		this.config = new FileResource(fileName) ;
		this.autoCloseResource = true ;
	}
	
	public GuzzContext newGuzzContext() throws Exception{
		GuzzContextImpl gf =  new GuzzContextImpl() ;
		gf.initFromMainConfig(this.config) ;
		
		if(autoCloseResource){
			CloseUtil.close(config) ;
		}
		
		return gf ;
	}

}
