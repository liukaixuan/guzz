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

import org.guzz.util.lb.LBRound;

import junit.framework.TestCase;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestRound extends TestCase{
	
	public void testGcd(){
		LBRound r = new LBRound() ;
		
		assertEquals(r.gcd(0, 3), 3) ;
		
		assertEquals(r.gcd(1, 3), 1) ;
		assertEquals(r.gcd(2, 3), 1) ;
		
		assertEquals(r.gcd(200, 300), 100) ;
		assertEquals(r.gcd(200, 350), 50) ;		
	}
	
	public void testNGcd(){
		LBRound r = new LBRound() ;
		
		assertEquals(r.ngcd(new int[]{7}), 7) ;
		
		assertEquals(r.ngcd(new int[]{1, 3}), 1) ;
		assertEquals(r.ngcd(new int[]{2, 3}), 1) ;
		
		assertEquals(r.ngcd(new int[]{200, 300}), 100) ;
		assertEquals(r.ngcd(new int[]{200, 350}), 50) ;	
		

		assertEquals(r.ngcd(new int[]{100, 40, 350}), 10) ;
		
		assertEquals(r.ngcd(new int[]{32, 64, 128}), 32) ;
	}
	
	public void testCardPool(){
		LBRound r = new LBRound() ;
		
		r.addToPool(1, 100) ;
		r.addToPool(2, 100) ;
		
		r.applyNewPool() ;
		
		for(int i = 0 ; i < 1000 ; i++){
			assertEquals(r.getCard(), 1) ;
			assertEquals(r.getCard(), 2) ;
		}
	}

}
