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

import org.guzz.exception.DaoException;
import org.guzz.exception.ORMException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CustomCompiledSQL;
import org.guzz.orm.sql.MarkedSQL;
import org.guzz.orm.sql.NormalCompiledSQL;
import org.guzz.orm.sql.CustomCompiledSQL.DynamicSQLProvider;
import org.guzz.util.ArrayUtil;


/**
 * 
 * 用于构建结构话的sql语句。编译以后的常用sql可以进行缓存，以提高解析效率。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SQLCompiler {
	
	private final ObjectMappingManager omm ;
	
	private final CompiledSQLBuilder sqlBuilder ;
	
	public SQLCompiler(ObjectMappingManager omm, CompiledSQLBuilder sqlBuilder){
		this.omm = omm ;
		this.sqlBuilder = sqlBuilder ;
	}
	
	/**对sql继续编译。*/
	public NormalCompiledSQL compileNormalCS(ObjectMapping mapping, String markedSQL){
		NormalCompiledSQL cs = new NormalCompiledSQL() ;
		cs.setMapping(mapping) ;
		
		String sql = translateMark(cs, mapping, markedSQL) ;
		
		StringBuffer sb = new StringBuffer(sql) ;
		StringBuffer newsb = new StringBuffer(sb.length() + 16) ;
		int length = sb.length() ;
		int alpha_count = 0 ;
		int startPos = 0 ;
		for(int i = 0 ; i < length; i++){
			char c = sb.charAt(i) ;
			
			if(c == ':'){
				alpha_count++ ;
				startPos = i ;
				continue ;
			}else if(alpha_count == 0){ //没有需要处理的替换内容
				newsb.append(c) ;
				continue ;
			}
			
			if(alpha_count > 1){//连续2个:及以上，报错。
				throw new ORMException("too many : marks for named parameter. sql is:" + sql) ;
			}
			
			if(c == ' ' || c == ',' ||c == ')' ||c == '\'' ||c == '"' || i == length - 1){
				if(!(c == ' ' || c == ',' ||c == ')' ||c == '\'' ||c == '"' )){//到达字符串最后了。不能使用if(i == length -1)判断，因为符合")"可能也是最后一个字符。
					i++ ;//向前多走一个字符
				}
				
				String m_mark = sb.substring(startPos + 1, i) ;
					
				newsb.append('?') ;
				cs.addParamToLast(m_mark) ;
				
				if(i != length){
					newsb.append(c) ;
				}
				
				//完成一次翻译后，计数器归0
				alpha_count = 0 ;
				startPos = 0 ;
			}
		}
		
		cs.setSql(newsb.toString()) ;
		
		return cs ;
	}
	
	/**对sql继续编译。*/
	public CompiledSQL compile(String businessName, String markedSQL){
		Table table = this.omm.getTableByGhostName(businessName) ;
		
		if(table == null){
			throw new ORMException("unknown business:[" + businessName + "] for sql:" + markedSQL) ;
		}
		
		if(!table.isCustomTable()){
			return compileNormalCS(this.omm.getStaticObjectMapping(businessName), markedSQL) ;
		}else{
			//否则，按照CustomTable处理。
			CustomCompiledSQL ccs = new CustomCompiledSQL(omm, sqlBuilder, businessName) ;
			ccs.setSql(markedSQL) ;
			
			return ccs ;
		}
	}
	
	/**对sql编译。*/
	public CustomCompiledSQL compileCustom(String businessName, DynamicSQLProvider sqlProvider){
		Table table = this.omm.getTableByGhostName(businessName) ;
		
		if(table == null){
			throw new ORMException("unknown business:[" + businessName + "] for sql:" + sqlProvider) ;
		}
		
		if(!table.isCustomTable()){
			throw new ORMException("unknown custom business:[" + businessName + "] for sql:" + sqlProvider) ;
		}else{
			//否则，按照CustomTable处理。
			CustomCompiledSQL ccs = new CustomCompiledSQL(omm, sqlBuilder, businessName) ;
			ccs.setSqlProvider(sqlProvider) ;
			
			return ccs ;
		}
	}
	
//	/**对sql继续编译。*/
//	public CompiledSQL compile(MarkedSQL ms){
//		CompiledSQL cs = new CompiledSQL() ;
//		cs.setMapping(ms.getMapping()) ;
//		
//		String sql = translateMark(cs, ms) ;
//		
//		StringBuffer sb = new StringBuffer(sql) ;
//		StringBuffer newsb = new StringBuffer(sb.length() + 16) ;
//		int length = sb.length() ;
//		int alpha_count = 0 ;
//		int startPos = 0 ;
//		for(int i = 0 ; i < length; i++){
//			char c = sb.charAt(i) ;
//			
//			if(c == ':'){
//				alpha_count++ ;
//				startPos = i ;
//				continue ;
//			}else if(alpha_count == 0){ //没有需要处理的替换内容
//				newsb.append(c) ;
//				continue ;
//			}
//			
//			if(alpha_count > 1){//连续2个@及以上，报错。
//				throw new ORMException("too many : marks for named parameter. sql is:" + sql) ;
//			}
//			
//			if(c == ' ' || c == ',' ||c == ')' ||c == '\'' ||c == '"' || i == length - 1){
//				if(!(c == ' ' || c == ',' ||c == ')' ||c == '\'' ||c == '"' )){//到达字符串最后了。不能使用if(i == length -1)判断，因为符合")"可能也是最后一个字符。
//					i++ ;//向前多走一个字符
//				}
//				
//				String m_mark = sb.substring(startPos + 1, i) ;
//					
//				newsb.append('?') ;
//				cs.addParamToLast(m_mark) ;
//				
//				if(i != length){
//					newsb.append(c) ;
//				}
//				
//				//完成一次翻译后，计数器归0
//				alpha_count = 0 ;
//				startPos = 0 ;
//			}
//		}
//		
//		cs.setSql(newsb.toString()) ;
//		
//		return cs ;
//	}
	
	
	/**将java属性转换为sql字段，sql表等。通过此方法后，返回的string为携带named param的sql语句。*/
	protected String translateMark(NormalCompiledSQL cs, ObjectMapping mapping, String markedSQL){
		StringBuffer sb = new StringBuffer(markedSQL) ;
		StringBuffer newsb = new StringBuffer(sb.length() + 16) ;
		int length = sb.length() ;
		int alpha_count = 0 ;
		int startPos = 0 ;
		
		for(int i = 0 ; i < length; i++){
			char c = sb.charAt(i) ;
			
			if(c == '@'){
				alpha_count ++ ;
				startPos = i ;
				continue ;
			}else if(alpha_count == 0){ //没有需要处理的替换内容
				newsb.append(c) ;
				
				continue ;
			}
			
			if(alpha_count > 2){//连续3个@及以上，报错。
				throw new ORMException("too many @@@ marks.") ;
			}
			
			if(c == ' ' || c == '	' || c == '=' || c == ',' ||c == '('  ||c == ')' ||c == '\'' ||c == '"' ||i == length - 1){//变量替换结束。
				String m_mark ;
				if(!(c == ' '  || c == '	' || c == '=' || c == ',' ||c == '(' ||c == ')' ||c == '\'' ||c == '"' )){//到达字符串最后了。不能使用if(i == length -1)判断，因为符合")"可能也是最后一个字符。
					i++ ;//向前多走一个字符
				}
				
				m_mark = sb.substring(startPos + 1, i) ;
				
				if(alpha_count == 1){ //属性替换开始
					String colName = mapping.getColNameByPropNameForSQL(m_mark) ;
					if(colName == null){
						throw new ORMException("unknown property[" + m_mark + "] in sql:" + markedSQL) ;
					}
					
					newsb.append(colName) ;
					
				}else{//表替换
					Table m_table = null ;
					
					//表可能是local orm的映射，而local orm在oom中并没有注册，需要从传入的ObjectMapping中获取。
					if(ArrayUtil.inArray(mapping.getUniqueName(), m_mark)){
						m_table = mapping.getTable() ;
					}
					
					if(m_table == null){
						m_table = omm.getTableByGhostName(m_mark) ;
					}					
					
					if(m_table != null){
						if(m_table.isShadow()){
							newsb.append(MarkedSQL.TABLE_START_TAG_IN_MARKED_SQL).append(m_mark) ;
							cs.addShadowMapping(m_mark, m_table) ;
						}else{
							newsb.append(m_table.getConfigTableName()) ;
						}
					}else{
						throw new DaoException("unknown table mark:" + m_mark) ;
					}
				}
				
				if(i != length){
					newsb.append(c) ;
				}
				
				//完成一次翻译后，计数器归0
				alpha_count = 0 ;
				startPos = 0 ;
			}
		}
		
		return newsb.toString() ;
	}
	
}
