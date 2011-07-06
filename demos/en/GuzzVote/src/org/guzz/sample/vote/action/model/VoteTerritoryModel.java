/**
 * VoteTerritoryModel.java created at 2009-9-22 下午04:18:38 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.VoteTerritory;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteTerritoryModel implements Serializable {
	
	private VoteTerritory city ;
	
	private boolean isNew ;
	
	public VoteTerritoryModel(int voteId){
		city = new VoteTerritory() ;
		city.setVoteId(voteId) ;
		isNew = true ;
	}
	
	public VoteTerritoryModel(VoteTerritory city){
		this.city = city ;
		isNew = false ;
	}

	public VoteTerritory getCity() {
		return city;
	}

	public void setCity(VoteTerritory city) {
		this.city = city;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
