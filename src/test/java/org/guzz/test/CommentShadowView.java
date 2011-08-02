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
package org.guzz.test;

import org.guzz.exception.GuzzException;
import org.guzz.orm.AbstractShadowTableView;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CommentShadowView extends AbstractShadowTableView {

	public String toTableName(Object tableCondition) {
		if(tableCondition == null){ //强制要求必须设置表分切条件，避免编程时疏忽。
			throw new GuzzException("null table conditon not allowed.") ;
		}
		
		User u = (User) tableCondition ;
		
		//如果用户ID为偶数，记入TB_COMMENT1, 否则写入TB_COMMENT2
		int i = u.getId() % 2 + 1 ;
		
		return super.getConfiguredTableName() + i;
	}

}
