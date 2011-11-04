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
package org.guzz.transaction;

import org.guzz.dao.WriteTemplate;
import org.guzz.orm.sql.CompiledSQLBuilder;

/**
 * 
 * 供上层应用调用的数据库连接管理工厂。
 * 上层应用通过此类申请打开一个事务（每个事务包含一个连接），在打开的事务(@link TranSession) session中进行数据库操作（执行查询，更新等）。
 * 此类的功能类似于Hibernate中的SessionFactory，只不过guzz不提供HibernateSession，而强调事务，使得用户可以更好的控制事务和批处理，提供更好的控制性。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface TransactionManager {
	
	/**打开一个只读的事务。本事务只读，但是要求不能有数据延迟。*/
	public ReadonlyTranSession openNoDelayReadonlyTran() ;
		
	/**
	 * 打开一个只读并允许数据延迟的事务。此时，事务内的连接将选取负载较低的数据库提供服务，选择的数据库数据可能会有少量的同步延迟（如主从同步中从库的延迟）
	 * */
	public ReadonlyTranSession openDelayReadTran() ;
	
	/**
	 * 打开一个可读写的数据连接。
	 * @param autoCommit 事务提交是否设定为自动提交。true，自动提交; false手动提交。
	 * */
	public WriteTranSession openRWTran(boolean autoCommit) ;
	
	public CompiledSQLBuilder getCompiledSQLBuilder() ;
	
	public WriteTemplate createBindedWriteTemplate() ;
	
}
