/**
 * VoterInfo.java created at 2009-9-21 下午05:19:01 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 投票者信息。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoterInfo {
	
	private HttpServletRequest request ;
	
	private HttpServletResponse response ;
		
	private String[] possibleIPs ;
	
	private Map params ;

	public String[] getPossibleIPs() {
		return possibleIPs;
	}

	public void setPossibleIPs(String[] possibleIPs) {
		this.possibleIPs = possibleIPs;
	}
	
	public String getXIP(){
		return possibleIPs[0] ;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}
	
	public String getParamValue(String key){
		Object value = params.get(key) ;
		if(value == null) return null ;
		
		if(value.getClass().isArray()){
			return ((String[]) value)[0] ;
		}else{
			return (String) value ;
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
