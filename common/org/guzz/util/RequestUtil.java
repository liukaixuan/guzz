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
    
//    public static String getAttributeAsTrimStr(HttpServletRequest req, String attrName) {
//        return getAttributeAsTrimStr(req, attrName, "");
//    }
//
//    public static String getAttributeAsTrimStr(HttpServletRequest req, String attrName, String defValue) {
//        Object obj = req.getAttribute(attrName);
//        if(obj == null) return defValue ;
//        
//        return (obj instanceof String) ? ((String) obj).trim() : defValue;
//    }

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
    
//
//    public static String getCurrentPage(HttpServletRequest req) {
//        final String requestURI = req.getRequestURI();
//        return requestURI.substring(requestURI.lastIndexOf('/') + 1);
//    }
//
//    public static String getCurPageWithQryStr(HttpServletRequest req) {
//        return getCurPageWithQryStr(req, null);
//    }
//
//    public static String getCurPageWithQryStr(HttpServletRequest req, String param) {
//        String qryStr = removeQryParam(req.getQueryString(), param);
//        if (qryStr == null) {
//            return getCurrentPage(req);
//        }
//        return new StringBuffer(getCurrentPage(req)).append('?').append(qryStr).toString();
//    }
//
//    public static String removeQryParam(String qryStr, String param) {
//        if (qryStr == null || param == null) {
//            return qryStr;
//        }
//        String[] params = qryStr.split("&");
//        StringBuffer sb = new StringBuffer(qryStr.length());
//        for (int i = 0; i < params.length; i++) {
//            if (params[i].startsWith(param + "=")) {
//                continue;
//            }
//            sb.append(params[i]).append('&');
//        }
//        return (sb.length() > 0) ? sb.deleteCharAt(sb.length() - 1).toString() : null;
//    }
//    public static String getRequestInfo(HttpServletRequest req) {
//        StringBuffer sb = new StringBuffer(320);
//        sb.append("[Req]");
//        sb.append(req.getClass().getName());
//        sb.append(": (").append(req.getScheme()).append(')').append(req.getServerName()).append(':').append(req.getServerPort());
//        sb.append(", ").append(req.getMethod()).append(' ').append(req.getProtocol());
//        sb.append(", uri=").append(req.getRequestURI());
//        sb.append(", ctx=").append(req.getContextPath());
//        sb.append(", servlet=").append(req.getServletPath());
//        sb.append(", qryStr=").append(req.getQueryString());
//        sb.append(", refer=").append(req.getHeader(HEADER_REFER));
//        sb.append(", useragt=").append(req.getHeader(HEADER_USER_AGENT));
//        sb.append(", ip=").append(req.getRemoteAddr());
//        return sb.toString();
//    }
//
//    public static String getRequestBrief(HttpServletRequest req) {
//        StringBuffer sb = new StringBuffer(320);
//        sb.append("[Req]");
//        sb.append(req.getMethod()).append(' ');
//        sb.append(req.getRequestURI());
//        return sb.toString();
//    }
//
//    /**
//     * 获取指定request的完整URL请求, 包括全部参数项和值(GET方式和POST方式都适用).
//     * 该方法会影响request中的编码.
//     * @param rq 指定的request
//     * @return 表示URL请求的字符串, 包括完整URL和提交的全部参数项和值
//     * @see #getParamString(HttpServletRequest)
//     */
//    public static String getFullRequestStr(HttpServletRequest rq) {
//        return new StringBuffer(256).append(rq.getRequestURL()).append(
//                getParamString(rq)).toString();
//    }
//
//    /**
//     * 获取指定request的全部参数项和值. 该方法会影响request中的编码.
//     * @param rq 指定的request
//     * @return 全部参数项和值构成的字符串
//     */
//    public static String getParamString(HttpServletRequest rq) {
//        StringBuffer sb = new StringBuffer(256);
//        int i = 0;
//        for (Enumeration params = rq.getParameterNames(); params.hasMoreElements();) {
//            String param = (String) params.nextElement();
//            sb.append((++i) == 1 ? "?" : "&").append(param).append("=").append(
//                    rq.getParameter(param));
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 获取给定的request中全部的Header信息.
//     * @param req 给定的request
//     * @return 全部Header信息构成的字符串.
//     */
//    public static String getAllHeadersStr(HttpServletRequest req) {
//        StringBuffer sb = new StringBuffer(256);
//        String header = null;
//        for (Enumeration headers = req.getHeaderNames(); headers.hasMoreElements();) {
//            header = (String) headers.nextElement();
//            sb.append(header);
//            sb.append("=");
//            sb.append(req.getHeader(header));
//            sb.append("\r\n");
//        }
//        return sb.toString();
//    }

    /**
     * 获取给定的Http请求的Referer URL, 即上一个页面.
     * @param req 给定的Http请求
     * @return 给定Http请求的referer头的值. 如果不存在, 返回null.
     */
    public static String getReferUrl(HttpServletRequest req) {
        return req.getHeader(HEADER_REFER);
    }
//
//    /**
//     * 获取指定request的指定参数的指定编码字符串值. <BR><BR>
//     * 提供给子类使用的工具方法.
//     * @param req 给定Http请求对象
//     * @param param 指定参数
//     * @param originEncoding 参数值的原始编码
//     * @param toEncoding 解析参数值的指定编码
//     * @return 给定参数的取值, 如果为null则返回"".
//     */
//    public static String getParamByEncoding(HttpServletRequest req,
//            String param, String originEncoding, String toEncoding) {
//        if (param == null) {
//            return "";
//        }
//        String result = req.getParameter(param);
//        try {
//            return (result == null) ? "" : new String(result
//                    .getBytes(originEncoding), toEncoding);
//        } catch (UnsupportedEncodingException e) {
//            logger.error("unspport encoding! origin=" + originEncoding + ", to=" + toEncoding, e);
//        }
//        return "";
//    }
//
//    /**
//     * 获取给定字符串在给定请求的URL(相对于该应用)中的位置. <BR>
//     * 对动态页面,等价于<code>req.getServletPath().indexOf(someUri)</code>
//     * 例子: requestURI: /app/login.htm; ctx: /app; uri: /login.htm; return: 0
//     * @param req 给定请求
//     * @param someUri 给定字符串
//     * @return 给定字符串在请求URL中的位置. 如果给定字符串(someUri)为null或"", 返回-2.
//     */
//    public static int getPageUriPosInRequest(HttpServletRequest req, String someUri) {
//        if (someUri == null || someUri.trim().length() == 0) {
//            return -2;
//        }
//        return getRelativePath(req).indexOf(someUri);
////        return req.getServletPath().indexOf(someUri);
//    }
//
//    /**
//     * Return the webapp root path.<br>
//     * Example:<br>
//     * if request "http://localhost:8080/app1/dir1/page1.jsp", the method return
//     * "http://localhost:8080/app1".
//     */
//    public static String getContextRoot(HttpServletRequest request) {
//        final String sysUrl = request.getRequestURL().toString();
//        final String servletPath = request.getServletPath();
//		final int servletPathIndex = sysUrl.indexOf(servletPath);
//		if (servletPathIndex == -1) {
//			logger.error("servletPath=" + servletPath + ", sysUrl=" + sysUrl + ", so index=" + servletPathIndex);
//			return sysUrl;
//		}
//		return sysUrl.substring(0, servletPathIndex);
//    }
//
//    /**
//     * for Dynamic Pages, this method as same as <code>req.getServletPath()</code>,
//     * but the method also valid for Static Content, such as html, gif, css etc.<br>
//     * Example:<br>
//     * if request "http://localhost:8080/app1/dir1/page1.jsp", the method return
//     * "/dir1/page1.jsp".
//     * @param req the spec request
//     * @return the relative url
//     */
//    public static String getRelativePath(HttpServletRequest req) {
//        // ls@2005-11-02 req.getRequestURI().substring(req.getContextPath().length()) == req.getServletPath() ? NO! i.e.WebLogic!
//        return req.getRequestURI().substring(req.getContextPath().length());
//    }
//
//    public static String getRelativePathWithQryStr(HttpServletRequest req) {
//        final String qryStr = req.getQueryString();
//        final String relativePath = getRelativePath(req);
//        if (qryStr == null) {
//            return relativePath;
//        } else {
//            return new StringBuffer(relativePath.length() + qryStr.length() + 1)
//                    .append(relativePath).append('?').append(qryStr).toString();
//        }
//    }
    
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

//    /**
//     * @param application
//     * @return ServletContainerInfo
//     */
//    public static String getServletContainerInfo(final ServletContext application) {
//        StringBuffer sb = new StringBuffer(64);
//        sb.append(application.getServerInfo());
//        sb.append(" (Servlet ").append(application.getMajorVersion()).append('.').append(application.getMinorVersion());
//        return sb.toString();
//    }
//
//    /**
//     * simple log method for jsp page.
//     * @param obj
//     * @param req
//     */
//    public static void log(Object obj, HttpServletRequest req) {
//        StringBuffer sb = new StringBuffer(256);
//        sb.append(new java.sql.Timestamp(System.currentTimeMillis()));
//        if (req != null) {
//            sb.append('\t').append(req.getRequestURI());
//        }
//        sb.append('\t').append(obj);
//        logger.info(sb.toString()) ;
//    }
//
//    /**
//     * simple log method for jsp page.
//     * @param req
//     */
//    public static void log(HttpServletRequest req) {
//        log(getRequestInfo(req), null);
//    }
//
//	public static String getParameter(HttpServletRequest request, String paramName) {		
//		return request.getParameter(paramName) ;
//	}
}