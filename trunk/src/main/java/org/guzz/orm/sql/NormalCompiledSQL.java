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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.util.StringUtil;

/**
 * 
 * 编译好的sql语句。包括可以直接在数据库执行的sql语句，sql执行参数（参数值可以替换），以及ORM信息。
 * 
 * <br><br>一般来说，所有的sql都需要转换成CompliedSQL，然后绑定参数后进行实际的执行操作。
 * 
 * @see BindedCompiledSQL
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NormalCompiledSQL extends CompiledSQL {
	
	private String sql ;

	private List orderedParams = new ArrayList() ;
	
	private ObjectMapping mapping ;
		
	private String[] cached_orderedParams = null ;
	
	/**保存sql中用到的shadow表，如果查询中没有shadow表，值为null*/
	private Map shadowMapping = null ;
	
	/**
	 * 根据tableCondition获取完成shadow映射后的sql语句。
	 */
	public String getSql(Object tableCondition) {
		if(shadowMapping == null){
			return sql;
		}else{
			String sql2 = sql ;
			//替换shadow table name
			Iterator i = shadowMapping.entrySet().iterator() ;
			while(i.hasNext()){
				Entry e = (Entry) i.next() ;
				Table shadowTable = (Table) e.getValue() ;
				
				sql2 = StringUtil.replaceString(sql2, (String) e.getKey(), shadowTable.getTableName(tableCondition)) ;
			}
			
			return sql2 ;
		}
	}
	
	/**
	 * 设置查询sql，如果sql中涉及shadow表，表名可以用@@businessName替代；
	 * 替代后，调用 {@link #addShadowMapping(String, Table)} 声明映射。
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	/**
	 * 添加shadow表映射。
	 */
	public void addShadowMapping(String businessName, Table shadowTable){
		if(shadowMapping == null){
			shadowMapping = new HashMap() ;
		}
		
		this.shadowMapping.put(MarkedSQL.TABLE_START_TAG_IN_MARKED_SQL + businessName, shadowTable) ;
	}
	
	public void addParamToLast(String paramName){
		orderedParams.add(paramName) ;
		cached_orderedParams = null ;
	}

	/**如果没有参数，返回长度为0的数组。*/
	public String[] getOrderedParams() {
		if(cached_orderedParams == null){
			cached_orderedParams = (String[]) orderedParams.toArray(new String[0]);
		}
		
		return cached_orderedParams ;
	}

	public void setOrderedParams(List orderedParams) {
		this.orderedParams = orderedParams;
		cached_orderedParams = null ;
	}

	public ObjectMapping getMapping() {
		return mapping;
	}

	public void setMapping(ObjectMapping mapping) {
		this.mapping = mapping;
	}
	
	/**绑定sql执行需要的参数*/
	public BindedCompiledSQL bind(String paramName, Object paramValue){
		return new NormalBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, int paramValue){
		return new NormalBindedCompiledSQL(this).bind(paramName, paramValue) ;
	}
	
	public BindedCompiledSQL bind(String paramName, long paramValue){
		return new NormalBindedCompiledSQL(this).bind(paramName, new Long(paramValue)) ;
	}
	
	public BindedCompiledSQL bind(Map params){
		return new NormalBindedCompiledSQL(this).bind(params) ;
	}
	
	public BindedCompiledSQL bindNoParams(){
		return new NormalBindedCompiledSQL(this) ;
	}

}
