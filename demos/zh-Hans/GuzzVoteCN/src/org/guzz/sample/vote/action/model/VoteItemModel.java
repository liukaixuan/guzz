/**
 * VoteItemModel.java created at 2009-9-22 下午04:08:42 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.VoteItem;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteItemModel implements Serializable {
	
	private VoteItem item ;
	
	private boolean isNew ;
	
	public VoteItemModel(int voteId){
		item = new VoteItem() ;
		item.setVoteId(voteId) ;
		isNew = true ;
	}
	
	public VoteItemModel(VoteItem voteItem){
		this.item = voteItem ;
		isNew = false ;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public VoteItem getItem() {
		return item;
	}

	public void setItem(VoteItem voteItem) {
		this.item = voteItem;
	}

}
