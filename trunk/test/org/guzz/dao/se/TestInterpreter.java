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
package org.guzz.dao.se;

import org.guzz.Guzz;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.Business;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.se.CompareTerm;
import org.guzz.test.DBBasedTestCase;
import org.guzz.test.User;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TestInterpreter extends DBBasedTestCase {
	
	public void testParseConditionSegment() throws Exception{
		String name = "comment" ;
		
		Business bi = gf.getBusiness(name) ;
		ObjectMapping mapping = gf.getObjectMappingManager().getObjectMapping(name, Guzz.getTableCondition()) ;
		
		CompareTerm st = (CompareTerm) bi.getInterpret().explainCondition(mapping, "userId=50") ;
		assertNotNull(st) ;
		
		bi.getInterpret().explainCondition(mapping, "userId>=50") ;
		bi.getInterpret().explainCondition(mapping, "userId==50") ;
		bi.getInterpret().explainCondition(mapping, "userId!=50") ;
		bi.getInterpret().explainCondition(mapping, "userId!=50") ;
		bi.getInterpret().explainCondition(mapping, "userId~=50") ;
		bi.getInterpret().explainCondition(mapping, "content=~=50") ;
		bi.getInterpret().explainCondition(mapping, "userId<>50") ;
		bi.getInterpret().explainCondition(mapping, "content=我是谁~~啊=~===呵呵~~") ;
		bi.getInterpret().explainCondition(mapping, "content~=我是谁~~啊=~===呵呵~~") ;
		
		bi.getInterpret().explainCondition(mapping, "userId==0") ;
		
		try{
			bi.getInterpret().explainCondition(mapping, "userId==") ;
			fail("should fail") ;
		}catch(NumberFormatException e){
		}
		
		//content set to empty string.
		bi.getInterpret().explainCondition(mapping, "content=") ;
		
		try{
			bi.getInterpret().explainCondition(mapping, "wrongField=12") ;
			fail("should fail") ;
		}catch(DataTypeException e){
			assertEquals("propName[wrongField] has no mapping.", e.getMessage()) ;
		}
		
		try{
			bi.getInterpret().explainCondition(mapping, "wrong thing!") ;
			fail("should fail") ;
		}catch(Exception e){}
		
		try{
			bi.getInterpret().explainCondition(mapping, null) ;
			fail("should fail") ;
		}catch(NullPointerException e){}
		
		try{
			bi.getInterpret().explainCondition(mapping, new User(1)) ;
			fail("should fail") ;
		}catch(Exception e){}
		
	}

}
