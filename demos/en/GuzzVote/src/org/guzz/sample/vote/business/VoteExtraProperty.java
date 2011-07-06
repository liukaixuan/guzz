/**
 * VoteExtraProperty.java created at 2009-10-16 上午10:55:53 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;

import org.guzz.util.StringUtil;
import org.guzz.util.ViewFormat;

/**
 * 
 * 投票自定义属性。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteExtraProperty implements Serializable {
	
	private int id ;
	
	private int voteId ;
	
	private String paramName ;
	
	private String showName ;
	
	/**是否为必填项*/
	private boolean mustProp ;
	
	private String validValues ;
	
	private String defaultValue ;
	
	private String validRuleName ;
	
	/**用于给规则使用的参数*/
	private String ruleParamValue ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVoteId() {
		return voteId;
	}

	public void setVoteId(int voteId) {
		this.voteId = voteId;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getValidValues() {
		return validValues;
	}
	
	public String[] getArrayValidValues() {
		if(validValues == null) return null ;
		
		return StringUtil.splitString(this.validValues, ";") ;
	}

	public void setValidValues(String validValues) {
		this.validValues = ViewFormat.reassembleKeywords(validValues) ;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValidRuleName() {
		return validRuleName;
	}

	public void setValidRuleName(String validRule) {
		this.validRuleName = validRule;
	}

	public boolean isMustProp() {
		return mustProp;
	}

	public void setMustProp(boolean mustProp) {
		this.mustProp = mustProp;
	}

	public String getRuleParamValue() {
		return ruleParamValue;
	}

	public void setRuleParamValue(String ruleParamValue) {
		this.ruleParamValue = ruleParamValue;
	}

}
