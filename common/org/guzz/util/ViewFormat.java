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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
//import java.text.SimpleDateFormat;
import java.util.* ;



/**
 * 格式化
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ViewFormat {	

	public static final ViewFormat instance = new ViewFormat() ;
	
	/**
	 * 把给定的时间段字符串转换成秒的值。<br>
	 * 为了方便，自动把"："改成":", 把"o"改为"0"
	 * @see #seconds2TimeSeg(int)
	 * @return 返回秒数，如果输入的字段无效，返回-1。
	 */
	public static int timeSeg2Seconds(String seg){
		if(seg == null ||seg.length() < 5) return -1 ;
		
		seg = StringUtil.replaceString(seg, "：", ":") ;
		seg = StringUtil.replaceString(seg, "o", "0") ;
		String[] segs = StringUtil.splitString(seg, ":") ;
		
		try{
			int hour = new Integer(segs[0]).intValue() ;
			int min = new Integer(segs[1]).intValue() ;
			int sec = new Integer(segs[2]).intValue() ;
			return hour*3600 + min* 60 + sec ;
		}catch(Exception e){
			//e.printStackTrace() ;
			return -1 ;
		}		
	}
	
	/**将用户输入的文本，按照文本格式转换成前端显示的html。*/
	public String toDisplayHtml(String content, String type){
		content = StringUtil.replaceString(content, "\r\n", "<br/>") ;
		content = StringUtil.replaceString(content, "\n", "<br/>") ;
		content = StringUtil.replaceString(content, "\r", "<br/>") ;
		
//		String c = ubbParser.parse(content) ;
		
		return StringUtil.replaceString(content, " ", "&nbsp;") ;
		
//		return c ;
	}
	
	/**
	 * timeSeg2Seconds的反向函数
	 * @see #timeSeg2Seconds(String)
	 * @return 如果输入秒数<=0，返回 ""
	 */
	public static String seconds2TimeSeg(int sec){
		if(sec <=0) return "00:00:00" ;
		
		int ms = sec%60 ;
		int mm = ((sec - ms)%3600)/60 ;
		int mh = (sec - sec%3600)/3600 ;
		
		StringBuffer sb = new StringBuffer() ;
		if(mh == 0){
			sb.append("00") ;
		}else if(mh <10){
			sb.append('0') ;
			sb.append(mh) ;
		}else{
			sb.append(mh) ;
		}
		
		sb.append(':') ;
		
		if(mm == 0){
			sb.append("00") ;
		}else if(mm <10){
			sb.append('0') ;
			sb.append(mm) ;
		}else{
			sb.append(mm) ;
		}
		
		sb.append(':') ;
		
		if(ms == 0){
			sb.append("00") ;
		}else if(ms < 10){
			sb.append('0') ;
			sb.append(ms) ;
		}else{
			sb.append(ms) ;
		}
		
		return sb.toString() ;
	}
	
	/**
	 * 把给定的秒数转变成如 "245天3小时34分7秒" 这样的格式。
	 * @return 返回转换后的字符串，如果输入秒数<=0，返回 "0秒" 。
	 */
	public static String seconds2String(int sec){
		if(sec <= 0) return "0秒" ;
		
		int left = sec%86400 ;
		int day = (sec - left)/86400 ;
		
		sec = left ;
		int hour = sec/3600 ;
		int min  = (sec - hour*3600)/60 ;
		int second = sec - hour*3600 - min*60 ;
		
		String tf = "" ;
		if(day > 0){
			tf = tf + day + "天" ;
		}
		if(hour > 0 ){
			tf = tf + hour + "小时" ;
		}
		if(min > 0){
			tf = tf + min + "分" ;
		}
		if(second > 0){
			tf = tf + second + "秒" ;
		}		
		
		return tf ;
	}
	
	public static String formatDate(Date d){
		if(d == null) return "" ;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;
		return fmt.format(d) ;
	}
	
	/**
	 * 按照参数1格式，格式化日期。	
	 * @return 格式以后的串。
	 */
	public static String formatDate(String regx, Date d){
		if(regx == null || d == null) return "" ;
		SimpleDateFormat fmt = new SimpleDateFormat(regx) ;
		return fmt.format(d) ;
	}
			
	public static String formatFileLength(int bytes){
		return formatFileLength((long) bytes) ;
	}
	
	/**
	 * 将byte长度转换成 "32.7Mb", "24.56Kb", "234字节" 格式
	 */
	public static String formatFileLength(long bytes){
		if(bytes <= 0) return "0字节" ;
		
		double gb = (bytes)/1073741824.0 ;		
		double mb = bytes/1048576.0 ;
		double kb  = bytes/1024.0 ;
		
		NumberFormat fm = NumberFormat.getInstance() ;
		fm.setMaximumFractionDigits(2) ;
		
		if(gb >= 1){
			return fm.format(gb) + "GB" ;
		}else if(mb >= 1){
			return fm.format(mb) + "MB" ;
		}else if(kb >= 1){
			return fm.format(kb) + "KB" ;
		}
		
		return fm.format(bytes) + "字节" ;
	}
	
	/**返回当前日期时间*/
	public static Date getCurrentTime(){
		return Calendar.getInstance().getTime() ;
	}
	
	/**
	 * 把用户输入的keywords串换成数据库中保存的串.
	 * 如果输入的串为null或者长度为0，返回null。
	 */
	public static String reassembleKeywords(String keywords){
		
		if(StringUtil.isEmpty(keywords)){
    		return null;
    	}
		
		keywords = keywords.trim() ;
		
		keywords = keywords.replace(',', ' ') ;
		keywords = keywords.replace('；', ' ') ;
		keywords = keywords.replace('，', ' ') ;
		keywords = StringUtil.squeezeWhiteSpace(keywords) ;
    	
		keywords = keywords.replace(' ', ';') ;
    	
    	return keywords ;
	}
	
	/**
	 * 把格式化后的关键字串分割成关键字数组。
	 * 如果关键字为null，返回长度为0的数组。
	 */
	public static String[] splitKeywords(String keywords){
		if(keywords == null) return new String[0] ;
				
		String[] words = StringUtil.splitString(keywords, ";") ;
		
		for(int i = 0 ; i < words.length ; i++ ){
			words[i] = words[i].trim() ;
		}
		
		return words ;
	}
			
	/**
	 * 把用户关键字串分割成关键字数组，分割时自动把分割的字符trim处理。
	 * 如果关键字为null，返回长度为0的数组。
	 */
	public static String[] reassembleAndSplitKeywords(String keywords){
		if(keywords == null) return new String[0] ;
		
		keywords = reassembleKeywords(keywords) ;
		
		String[] words = StringUtil.splitString(keywords, ";") ;
		
		for(int i = 0 ; i < words.length ; i++ ){
			words[i] = words[i].trim() ;
		}
		
		return words ;
	}
	
	/**
	 * 把用户输入的关键字串分割成关键字数组，并且自动踢去值为空的数组内容。
	 * 如果关键字为null，返回长度为0的数组。
	 */
//	public static String[] inputToNoEmptyValueArray(String keywords){
//		if(keywords == null) return new String[0] ;
//				
//		String[] words = reassembleAndSplitKeywords(keywords) ;
//		String[] mw = new String[0] ;
//		for(int i = 0 ; i < words.length ; i++ ){
//			if(StringUtil.notEmpty(words[i])){
//				mw = (String[]) ArrayUtils.add(mw, words[i].trim()) ;
//			}
//		}
//		
//		return mw ;
//	}
	
	/**
	 * 删除两个数组相同的元素。<br>
	 * 例如数组1和数组2都含有"book"，则此方法将同时删除数组1和数组2的"book"元素。
	 * 如果一个数组中包含重复元素，不保证全部都被删除。
	 * <b>删除操作只是把对应的字段设置成""</b>
	 * 
	 * 
	 * @param array1 数组1
	 * @param array2 数组2
	 * @param ignoreCase 是否区分大小写
	 * @return 数组是否包含重复元素并且被修改。注意：""在两个数组中重复不认为是重复元素。
	 */
	public static boolean removeDuplicateKeywords(String[] array1, String[] array2, boolean ignoreCase){
		boolean modified = false ;
		
		if(array1 == null || array2 == null) return modified ;
		if(array1.length == 0 || array2.length == 0) return modified ;
				
		for(int i = 0 ; i < array1.length ; i++){
			String word = array1[i] ;
			if(word == null) continue ;
			
			for(int j = 0 ; j < array2.length ; j++){
				if(ignoreCase){
					if(word.equalsIgnoreCase(array2[j]) && word.length() > 0){
						array1[i] = "" ;
						array2[j] = "" ;
						modified = true ;
						//break ;
					}
				}else{
					if(word.equals(array2[j]) && word.length() > 0){
						array1[i] = "" ;
						array2[j] = "" ;
						modified = true ;
						//break ;
					}
				}
			}
		}
		
		return modified ;
	}
	
//	public static void  main(String[] args){
//		System.out.println(formatFileLength(2142208)) ;
//		System.out.println(formatFileLength(11870208)) ;
//		System.out.println(formatFileLength(1)) ;
//		System.out.println(formatFileLength(1218422352)) ;
//	}

}
