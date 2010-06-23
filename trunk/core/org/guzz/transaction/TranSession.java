/*
 * Copyright 2008-2010 the original author or authors.
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
package org.guzz.transaction;

import java.sql.Connection;

import javax.transaction.Transaction;

import org.guzz.jdbc.JDBCTemplate;

/**
 * 
 * A database {@link Transaction} and {@link Connection} holder. 
 * 
 * <p>Maintain a distributed Transaction among all involved databases.
 * </p>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface TranSession {

	/**
	 * create JDBCTemplate for a domain class's database group.
	 */
	public JDBCTemplate createJDBCTemplate(Class domainClass) ;
	
	/**
	 * create JDBCTemplate for a business's database group.
	 */
	public JDBCTemplate createJDBCTemplate(String businessName) ;
	
	/**
	 * create JDBCTemplate for a database group.
	 */
	public JDBCTemplate createJDBCTemplateByDbGroup(String dbGroup) ;

	/**
	 * Close the {@link Transaction} and all {@link Connection}s, ignoring any exceptions.
	 */
	public void close() ;
	
}
