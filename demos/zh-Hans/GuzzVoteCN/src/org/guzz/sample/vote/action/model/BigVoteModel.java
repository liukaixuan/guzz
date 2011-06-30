/**
 * BigVoteModel.java created at 2009-9-22 下午02:16:03 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.BigVote;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVoteModel implements Serializable {
	
	private BigVote vote ;
	
	private boolean isNew ;
	
	private boolean addChineseProvinces ;
	
	private String startTime ;
	
	private String endTime ;
	
	public BigVoteModel(){
		vote = new BigVote() ;
		isNew = true ;
	}
	
	public BigVoteModel(BigVote vote){
		this.vote = vote ;
		isNew = false ;
	}


	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public BigVote getVote() {
		return vote;
	}

	public void setVote(BigVote vote) {
		this.vote = vote;
	}

	public boolean isAddChineseProvinces() {
		return addChineseProvinces;
	}

	public void setAddChineseProvinces(boolean addChineseProvinces) {
		this.addChineseProvinces = addChineseProvinces;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
