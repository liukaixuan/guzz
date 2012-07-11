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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;
import org.guzz.util.Assert;

/**
 * 
 * g:boundary
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzBoundaryDirective extends Directive {

	protected static final String BOUNDARY_CONTEXT_NAME = "guzz_boundary_context_name" ;

	public String getName() {
		return "guzzBoundary" ;
	}

	public int getType() {
		return BLOCK;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		BoundaryChain chain = (BoundaryChain) context.get(BOUNDARY_CONTEXT_NAME) ;
		if(chain == null){
			chain = new BoundaryChain() ;
			context.put(BOUNDARY_CONTEXT_NAME, chain) ;
		}
		
		int count = node.jjtGetNumChildren() ;
		ASTBlock mn = null ;
		
		if(count == 1){
			chain.startNewBoudary(null) ;
			mn = (ASTBlock) node.jjtGetChild(0) ;
		}else if(count == 2){
			Map params = (Map) node.jjtGetChild(0).value(context) ;
			chain.startNewBoudary(params) ;
			
			mn = (ASTBlock) node.jjtGetChild(1) ;
		}else{
			 throw new RuntimeException(getName() + " only accepts one Map parameter!") ;
		}
		
		mn.render(context, writer);
		 
		chain.endBoundary() ;
		
		//If this is the end of a last chain, quit the chain, or others summon tags below will find some null boundary.
        if(!chain.hasBoundary()){
        	context.remove(BOUNDARY_CONTEXT_NAME) ;
        }
		
        return true;
	}
	
	public static class BoundaryChain{
		
		private LinkedList<Boundary> boundaries = new LinkedList<Boundary>() ;
		
		private Boundary lastBoundary ;
		
		public void startNewBoudary(Map params){
			Boundary nb = new Boundary(this.lastBoundary, params) ;
			boundaries.addLast(nb) ;
			this.lastBoundary = nb ;
		}
		
		public boolean hasBoundary(){
			return this.lastBoundary != null ;
		}
		
		public void endBoundary(){
			this.boundaries.removeLast() ;
			this.lastBoundary = this.boundaries.isEmpty() ? null : this.boundaries.getLast() ;
		}
		
		public void addLimitCondition(Object limitTo){
			Assert.assertNotNull(this.lastBoundary, "addLimit must be used inside a boundary!") ;
			
			this.lastBoundary.limits.addLast(limitTo) ;
		}
		
		public void addLimitConditions(Collection limitTos){
			Assert.assertNotNull(this.lastBoundary, "addLimit must be used inside a boundary!") ;
			
			this.lastBoundary.limits.addAll(limitTos) ;
		}
		
		public Object getTableCondition(){
			return this.lastBoundary.tableCondition ;
		}
		
		public List getBoundaryLimits(){
			return this.lastBoundary.limits ;
		}
		
	}
	
	private static class Boundary{
		
		public Boundary(Boundary parent, Map params){
			boolean inherit = params == null ? true : Boolean.TRUE.equals(params.get("inherit")) ;
			String tableCondition = params == null ? null : (String) params.get("tableCondition") ;
			Object limit = params == null ? null : params.get("limit") ;
			
			if(parent != null){
				if(inherit){
					this.limits.addAll(parent.limits) ;
				}
				
				this.tableCondition = parent.tableCondition ;
			}
			
			if(limit != null){
				this.limits.addLast(limit) ;
			}
			
			if(tableCondition != null){
				this.tableCondition = tableCondition ;
			}
		}
		
		public LinkedList<Object> limits = new LinkedList<Object>() ;
		
		public Object tableCondition ;
		
	}

}
