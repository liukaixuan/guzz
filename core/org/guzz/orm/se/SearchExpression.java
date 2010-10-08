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
package org.guzz.orm.se;

import java.util.List;

import org.guzz.Guzz;
import org.guzz.exception.DataTypeException;
import org.guzz.exception.GuzzException;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.mapping.RowDataLoader;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.MarkedSQL;


/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class SearchExpression {
	
	private static Class SEImpClass = DefaultSearchExpression.class ; 
	
	public static int FIRST_PAGE = 1 ;
	
	public static int DEFAULT_PAGE_SIZE = 20 ;
	
	/**不进行分页，如果每页的包含的记录为此，则不进行分页处理。pageNo自动按照第一页处理。*/
	public static final int UNLIMITED_PAGE_SIZE = Integer.MAX_VALUE ;

	private OrderByTerm orderByTerm ;
	
	private String from ;
		
	private PropsSelectTerm selectTerm ;
	
	/**设置用于select count(*) 的内容。例如可以为：count(id), sum(score) 等*/
	private String countSelectPhrase ;
	
	private SearchTerm conditionTerm ;
	
	/**开始位置，设定应该跳过多少个记录进行读取。*/
	private int skipCount = 0 ;
	
	private int pageNo = FIRST_PAGE ;
	
	private int pageSize = DEFAULT_PAGE_SIZE ;
	
	private boolean computeRecordNumber = true ;
	
	private boolean loadRecords = true ;
	
	private int cacheMode ;
	
	/**定义用于翻页的类，默认使用 (@link PageFlip)*/
	private Class pageFlipClass ;
	
	private Object tableCondition ;
	
	private RowDataLoader rowDataLoader ;
	
	/**主从使用策略*/
//	private int persistPolicy = PersistPolicyParameter.PERSIST_POLICY_AUTO;
		
	protected static SearchExpression newInstance(){
		if(SEImpClass == null){
			SEImpClass = DefaultSearchExpression.class ; 
		}
		try {
			return (SearchExpression) SEImpClass.newInstance() ;
		} catch (Exception e) {
			throw new DataTypeException("unknown SearchExpression class:" + SEImpClass, e) ;
		}	
	}
		
	public static SearchExpression forClass(Class from){
		return forBusiness(from.getName()) ;
	}
	
	public static SearchExpression forLoadAll(Class from){
		return forLoadAll(from.getName()) ;
	}
	
	public static SearchExpression forClass(Class from, int pageNo, int pageSize){
		return forBusiness(from.getName(), pageNo, pageSize) ;
	}
	
	public static SearchExpression forBusiness(String business){
		SearchExpression se = newInstance() ;
		se.from = business ;
		return se ;
	}
	
	public static SearchExpression forLoadAll(String ghost){
		return forBusiness(ghost).setPageSize(UNLIMITED_PAGE_SIZE) ;
	}
	
	public static SearchExpression forBusiness(String business, int pageNo, int pageSize){
		SearchExpression se = forBusiness(business) ;
		se.setPageNo(pageNo) ;
		se.setPageSize(pageSize) ;
		
		return se ;
	}
	
	/**
	 * 添加一个and类型的检索条件。
	 */
	public SearchExpression and(SearchTerm term){
		conditionTerm = new AndTerm(conditionTerm, term) ;
//		Map m_params = term.getParameters() ;
//		
//		if(m_params != null){
//			this.params.putAll(m_params) ;
//		}
		return this ;
	}
	
	/**
	 * 添加一批and类型的检索条件。
	 */
	public SearchExpression and(List terms){
		if(terms != null && !terms.isEmpty()){
			for(int i = 0 ; i < terms.size() ; i++){
				this.and((SearchTerm) terms.get(i)) ;
			}
		}
		
		return this ;
	}
	
	/**
	 * 添加一个or类型的检索条件。
	 */
	public SearchExpression or(SearchTerm term){
		conditionTerm = new OrTerm(conditionTerm, term) ;
//		Map m_params = term.getParameters() ;
//		
//		if(m_params != null){
//			this.params.putAll(m_params) ;
//		}
		
		return this ;
	}
	
	public MarkedSQL toLoadRecordsMarkedSQL(POJOBasedObjectMapping mapping, SearchParams params){
		StringBuffer sb = new StringBuffer(128) ;
		Table table = mapping.getTable() ;
		
		if(selectTerm != null){
        	sb.append("select ").append(selectTerm.toExpression(this, mapping, params)) ;
        }else{
        	/**
        	 * select * 的时候通过columnsForSelect进行加载，避免加载不需要的字段（可能是Lob/Text等大字段）。
        	 */
        	sb.append("select ") ;
        	
    		TableColumn[] columns = table.getColumnsForSelect() ;
    		
    		boolean firstProp = true ;
    		for(int i = 0 ; i < columns.length ; i++){
    			if(!firstProp){
    				sb.append(", ") ;
    			}else{
    				firstProp = false ;
    			}
    			
    			sb.append(columns[i].getColNameForSQL()) ;
    		}
        }
		
		sb.append(" from ").append(table.isShadow() ? table.getBusinessShape() : table.getConfigTableName()) ;
              
        if (conditionTerm != null) {
        	sb.append(' ').append(new WhereTerm(conditionTerm).toExpression(this, mapping, params)) ;
        }
        
        if (orderByTerm != null) {
    		sb.append(' ').append(orderByTerm.toExpression(this, mapping, params));
        }
        
        MarkedSQL sql = new MarkedSQL(mapping, sb.toString()) ;
        
        return sql ;
	}
	
//	public String toLoadRecordsSQL(POJOBasedObjectMapping mapping, SearchParams params){
//		StringBuffer sb = new StringBuffer(128) ;
//		
//		if(selectTerm != null){
//        	sb.append("select ").append(selectTerm.toExpression(this, mapping, params)).append(" ") ;
//        }else{
//        	sb.append("select * ") ;
//        }
//		
//		String tableName = mapping.getTable().getTableName() ;		
//		sb.append("from ").append(tableName) ;
//              
//        if (conditionTerm != null) {
//        	sb.append(' ').append(new WhereTerm(conditionTerm).toExpression(this, mapping, params)) ;
//        }     
//        
//        if (orderByTerm != null) {
//    		sb.append(orderByTerm.toExpression(this, mapping, params));
//        }
//        
//        return sb.toString() ;
//	}
	
	public MarkedSQL toComputeRecordNumberSQL(POJOBasedObjectMapping mapping, SearchParams params){
		StringBuffer sb = new StringBuffer(128) ;
		Table table = mapping.getTable() ;
		
		if(countSelectPhrase == null){
			sb.append("select count(*) ") ;
		}else{
			int startPos = countSelectPhrase.indexOf('(') ;
			int endPos = countSelectPhrase.indexOf(')') ;
			
			//格式不对。
			if(startPos < 1 || endPos <= startPos){
				throw new GuzzException("countSelectPhrase in wrong format. should be [function(propName), eg:max(id), count(clickCount)]") ;
			}
			
			String colName = countSelectPhrase.subSequence(startPos + 1, endPos).toString().trim() ;
			String m_propName = mapping.getColNameByPropNameForSQL(colName) ;
			if(m_propName == null){
				throw new GuzzException("unknown column:[" + colName + "] in mapping:[" + mapping.dump() + "], countSelectPhrase is:[" + countSelectPhrase + "]") ;
			}
			
			sb.append("select ")
			  .append(countSelectPhrase.substring(0, startPos + 1))
			  .append(m_propName)
			  .append(countSelectPhrase.substring(endPos))
			  .append(" ") ;
		}
		
		sb.append("from ").append(table.isShadow() ? table.getBusinessShape() : table.getConfigTableName()) ;
              
		if (conditionTerm != null) {
        	sb.append(' ').append(new WhereTerm(conditionTerm).toExpression(this, mapping, params)) ;
        }
        
		MarkedSQL sql = new MarkedSQL(mapping, sb.toString()) ;
	        
	    return sql ;
	}	
	
	public MarkedSQL toDeleteRecordString(POJOBasedObjectMapping mapping, SearchParams params){
		Table table = mapping.getTable() ;
		String tableName = table.isShadow() ? table.getBusinessShape() : table.getConfigTableName() ;
		
		StringBuffer sb = new StringBuffer(64) ;
		sb.append("delete from ").append(tableName).append(' ') ;
		
		if(conditionTerm != null){
			sb.append(new WhereTerm(conditionTerm).toExpression(this, mapping, params)) ;
		}
		        
		MarkedSQL sql = new MarkedSQL(mapping, sb.toString()) ;
	        
	    return sql ;
	}
	
	public BindedCompiledSQL prepareHits(BindedCompiledSQL bsql){
		bsql.setTableCondition(getTableCondition()) ;
		
		if(this.rowDataLoader != null){
			bsql.setRowDataLoader(this.rowDataLoader) ;
		}
		
		return bsql ;
	}

	public OrderByTerm getOrderBy() {
		return orderByTerm;
	}

	public SearchExpression setOrderBy(String orderBy) {
		this.orderByTerm = new OrderByTerm(orderBy);
		return this ;
	}
	
	/**获取记录读取的开始位置，从1开始 */
	public int getStartPos(){
		int pn = pageNo > FIRST_PAGE ? pageNo : FIRST_PAGE ;
		return (pn - 1) * pageSize + 1 + skipCount ;
	}

	public int getPageNo() {
		return pageNo > FIRST_PAGE ? pageNo : FIRST_PAGE ;
	}

	public SearchExpression setPageNo(int pageNo) {
		this.pageNo = pageNo;
		return this ;
	}

	public int getPageSize() {
		return pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE ;
	}

	public SearchExpression setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this ;
	}

	/**设置要提取的域对象属性，多个之间用,分割。如果提取全部不需要填写，默认使用* 
	 */
	public SearchExpression setSelectedProps(String selectedProps) {
		this.selectTerm = new PropsSelectTerm(selectedProps) ;
		return this ;
	}

	public boolean isComputeRecordNumber() {
		return computeRecordNumber;
	}

	public void setComputeRecordNumber(boolean computePageSize) {
		this.computeRecordNumber = computePageSize;
	}

	public boolean isLoadRecords() {
		return loadRecords;
	}

	public void setLoadRecords(boolean loadRecords) {
		this.loadRecords = loadRecords;
	}

	public SearchTerm getCondition() {
		return conditionTerm;
	}

	/**
	 * 设置查询条件。用于sql语句 where 后面的句子，不包括关键词“where”。
	 */
	public SearchExpression setCondition(SearchTerm searchTerm) {
		this.conditionTerm = searchTerm;
		return this ;
	}

	public int getCacheMode() {
		return cacheMode;
	}

	public void setCacheMode(int cacheMode) {
		this.cacheMode = cacheMode;
	}

	public String getCountSelectPhrase() {
		return countSelectPhrase;
	}

	/**设置用于select count(*) 的内容。例如可以为：count(id), sum(score) 等*/
	public void setCountSelectPhrase(String countSelectPhrase) {
		this.countSelectPhrase = countSelectPhrase;
	}

	public String getFrom() {
		return from;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public Class getPageFlipClass() {
		return pageFlipClass;
	}

	public void setPageFlipClass(Class pageFlipClass) {
		this.pageFlipClass = pageFlipClass;
	}

	public final Object getTableCondition() {
		return tableCondition == null ? Guzz.getTableCondition() : this.tableCondition;
	}

	public SearchExpression setTableCondition(Object tableCondition) {
		this.tableCondition = tableCondition;
		return this ;
	}

	public RowDataLoader getRowDataLoader() {
		return rowDataLoader;
	}

	public void setRowDataLoader(RowDataLoader rowDataLoader) {
		this.rowDataLoader = rowDataLoader;
	}	

}
