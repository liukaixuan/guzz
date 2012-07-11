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
import org.guzz.api.taglib.SummonTag;
import org.guzz.api.velocity.GuzzBoundaryDirective.BoundaryChain;
import org.guzz.orm.Business;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.ObjectMapping;
import org.guzz.util.Assert;

/**
 * 
 * See {@link SummonTag}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class SummonDirective extends Directive {

	public static final String GUZZ_CONTEXT_NAME = "guzz_context_name" ;
	
	protected GuzzContext guzzContext ;

	public int getType() {
		return LINE ;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		if(node.jjtGetNumChildren() != 1){
			throw new RuntimeException(getName() + " only and must accept one Map parameter!") ;
		}
		
		Map params = (Map) node.jjtGetChild(0).value(context) ;
		
		Object business = params.get("business") ;
		String ghostName ;
		
		if(business instanceof java.lang.String){
			ghostName = (String) business ;
		}else{
			ghostName = business.getClass().getName() ;
		}
		
		String var = (String) params.get("var") ;
		Object tableCondition = params.get("tableCondition") ;
		Object limit = params.get("limit") ;
		
		Assert.assertResouceNotNull(ghostName, "parameter [business] in Map is requried.") ;
		Assert.assertResouceNotNull(var, "parameter [var] in Map is requried.") ;
		
		LinkedList conditions = new LinkedList() ;
		
		BoundaryChain chain = (BoundaryChain) context.get(GuzzBoundaryDirective.BOUNDARY_CONTEXT_NAME) ;
		if(chain != null){
			if(tableCondition == null){
				tableCondition = chain.getTableCondition() ;
			}
			
			conditions.addAll(chain.getBoundaryLimits()) ;
		}
		
		if(limit != null){
			conditions.addLast(limit) ;
		}
		
		Business bi = guzzContext.getBusiness(ghostName) ;
		ObjectMapping mapping = guzzContext.getObjectMappingManager().getObjectMapping(ghostName, tableCondition) ;
		
		if(bi == null){
			throw new ParseErrorException("unknown business:[" + business + "], business name:" + ghostName) ;
		}		
				
		Object result = innerSummonGhosts(bi, mapping, tableCondition, conditions, params);
		
		//保存结果
		context.put(var, result) ;
        
        return true;
	}
	
	/**
	 * @param business business to operate
	 * @param conditions raw conditions passed from vm template.
	 * @param params from vm template
	 */
	protected Object innerSummonGhosts(Business business, ObjectMapping mapping, Object tableCondition, List conditions, Map params) throws IOException {
		LinkedList list = new LinkedList() ;
		
		BusinessInterpreter gi = business.getInterpret() ;
		
		if(conditions != null && !conditions.isEmpty()){
			for(int i = 0 ; i < conditions.size() ; i++){
				Object condition = conditions.get(i) ;
				
				try {
					if(condition != null){
						Object mc = gi.explainCondition(mapping, condition) ;
						if(mc != null){
							list.addLast(mc) ;
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("conditions:" + conditions, e) ;
				}
			}
		}
		
		return summonGhosts(business, tableCondition, list, params) ;
	}
	
	protected abstract Object summonGhosts(Business business, Object tableCondition, List conditions, Map params) throws IOException ;

	public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
		super.init(rs, context, node);
		
		this.guzzContext = (GuzzContext) rs.getApplicationAttribute(GUZZ_CONTEXT_NAME) ;
	}
}
