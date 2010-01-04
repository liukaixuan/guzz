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
package org.guzz.orm.sql.impl;

import java.util.HashMap;
import java.util.Map;

import org.guzz.exception.GuzzException;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CompiledSQLManagerImpl implements CompiledSQLManager {
	
	private Map sqls = new HashMap() ;
	
	private CompiledSQLBuilder compiledSQLBuilder ;
	
	public CompiledSQLManagerImpl(CompiledSQLBuilder compiledSQLBuilder){
		this.compiledSQLBuilder = compiledSQLBuilder ;	
	}

	public CompiledSQL getSQL(String id) {
		return (CompiledSQL) sqls.get(id) ;
	}
	
	public void addCompliedSQL(String id, CompiledSQL cs){
		sqls.put(id, cs) ;
	}	
		
	public void addDomainBusiness(POJOBasedObjectMapping mapping){
		//注册compiledSQL		
		if(mapping.getTable().getIdentifierGenerator().insertWithPKColumn()){
			this.addCompliedSQL(CS_PREFIX.BUSINESS_INSERT_PREFIX + mapping.getBusiness().getName(), buildInsertSQLWithPK(mapping)) ;
		}else{
			this.addCompliedSQL(CS_PREFIX.BUSINESS_INSERT_PREFIX + mapping.getBusiness().getName(), buildInsertSQLWithoutPK(mapping)) ;
		}		
		this.addCompliedSQL(CS_PREFIX.BUSINESS_UPDATE_PREFIX + mapping.getBusiness().getName(), buildUpdateSQL(mapping)) ;
		this.addCompliedSQL(CS_PREFIX.BUSINESS_DELETE_PREFIX + mapping.getBusiness().getName(), buildDeleteSQL(mapping)) ;
		this.addCompliedSQL(CS_PREFIX.BUSINESS_SELECT_PREFIX + mapping.getBusiness().getName(), buildSelectSQL(mapping)) ;
		
		

		if(mapping.getTable().getIdentifierGenerator().insertWithPKColumn()){
			this.addCompliedSQL(CS_PREFIX.BUSINESS_INSERT_PREFIX + mapping.getBusiness().getDomainClass().getName(), buildInsertSQLWithPK(mapping)) ;
		}else{
			this.addCompliedSQL(CS_PREFIX.BUSINESS_INSERT_PREFIX + mapping.getBusiness().getDomainClass().getName(), buildInsertSQLWithoutPK(mapping)) ;
		}
		
		this.addCompliedSQL(CS_PREFIX.BUSINESS_UPDATE_PREFIX + mapping.getBusiness().getDomainClass().getName(), buildUpdateSQL(mapping)) ;
		this.addCompliedSQL(CS_PREFIX.BUSINESS_DELETE_PREFIX + mapping.getBusiness().getDomainClass().getName(), buildDeleteSQL(mapping)) ;
		this.addCompliedSQL(CS_PREFIX.BUSINESS_SELECT_PREFIX + mapping.getBusiness().getDomainClass().getName(), buildSelectSQL(mapping)) ;
		
		//注册
	}
	
	public CompiledSQL getDefinedSelectSQL(String className){
		String key = CompiledSQLManager.CS_PREFIX.BUSINESS_SELECT_PREFIX + className ;
		return getSQL(key) ;
	}
	
	public CompiledSQL getDefinedInsertSQL(String className){
		String key = CompiledSQLManager.CS_PREFIX.BUSINESS_INSERT_PREFIX + className ;
		return getSQL(key) ;
	}
	
	public CompiledSQL getDefinedUpdateSQL(String className){
		String key = CompiledSQLManager.CS_PREFIX.BUSINESS_UPDATE_PREFIX + className ;
		return getSQL(key) ;
	}
	
	public CompiledSQL getDefinedDeleteSQL(String className){
		String key = CompiledSQLManager.CS_PREFIX.BUSINESS_DELETE_PREFIX + className ;
		return getSQL(key) ;
	}
	
	protected CompiledSQL buildInsertSQLWithoutPK(POJOBasedObjectMapping mapping){
		String[] columns = mapping.getTable().getColumnsForInsert() ;
//		String primaryProp = mapping.getTable().getPKPropName() ;
		String primaryColumn = mapping.getTable().getPKColName() ;
		
		HashMap paramPropMapping = new HashMap() ;
		
		//insert
		StringBuffer sb_insert = new StringBuffer() ;
		
		sb_insert.append("insert into ")
				 .append(mapping.getTable()
				 .getTableName())
				 .append("(") ;
		
		boolean firstProp = true ;
		
		for(int i = 0 ; i < columns.length ; i++){
			if(primaryColumn.equals(columns[i])) continue ;
						
			if(!firstProp){
				sb_insert.append(", ") ;
			}else{
				firstProp = false ;
			}
			
			sb_insert.append(columns[i]) ;
		}		
		
		sb_insert.append(") values(") ;
		
		firstProp = true ;
		for(int i = 0 ; i < columns.length ; i++){
			if(primaryColumn.equals(columns[i])) continue ;
			
			if(!firstProp){
				sb_insert.append(", ") ;
			}else{
				firstProp = false ;
			}
			
			String propName = mapping.getPropNameByColName(columns[i].toLowerCase()) ;
			sb_insert.append(":").append(propName) ;
			
			paramPropMapping.put(propName, propName) ;
		}
		
		sb_insert.append(")") ;
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb_insert.toString()) ;
		cs.setParamPropMapping(paramPropMapping) ;
		
		return cs ;
	}
	
	protected CompiledSQL buildInsertSQLWithPK(POJOBasedObjectMapping mapping){
		String[] columns = mapping.getTable().getColumnsForInsert() ;
		
		//insert
		StringBuffer sb_insert = new StringBuffer() ;
		HashMap paramPropMapping = new HashMap() ;
		
		sb_insert.append("insert into ")
				 .append(mapping.getTable().getTableName()).append("(") ;
		
		for(int i = 0 ; i < columns.length ; i++){
			if(i > 0){
				sb_insert.append(", ") ;
			}
			sb_insert.append(columns[i]) ;
		}
		
		
		sb_insert.append(") values(") ;
		
		for(int i = 0 ; i < columns.length ; i++){
			if(i > 0){
				sb_insert.append(", ") ;
			}
			String propName = mapping.getPropNameByColName(columns[i].toLowerCase()) ;
			sb_insert.append(":").append(propName) ;
			paramPropMapping.put(propName, propName) ;
		}
		
		sb_insert.append(")") ;
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb_insert.toString()).setParamPropMapping(paramPropMapping) ;
		
		return cs ;
	}
	
	//update by primary key.
	protected CompiledSQL buildUpdateSQL(POJOBasedObjectMapping mapping){
		String[] columns = mapping.getTable().getColumnsForUpdate() ;
		String primaryKey = mapping.getTable().getPKColName() ;
		String primaryProp = mapping.getTable().getPKPropName() ;
		if(StringUtil.isEmpty(primaryProp)){
			throw new GuzzException("business domain must has a primary key. table:" + mapping.getTable().getTableName()) ;
		}
		
		StringBuffer sb = new StringBuffer() ;
		HashMap paramPropMapping = new HashMap() ;
		
		sb.append("update ")
		  .append(mapping.getTable().getTableName()).append(" set ") ;
		
		boolean firstProp = true ;
		for(int i = 0 ; i < columns.length ; i++){
			if(primaryKey.equals(columns[i])) continue ;
			
			if(!firstProp){
				sb.append(", ") ;
			}else{
				firstProp = false ;
			}
			
			String propName = mapping.getPropNameByColName(columns[i].toLowerCase()) ;
			sb.append(columns[i]).append("=:").append(propName) ;
			paramPropMapping.put(propName, propName) ;
		}
		
		sb.append(" where ")
		  .append(primaryKey)
		  .append("=:")
		  .append(primaryProp) ;		
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb.toString()).setParamPropMapping(paramPropMapping) ;
		cs.addParamPropMapping(primaryProp, primaryProp) ;
		
		return cs ;
	}
	
	//delete one object by primary key.
	protected CompiledSQL buildDeleteSQL(POJOBasedObjectMapping mapping){
		String primaryKey = mapping.getTable().getPKColName() ;
		String primaryProp = mapping.getTable().getPKPropName() ;
		if(StringUtil.isEmpty(primaryProp)){
			throw new GuzzException("business domain must has a primary key. table:" + mapping.getTable().getTableName()) ;
		}
		
		StringBuffer sb = new StringBuffer() ;
		
		sb.append("delete from ")
		  .append(mapping.getTable().getTableName())
		  .append(" where ")		  
		  .append(primaryKey)
		  .append("=:")
		  .append(primaryProp);		
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb.toString()) ;
		cs.addParamPropMapping(primaryProp, primaryProp) ;
		
		return cs ;
	}
	
	//select one object by primary key.
	protected CompiledSQL buildSelectSQL(POJOBasedObjectMapping mapping){
		String primaryKey = mapping.getTable().getPKColName() ;
		String primaryProp = mapping.getTable().getPKPropName() ;
		String[] columns = mapping.getTable().getColumnsForSelect() ;
		
		if(StringUtil.isEmpty(primaryProp)){
			throw new GuzzException("business domain must has a primary key. table:" + mapping.getTable().getTableName()) ;
		}
		
		StringBuffer sb = new StringBuffer() ;
		sb.append("select ") ;
		
		boolean firstProp = true ;
		for(int i = 0 ; i < columns.length ; i++){
			if(!firstProp){
				sb.append(", ") ;
			}else{
				firstProp = false ;
			}
			
			sb.append(columns[i]) ;
		}
		
		sb.append(" from ")
		  .append(mapping.getTable().getTableName())
		  .append(" where ")		  
		  .append(primaryKey)
		  .append("=:")
		  .append(primaryProp);		
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb.toString()) ;
		cs.addParamPropMapping(primaryProp, primaryProp) ;
		
		return cs ;
	}
	
	//update by primary key.
	public CompiledSQL buildUpdateSQL(POJOBasedObjectMapping mapping, String[] propsToUpdate){
		String primaryKey = mapping.getTable().getPKColName() ;
		String primaryProp = mapping.getTable().getPKPropName() ;
		if(StringUtil.isEmpty(primaryProp)){
			throw new GuzzException("business domain must has a primary key. table:" + mapping.getTable().getTableName()) ;
		}
		
		StringBuffer sb = new StringBuffer() ;
		HashMap paramPropMapping = new HashMap() ;
		
		sb.append("update ")
		  .append(mapping.getTable().getTableName()).append(" set ") ;
		
		boolean firstProp = true ;
		for(int i = 0 ; i < propsToUpdate.length ; i++){
			String propName = propsToUpdate[i] ;
			
			if(primaryProp.equals(propName)) continue ;
			
			if(!firstProp){
				sb.append(", ") ;
			}else{
				firstProp = false ;
			}			
			
			sb.append("@").append(propName).append("=:").append(propName) ;
			paramPropMapping.put(propName, propName) ;
		}
		
		sb.append(" where ")
		  .append(primaryKey)
		  .append("=:")
		  .append(primaryProp) ;		
		
		CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(mapping, sb.toString()).setParamPropMapping(paramPropMapping) ;
		cs.addParamPropMapping(primaryProp, primaryProp) ;
		
		return cs ;
	}

}
