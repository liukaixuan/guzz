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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期和时间相关工具方法. <BR>
 * 
 */
public class DateUtil {

    /**
     * 将使用的毫秒数转化为可读的字符串, 如1天1小时1分1秒. <BR>
     * <code>assertEquals("1天1小时1分1秒", DateUtil.timeToString(90061000));</code>
     * @param msUsed 使用的毫秒数.
     * @return 可读的字符串, 如1天1小时1分1秒.
     */
    public static  String timeToString(long msUsed) {
        // TODO 用移位运算提高性能.
        if (msUsed < 0) {
            return String.valueOf(msUsed);
        }
        if (msUsed < 1000) {
            return String.valueOf(msUsed) + "毫秒";
        }
        //长于1秒的过程，毫秒不计
        msUsed /= 1000;
        if (msUsed < 60) {
            return String.valueOf(msUsed) + "秒";
        }
        if (msUsed < 3600) {
            long nMinute = msUsed / 60;
            long nSecond = msUsed % 60;
            return String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
        }
        //3600 * 24 = 86400
        if (msUsed < 86400) {
            long nHour = msUsed / 3600;
            long nMinute = (msUsed - nHour*3600) / 60;
            long nSecond = (msUsed - nHour*3600) % 60;
            return String.valueOf(nHour) + "小时" + String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
        }
        
        long nDay = msUsed / 86400;
        long nHour = (msUsed - nDay*86400) / 3600;
        long nMinute = (msUsed - nDay*86400 - nHour*3600) / 60;
        long nSecond = (msUsed - nDay*86400 - nHour*3600) % 60;
        return String.valueOf(nDay) + "天" + String.valueOf(nHour) + "小时" + String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
    }
    
    /**
     * 取本周一.
     * @return 本周一
     */
    public static Calendar getThisMonday() {
        return getThatMonday(Calendar.getInstance());
    }

    /**
     * 获取cal所在周的周一.
     * @param cal 给定日期
     * @return cal所在周的周一
     */
    public static Calendar getThatMonday(Calendar cal) {
        int n = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        cal.add(Calendar.DATE, n);
        return cal;
    }

    /**
     * 取本周日.
     * @return 本周日
     */
    public static Calendar getThisSunday() {
        return getThatSunday(Calendar.getInstance());
    }

    /**
     * 获取cal所在周的周日.
     * @param cal 给定日期
     * @return cal所在周的周日
     */
    public static Calendar getThatSunday(Calendar cal) {
        int n = (Calendar.SUNDAY + 7) - cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, n);
        return cal;
    }
    
    /**
     * 获取两个日期相差的天数.
     * @return 两个日期相差的天数.
     */
    public static int minus(Calendar cal1, Calendar cal2) {
        if (cal1.after(cal2)) {
            int nBase = (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 365;
            return nBase + cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return minus(cal2, cal1);
        }
    }
    
    /**
     * 从Date对象得到Calendar对象. <BR>
     * JDK提供了Calendar.getTime()方法, 可从Calendar对象得到Date对象, 但没有提供从Date对象得到Calendar对象的方法.
     * @param date 给定的Date对象 
     * @return 得到的Calendar对象. 如果date参数为null, 则得到表示当前时间的Calendar对象.
     */
    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        return cal;
    }
    
    /**
     * 完成日期串到日期对象的转换. <BR>
     * @param dateString 日期字符串
     * @param dateFormat 日期格式
     * @return date 日期对象
     */
    public static Date stringToDate(String dateString, String dateFormat) {
        if ("".equals(dateString) || dateString == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(dateFormat).parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取和指定cal对象相隔指定天数的cal对象. 大于0表示之后, 小于0表之前.
     * @param cal 指定cal对象
     * @param relativeDay 相隔指定天数
     * @return cal对象
     */
    public static Calendar getCalendar(Calendar cal, int relativeDay) {
        cal.add(Calendar.DATE, relativeDay);
        return cal;
    }
    
    /**
     * 获取和当天相隔指定天数的Date对象. 大于0表示之后, 小于0表之前.
     * @param relativeDay 相隔指定天数
     * @return Date对象
     * @see #getCalendar(Calendar, int)
     */
    public static Date getDate(int relativeDay) {
        return getCalendar(Calendar.getInstance(), relativeDay).getTime();
    }
    
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).format(date);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查一个时间是否接近于一个时间。
     * 
     * @param date 要比较的时间
     * @param baseDate 基础时间
     * @param seconds 秒数
     * 
     * @return 如果 date 在 baseDate前后 seonds 秒数，则返回true，否则返回false。
     */
    public static boolean isDateClose(Date date, Date baseDate, int seconds){
		
		long m_time = date.getTime() ;
		long b_time = baseDate.getTime() ;
		long ms = seconds * 1000L ;
		
		if(m_time == b_time) return true ;
		
		if(m_time > b_time){ //现在时间在基础时间之前
			return b_time + ms > m_time ;
		}else if(m_time < b_time){ //现在时间在基础时间之后
			return m_time + ms > b_time ;
		}else{ //同一个时间
			return true ;
		}		
	}
    
    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm");
    }
	
	
}