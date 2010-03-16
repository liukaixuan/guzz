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
package org.guzz.test.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guzz.orm.AbstractCustomTableView;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.util.Assert;

/**
 * 
 * Key class. Mapping different cargo to different table and different properties.
 * 
 * <p>We define cargo's name to be the table condition.</p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CargoCustomTableView extends AbstractCustomTableView {
	
	/**
	 * Lookup mapping every time is expensive, we cache it.
	 * <p>
	 * In real product system, you should replace it with a real cache, and refresh it on special properties' changing.
	 * <br>
	 * The real cache can be started and shutted down in the {@link #startup()} and {@link #shutdown()} methods(don't forget to call super).
	 * <p/>
	 */
	private Map orms_cache = new HashMap() ;

	public void setCustomPropertyValue(Object beanInstance, String propName, Object value) {
		Cargo c = (Cargo) beanInstance ;
		
		//store the special property in the map.
		c.getSpecialProps().put(propName, value) ;
	}
	
	public Object getCustomPropertyValue(Object beanInstance, String propName) {
		Cargo c = (Cargo) beanInstance ;
		
		//return special property from the map.
		return c.getSpecialProps().get(propName) ;
	}

	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default table to store un-categoried cargoes.") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) this.orms_cache.get(cargoName) ;
		
		if(map == null){
			//create it
			map = super.createRuntimeObjectMapping(cargoName) ;
			
			//cache
			this.orms_cache.put(cargoName, map) ;
		}
		
		return map ;
	}

	protected void initCustomTableColumn(POJOBasedObjectMapping mapping, Object tableCondition) {
		String cargoName = (String) tableCondition ;
		
		//read the special properties from the master database.
		ReadonlyTranSession session = this.guzzContext.getTransactionManager().openNoDelayReadonlyTran() ;
		
		try{
			SearchExpression se = SearchExpression.forLoadAll(SpecialProperty.class) ;
			se.and(Terms.eq("cargoName", cargoName)) ;
			
			List properties = session.list(se) ;
			
			for(int i = 0 ; i < properties.size() ; i++){
				SpecialProperty sp = (SpecialProperty) properties.get(i) ;
				
				//create the TableColumn with the super helper method.
				TableColumn tc = super.createTableColumn(mapping, sp.getPropName(), sp.getColName(), sp.getDataType(), null) ;
				
				//add it to the mapping with the super helper method too.
				super.addTableColumn(mapping, tc) ;
			}
			
		}finally{
			session.close() ;
		}
	}
	
	public String toTableName(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default table to store un-categoried cargoes.") ;
				
		//different tableConditions mapped to different tables.
		return "tb_cargo_" + cargoName;
	}

}
