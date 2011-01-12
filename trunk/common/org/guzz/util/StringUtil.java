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

import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.guzz.exception.GuzzException;


/**
 * String helper class.
 * 
 * TODO: check TagSupportUtil#escapeXml to improve performance.
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class StringUtil {
	
	public static final StringUtil instance = new StringUtil() ;

	/**
	 * 检测字符串是否为null，或者trim()以后的长度是否为0。
	 */
	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}
	
	public static boolean notEmpty(String s){
		return !isEmpty(s) ;
	}

	public final static char[] filterChars =  {',','，',';','；','"','“','”','‘','’', '=', '(', ')', '[', ']','，', '/', '@', '>', '<', '!', '&', '*', '^', '-', '+', '\'', '\\' };

	private static boolean[] xlstInvalidChars = new boolean[256] ; 
	
	static{
		for(int i = 0 ; i < xlstInvalidChars.length ; i++){
			xlstInvalidChars[i] = false ;
		}
		
		//0-8
		for(int i = 0 ; i < 9 ; i++){
			xlstInvalidChars[i] = true ;
		}
		
		//14-31
		for(int i = 14 ; i < 32 ; i++){
			xlstInvalidChars[i] = true ;
		}
		
		xlstInvalidChars[127] = true ;
	}
	
	/**删掉xlst不支持的字符，然后返回原文*/
	public static String getXSLTFreeText(String text){
		if(text == null) return null ;
		
		StringBuffer sb = new StringBuffer(text.length()*2) ;
		for(int i = 0 ; i < text.length() ; i++){
			char c = text.charAt(i) ;
			if(c > 127){
				sb.append(c) ;
			}else{
				if(!xlstInvalidChars[c]){
					sb.append(c) ;
				}
			}
		}
		
		return sb.toString() ;
	}
	
	/**转换字符串，用于js中引用。*/
	public static String js_string(String string){
		if(string == null) return "" ;
				
		StringBuffer sb = new StringBuffer(string.length() * 2 + 2) ;
		for(int i = 0 ; i < string.length() ; i++){
			char c = string.charAt(i) ;
			
			if(c == '\'' || c == '\"' || c == '\\'){
				sb.append("\\") ;
			}
			
			sb.append(c) ;
		}
		
		return sb.toString() ;
	}
	
	/**native character 2 unicode ascill code*/
	public static String native2ascii(String text){
		if(text == null) return null ;
		
		char[] myBuffer = text.toCharArray() ;
        StringBuffer sb = new StringBuffer() ;
        
        for (int i = 0; i < myBuffer.length; i++) {
        	char c = myBuffer[i] ;
            Character.UnicodeBlock ub = UnicodeBlock.of(c) ;
            
            if(ub == UnicodeBlock.BASIC_LATIN){
                //英文及数字等
                sb.append(c) ;
            }else{
                //汉字
                String hexS = Integer.toHexString(c & 0xffff) ;
                sb.append("\\u").append(hexS.toLowerCase()) ;
            }
        }
        return sb.toString() ;
	}

	/**
	 * 将URL地址中给定的一个参数去掉。
	 *
	 * @param queryString 将要处理的URL地址
	 * @param toescape 要删掉的参数名称。
	 */
	public static String getSubQueryString(String queryString, String toescape) {
		// String queryString = request.getQueryString() ;
		if (queryString == null)
			return "";

		int pos = queryString.indexOf(toescape);
		if (pos < 0)
			return queryString; // 不存在需要的数

		StringBuffer sb = new StringBuffer(128);
		int total = queryString.length();

		if (pos > 0) {
			int i = 0;
			while (i < pos) {
				sb.append(queryString.charAt(i));
				i++;
			}
		}

		while (pos < total && queryString.charAt(pos++) != '&') {
			;
		}
		while (pos < total) {
			sb.append(queryString.charAt(pos));
			pos++;
		}
		int sbl = sb.length();
		if (sbl > 0 && sb.charAt(sbl - 1) == '&') {
			sb.setLength(sbl - 1);
		}

		return sb.toString();
	}

	public static String getURLWithoutPageNo(HttpServletRequest request, String pageNoParamName){
		String queryString = request.getQueryString() ;
		queryString =  getSubQueryString(queryString, pageNoParamName) ;

		StringBuffer spath = request.getRequestURL() ;

		if(StringUtil.isEmpty(queryString)){
			spath.append("?") ;
		}else{
			spath.append("?").append(queryString) ;
		}
		return spath.toString() ;
	}

	/**
	 * 把null变成""<br>
	 *
	 */
	public static String dealNull(String str) {
		String returnstr = null;
		if (str == null)
			returnstr = "";
		else
			returnstr = str;
		return returnstr;
	}

	/**
	 * 字符串替换函数
	 *
	 * @param str 原始字符串
	 * @param substr 要替换的字符
	 * @param restr 替换后的字符
	 * @return 替换完成的字符串
	 */
	public static String replaceString(String str, String substr, String restr) {
		if(str == null) return null ;
		
		if(substr == null || substr.length() == 0) return str ;
		
		String[] tmp = splitString(str, substr);
		String returnstr = null;
		if (tmp.length != 0) {
			returnstr = tmp[0];
			for (int i = 0; i < tmp.length - 1; i++)
				returnstr = dealNull(returnstr) + restr + tmp[i + 1];
		}
		return dealNull(returnstr);
	}

	/**
	 * 分割字串，一般情况下客户端要考虑把返回字符数组中每个字符串进行trim()<br>
	 * 本方法本身不会添加空格，不过也不会把前后空格删除。
	 *
	 * @param toSplit 原始字符串
	 * @param delimiter 分割字符串
	 * @return 字符串数组
	 */
	public static String[] splitString(String toSplit, String delimiter) {
		if (toSplit == null)
			return new String[0];

		int arynum = 0, intIdx = 0, intIdex = 0, div_length = delimiter.length();
		if (toSplit.compareTo("") != 0) {
			if (toSplit.indexOf(delimiter) != -1) {
				intIdx = toSplit.indexOf(delimiter);
				for (int intCount = 1;; intCount++) {
					if (toSplit.indexOf(delimiter, intIdx + div_length) != -1) {
						intIdx = toSplit
								.indexOf(delimiter, intIdx + div_length);
						arynum = intCount;
					} else {
						arynum += 2;
						break;
					}
				}
			} else
				arynum = 1;
		} else
			arynum = 0;

		intIdx = 0;
		intIdex = 0;
		String[] returnStr = new String[arynum];

		if (toSplit.compareTo("") != 0) {
			if (toSplit.indexOf(delimiter) != -1) {
				intIdx = (int) toSplit.indexOf(delimiter);
				returnStr[0] = (String) toSplit.substring(0, intIdx);
				for (int intCount = 1;; intCount++) {
					if (toSplit.indexOf(delimiter, intIdx + div_length) != -1) {
						intIdex = (int) toSplit.indexOf(delimiter, intIdx
								+ div_length);
						returnStr[intCount] = (String) toSplit.substring(intIdx
								+ div_length, intIdex);
						intIdx = (int) toSplit.indexOf(delimiter, intIdx
								+ div_length);
					} else {
						returnStr[intCount] = (String) toSplit.substring(intIdx
								+ div_length, toSplit.length());
						break;
					}
				}
			} else {
				returnStr[0] = (String) toSplit.substring(0, toSplit.length());
				return returnStr;
			}
		} else {
			return returnStr;
		}
		return returnStr;
	}
	
	
	/**
	 * 字符串替换函数
	 *
	 * @param str 原始字符串
	 * @param substr 要替换的字符
	 * @param restr 替换后的字符
	 * @return 替换完成的字符串
	 */
	public static String replaceStringIgnoreCase(String str, String substr, String restr) {
		if(str == null) return null ;
		
		if(substr == null || substr.length() == 0) return str ;
		
		String[] tmp = splitStringIgnoreCase(str, substr);
		String returnstr = null;
		if (tmp.length != 0) {
			returnstr = tmp[0];
			for (int i = 0; i < tmp.length - 1; i++)
				returnstr = dealNull(returnstr) + restr + tmp[i + 1];
		}
		return dealNull(returnstr);
	}
	
	/**
	 * 分割字串，一般情况下客户端要考虑把返回字符数组中每个字符串进行trim()<br>
	 * 本方法本身不会添加空格，不过也不会把前后空格删除。
	 * 
	 * @param toSplit 原始字符串
	 * @param delimiter 分割字符串
	 * @return 字符串数组
	 */
	public static String[] splitStringIgnoreCase(String toSplit, String delimiter) {
		if (toSplit == null)
			return new String[0];
		

		//我们基于按照大小写字符长度进行。
		String orginalToSplit = toSplit ;
		
		toSplit = toSplit.toLowerCase() ;
		delimiter = delimiter.toLowerCase() ;

		int arynum = 0, intIdx = 0, intIdex = 0, div_length = delimiter.length();
		if (toSplit.compareTo("") != 0) {
			if (toSplit.indexOf(delimiter) != -1) {
				intIdx = toSplit.indexOf(delimiter);
				for (int intCount = 1;; intCount++) {
					if (toSplit.indexOf(delimiter, intIdx + div_length) != -1) {
						intIdx = toSplit.indexOf(delimiter, intIdx + div_length);
						arynum = intCount;
					} else {
						arynum += 2;
						break;
					}
				}
			} else
				arynum = 1;
		} else
			arynum = 0;

		intIdx = 0;
		intIdex = 0;
		String[] returnStr = new String[arynum];

		if (toSplit.compareTo("") != 0) {
			if (toSplit.indexOf(delimiter) != -1) {
				intIdx = (int) toSplit.indexOf(delimiter);
				returnStr[0] = (String) orginalToSplit.substring(0, intIdx);
				for (int intCount = 1;; intCount++) {
					if (toSplit.indexOf(delimiter, intIdx + div_length) != -1) {
						intIdex = (int) toSplit.indexOf(delimiter, intIdx + div_length);
						returnStr[intCount] = (String) orginalToSplit.substring(intIdx	+ div_length, intIdex);
						intIdx = (int) toSplit.indexOf(delimiter, intIdx + div_length);
					} else {
						returnStr[intCount] = (String) orginalToSplit.substring(intIdx + div_length, toSplit.length());
						break;
					}
				}
			} else {
				returnStr[0] = (String) orginalToSplit.substring(0, toSplit.length());
				return returnStr;
			}
		} else {
			return returnStr;
		}
		return returnStr;
	}

	/**
	 * 删除字符串中的多余空格。并且把字符串的前后空格删掉。
	 * <pre>
	 * 例如把"     "变成" "，把制表符'\t'变成" ";
	 * </pre>
	 */
	public static String squeezeWhiteSpace(String str) {
		if(str == null) return null ;
		char[] cs = str.toCharArray() ;
		
		StringBuffer sb2 = new StringBuffer(cs.length);
		
		boolean alreadyMeetSpace = true ;
		
		for(int i = 0 ; i < cs.length ; i++){
			char c = cs[i] ;
			
			//whitespace, tab, Chinese whitespace, no-breaking whitespace(ASCII code 160)
			if(c == ' ' || c == '\t' || c == '　' || c == ' '){
				if(alreadyMeetSpace){
					continue ;
				}else{
					sb2.append(' ') ;
					alreadyMeetSpace = true ;
				}
			}else{
				sb2.append(c) ;
				
				alreadyMeetSpace = false ;
			}
		}
		
		//check the last char is not space
		if(alreadyMeetSpace){
			//perform right-trim()
			if(sb2.length() > 0){
				sb2.setLength(sb2.length() - 1) ;
			}
		}
		
		return sb2.toString() ;
	}

	public static int toInt(String s, int defaultValue) {
		if(s == null) return defaultValue ;
		
		try {
			return new Integer(s).intValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float toFloat(String s, float defaultValue) {
		if(s == null) return defaultValue ;
		
		try {
			return new Float(s).floatValue() ;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 把string转换成int
	 *
	 * @param s 要转换成int的String
	 * @return 如果转换失败，返回-1
	 */
	public static int toInt(String s) {
		return toInt(s, -1);
	}

	public static boolean toBoolean(String s, boolean defaultValue) {
		if(s == null) return defaultValue ;
		
		try {
			return Boolean.valueOf(s).booleanValue() ;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 删除数组中相同的元素。<br>
	 * 例如数组中元素为a b b c c c,合并重复元素后为a b c
	 *
	 * @param s 原始数组
	 * @return 不含重复元素的数组。
	 */
	public static String[] mergeDuplicateArray(String[] s)
	{
		List list = Arrays.asList(s);
		Set set = new HashSet(list);
		return (String [])set.toArray(new String[set.size()]);
	}
	
	/**将用;分割的字符串，转换成Map返回。如果传入的字符串为空，返回null。格式：paramName=paramValue;paramNam2=paramValue2*/
	public static Map stringParamsToMap(String stringParams, String splitString){
		if(StringUtil.isEmpty(stringParams)) return null ;
		
		String[] cs = StringUtil.splitString(stringParams, splitString) ;
		
		HashMap map = new HashMap() ;
		
		for(int i = 0 ; i < cs.length ; i++){
			String param = cs[i].trim() ;
			if(param.length() == 0) continue ;
			
			int eqSybol = param.indexOf('=') ;
			
			if(eqSybol < 0){
				throw new GuzzException(param + " is invalid. should be:paramName=paramValue;paramNam2=paramValue2.") ;
			}
			
			String name = param.substring(0, eqSybol) ;
			String value = param.substring(eqSybol + 1) ;
			
			map.put(name, value) ;
		}
		
		return map ;
	}

}
