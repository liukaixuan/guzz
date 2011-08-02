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
package org.guzz.jdbc;

import org.guzz.id.IdentifierGenerator;


/**
 * 
 * 用于处理领域对象的批处理器。此处理器可以处理增删改操作，但是每个 {@link ObjectBatcher} 只允许进行一种操作。
 * <p/>例如已经调用了add(...)方法后，update和delete便不允许再调用。
 * 
 * <p>
 * One ObjectBatcher works only for one table. If the object is shadow, each real table should use a separate ObjectBatcher.
 * <p>
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ObjectBatcher extends Batcher {
	
	/**
	 * add a object to the insert batch.
	 * <p>
	 * <b>WARNING:</b> batch update does not support binding postCreated primary key. The domainObject you passed may not gain any primary key.
	 * <br>To avoid this problem, the domainObject's primary key should be auto_increment 
	 * or a sequence(pk fetched before domainObject inserted). Or you cann't get the primary key of the inserted object right after the batch.
	 * 
	 * </p>
	 * IdentifierGenerator#preInsert(org.guzz.transaction.WriteTranSession, Object) will be invoked before batchUpdate.
	 * <br>
	 * IdentifierGenerator#postInsert(org.guzz.transaction.WriteTranSession, Object) won't be invoked at all.
	 * 
	 * @see IdentifierGenerator#preInsert(org.guzz.transaction.WriteTranSession, Object)
	 * @see IdentifierGenerator#postInsert(org.guzz.transaction.WriteTranSession, Object)
	 * @param domainObject
	 */
	public void insert(Object domainObject) ;
	
	public void update(Object domainObject) ;
	
	public void delete(Object domainObject) ;

	public void setTableCondition(Object tableCondition) ;
	
}
