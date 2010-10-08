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
package org.guzz.test.sample;

import org.guzz.service.AbstractService;
import org.guzz.service.ServiceChangeCommand;
import org.guzz.service.ServiceConfig;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SampleTestService2 extends AbstractService {
	
	public SampleTestService sampleTestService1 ;

	/* @see org.guzz.Service#configure(org.guzz.ConfigServer) */
	public boolean configure(ServiceConfig[] scs) {
		return true ;
	}

	/* @see org.guzz.Service#executeCommand(org.guzz.service.ServiceChangeCommand) */
	public void executeCommand(ServiceChangeCommand cmd) {

	}

//	/* @see org.guzz.Service#getServiceName() */
//	public String getServiceName() {
//		return "onlyForTest2";
//	}

	/* @see org.guzz.Service#isAvailable() */
	public boolean isAvailable() {
		return true;
	}

	/* @see org.guzz.Service#shutdown() */
	public void shutdown() {

	}

	public void startup() {
	}

	public SampleTestService getSampleTestService() {
		return sampleTestService1;
	}

	public void setSampleTestService(SampleTestService sampleTestService1) {
		this.sampleTestService1 = sampleTestService1;
	}

}
