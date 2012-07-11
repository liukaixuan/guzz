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
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.guzz.util.StringUtil;

/**
 * 
 * {@link StringUtil#notEmpty(String)}
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NotEmptyDirective extends Directive {

	public int getType() {
		return BLOCK ;
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		Object value = node.jjtGetChild(0).value(context);

		
		boolean isEmpty = false ;
		if(value == null){
			isEmpty = true ;
		}else{
			if(value instanceof String){
				isEmpty = StringUtil.isEmpty((String) value) ;
			}else if(value instanceof Collection){
				isEmpty = ((Collection) value).isEmpty() ;
			}else if(value.getClass().isArray()){
				isEmpty = Array.getLength(value) > 0 ;
			}
		}
		if (!isEmpty) {
            Node content = node.jjtGetChild(1);
            content.render(context, writer);
        }
        
        return true;
	}

	public String getName() {
		return "notEmpty" ;
	}
}
