/*
 * Copyright 2008-2012 the original author or authors.
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
package org.guzz.api.velocity;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.guzz.GuzzContext;
import org.guzz.Service;
import org.guzz.api.taglib.SummonTag;
import org.guzz.api.taglib.TypeConvertHashMap;
import org.guzz.api.velocity.GuzzBoundaryDirective.BoundaryChain;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.util.Assert;

/**
 * 
 * See {@link SummonTag}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzIncDirective extends Directive {

	protected GuzzContext guzzContext ;
	
	protected SlowUpdateService slowUpdateService ;

	public int getType() {
		return LINE ;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		if(node.jjtGetNumChildren() != 1){
			throw new RuntimeException(getName() + " only and must accept one Map parameter!") ;
		}
		
		if(this.slowUpdateService == null){
			throw new ResourceNotFoundException("slowUpdateService is not available.") ;
		}
		
		Map params = (Map) node.jjtGetChild(0).value(context) ;
		
		Object business = params.get("business") ;
		String updatePropName = (String) params.get("updatePropName") ;
		int count = TypeConvertHashMap.getIntParam(params, "count", 1) ;
		Serializable pkValue = (Serializable) params.get("pkValue") ;
		Object tableCondition = params.get("tableCondition") ;
		
		Assert.assertResouceNotNull(business, "parameter [business] in Map is requried.") ;
		Assert.assertResouceNotNull(updatePropName, "parameter [updatePropName] in Map is requried.") ;
		Assert.assertResouceNotNull(pkValue, "parameter [pkValue] in Map is requried.") ;
		
		String ghostName ;
		
		if(business instanceof java.lang.String){
			ghostName = (String) business ;
		}else{
			ghostName = business.getClass().getName() ;
		}
		
		if(tableCondition == null){
			BoundaryChain chain = (BoundaryChain) context.get(GuzzBoundaryDirective.BOUNDARY_CONTEXT_NAME) ;
			if(chain != null){
				tableCondition = chain.getTableCondition() ;
			}
		}
		
		this.slowUpdateService.updateCount(ghostName, tableCondition, updatePropName, pkValue, count) ;
        
        return true;
	}
	public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
		super.init(rs, context, node);
		
		this.guzzContext = (GuzzContext) rs.getApplicationAttribute(SummonDirective.GUZZ_CONTEXT_NAME) ;
		this.slowUpdateService = (SlowUpdateService) this.guzzContext.getService(Service.FAMOUSE_SERVICE.SLOW_UPDATE) ;
	}

	public String getName() {
		return "guzzInc" ;
	}
}
