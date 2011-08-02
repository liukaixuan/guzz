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
package org.guzz.id;

import java.io.Serializable;
import java.util.Properties;

import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AssignedIdGenerator implements IdentifierGenerator, Configurable {
	private POJOBasedObjectMapping mapping ;
	private Table table ;
	private String primaryKeyPropName ;
	
	public Serializable preInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		Object pk = mapping.getBeanWrapper().getValue(domainObject, primaryKeyPropName) ;
		
		if(pk == null){ //没有设置主键，抛出异常。
			throw new DaoException("primary column cann't be null. domainObject:[" + domainObject.getClass() + "]") ;
		}
		
		return (Serializable) pk ;
	}
	
	public Serializable postInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		return null ;
	}

	public boolean insertWithPKColumn() {
		return true;
	}
	
	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.mapping = mapping ;
		this.table = mapping.getTable() ;
		primaryKeyPropName = table.getPKPropName() ;
	}

}
