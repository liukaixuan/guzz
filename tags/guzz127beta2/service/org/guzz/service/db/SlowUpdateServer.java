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
package org.guzz.service.db;

import org.guzz.service.core.SlowUpdateService;

/**
 * 
 * 用于处理@link {@link SlowUpdateService} 插入数据库的临时数据的服务类。
 * 此服务会读取临时数据，然后应用更新到主数据库中。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SlowUpdateServer {
	
	/**
	 * 返回当前临时表中的数据，如果全部更新到主库中，还需要的时间。
	 * 
	 * @return 单位毫秒
	 */
	public int getLatency() ;

}
