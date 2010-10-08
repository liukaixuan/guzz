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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

	public final static int COOKIE_AGE_1Min = 60;

	public final static int COOKIE_AGE_1H = 3600;

	public final static int COOKIE_AGE_24H = 3600 * 24;

	public final static int COOKIE_AGE_1Week = COOKIE_AGE_24H * 7;

	public final static int COOKIE_AGE_1Year = COOKIE_AGE_24H * 365;

	public final static int COOKIE_AGE_SESSION = -1;
	
	private String cookiePath = "/" ;
	
	private int version = 0 ;
	
	public static CookieUtil forVersion0(){
		CookieUtil u = new CookieUtil() ;
		
		return u ;
	}
	
	public static CookieUtil forVersion0(String cookiePath){
		CookieUtil u = new CookieUtil(cookiePath) ;
		
		return u ;
	}
	
	public static CookieUtil forVersion1(){
		CookieUtil u = new CookieUtil() ;
		u.version = 1 ;
		
		return u ;
	}
	
	public static CookieUtil forVersion1(String cookiePath){
		CookieUtil u = new CookieUtil(cookiePath) ;
		u.version = 1 ;
		
		return u ;
	}
	
	private CookieUtil(){
	}
	
	private CookieUtil(String cookiePath){
		this.cookiePath = cookiePath ;
	}

	public void writeCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setVersion(version) ;
		cookie.setPath(cookiePath);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	
	public void writeCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setVersion(version) ;
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public void writeTempCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setVersion(version) ;
		cookie.setPath(cookiePath);
		cookie.setMaxAge(COOKIE_AGE_SESSION) ;
		response.addCookie(cookie);
	}

	public void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setVersion(version) ;
		cookie.setPath(cookiePath);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
	public void deleteCookie(HttpServletResponse response, String name, String path) {
		Cookie cookie = new Cookie(name, "");
		cookie.setVersion(version) ;
		cookie.setPath(path);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public String readCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i] ;
				cookie.setVersion(version) ;
				
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	public int readCookieAsInt(HttpServletRequest request, String name, int defaultValue) {
		String cookieValue = readCookie(request, name);

		return StringUtil.toInt(cookieValue, defaultValue);
	}
	
	/**
	 * Read all cookies from the request's 'cookie' header, and parse it based on Cookie Version 1.
	 * <p/>This method can resolve tomcat's bug in handling special characters in cookie (The cookie is maybe written by other applications).
	 * 
	 * @param request HttpServletRequest
	 * @param cookieName
	 */
	public static String readCookieIgnoreSpecialCharacters(HttpServletRequest request, String cookieName) {
		String cs = request.getHeader("cookie") ;
		if(cs == null) return null ;
		
		int start = cs.indexOf(cookieName) ;
		if(start < 0 ) return null ;
		
		cs = cs.substring(start + 1 + cookieName.length()) ;
		
		int end = cs.indexOf(';') ;
		if(end > 0 ){
			cs = cs.substring(0, end) ;
		}
		
		return cs ;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

}
