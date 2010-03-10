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
 * 封装一些常见的小型cache需求。
 * 
 * @author liu kaixuan
 */
public class CacheUtil {	
	
	public static IntCache createIntCache(long cacheMillSeconds){
		return new IntCache(cacheMillSeconds) ;
	}
	
	public static LongCache createLongCache(long cacheMillSeconds){
		return new LongCache(cacheMillSeconds) ;
	}
	
	public static ObjectCache createObjectCache(long cacheMillSeconds){
		return new ObjectCache(cacheMillSeconds) ;
	}
	
	public static IntCache createIntCache(long cacheMillSeconds, ICachedDataLoader loader){
		return new IntCache(cacheMillSeconds, loader) ;
	}
	
	public static LongCache createLongCache(long cacheMillSeconds, ICachedDataLoader loader){
		return new LongCache(cacheMillSeconds, loader) ;
	}
	
	public static ObjectCache createObjectCache(long cacheMillSeconds, ICachedDataLoader loader){
		return new ObjectCache(cacheMillSeconds, loader) ;
	}
	
	public static class IntCache{
		
		private long cacheMillSeconds ;
		private int value = -1 ;
		private long lastTime ;
		private ICachedDataLoader loader ;
		private boolean resetted = true ;
		
		private IntCache(long cacheMillSeconds){
			this.cacheMillSeconds = cacheMillSeconds ;
		}
		
		private IntCache(long cacheMillSeconds, ICachedDataLoader loader){
			this.cacheMillSeconds = cacheMillSeconds ;
			this.loader = loader ;
		}
		
		/**返回cache的值，如果cache的值已经过期返回-1 */
		public int getCachedValue(){
			long now = System.currentTimeMillis() ;
			
			if(resetted || now - lastTime > cacheMillSeconds){				
				if(loader == null) return -1 ;
				
				int value = ((Integer) loader.reloadStaleData()).intValue() ;
				setCachedValue(value) ;
				return value ;
				
			}
			
			return value ;
		}
		
		public void setCachedValue(int value){
			this.value = value ;
			this.lastTime = System.currentTimeMillis() ;
			this.resetted = false ;
		}

		public long getCacheMillSeconds() {
			return cacheMillSeconds;
		}

		public void setCacheMillSeconds(long cacheMillSeconds) {
			this.cacheMillSeconds = cacheMillSeconds;
		}
		
		public void reset(){
			this.resetted = true ;
		}
		
	}
	
	public static class LongCache{
		
		private long cacheMillSeconds ;
		private long value = -1L ;
		private long lastTime ;
		private ICachedDataLoader loader ;
		
		private boolean resetted = true ;
		
		private LongCache(long cacheMillSeconds){
			this.cacheMillSeconds = cacheMillSeconds ;
		}
		
		private LongCache(long cacheMillSeconds, ICachedDataLoader loader){
			this.cacheMillSeconds = cacheMillSeconds ;
			this.loader = loader ;
		}
		
		/**返回cache的值，如果cache的值已经过期返回-1L */
		public long getCachedValue(){
			long now = System.currentTimeMillis() ;
			
			if(resetted ||now - lastTime > cacheMillSeconds){
				if(loader == null) return -1L ;
				
				long value = ((Long) loader.reloadStaleData()).longValue() ;
				setCachedValue(value) ;
				return value ;
			}
			
			return value ;
		}
		
		public void setCachedValue(long value){
			this.value = value ;
			this.lastTime = System.currentTimeMillis() ;
			this.resetted = false ;
		}

		public long getCacheMillSeconds() {
			return cacheMillSeconds;
		}

		public void setCacheMillSeconds(long cacheMillSeconds) {
			this.cacheMillSeconds = cacheMillSeconds;
		}	
		
		public void reset(){
			this.resetted = true ;
		}	
	}
	
	public static class ObjectCache{
		
		private long cacheMillSeconds ;
		private Object value = null ;
		private long lastTime ;
		private ICachedDataLoader loader ;
		private boolean resetted = true ;
		
		private ObjectCache(long cacheMillSeconds){
			this.cacheMillSeconds = cacheMillSeconds ;
		}
		
		private ObjectCache(long cacheMillSeconds, ICachedDataLoader loader){
			this.cacheMillSeconds = cacheMillSeconds ;
			this.loader = loader ;
		}
		
		/**返回cache的值，如果cache的值已经过期返回null */
		public Object getCachedObject(){
			long now = System.currentTimeMillis() ;
			
			if(resetted || now - lastTime > cacheMillSeconds){
				if(loader == null) return null ;
				
				Object value = loader.reloadStaleData() ;
				setCachedObject(value) ;
				return value ;
			}
			
			return value ;
		}
		
		public void setCachedObject(Object value){
			this.value = value ;
			this.lastTime = System.currentTimeMillis() ;
			this.resetted = false ;
		}

		public long getCacheMillSeconds() {
			return cacheMillSeconds;
		}

		public void setCacheMillSeconds(long cacheMillSeconds) {
			this.cacheMillSeconds = cacheMillSeconds;
		}
		
		public void reset(){
			this.resetted = true ;
		}
	}
	
	public interface ICachedDataLoader{
		/**重新加载cache的数据。*/
		public Object reloadStaleData() ;
	}

}
