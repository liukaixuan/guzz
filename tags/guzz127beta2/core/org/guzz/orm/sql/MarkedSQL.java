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
package org.guzz.orm.sql;

import org.guzz.orm.ObjectMapping;

/**
 * 
 * 包含有@等转义信息的sql语句。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MarkedSQL {
	
	public static final String PROP_START_TAG_IN_MARKED_SQL = "@" ;
	public static final String TABLE_START_TAG_IN_MARKED_SQL = "@@" ;

	private String orginalSQL ;
	
	private ObjectMapping mapping ;
	
	public MarkedSQL(ObjectMapping mapping, String orginalSQL){
		this.orginalSQL = orginalSQL ;
		this.mapping = mapping ;
	}

	public String getOrginalSQL() {
		return orginalSQL;
	}

	public void setOrginalSQL(String orginalSQL) {
		this.orginalSQL = orginalSQL;
	}

	public ObjectMapping getMapping() {
		return mapping;
	}

	public void setMapping(ObjectMapping mapping) {
		this.mapping = mapping;
	}
	
}
