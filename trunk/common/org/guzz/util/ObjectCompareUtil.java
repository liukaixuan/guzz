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

/**
 * 
 * 用于进行对象比较的工具类。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date 2007-9-14 下午04:04:05
 */
public abstract class ObjectCompareUtil {
	
	public static boolean objectEquals(Object src, Object to){
		if(src == to) return true ;
		if(src == null && to != null) return false ;
		if(src != null && to == null) return false ;
		
		//数字判断
		if(src instanceof java.lang.Number){//如果都是数字的话。
			if(to instanceof java.lang.Number){
				return ((Number) src).doubleValue() == ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		if(to instanceof java.lang.Number){//如果都是数字的话。
			if(src instanceof java.lang.Number){
				return ((Number) src).doubleValue() == ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		//字符传类型检查
		if(src instanceof Comparable && to instanceof Comparable){
			return ((Comparable) src).compareTo(to) == 0 ;
		}
		
		return src.equals(to) ;
	}
	
	public static boolean objectBigger(Object src, Object to){
		if(src == to) return false ;
		if(src == null || to == null) return false ;
		
		//数字判断
		if(src instanceof java.lang.Number){//如果都是数字的话。
			if(to instanceof java.lang.Number){
				return ((Number) src).doubleValue() > ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		if(to instanceof java.lang.Number){//如果都是数字的话。
			if(src instanceof java.lang.Number){
				return ((Number) src).doubleValue() < ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		//字符传类型检查
		if(src instanceof Comparable && to instanceof Comparable){
			return ((Comparable) src).compareTo(to) > 0 ;
		}
		
		return false ;
	}
	
	public static boolean objectBiggerOrEquals(Object src, Object to){
		if(src == to) return true ;
		if(src == null && to != null) return false ;
		if(src != null && to == null) return false ;
		
		//数字判断
		if(src instanceof java.lang.Number){//如果都是数字的话。
			if(to instanceof java.lang.Number){
				return ((Number) src).doubleValue() >= ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		if(to instanceof java.lang.Number){//如果都是数字的话。
			if(src instanceof java.lang.Number){
				return ((Number) src).doubleValue() <= ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		//字符传类型检查
		if(src instanceof Comparable && to instanceof Comparable){
			return ((Comparable) src).compareTo(to) >= 0 ;
		}
		
		return src.equals(to) ;
	}
	
	public static boolean objectSmaller(Object src, Object to){
		if(src == to) return false ;
		if(src == null || to == null) return false ;
		
		//数字判断
		if(src instanceof java.lang.Number){//如果都是数字的话。
			if(to instanceof java.lang.Number){
				return ((Number) src).doubleValue() < ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		if(to instanceof java.lang.Number){//如果都是数字的话。
			if(src instanceof java.lang.Number){
				return ((Number) src).doubleValue() > ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		//字符传类型检查
		if(src instanceof Comparable && to instanceof Comparable){
			return ((Comparable) src).compareTo(to) < 0 ;
		}
		
		return false ;
	}
	
	public static boolean objectSmallerOrEquals(Object src, Object to){
		if(src == to) return true ;
		if(src == null && to != null) return false ;
		if(src != null && to == null) return false ;
		
		//数字判断
		if(src instanceof java.lang.Number){//如果都是数字的话。
			if(to instanceof java.lang.Number){
				return ((Number) src).doubleValue() <= ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		if(to instanceof java.lang.Number){//如果都是数字的话。
			if(src instanceof java.lang.Number){
				return ((Number) src).doubleValue() >= ((Number) to).doubleValue() ;
			}
			return false ;
		}
		
		//字符传类型检查
		if(src instanceof Comparable && to instanceof Comparable){
			return ((Comparable) src).compareTo(to) <= 0 ;
		}
		
		return src.equals(to) ;
	}

}
