/**
 * AntiCheatPolicy.java created at 2009-10-21 下午04:23:01 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 可以定制的反作弊策略。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AntiCheatPolicy implements Serializable {
	
	private int id ;
	
	private int voteId ;

	private String name ;
	
	private String policyImpl ;
	
	private String limitedField = "" ;
	
	private int maxLife = 900 ;
	
	private int allowedCount = 1 ;
	
	private Date createdTime ;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLimitedField() {
		return limitedField;
	}

	public void setLimitedField(String limitedField) {
		this.limitedField = limitedField;
	}

	public int getMaxLife() {
		return maxLife;
	}

	public void setMaxLife(int maxLife) {
		this.maxLife = maxLife;
	}

	public int getAllowedCount() {
		return allowedCount;
	}

	public void setAllowedCount(int allowedCount) {
		this.allowedCount = allowedCount;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getPolicyImpl() {
		return policyImpl;
	}

	public void setPolicyImpl(String policyImpl) {
		this.policyImpl = policyImpl;
	}

}
