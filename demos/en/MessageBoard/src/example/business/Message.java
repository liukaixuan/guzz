/**
 * 
 */
package example.business;

import java.util.Date;

public class Message implements java.io.Serializable {
	
	private int id ;

	private String content ;
	
	private int userId ;
	
	private int voteYes ;
	
	private int voteNo ;
	
	private int voteScore ;
	
	private Date createdTime ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getVoteYes() {
		return voteYes;
	}

	public void setVoteYes(int voteYes) {
		this.voteYes = voteYes;
	}

	public int getVoteNo() {
		return voteNo;
	}

	public void setVoteNo(int voteNo) {
		this.voteNo = voteNo;
	}

	public int getVoteScore() {
		return voteScore;
	}

	public void setVoteScore(int voteScore) {
		this.voteScore = voteScore;
	}
	
}
