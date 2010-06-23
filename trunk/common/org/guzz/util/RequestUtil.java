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
package org.guzz.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 封装HttpRequest相关操作的工具类 <BR>
 */
public class RequestUtil {

    private static final Log logger = LogFactory.getLog(RequestUtil.class) ;

    // HTTP标准头
    public static final String HEADER_USER_AGENT = "user-agent";

    public static final String HEADER_REFER = "referer";

    /**
     * 获取指定request的指定参数的整数值.
     * @param request 指定request
     * @param param 指定参数
     * @return 参数值的整数形式. 如果该参数不存在或者解析整数时发生了异常, 则返回-1.
     */
    public static int getParameterAsInt(HttpServletRequest request, String param) {
        return getParameterAsInt(request, param, -1);
    }

    /**
     * 获取指定request的指定参数的整数值. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
     * @param request 指定request
     * @param param 指定参数
     * @param defaultValue 给定的默认值.
     * @return 参数值的整数形式. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
     */
    public static int getParameterAsInt(HttpServletRequest request, String param, int defaultValue) {
        String value = request.getParameter(param);
        if (value == null) {
        	return defaultValue;
        }
        value = value.trim();
        if (value.length() == 0) {
        	return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.warn("err=" + e + "! param=" + param + ", value=" + value + "! return " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * 获取指定request的指定参数的整数值. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
     * @param request 指定request
     * @param param 指定参数
     * @param defaultValue 给定的默认值.
     * @return 参数值的整数数组形式. 如果该参数不存在返回int[0]；解析整数时发生了异常, 则设定为默认值.
     */
    public static int[] getParameterAsIntArray(HttpServletRequest request, String param, int defaultValue) {
        String[] values = request.getParameterValues(param) ;
        
        if (values == null) {
        	return new int[0];
        }
        
        int[] ints = new int[values.length] ;
        
        for(int i = 0 ; i < values.length ; i++){
        	String value = values[i] ;

        	if(value == null){
        		ints[i] = defaultValue ;
        		continue ;
        	}
        	
        	value = value.trim();
        	if(value.length() == 0){
        		ints[i] = defaultValue ;
        		continue ;
        	}
        	
        	try {
        		ints[i] = Integer.parseInt(value);
            } catch (Exception e) {
                logger.warn("err=" + e + "! param=" + param + ", value=" + value + "! return " + defaultValue);
                ints[i] = defaultValue ;
            }
        	
        }
        
        return ints ;
    }

    public static int getAttributeAsInt(HttpServletRequest req, String attrName, int defValue) {
        Object obj = req.getAttribute(attrName);
        return (obj instanceof Integer) ? ((Integer) obj).intValue() : defValue;
    }

    /**
     * 获取表示给定HTTP GET请求的完整URL字符串(包括QueryString). <BR>
     * @param req 指定的request
     * @return 表示给定HTTP GET请求的完整URL字符串(包括QueryString)
     */
    public static String getFullGetStr(HttpServletRequest req) {
        final String qryStr = req.getQueryString();
        if (qryStr == null) {
            return req.getRequestURL().toString();
        }
        return req.getRequestURL().append('?').append(qryStr).toString();
    }
    
    /**
     * Get all parameters in the request. This method is different of {@link HttpServletRequest#getParameterMap()} in returning.
     * 
     * @return Both the key and the value are of type String. If a key has multiple values, the first one is used as the value. 
     */
    public static Map getAllParamsAsMap(HttpServletRequest req){
    	Map p = req.getParameterMap() ;
    	HashMap params = new HashMap() ;
    	
    	Iterator i = p.entrySet().iterator() ;
    	
    	while(i.hasNext()){
    		Entry e = (Entry) i.next() ;
    		String key = (String) e.getKey() ;
    		String[] values = (String[]) e.getValue() ;
    		
    		if(values != null && values.length > 0){
    			params.put(key, values[0]) ;
    		}else{
    			params.put(key, "") ;
    		}
    	}
    	
    	return params ;
    }

    /**
     * 获取给定的Http请求的Referer URL, 即上一个页面.
     * @param req 给定的Http请求
     * @return 给定Http请求的referer头的值. 如果不存在, 返回null.
     */
    public static String getReferUrl(HttpServletRequest req) {
        return req.getHeader(HEADER_REFER);
    }
    
    /**检测请求是否来自搜索引擎*/
    public static boolean isRobotRequest(HttpServletRequest request){
    	String userAgent = request.getHeader("User-Agent") ;
    	if(userAgent == null) return false ;
    	
    	userAgent = userAgent.toLowerCase() ;
    	
    	if(userAgent.indexOf("spider") != -1){
    		return true ;
    	}else if(userAgent.indexOf("bot") != -1){
    		return true ;
    	}else if(userAgent.indexOf("nutch") != -1){
    		return true ;
    	}else if(userAgent.indexOf("yahoo") != -1){
    		return true ;
    	}else if(userAgent.indexOf("gougou") != -1){
    		return true ;
    	}else if(userAgent.indexOf("scooter") != -1){
    		return true ;
    	}else if(userAgent.indexOf("lilina") != -1){
    		return true ;
    	}
    	
    	return false ;
    }
    
    public static String getRealIP(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}		

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		int pos = ip.indexOf(",") ;
		int startPos = 0 ;
		
		while(pos > 0){
			String ip1 = ip.substring(startPos, pos).trim() ;
			
			if(ip1.length() > 0 && !"unknown".equalsIgnoreCase(ip1)){
				return ip1 ;
			}
			
			startPos = pos + 1 ;
			pos = ip.indexOf(",", startPos) ;
		}
    	
    	return ip ;
    }

}