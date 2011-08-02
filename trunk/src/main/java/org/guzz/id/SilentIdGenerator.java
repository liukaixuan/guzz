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

import org.guzz.transaction.WriteTranSession;

/**
 * 
 * This id generator will do nothing. the id should be maintained by the underly database(eg: use a trigger).
 * <p/>
 * guzz won't and unable to bind the new created id to the inserted object after performing database inserting operation.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SilentIdGenerator implements IdentifierGenerator {

	public Serializable preInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		return null ;
	}
	
	public Serializable postInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		return null ;
	}

	public boolean insertWithPKColumn() {
		return false;
	}

}
