/*
 * Copyright 2008-2012 the original author or authors.
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
package org.guzz.api.velocity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.guzz.api.taglib.GhostPageTag;
import org.guzz.api.taglib.TypeConvertHashMap;
import org.guzz.dao.PageFlip;
import org.guzz.orm.Business;
import org.guzz.orm.se.SearchExpression;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.util.StringUtil;

/**
 * 
 * See {@link GhostPageTag}.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzPageDirective extends SummonDirective {

	protected Object summonGhosts(Business business, Object tableCondition, List conditions, Map params) throws IOException {
		int skipCount = TypeConvertHashMap.getIntParam(params, "skipCount", 0) ;
		int pageNo = TypeConvertHashMap.getIntParam(params, "pageNo", 1) ;
		int pageSize = TypeConvertHashMap.getIntParam(params, "pageSize", 20) ;
		String orderBy = (String) params.get("orderBy") ;
		
		int pageSpan = TypeConvertHashMap.getIntParam(params, "pageSpan", 10) ;
		int pageBeforeSpan = TypeConvertHashMap.getIntParam(params, "pageBeforeSpan", -1) ;
		int pageAfterSpan = TypeConvertHashMap.getIntParam(params, "pageAfterSpan", -1) ;
		
		SearchExpression se = SearchExpression.forBusiness(business.getName(), pageNo, pageSize) ;
		se.setTableCondition(tableCondition) ;
		se.setSkipCount(skipCount) ;
		se.and(conditions) ;
		if(StringUtil.notEmpty(orderBy)){
			se.setOrderBy(orderBy) ;
		}
		
		ReadonlyTranSession tran = guzzContext.getTransactionManager().openDelayReadTran() ;
		
		PageFlip page ;
		try{
			page = tran.page(se) ;
		}finally{
			tran.close() ;
		}

		page.setPagesShow(pageSpan) ;
		
		if(pageBeforeSpan > 0){
			page.setPageBeforeSpan(pageBeforeSpan) ;
		}
		
		if(pageAfterSpan > 0){
			page.setPageAfterSpan(pageAfterSpan) ;
		}
		
		return page ;
	}

	public String getName() {
		return "guzzPage" ;
	}
	
}
