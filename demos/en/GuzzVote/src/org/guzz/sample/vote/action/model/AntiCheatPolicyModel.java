/**
 * AntiCheatPolicyModel.java created at 2009-10-21 下午04:53:38 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.AntiCheatPolicy;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AntiCheatPolicyModel implements Serializable {
	
	private AntiCheatPolicy policy ;
	
	private boolean isNew ;
	
	public AntiCheatPolicyModel(int voteId){
		policy = new AntiCheatPolicy() ;
		policy.setVoteId(voteId) ;
		isNew = true ;
	}
	
	public AntiCheatPolicyModel(AntiCheatPolicy policy){
		this.policy = policy ;
		isNew = false ;
	}

	public AntiCheatPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(AntiCheatPolicy policy) {
		this.policy = policy;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
