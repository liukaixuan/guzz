/**
 * VoteItemGroup.java created at 2009-10-16 下午05:20:29 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteItemGroup  implements Serializable {
	
	private int id ;
	
	private int voteId ;
	
	private String name ;
	
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}
