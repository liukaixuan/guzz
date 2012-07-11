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

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.guzz.api.taglib.GhostAddLimitTag;
import org.guzz.api.velocity.GuzzBoundaryDirective.BoundaryChain;

/**
 * 
 * See {@link GhostAddLimitTag}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzAddLimitDirective extends Directive {

	public int getType() {
		return LINE ;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		if(node.jjtGetNumChildren() != 2){
			throw new RuntimeException(getName() + " accepts 2 parameters. The first is a boolean value indicating whether or not to add this conditon; the second is a List containing your conditions!") ;
		}
		
		Boolean test = (Boolean) node.jjtGetChild(0).value(context) ;
		if(Boolean.FALSE.equals(test)){ //ignore this condition.
			return true ;
		}
		
		Collection conditions = (Collection) node.jjtGetChild(1).value(context) ;
		
		BoundaryChain chain = (BoundaryChain) context.get(GuzzBoundaryDirective.BOUNDARY_CONTEXT_NAME) ;
		if(chain == null){
			throw new ParseErrorException(getName() + " must be resided inside a guzzBoundary directive.") ;
		}
		
		chain.addLimitConditions(conditions) ;
        
        return true;
	}
	public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
		super.init(rs, context, node);
	}

	public String getName() {
		return "guzzAddLimit" ;
	}
}
