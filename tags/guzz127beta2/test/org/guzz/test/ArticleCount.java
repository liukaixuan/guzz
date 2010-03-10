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

/**
 * 
 * 文章阅读次数。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ArticleCount {

	private int articleId ;
	
	private String articleTitle ;
	
	private int readCount ;
	
	private int supportCount ;
	
	private int opposeCount ;
	
	private Date createdTime ;

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int id) {
		this.articleId = id;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public int getSupportCount() {
		return supportCount;
	}

	public void setSupportCount(int supportCount) {
		this.supportCount = supportCount;
	}

	public int getOpposeCount() {
		return opposeCount;
	}

	public void setOpposeCount(int opposeCount) {
		this.opposeCount = opposeCount;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
