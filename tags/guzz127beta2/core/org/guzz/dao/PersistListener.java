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
package org.guzz.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.transaction.WriteTranSession;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface PersistListener {
	
	public void preInsert(WriteTranSession tran, Connection conn, PreparedStatement pstm, BindedCompiledSQL bsql, Object domainObject, Serializable pk) ;
	
	public void postInsert(WriteTranSession tran, Connection conn, Object domainObject, Serializable pk) ;
	
	public void preUpdate(WriteTranSession tran, Connection conn, PreparedStatement pstm, BindedCompiledSQL bsql, Object domainObject) ;
	
	public void postUpdate(WriteTranSession tran, Connection conn, Object domainObject) ;
	
	public void preDelete(WriteTranSession tran, Connection conn, PreparedStatement pstm, BindedCompiledSQL bsql, Object domainObject) ;
	
	public void postDelete(WriteTranSession tran, Connection conn, Object domainObject) ;

}
