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
	
	private String cookiePath = "/" ;
	
	public CookieUtil(){		
	}
	
	public CookieUtil(String cookiePath){
		this.cookiePath = cookiePath ;
	}

	public void writeCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(cookiePath);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public void writeTempCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(cookiePath);
		response.addCookie(cookie);
	}

	public void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setPath(cookiePath);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
	}

	public static String readCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (name.equals(cookies[i].getName())) {
					return cookies[i].getValue();
				}
			}
		}

		return null;
	}

	public static int readCookieAsInt(HttpServletRequest request, String name, int defaultValue) {
		String cookieValue = readCookie(request, name);

		return StringUtil.toInt(cookieValue, defaultValue);
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

}
