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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 
 * item for sale.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */

@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName="cargo")
@org.guzz.annotations.Table(name="tb_cargo", dbGroup="cargoDB" , shadow=CargoCustomTableView.class)
public class Cargo {

	@javax.persistence.Id
	private int id ;
	
	private String name ;
	
	private String description ;
	
	/**how many items left in the store. */
	private int storeCount ;
	
	private double price ;
	
	private Date onlineTime ;
	
	private CargoStatus statusThisWeek ;
	
	@Enumerated(EnumType.STRING)
	private CargoStatus statusNextWeek ;
	
	/*for the sake of concision, igore other useful properties.*/
	
	/**
	 * special properties owned by only this Item.
	 */
	private Map specialProps = new HashMap() ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStoreCount() {
		return storeCount;
	}

	public void setStoreCount(int storeCount) {
		this.storeCount = storeCount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Map getSpecialProps() {
		return specialProps;
	}

	public void setSpecialProps(Map specialProps) {
		this.specialProps = specialProps;
	}

	public CargoStatus getStatusThisWeek() {
		return statusThisWeek;
	}

	public void setStatusThisWeek(CargoStatus statusThisWeek) {
		this.statusThisWeek = statusThisWeek;
	}

	public CargoStatus getStatusNextWeek() {
		return statusNextWeek;
	}

	public void setStatusNextWeek(CargoStatus statusNextWeek) {
		this.statusNextWeek = statusNextWeek;
	}

}
