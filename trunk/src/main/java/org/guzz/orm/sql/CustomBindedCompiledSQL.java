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

import org.guzz.orm.mapping.POJOBasedObjectMapping;

/**
 * 
 * 绑定完参数的 {@link CompiledSQL} ，这是临时对象，需要时生成，用完就消失。
 * 此对象方法不支持多线程操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CustomBindedCompiledSQL extends BindedCompiledSQL {
	private CustomCompiledSQL customCS ;
	
	/**the sql ready to execute in database.*/
	private String cachedSql ;	
	
	/**
	 * 根据当前tableCondition决定的运行时CS；每次都会变化，不能做缓存。
	 */
	private NormalCompiledSQL runtimeCS ;
	
	public CustomBindedCompiledSQL(CustomCompiledSQL cs){
		super(cs.getResultClass()) ;
		this.customCS = cs ;
	}
	
	public CustomCompiledSQL getCustomCompiledSQL() {
		return customCS ;
	}

	protected void notifyTableConditionChanged() {
		this.cachedSql = null ;
		this.runtimeCS = null ;
	}

	public NormalCompiledSQL getCompiledSQLToRun() {
		if(runtimeCS == null){
			POJOBasedObjectMapping om = this.customCS.getObjectMapping(getTableCondition()) ;
			
			NormalCompiledSQL cs = this.customCS.getSql(om) ;
			//将用户设置的param和prop的mapping复制给CompiledSQL
			cs.addParamPropMappings(this.customCS.paramPropMapping) ;
			cs.registerParamTypes(this.customCS.paramTypes) ;
			
			this.runtimeCS = cs ;
		}
		
		return runtimeCS ;
	}

	public String getSQLToRun() {
		if(this.cachedSql != null){
			return this.cachedSql ;
		}else{
			this.cachedSql = getCompiledSQLToRun().getSql(getTableCondition()) ;
			
			return this.cachedSql;
		}
	}

}
