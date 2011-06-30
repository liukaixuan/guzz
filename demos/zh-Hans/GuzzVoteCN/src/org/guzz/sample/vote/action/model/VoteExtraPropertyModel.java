/**
 * VoteExtraPropertyModel.java created at 2009-10-16 上午11:36:56 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.VoteExtraProperty;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteExtraPropertyModel  implements Serializable {
	
	private VoteExtraProperty prop ;
	
	private boolean isNew ;	
	
	public VoteExtraPropertyModel(int voteId){
		prop = new VoteExtraProperty() ;
		prop.setVoteId(voteId) ;
		isNew = true ;
	}
	
	public VoteExtraPropertyModel(VoteExtraProperty prop){
		this.prop = prop ;
		isNew = false ;
	}

	public VoteExtraProperty getProp() {
		return prop;
	}

	public void setProp(VoteExtraProperty prop) {
		this.prop = prop;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
