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


/**
 * 
 * 绑定完参数的 {@link CompiledSQL} ，这是临时对象，需要时生成，用完就消失。
 * 此对象方法不支持多线程操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NormalBindedCompiledSQL extends BindedCompiledSQL{
	
	private NormalCompiledSQL compiledSQL ;
	
	/**the sql ready to execute in database.*/
	private String cachedSql ;
	
	public NormalBindedCompiledSQL(NormalCompiledSQL cs){
		this.compiledSQL = cs ;
	}	

	protected void notifyTableConditionChanged() {
		this.cachedSql = null ;
	}

	public NormalCompiledSQL getCompiledSQLToRun() {
		return compiledSQL;
	}

	public String getSQLToRun() {
		if(this.cachedSql != null){
			return this.cachedSql ;
		}else{
			this.cachedSql = this.compiledSQL.getSql(getTableCondition()) ;
			
			return this.cachedSql;
		}
	}

}
