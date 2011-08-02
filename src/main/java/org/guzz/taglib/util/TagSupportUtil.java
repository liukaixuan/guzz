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
package org.guzz.taglib.util;

import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.guzz.dao.PageFlip;
import org.guzz.exception.IllegalParameterException;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TagSupportUtil {
	
	public static int getScopeInCode(String scope){
		if(scope == null) return PageContext.PAGE_SCOPE ;
		
		String m_scope = scope.toLowerCase().trim() ;
		
		if("request".equals(m_scope)){
			return PageContext.REQUEST_SCOPE ;
		}else if("session".equals(m_scope)){
			return PageContext.SESSION_SCOPE ;
		}else if("application".equals(m_scope)){
			return PageContext.APPLICATION_SCOPE ;
		}else if("page".equals(m_scope)){
			return PageContext.PAGE_SCOPE ;
		}else{
			throw new IllegalParameterException("scope:" + scope + " is not valid!") ;
		}
	}
	
	private static final HashMap primaryTypes = new HashMap() ;
	static{
		primaryTypes.put("int", int.class) ;
		primaryTypes.put("boolean", boolean.class) ;
		primaryTypes.put("float", float.class) ;
		primaryTypes.put("double", double.class) ;
		primaryTypes.put("void", void.class) ;
		primaryTypes.put("long", long.class) ;
	}
	
	public static Class getPrimaryType(String type){
		if(type == null) return null  ;
		
		return (Class) primaryTypes.get(type) ;
	}
	
	public static boolean isLoadedDataEmpty(Object data){
		boolean hasData  = true ;
		
		if(data == null){
			hasData = false ;
		}else if(data instanceof List){
			if(((List)data).isEmpty()){
				hasData = false ;
			}
		}else if(data instanceof PageFlip){
			if(((PageFlip)data).getTotalCount() < 1){
				hasData = false ;
			}
		}
		
		return !hasData ;
	}
	
	public static boolean isYes(String condition, boolean defaultResult){
		if(StringUtil.isEmpty(condition)) return defaultResult ;
		
		char c = condition.charAt(0) ;
		
		if(c == 'y' || c == 'Y' || c == 'T' || c == 't' || c == '1'){
			return true ;
		}else if("on".equalsIgnoreCase(condition)){
			return true ;
		}
		
		return false ;
	}
	
    public static final int HIGHEST_SPECIAL = '>';
    
    private static final char[][] specialCharactersRepresentation = new char[HIGHEST_SPECIAL + 1][];
    static {
        specialCharactersRepresentation['&'] = "&amp;".toCharArray();
        specialCharactersRepresentation['<'] = "&lt;".toCharArray();
        specialCharactersRepresentation['>'] = "&gt;".toCharArray();
        specialCharactersRepresentation['"'] = "&#034;".toCharArray();
        specialCharactersRepresentation['\''] = "&#039;".toCharArray();
    }

    /**
     * Performs the following substring replacements
     * (to facilitate output to XML/HTML pages):
     *
     *    & -> &amp;
     *    < -> &lt;
     *    > -> &gt;
     *    " -> &#034;
     *    ' -> &#039;
     *
     * See also OutSupport.writeEscapedXml().
     */
    public static String escapeXml(String buffer) {
        int start = 0;
        int length = buffer.length();
        char[] arrayBuffer = buffer.toCharArray();
        StringBuffer escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= HIGHEST_SPECIAL) {
                char[] escaped = specialCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuffer to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuffer(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedBuffer.append(escaped);
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return buffer;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }

    /**
     * Get the value associated with a content-type attribute.
     * Syntax defined in RFC 2045, section 5.1.
     */
    public static String getContentTypeAttribute(String input, String name) {
	int begin;
	int end;
        int index = input.toUpperCase().indexOf(name.toUpperCase());
        if (index == -1) return null;
        index = index + name.length(); // positioned after the attribute name
        index = input.indexOf('=', index); // positioned at the '='
        if (index == -1) return null;
        index += 1; // positioned after the '='
        input = input.substring(index).trim();
        
        if (input.charAt(0) == '"') {
            // attribute value is a quoted string
            begin = 1;
            end = input.indexOf('"', begin);
            if (end == -1) return null;
        } else {
            begin = 0;
            end = input.indexOf(';');
            if (end == -1) end = input.indexOf(' ');
            if (end == -1) end = input.length();
        }
        return input.substring(begin, end).trim();
    }   
  	
	

}
