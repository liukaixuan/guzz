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
package org.guzz.util.lb;

import java.util.LinkedList;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LBRound implements RoundCard{
	
	private LinkedList hs = new LinkedList() ;
	
	private Object[] card_services = new Object[0] ;
	private int currentPos = 0 ;
	
	
	/**
	 * 添加一个候选因子。
	 * 
	 * @param card
	 * @param lvFactor 负载因子，越大轮询到的机率越高。最大10000，最小1.
	 * */
	public void addToPool(Object card, int lvFactor){
		_Holder h = new _Holder() ;
		h.service = card ;
		h.lvFactor = lvFactor ;
		
		hs.add(h) ;
	}
	
	/**对负载因子进行最大公约数处理*/
	protected void prepareNGcd(){
		int[] facs = new int[hs.size()] ;
		
		for(int i = 0 ; i < hs.size() ; i++){
			_Holder h = (_Holder) hs.get(i) ;
			facs[i] = h.lvFactor ;
		}
		
		int gcd = ngcd(facs) ;
		
		if(gcd > 1){
			for(int i = 0 ; i < hs.size() ; i++){
				_Holder h = (_Holder) hs.get(i) ;
				h.lvFactor = h.lvFactor / gcd ;
			}
		}
	}
	
	/**应用新添加的对象到轮询中。*/
	public void applyNewPool(){
		prepareNGcd() ;
		
		//set up load balance base.
		double baseNum = 0 ;
		
		for(int i = 0 ; i < hs.size() ; i++){
			_Holder h = (_Holder) hs.get(i) ;
			
			//设定范围，防止溢出。
			if(h.lvFactor < 1){
				h.lvFactor = 1 ;
			}else if(h.lvFactor > 10000){
				h.lvFactor = 10000 ;
			}
			
			int fac = h.lvFactor ;
			
			baseNum += fac ;
		}
		
		Object[] lv = new Object[(int) baseNum] ;
		
		for(int i = 0 ; i < hs.size() ; i++){
			_Holder h = (_Holder) hs.get(i) ;
			
			Object service = h.service ;
			int fac = h.lvFactor ;
			
			if(i < hs.size() - 1){ //不是最后一个DataSourceProvider
				double offset = baseNum / fac ;
				
				for(int k = 0 ; k < fac ; k++){
					int pos = (int) (offset * k) ;
					pos += i ;
					
					while(lv[pos] != null && pos < baseNum){
						pos++ ;
					}
					
					if(pos < baseNum){ //找到了空闲的位置
						lv[pos] = service ;
					}
				}
			}else{ //最后一个DataSourceProvider填补所有空闲
				for(int k = 0 ; k < lv.length ; k++){
					if(lv[k] == null){
						lv[k] = service ;
					}
				}
			}
		}
		
		this.card_services = lv ;
	}

	public Object getCard() {
		int maxSize = card_services.length ;
		if(maxSize == 0){
			return null ;
		}else if(maxSize == 1){
			return card_services[0] ;
		}
		
		int pos = this.currentPos++ ;
		
		if(pos >= maxSize){
			pos = pos % maxSize ;
			this.currentPos = pos + 1 ;
		}
		
		return card_services[pos];
	}
	
	//计算最大约数，通过最大公约数缩写lv数组大小。
	public int gcd(int a, int b) {
		if (a < b) {
			int c = a;
			a = b;
			b = c;
		}
		if (b == 0){
			return a;
		}
		else{
			return gcd(b, a % b);
		}
	}

	public int ngcd(int[] a) {
		return _ngcd(a, a.length);
	}
	
	protected int _ngcd(int[] a, int n) {
		if (n == 1){
			return a[0];
		}
		return gcd(a[n - 1], _ngcd(a, n - 1));
	}

	static class _Holder{
		public Object service ;
		public int lvFactor ;		
	}

}
