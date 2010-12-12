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
	
	/**variable name stored in request*/
	public static final String FLIP_SOURCE = "PAGE_FLIP" ;
	
	public PageFlip(){}
	

	/**
	 * @param totalCount total count
	 * @param pageNo current page number. starts from 1
	 * @param pageSize page size
	 * @param elements data of the this page
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

	private int pagesShow = 10 ;

	private List elements;

	private int index = -1 ;
	
	private String webPageNoParam = "pageNo" ;

	//base url with parameter pageNo：http://www.book.com/listBook.do?uid=1&pageSize=15
	private String flipURL ;
	
	private int pageBeforeSpan = 5 ;
	
	private int pageAfterSpan = 5 ;
	
	/**
	 * @return Returns the pageStart.
	 */
	public int getPageStart() {
		int before = pageNo - 1 ;
		int after = pageCount - pageNo ;

		if(before < pageBeforeSpan) return 1 ;
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
	 * Set page navigation's based url, and store this object to variable "FLIP_SOURCE" in the request.
	 * @param request HttpServletRequest
	 * @param pageNoParamName parameter name of the page number.
	 */
	public void setFlipURL(HttpServletRequest request, String pageNoParamName){
		//servlet 1.4 --handle forward. http://www.caucho.com/resin-3.0/webapp/faq.xtp
		String queryString = (String) request.getAttribute("javax.servlet.forward.query_string") ;
		if(queryString == null){
			queryString = request.getQueryString() ;
		}
		
		queryString =  StringUtil.getSubQueryString(queryString, pageNoParamName) ;
		
		String requestUri =	(String) request.getAttribute("javax.servlet.forward.request_uri");
		if(requestUri == null){
			requestUri = request.getRequestURI() ;
		}
		
		StringBuffer spath = new StringBuffer(requestUri.length() + 16) ;
		spath.append(requestUri) ;

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
