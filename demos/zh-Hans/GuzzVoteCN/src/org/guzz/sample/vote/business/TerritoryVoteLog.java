/**
 * TerritoryVoteLog.java created at 2009-9-21 下午02:46:58 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;

/**
 * 
 * 地区投票记录。<p/>
 * 
 * 在某个地区或者投票项目被删除时，此对象记录不级联删除。<p/>
 * 
 * 当所在投票被删除时，对象被级联删除。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TerritoryVoteLog implements Serializable {

	private String id ;
	
	private int voteId ;
	
	private int itemId ;
	
	private int territoryId ;
	
	private int voteNum ;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getTerritoryId() {
		return territoryId;
	}

	public void setTerritoryId(int territoryId) {
		this.territoryId = territoryId;
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
	
}
