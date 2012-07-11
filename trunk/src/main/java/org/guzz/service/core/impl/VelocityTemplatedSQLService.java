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
package org.guzz.service.core.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.guzz.GuzzContext;
import org.guzz.api.velocity.GuzzAddInLimitDirective;
import org.guzz.api.velocity.GuzzAddLimitDirective;
import org.guzz.api.velocity.GuzzBoundaryDirective;
import org.guzz.api.velocity.GuzzCountDirective;
import org.guzz.api.velocity.GuzzGetDirective;
import org.guzz.api.velocity.GuzzIncDirective;
import org.guzz.api.velocity.GuzzListDirective;
import org.guzz.api.velocity.GuzzPageDirective;
import org.guzz.api.velocity.IsEmptyDirective;
import org.guzz.api.velocity.NotEmptyDirective;
import org.guzz.api.velocity.SummonDirective;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.TemplatedSQLService;
import org.guzz.util.StringUtil;
import org.guzz.web.context.GuzzContextAware;


/**
 * 
 * The Velocity implementation of {@link TemplatedSQLService}.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public final class VelocityTemplatedSQLService extends AbstractService implements TemplatedSQLService, GuzzContextAware {
	
	protected GuzzContext guzzContext ;
	
	protected CompiledSQLBuilder compiledSQLBuilder ;
	
	protected VelocityEngine ve ;
	
	private boolean enableDBAccess ;
	
	private String userdirective ;
	
	private HashMap<String, TemplateData> templates = new HashMap<String, TemplateData>() ;
	
	static class TemplateData{
		public final byte[] sql ;
		
		public final ObjectMapping mapping ;
		
		public final String ormName ;
		
		public TemplateData(ObjectMapping mapping, byte[] sql){
			this.mapping = mapping ;
			this.ormName = null ;
			this.sql = sql ;
		}
		
		public TemplateData(String ormName, byte[] sql){
			this.mapping = null ;
			this.ormName = ormName ;
			this.sql = sql ;
		}
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext ;
		this.compiledSQLBuilder = guzzContext.getTransactionManager().getCompiledSQLBuilder() ;
	}
	
	public void addImutableSql(String id, ObjectMapping mapping, String sqlStatement) {
		try {
			this.templates.put(id, new TemplateData(mapping, sqlStatement.getBytes("UTF-8"))) ;
		} catch (UnsupportedEncodingException e) {
			throw new TemplateInitException(e.getMessage(), id, 1, 1) ;
		}
	}
	
	public void addImutableSql(String id, String ormName, String sqlStatement) {
		try {
			this.templates.put(id, new TemplateData(ormName, sqlStatement.getBytes("UTF-8"))) ;
		} catch (UnsupportedEncodingException e) {
			throw new TemplateInitException(e.getMessage(), id, 1, 1) ;
		}
	}
	
	public CompiledSQL getSqlById(String id, Object tableCondition, Map params) {
		TemplateData data = this.templates.get(id) ;
		if (data == null) {
			throw new ResourceNotFoundException("No Template for id:" + id);     
	    }
		
		VelocityContext context = new VelocityContext(params) ;
		StringWriter w = new StringWriter() ;
		
		Template template = ve.getTemplate(id);
		template.merge(context, w) ;		
		String sql = w.toString() ;
		
		if(data.mapping != null){
			return compiledSQLBuilder.buildCompiledSQL(data.mapping, sql) ;
		}else{
			return this.compiledSQLBuilder.buildCompiledSQL(data.ormName, sql) ;
		}
	}

	public CompiledSQL getSqlByStatement(ObjectMapping mapping, Object tableCondition, String sqlStatement, Map params) {
		VelocityContext context = new VelocityContext(params) ;
		StringWriter w = new StringWriter() ;		
		ve.evaluate(context, w, sqlStatement, sqlStatement) ;		
		String sql = w.toString() ;
		
		return compiledSQLBuilder.buildCompiledSQL(mapping, sql) ;
	}

	public CompiledSQL getSqlByStatement(String ormName, Object tableCondition, String sqlStatement, Map params) {
		VelocityContext context = new VelocityContext(params) ;
		StringWriter w = new StringWriter() ;		
		ve.evaluate(context, w, sqlStatement, sqlStatement) ;		
		String sql = w.toString() ;
		
		return this.compiledSQLBuilder.buildCompiledSQL(ormName, sql) ;
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length > 0){
			this.enableDBAccess = StringUtil.toBoolean(scs[0].getProps().getProperty("enableDBAccess"), false) ;
			this.userdirective = scs[0].getProps().getProperty("userdirective") ;
		}
		
		return true;
	}

	public boolean isAvailable() {
		return true;
	}

	public void startup() {
		Properties p = new Properties() ;
		p.put("input.encoding","UTF-8");
		p.put("output.encoding","UTF-8");
		p.put("resource.loader","guzzvtsrl");
        p.put("guzzvtsrl.resource.loader.class", PreStringResourceLoader.class.getName());
        
        String directive = IsEmptyDirective.class.getName() + ", " + NotEmptyDirective.class.getName() ;
        
        if(this.enableDBAccess){
        	directive = directive
			+ ", " + GuzzAddInLimitDirective.class.getName()
			+ ", " + GuzzAddLimitDirective.class.getName()
			+ ", " + GuzzBoundaryDirective.class.getName()
			+ ", " + GuzzCountDirective.class.getName()
			+ ", " + GuzzGetDirective.class.getName()
			+ ", " + GuzzIncDirective.class.getName()
			+ ", " + GuzzListDirective.class.getName()
			+ ", " + GuzzPageDirective.class.getName() ;
        }
        
        if(StringUtil.notEmpty(this.userdirective)){
        	directive = directive
			+ ", " + this.userdirective ;
        }
        
        p.setProperty("userdirective", directive) ;
        
		this.ve = new VelocityEngine();
		
		ve.setApplicationAttribute(SummonDirective.GUZZ_CONTEXT_NAME, this.guzzContext) ;
		ve.setApplicationAttribute("guzz_vts_templates_name", templates) ;

		try {
			ve.init(p) ;
		} catch (Exception e) {
			throw new InvalidConfigurationException(e) ;
		}
	}

	public void shutdown() {
	}
	
//////////////////////////////////////// Velocity Helper Classes //////////////////////////////
	
	public static class PreStringResourceLoader extends ResourceLoader {
		
		private HashMap<String, TemplateData> templates ;
		
		public void init(ExtendedProperties configuration) {
			templates = (HashMap<String, TemplateData>) this.rsvc.getApplicationAttribute("guzz_vts_templates_name") ;
			
			this.setCachingOn(false) ;
			this.setModificationCheckInterval(0L) ;
		}

		public InputStream getResourceStream(String source) throws ResourceNotFoundException {
			TemplateData bs = templates.get(source) ;
			
	        if (bs == null) {
	            throw new ResourceNotFoundException("No Template for resource:" + source);     
	        }
	        
	        return new ByteArrayInputStream(bs.sql);    
		}

		public boolean isSourceModified(Resource resource) {
			return false;
		}

		public long getLastModified(Resource resource) {
			return 0;
		}

		public boolean resourceExists(String resourceName) {
			return templates.get(resourceName) != null ;
		} 
		
	}

}
