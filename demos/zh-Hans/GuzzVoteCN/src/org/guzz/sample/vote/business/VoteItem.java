/**
 * VoteItem.java created at 2009-9-21 下午02:43:56 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;

/**
 * 
 * 投票项目
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteItem implements Serializable {
	
	private int id ;
	
	private int voteId ;
	
	private int groupId ;
	
	private String name ;	

	/**用于前台的显示名称，可以包含html装饰等。*/
	private String showName = "" ;
	
	private int voteNum ;
	
	/**手工追加投出票数*/
	private int addedVoteNum ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVoteNum() {
		return voteNum;
	}

	public void setVoteNum(int voteNum) {
		this.voteNum = voteNum;
	}

	public int getVoteId() {
		return voteId;
	}

	public void setVoteId(int voteId) {
		this.voteId = voteId;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public int getAddedVoteNum() {
		return addedVoteNum;
	}
	
	public int getShowVoteNum(){
		return this.voteNum + this.getAddedVoteNum() ;
	}

	public void setAddedVoteNum(int addedVoteNum) {
		this.addedVoteNum = addedVoteNum;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	

}
