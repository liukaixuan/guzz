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
package org.guzz.service.core.impl;

import org.guzz.GuzzContext;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.service.AbstractService;
import org.guzz.service.core.DynamicSQLService;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * The basic implementation of {@link DynamicSQLService}.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractDynamicSQLService extends AbstractService implements DynamicSQLService, GuzzContextAware {
	protected GuzzContext guzzContext ;
	protected CompiledSQLBuilder compiledSQLBuilder ;

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext ;
		this.compiledSQLBuilder = guzzContext.getTransactionManager().getCompiledSQLBuilder() ;
	}

}
