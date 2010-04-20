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

import org.guzz.annotations.Table;

/**
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */

@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName = "comment")
@Table(name="TB_COMMENT", shadow = CommentShadowView.class)
public class Comment {

	@javax.persistence.Id
	private int id ;
	
	private int userId ;
	
	private String userName ;
	
	@Column(name="DESCRIPTION")
	private String content ;
	
	private Date createdTime ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
