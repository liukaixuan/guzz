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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;

/**
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */

@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName="book")
@org.guzz.annotations.Table(name="TB_BOOK", dynamicUpdate=true)
public class Book {
	
	private int id ;
	
	public String title ;
	
	public String content ;
	
	private Date createdTime ;
	
	private String ISDN ;
	
	private byte[] checksum ;

	@javax.persistence.Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="NAME")
	@Basic(fetch=FetchType.EAGER)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="DESCRIPTION")
	@Basic(fetch=FetchType.LAZY)
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

	public String getISDN() {
		return ISDN;
	}

	public void setISDN(String isdn) {
		ISDN = isdn;
	}

	public byte[] getChecksum() {
		return checksum;
	}

	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}
	
}
