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
	
	private static boolean[] delimiterChars = new boolean[128] ;
	
	static{
		delimiterChars[' '] = true ;
		delimiterChars['	'] = true ;
		delimiterChars['='] = true ;
		delimiterChars['>'] = true ;
		delimiterChars['<'] = true ;
		delimiterChars[','] = true ;
		delimiterChars['('] = true ;
		delimiterChars[')'] = true ;
		delimiterChars['\''] = true ;
		delimiterChars['"'] = true ;
		delimiterChars['~'] = true ;
		delimiterChars['!'] = true ;
		delimiterChars['\r'] = true ;
		delimiterChars['\n'] = true ;
		delimiterChars['\t'] = true ;
	}
	
	public SQLCompiler(ObjectMappingManager omm, CompiledSQLBuilder sqlBuilder){
		this.omm = omm ;
		this.sqlBuilder = sqlBuilder ;
	}
	
	/**对sql继续编译。*/
	public NormalCompiledSQL compileNormalCS(ObjectMapping mapping, String markedSQL){
		NormalCompiledSQL cs = new NormalCompiledSQL() ;
		cs.setMapping(mapping) ;
		
		String sql = translateMark(cs, mapping, markedSQL) ;
		
		char[] chars = sql.toCharArray();
		StringBuffer newsb = new StringBuffer(chars.length + 16) ;
		for(int i = 0 ; i < chars.length; ){
			char c = chars[i];
			
			if(c == '\\' && i != chars.length - 1){
				char nextC = chars[i + 1] ;

				//转义字符
				if(nextC == ':' || nextC == '\'' || nextC == '\\' || nextC == '"'){
					newsb.append(nextC);
					i+=2;
					
					continue ;
				}else{
					throw new ORMException("unknown character:[\\" + nextC + "] for sql:" + markedSQL) ;
				}
			}
			
			if(c == ':'){
				// 去掉空格
				i++;
				for( ; i < chars.length; ){
					if( chars[i] == ' ' || chars[i] == '\t') {
						i++;
					} else {
						break;
					}
				}
				
				int j = i;
				for( ; j < chars.length; j++){
					c = chars[j];
					
					if(c < 128 && delimiterChars[c]){
						break ;
					}
				}
				String m_mark = new String(chars, i, j - i) ;				
				newsb.append('?') ;
				cs.addParamToLast(m_mark) ;

				i = j;
			} else {
				newsb.append(c);
				i++;
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
	
	/**将java属性转换为sql字段，sql表等。通过此方法后，返回的string为携带named param的sql语句。*/
	protected String translateMark(NormalCompiledSQL cs, ObjectMapping mapping, String markedSQL){
		char[] chars = markedSQL.toCharArray();
		StringBuffer newsb = new StringBuffer(chars.length + 16) ;
		for(int i = 0 ; i < chars.length; ){
			char c = chars[i];
			
			if(c == '\\' && i != chars.length - 1 && chars[i + 1] == '@'){
				//转义@符号
				newsb.append('@');
				i+=2;
			}else if(c == '@'){
				i++;
				if (i == chars.length) {
					throw new ORMException("Name needed after @");
				}
				
				boolean isTable = chars[i] == '@';
				if (isTable) {
					i++;
				}
				
				// 去掉空格
				for( ; i < chars.length; ){
					if( chars[i] == ' ' || chars[i] == '\t') {
						i++;
					} else {
						break;
					}
				}
				
				int j = i;
				for( ; j < chars.length; j++){
					c = chars[j];
					
					if(c < 128 && delimiterChars[c]){
						break ;
					}
				}
				
				String m_mark = new String(chars, i, j - i) ;
				if (m_mark.length() == 0) {
					throw new ORMException("Invalid format SQL:"+markedSQL);
				}
				if (isTable) {
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
				} else {
					String colName = mapping.getColNameByPropNameForSQL(m_mark) ;
					if(colName == null){
						throw new ORMException("unknown property[" + m_mark + "] in sql:" + markedSQL) ;
					}
					
					newsb.append(colName) ;
				}
				
				i = j;
			} else {
				newsb.append(c);
				i++;
			}
		}
		
		return newsb.toString() ;
	}	
}
