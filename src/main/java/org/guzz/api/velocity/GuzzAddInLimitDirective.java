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
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.guzz.api.taglib.GhostAddInLimitTag;
import org.guzz.api.velocity.GuzzBoundaryDirective.BoundaryChain;
import org.guzz.orm.Business;
import org.guzz.orm.se.InTerm;
import org.guzz.util.Assert;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * See {@link GhostAddInLimitTag}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzAddInLimitDirective extends Directive {
	private GuzzContext guzzContext ;
	
	public int getType() {
		return LINE ;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		BoundaryChain chain = (BoundaryChain) context.get(GuzzBoundaryDirective.BOUNDARY_CONTEXT_NAME) ;
		if(chain == null){
			throw new ParseErrorException(getName() + " must be resided inside a guzzBoundary directive.") ;
		}
		
		if(node.jjtGetNumChildren() != 2){
			throw new RuntimeException(getName() + " accepts 2 parameters. The first is a boolean value indicating whether or not to add this conditon; the second is a Map!") ;
		}
		
		Boolean test = (Boolean) node.jjtGetChild(0).value(context) ;
		if(Boolean.FALSE.equals(test)){ //ignore this condition.
			return true ;
		}
		
		Map params = (Map) node.jjtGetChild(1).value(context) ;
		String name = (String) params.get("name") ;
		Object value = (Object) params.get("value") ;
		String retrieveValueProp = (String) params.get("retrieveValueProp") ;
		
		Assert.assertResouceNotNull(name, "parameter [name] in Map is requried.") ;
		Assert.assertResouceNotNull(value, "parameter [value] in Map is requried.") ;
		
		if(value instanceof int[]){
			chain.addLimitCondition(new InTerm(name, (int[]) value)) ;
			
			return true;
		}
		
		List mvs = null ;
		
		if(value.getClass().isArray()){//是不是数组。
			mvs = Arrays.asList((Object[]) value) ;
		}else if(value instanceof List){
			mvs = (List) value ;
		}else{
			throw new ParseErrorException("value must be an array or a List.") ;
		}
		
		if(mvs.isEmpty()){
			throw new ParseErrorException("value can not be empty.") ;
		}
		
		if(retrieveValueProp == null){
			chain.addLimitCondition(new InTerm(name, mvs)) ;
		}else{
			LinkedList newValues = new LinkedList() ;
			Iterator i = mvs.iterator() ;
			Object valueItem = mvs.get(0) ;
			
			if(valueItem instanceof Map){
				while(i.hasNext()){
					newValues.addLast(((Map) i.next()).get(retrieveValueProp)) ;
				}
			}else{
				Class valueClass = mvs.get(0).getClass() ;
				
				BeanWrapper bw ;
				
				Business b = this.guzzContext.getBusiness(valueClass.getName()) ;
				if(b != null){//如果属于领域对象，使用领域对象的BeanWrapper(用以支持读取CustomTableView的特殊要求)
					bw = b.getBeanWrapper() ;
				}else{
					bw = BeanWrapper.createPOJOWrapper(valueClass) ;
				}
				
				while(i.hasNext()){
					newValues.addLast(bw.getValue(i.next(), retrieveValueProp)) ;
				}
			}
			
			chain.addLimitCondition(new InTerm(name, newValues)) ;
		}
        
        return true;
	}
	public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
		super.init(rs, context, node);
		
		this.guzzContext = (GuzzContext) rs.getApplicationAttribute(SummonDirective.GUZZ_CONTEXT_NAME) ;
	}

	public String getName() {
		return "guzzAddInLimit" ;
	}
}
