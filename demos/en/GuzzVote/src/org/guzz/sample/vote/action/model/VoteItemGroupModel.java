/**
 * VoteItemGroupModel.java created at 2009-10-19 上午10:35:09 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.VoteItemGroup;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteItemGroupModel implements Serializable {
	
	private VoteItemGroup group ;
	
	private boolean isNew ;	
	
	public VoteItemGroupModel(int voteId){
		group = new VoteItemGroup() ;
		group.setVoteId(voteId) ;
		isNew = true ;
	}
	
	public VoteItemGroupModel(VoteItemGroup group){
		this.group = group ;
		isNew = false ;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public VoteItemGroup getGroup() {
		return group;
	}

	public void setGroup(VoteItemGroup group) {
		this.group = group;
	}

}
