/**
 * VoteTerritory.java created at 2009-9-21 下午02:46:10 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;

/**
 * 
 * 投票地区。如北京，天津，上海等。。。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteTerritory implements Serializable {

	private int id ;
	
	private int voteId ;
	
	private String name ;
	
	/**来自此地区投票人数*/
	private int votePeople ;
	
	/**来自此地区投出票数*/
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

	public int getVotePeople() {
		return votePeople;
	}

	public void setVotePeople(int votePeople) {
		this.votePeople = votePeople;
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

	public int getAddedVoteNum() {
		return addedVoteNum;
	}
	
	public int getShowVoteNum(){
		return this.voteNum + this.getAddedVoteNum() ;
	}

	public void setAddedVoteNum(int addedVoteNum) {
		this.addedVoteNum = addedVoteNum;
	}	
	
}
