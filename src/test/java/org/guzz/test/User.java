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
package org.guzz.test;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.Parameter;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName="user")
@org.guzz.annotations.Table(name="TB_USER")
public class User {
	
	@javax.persistence.Id
	@Column(name="pk")
	@GeneratedValue(generator="userIdGen")
	@GenericGenerator(name = "userIdGen", strategy = "native", 
			parameters={@Parameter(name="sequence", value="seq_user_id")}
	)
	private int id ;
	
	@Column(name="userName")
	private String userName ;
	
	@Column(name="MyPSW")
	private String password ;
	
	@Column(name="VIP_USER")
	private boolean vip ;
	
	@Column(name="FAV_COUNT")
	@org.guzz.annotations.Column(nullValue="999")
	private Integer favCount ;
	
	private Date createdTime ;
	
	public User(){}
	
	public User(int id){
		this.id = id ;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public Integer getFavCount() {
		return favCount;
	}

	public void setFavCount(Integer favCount) {
		this.favCount = favCount;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}
