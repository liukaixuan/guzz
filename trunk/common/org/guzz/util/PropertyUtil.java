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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.io.Resource;

/**
 * 处理配置属性的工具类. <BR>
 */
public class PropertyUtil {

	private static transient final Log logger = LogFactory.getLog(Properties.class) ;
    
    /** Prefix for property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * 从指定文件名的文件中获取属性. <BR>
     * 文件名参数说明: by converting the given pathname string into an abstract
     * pathname.
     * @param fileName 给定的文件名
     * @return 参见 {@link #loadProperties(File)}方法.
     */
    public static Properties loadProperties(String fileName) {
        if (fileName == null) {
            return new Properties();
        }
        return loadProperties(new File(fileName));
    }

    /**
     * 从给定文件中获取属性.
     * @param f 给定的文件
     * @return 给定的文件中获取到的属性. 如果有异常发生, 则返回一个empty的属性.
     */
    public static Properties loadProperties(File f) {
        Properties props = new Properties();
        if (f == null || false == f.isFile()) {
            return props;
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(f);
            props.load(fis);
        } catch (Exception e) {
            logger.error("erron on load file: " + f, e);
        } finally {
            CloseUtil.close(fis);
        }
        return props;
    }

    /**
     * 从给定properties文件中读取给定key的属性. 本方法是仅读取一个key的情况时的便捷方法.
     * 如果要读取多个key, 出于效率原因, 不要使用本方法, 此时请先获取到Properties, 再逐一读取. 
     * @param f
     * @param key
     * @param defaultValue
     */
    public static String loadProperty(File f, String key, String defaultValue) {
        Properties props = loadProperties(f);
        return props.getProperty(key, defaultValue);
    }
    
    /**
     * 从给定的资源文件获取属性. <BR>
     * 资源文件是指位于classpath中的文件.
     * @param resName 给定的资源文件
     * @return 给定的资源文件中获取到的属性. 如果有异常发生, 则返回一个empty的属性.
     */
    public static Properties loadFromResource(String resName) {
        return loadFromResource(PropertyUtil.class, resName);
    }

    /**
     * 从给定的资源文件获取属性. <BR>
     * 资源文件是指位于classpath中的文件.
     * @param resName 给定的资源文件
     * @return 给定的资源文件中获取到的属性. 如果有异常发生, 则返回一个empty的属性.
     */
    public static Properties loadFromResource(Class clazz, String resName) {
        Properties props = new Properties();
        URL resUrl = clazz.getResource(resName);
        if (resUrl == null) {
            logger.warn("resUrl=null! resName=" + resName);
            return props;
        }
        InputStream fis = null;
        try {
            fis = resUrl.openStream();
        } catch (IOException e) {
            logger.error("erron on url.openStream(), url=" + resUrl, e);
        }
        
        try {
            props.load(fis);
        } catch (Exception e) {
            logger.error("erron on load resource: " + resName + ", url=" + resUrl, e);
        } finally {
            // [ls@2005-02-05] The JDK doesn't close the inputstream, so we must close it.
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    logger.error("erron on close resource: " + resName + ", url=" + resUrl, e1);
                }
            }
        }
        return props;
    }

    /**
     * 从给定的资源文件获取以指定前缀开始的配置项的取值属性集合. <BR>
     * 资源文件是指位于classpath中的文件.
     * @param resName 给定的资源文件
     * @return 给定的资源文件中获取到的所有以指定前缀开始的配置项的属性. 如果有异常发生, 则返回一个empty的属性.
     */
    public static Properties loadFromResource(Class clazz, String resName,
            String keyPrefix) {
        Properties props = loadFromResource(clazz, resName);
        Properties result = new Properties();
        synchronized (props) {
            int max = props.size() - 1;
            Iterator it = props.entrySet().iterator();
            for (int i = 0; i <= max; i++) {
                Map.Entry e = (Map.Entry) (it.next());
                String key = (String) e.getKey();
                if (key.startsWith(keyPrefix)) {
                    result.put(key, e.getValue());
                }
            }
        }
        return result;
    }

    /**
     * 以int值返回给定的Properties对象中的指定项的取值. <BR>
     * 如果Properties对象为null, 或Properties对象中找不到该key, 或该value解析成整数时发生异常, 则返回给定的默认值.
     * @param props 给定的Properties对象
     * @param key 指定项
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项的取值
     */
    public static int getPropertyAsInt(Properties props, String key,
            int defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(strValue.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 以GBK编码的字符串值返回给定的Properties对象中的指定项的取值. <BR>
     * 如果Properties对象为null或是Properties对象中找不到该key, 则返回空串("").
     * 该方法用于处理从Properties文件中获取中文值的问题.
     * @param props 给定的Properties对象
     * @param key 指定项
     * @return Properties对象中的指定项的取值
     */
    public static String getPropertyAsGBK(Properties props, String key) {
        return getPropertyAsGBK(props, key, "");
    }
    
    /**
     * 以GBK编码的字符串值返回给定的Properties对象中的指定项的取值. <BR>
     * 如果Properties对象为null或是Properties对象中找不到该key, 则返回给定的默认值.
     * 该方法用于处理从Properties文件中获取中文值的问题.
     * @param props 给定的Properties对象
     * @param key 指定项
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项的取值
     */
    public static String getPropertyAsGBK(Properties props, String key, String defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        try {
            return new String(strValue.getBytes("ISO-8859-1"), "GBK");
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 返回给定的Properties对象中的指定项的字符串取值, 并作trim()处理. <BR>
     * The method return defaultValue on one of these conditions:
     * <li>props == null 
     * <li>props.getProperty(key) == null 
     * <li>props.getProperty(key).trim().length() == 0 
     * @param props 给定的Properties对象
     * @param key 指定项
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项trim后的取值
     */
    public static String getTrimString(Properties props, String key, String defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        strValue = strValue.trim();
        return strValue.length() == 0 ? defaultValue : strValue;
    }
    
    /**
	 * Fetch the value of the given key, and resolve ${...} placeholders , then trim() the value and return.
	 * @param props Properties
	 * @param key the key to fetch
	 * @param defaultValue return defaultValue on props is null or the key doesn't exsits in props.
	 * @return the resolved value
	 * 
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 */
	public static String getTrimPlaceholdersString(Properties props, String key, String defaultValue) {
		if (props == null) {
			return defaultValue;
	    }
	    String text = props.getProperty(key);
	    if (text == null) {
	    	return defaultValue;
	    }	
		
		StringBuffer buf = new StringBuffer(text);

		// The following code does not use JDK 1.4's StringBuffer.indexOf(String)
		// method to retain JDK 1.3 compatibility. The slight loss in performance
		// is not really relevant, as this code will typically just run on startup.

		int startIndex = text.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = buf.toString().indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
				String propVal = props.getProperty(placeholder);
				if (propVal != null) {
					buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
					startIndex = buf.toString().indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
				}
				else {
					logger.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "]");
					startIndex = buf.toString().indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_SUFFIX.length());
				}
			}
			else {
				startIndex = -1;
			}
		}

		return buf.toString().trim();
	}
	
	/**
	 * >= JDK1.4
	 * resolve ${...} placeholders in the @param text , then trim() the value and return.
	 * @param props Map
	 * @param text the String text to replace
	 * @return the resolved value
	 * 
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 */
	public static String getTrimStringMatchPlaceholdersInMap(Map props, String text) {
		if (text == null || props == null || props.isEmpty()) {
			return text;
	    }	
		
		StringBuffer buf = new StringBuffer(text);
		
		int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
				String propVal = (String) props.get(placeholder);
				if (propVal != null) {
					buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
					startIndex = buf.indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
				}
				else {
					logger.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "]");
					startIndex = buf.indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_SUFFIX.length());
				}
			}
			else {
				startIndex = -1;
			}
		}

		return buf.toString().trim();
	}
	

    /**
     * 以long值返回给定的Properties对象中的指定项的取值. 如果Properties对象中找不到该key, 则返回给定的默认值.
     * @param props 给定的Properties对象
     * @param key 指定项
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项的取值
     */
    public static long getPropertyAsLong(Properties props, String key,
            long defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(strValue.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 以基本类型boolean值返回给定的Properties对象中的指定项的取值. 如果Properties对象中找不到该key,
     * 则返回给定的默认值. 表示布尔值的字符串大小写无关. 当且仅当表示布尔值的字符串为"true"时(忽略大小写), 返回true. 例如:
     * <tt>Boolean.valueOf("True")</tt> returns <tt>true</tt>.<br>
     * 再如: <tt>Boolean.valueOf("yes")</tt> returns <tt>false</tt>.
     * @param props 给定的Properties对象
     * @param key 指定项
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项的取值. 当且仅当表示布尔值的字符串为"true"时(忽略大小写), 返回true.
     */
    public static boolean getPropertyAsBool(Properties props, String key,
            boolean defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null || "".equals(strValue)) {
            return defaultValue;
        }
        try {
            return strValue.trim().equalsIgnoreCase("true");
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 以字符串数组返回给定的Properties对象中的指定项的取值. <BR><BR>
     * 下列情况下, 本方法返回null:
     * <li>给定的Properties对象为null.
     * <li>给定的Properties对象中找不到该key.
     * <li>给定的Properties对象中该key对应的字符串值为空(即<code>trim().length() == 0</code>).
     * @param props 给定的Properties对象
     * @param key 指定项. 不允许为null.
     * @param token 将字符串分割为字符串数组的分隔标记. 不允许为null.
     * @return Properties对象中的指定项的字符串数组取值.
     */
    public static String[] getPropertyAsStrAry(Properties props, String key, String token) {
        if (props == null) {
            return null;
        }
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() == 0) {
            return null;
        }
        return value.split(token);
    }

    /**
     * 以float值返回给定的Properties对象中的指定项的取值. 如果Properties对象中找不到该key, 则返回给定的默认值.
     * @param props 给定的Properties对象
     * @param key 指定项. 不允许为null.
     * @param defaultValue 给定的默认值
     * @return Properties对象中的指定项的取值
     */
    public static float getPropertyAsFloat(Properties props, String key,
            float defaultValue) {
        if (props == null) {
            return defaultValue;
        }
        String strValue = props.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(strValue.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 输出给定的JDK Properties对象中以给定字符串为前缀的所有Key及其取值. <BR>
     * 由于JDK Properties类自己的toString()方法得到的是所有key及其取值, 有时使得调试信息中无用的部分太多, 所以提供本方法.
     * @param props 给定的JDK Properties对象
     * @param keyPrefix 给定的key前缀
     * @return Properties对象中以给定字符串为前缀的所有Key及其取值组成的字符串.
     */
    public static String toString(Properties props, String keyPrefix) {
        if (props == null || keyPrefix == null) {
            return "null! keyPrefix=" + keyPrefix;
        }
        final String delit = ", ";
        final int delitLen = delit.length();
        StringBuffer sb = new StringBuffer(64);
        sb.append('{');
        // 添加符合条件(以给定字符串开头)的Key的信息. 借鉴JDK
        // Hashtable类(Properties类的父类)自己的toString过程的算法.
        synchronized (props) {
            int max = props.size() - 1;
            Iterator it = props.entrySet().iterator();
            for (int i = 0; i <= max; i++) {
                Map.Entry e = (Map.Entry) (it.next());
                String key = (String) e.getKey();
                if (key.startsWith(keyPrefix)) {
                    sb.append(key).append('=').append(e.getValue());
                    sb.append(delit);
                }
            }
        }
        sb.delete((sb.length() > delitLen) ? (sb.length() - delitLen) : 1, sb.length());
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * 加载类似Mysql配置文件my.cnf似的配置项，加载以后组成一个按照[key]的key为key，[key]下的值为value组成的Properties的Map
     * 
     * @param resource 加载的配置文件，加载完成后自动关闭resource
     * @return Map 组名vs组内prop组成的Properties[]对象。如果解析出错，返回null
     */
    public static Map loadGroupedProps(Resource resource) {
    	Map resources = new HashMap() ;
    	
		LineNumberReader lnr = null ;
		String line = null ;
		
		String groupName = null ;
		Properties props = null ;
		
		try{
			lnr = new LineNumberReader(new InputStreamReader(resource.getInputStream())) ;
			
			while((line = lnr.readLine()) != null){
				line = line.trim() ;
				int length = line.length() ;
				
				if(length == 0) continue ;
				if(line.charAt(0) == '#') continue ;
				if(line.startsWith("rem ")) continue ;
				
				if(line.charAt(0) == '[' && line.charAt(length - 1) == ']'){//如果是[xxxx]标示进入了一个新组。
					//遇到了一个组，保存上一个组。
					if(groupName != null){
						Properties[] p = (Properties[]) resources.get(groupName) ;
						
						if(p == null){
							resources.put(groupName, new Properties[]{props}) ;
						}else{
							resources.put(groupName, ArrayUtil.addToArray(p, props)) ;
						}
					}
					
					//开始新组的数据记录
					groupName = line.substring(1, length - 1) ;
					props = new Properties() ;
				}else{ //组内的一个属性
					if(groupName == null){
						logger.warn("ignore ungrouped config property:" + line) ;
					}else{
						int pos = line.indexOf('=') ;
						
						if(pos < 1){
							props.put(line, "") ;
							logger.warn("loading special config property:" + line) ;
						}else{
							String key = line.substring(0, pos).trim() ;
							String value = line.substring(pos + 1, length).trim() ;
							
							props.put(key, value) ;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error(resource.toString(), e) ;
			return null ;
		}finally{
			CloseUtil.close(lnr) ;
			CloseUtil.close(resource) ;
		}
		
		//保存最后一组
		if(groupName != null){
			Properties[] p = (Properties[]) resources.get(groupName) ;
			
			if(p == null){
				resources.put(groupName, new Properties[]{props}) ;
			}else{
				resources.put(groupName, ArrayUtil.addToArray(p, props)) ;
			}		
		}		
	
    	return resources ;
    	
    }
}