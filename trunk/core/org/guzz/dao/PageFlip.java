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
package org.guzz.dao;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class PageFlip {
	
	/**分页信息保存在request中的参数名称*/
	public static final String FLIP_SOURCE = "PAGE_FLIP" ;
	
	public PageFlip(){}
	

	/**
	 * @param totalCount 总的纪录数
	 * @param pageNo 当前的页，以1开始，作为第一个页。
	 * @param pageSize 每页显示的纪录数
	 * @param elements 数据
	 **/
	public void setResult(int totalCount, int pageNo, int pageSize, List elements) {
		pageStart = pageNo - 5;
		if (pageStart < 0)
			pageStart = 1;
		this.pageSize = (pageSize <= 0) ? 20 : pageSize;
		this.totalCount = totalCount;
		if (totalCount % pageSize == 0) {
			pageCount = (totalCount / pageSize);
		} else {
			pageCount = (totalCount / pageSize) + 1;
		}
		this.elements = elements;
		this.pageNo = pageNo > 0 ? pageNo : 1 ;
	}

	private int pageStart;

	private int pageNo;

	private int pageSize;

	private int pageCount;

	private int totalCount;
	
	private int skipCount ;

//	private int pageEnd ; //在页面上分页的结束页

	private int pagesShow = 10 ; //在页面上显示的分页数

	private List elements;

	private int index = -1 ;
	
	private String webPageNoParam = "pageNo" ;

	//基准地址，例如：http://www.book.com/listBook.do?uid=1&pageNo=3
	private String flipURL ;
	
	/**在当前页数前面的页面小于此值时显示前面所有页*/
	private int pageBeforeSpan = 5 ;
	
	/**在当前页数后面剩余的页数小于此值时显示后面所有页*/
	private int pageAfterSpan = 5 ;
	
	/**
	 * @return Returns the pageStart.
	 */
	public int getPageStart() {
		int before = pageNo - 1 ;
		int after = pageCount - pageNo ;

		if(before < pageBeforeSpan) return 1 ; //如果前面小于5页就全部留下，或者往后翻。
		else{
			if(after > pagesShow - pageAfterSpan){
				return pageNo - pageAfterSpan + 1 ;
			}else{
				if(pageCount > pagesShow){
					if(after <= pageAfterSpan){
						return pageCount - pagesShow + 1 ;
					}
					return pageCount - pagesShow ;
				}else{
					return 1 ;
				}
			}
		}
	}

	public int getPageEnd() {
        if(pageCount < 1) return 1 ;

		int after = pageCount - pageNo ;

		if(after < pageAfterSpan) return pageCount ;

		else{
			if(getPageStart() > 1){
				return Math.min(getPageStart() + pagesShow - 1, pageCount) ;
			}else{
				return Math.min(pageCount, pagesShow) ;
			}
		}
	}

	/**
	 * @param pageStart
	 *            The pageStart to set.
	 */
	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public boolean isHasNextPage(){
		if(pageNo < 1) pageNo = 1 ;
		return pageCount > pageNo ;
	}

	public boolean isHasPreviousPage(){
		return pageNo > 1 ;
	}

	public int getPreviousPageNum(){
		if(isHasPreviousPage()){
			return pageNo - 1 ;
		}
		return 1 ;
	}

	public int getNextPageNum(){
		if(!isHasNextPage()){
			return pageNo ;
		}
		if(pageNo > 1){
			return (pageNo + 1) ;
		}else{
			return 2 ;
		}
	}

	public int getIndex(){
		return getIndex(true) ;
	}

	public int getIndex(boolean updateIndex){
		if(index < 0){
			index = ((pageNo > 1 ? pageNo : 1) - 1) * pageSize + 1 ;
		}
		if(updateIndex){
			return index++ ;
		}else{
			return index ;
		}
	}

	/**
	 * @return Returns the totalCount.
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount
	 *            The totalCount to set.
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return Returns the pageCount.
	 */
	public int getPageCount() {
		return pageCount > 0 ? pageCount : 1 ;
	}

	/**
	 * @param pageCount
	 *            The pageCount to set.
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return Returns the pageNo.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo
	 *            The pageNo to set.
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo > 0 ? pageNo : 1 ;
	}

	/**
	 * @return Returns the pageSize.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            The pageSize to set.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return Returns the elements.
	 */
	public List getElements() {
		return elements;
	}

	/**
	 * @param elements
	 *            The elements to set.
	 */
	public void setElements(List elements) {
		this.elements = elements;
	}

	public boolean isEmpty() {
		return ((elements == null) || (elements.isEmpty()));
	}

	public Object getAt(int i) {
		if (elements.isEmpty())
			return null;
		return elements.get(i);
	}
	
	public int getPagesShow() {
		return pagesShow;
	}

	public void setPagesShow(int pagesShow) {
		this.pagesShow = pagesShow;
	}

	public int getSize(){
		return elements.size() ;
	}

	public int getPageStartIndex(){
		return getIndex(false) ;
	}

	public int getPageEndIndex(){
		int startIndex = getIndex(false) ;
		int elementNum = this.getElements().size() ;

		int toreturn ;
		if(startIndex + getPageSize() > elementNum){
			toreturn =  startIndex + elementNum - 1 ;
		}else{
			toreturn =  startIndex + getPageSize() - 1 ;
		}
		return toreturn > 0 ? toreturn : 1 ;
	}

	public String getFlipURL() {
		return flipURL;
	}

	public void setFlipURL(String flipURL) {
		this.flipURL = flipURL;
	}

	/**
	 * 设置翻页的基础URL。同时把此PageFlip通过key：FLIP_SOURCE保存到request的attribute中
	 * @param request HttpServletRequest
	 * @param pageNoParamName 存储在request中，用于表示当前页码的参数名称
	 */
	public void setFlipURL(HttpServletRequest request, String pageNoParamName){
		String queryString = request.getQueryString() ;
		queryString =  StringUtil.getSubQueryString(queryString, pageNoParamName) ;

		StringBuffer spath = request.getRequestURL() ;

		if(StringUtil.isEmpty(queryString)){
			spath.append("?") ;
		}else{
			spath.append("?").append(queryString) ;
		}
		setFlipURL(spath.toString()) ;
		
		this.webPageNoParam = pageNoParamName ;

		request.setAttribute(FLIP_SOURCE, this) ;
	}

	public String getWebPageNoParam() {
		return webPageNoParam;
	}

	public void setWebPageNoParam(String webPageNoParam) {
		this.webPageNoParam = webPageNoParam;
	}

	public int getPageBeforeSpan() {
		return pageBeforeSpan;
	}

	public void setPageBeforeSpan(int pageBeforeSpan) {
		this.pageBeforeSpan = pageBeforeSpan;
	}

	public int getPageAfterSpan() {
		return pageAfterSpan;
	}

	public void setPageAfterSpan(int pageAfterSpan) {
		this.pageAfterSpan = pageAfterSpan;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

}
