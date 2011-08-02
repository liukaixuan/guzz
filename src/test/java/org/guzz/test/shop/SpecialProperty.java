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

/**
 * 
 * special properties' declarations of our cargoes.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */

@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName="sp")
@org.guzz.annotations.Table(name="tb_s_property")
public class SpecialProperty {

	/**unqiue id for management.*/
	private int id ;
	
	/**which cargo this property belongs.*/
	private String cargoName ;
	
	/**property name of the cargo (used in java).*/
	private String propName ;
	
	/**the column name in the table of database to store the propety.*/
	private String colName ;
	
	/**
	 * dataType. take this as the 'type' property in hbm.xml file.
	 */
	private String dataType ;

	@javax.persistence.Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCargoName() {
		return cargoName;
	}

	public void setCargoName(String cargoName) {
		this.cargoName = cargoName;
	}
	
}
