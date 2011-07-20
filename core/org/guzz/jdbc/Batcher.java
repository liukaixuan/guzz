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

import java.sql.PreparedStatement;


/**
 * 
 * 批处理执行器，用于执行批量操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface Batcher {
	
	/**
	 * @see PreparedStatement#executeBatch()
	 * @deprecated Use {@link #executeBatch()} instead
	 */
	public int[] executeUpdate() ;

	/**
	 * @see PreparedStatement#executeBatch()
	 */
	public int[] executeBatch() ;
	
	/**@see PreparedStatement#clearBatch()*/
	public void clearBatch() ;
	
	/**
	 * Execute {@link #executeUpdate()} and {@link #clearBatch()} when the amount of the updates 
	 * reaches the limit of {@link #getBatchSize()}.
	 * 
	 * @param auto turn on the auto execute or not
	 * @see #setBatchSize(int)
	 */
	public void setAutoExecuteUpdate(boolean auto) ;
	
	public void setBatchSize(int batchSize) ;
	
	public boolean isAutoExecuteUpdate() ;
	
	public int getBatchSize() ;
		
}
