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
package org.guzz.service.core;

import java.io.Serializable;

/**
 * 
 * 延迟更新数据库服务，用于更新有大量操作的计数器。<br>
 * <br>
 * 原理：
 * <lo>
 * <li>将写操作放入队列(如果队列超长，自动丢弃)</li>
 * <li>后台线程将操作写入临时表</li>
 * <li>在集群的其中1台机器上，后台线程以单线程将临时表数据合并然后写入主表中。</li>
 * </lo>
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SlowUpdateService {
	
	/**
	 * 更新一个计数。
	 * 
	 * @param dbGroup 要更新的数据库表所在的数据库组
	 * @param tableName 要更新的数据库表名
	 * @param columnToUpdate 要更新的字段
	 * @param pkColName 对象的主键字段名称
	 * @param pk 对象的主键值
	 * @param countToInc
	 */
	public void updateCount(String dbGroup, String tableName, String columnToUpdate, String pkColName, Serializable pkValue, int countToInc) ;
	
	
	/**
	 * 更新一个计数。
	 * 
	 * @param businessName 要更新的域对象
	 * @param tableCondition shaodow table分表条件
	 * @param propToUpdate 要更新的属性名称
	 * @param pkValue 要更新对象的主键值
	 * @param countToInc
	 */
	public void updateCount(String businessName, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;
	
	
	/**
	 * 更新一个计数。
	 * 
	 * @param domainClass 要更新的域对象类
	 * @param tableCondition shaodow table分表条件
	 * @param propToUpdate 要更新的属性名称
	 * @param pkValue 要更新对象的主键值
	 * @param countToInc
	 */
	public void updateCount(Class domainClass, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;

}
