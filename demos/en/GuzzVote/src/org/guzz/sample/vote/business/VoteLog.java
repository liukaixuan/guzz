/**
 * VoteLog.java created at 2009-9-21 下午02:46:31 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 投票记录
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteLog implements Serializable {
	
	private int id ; 
	
	private int voteId ;
	
	private String logFileName ;
	
	private String itemName ;
	
	private String territoryName ;
		
	private String IP ;
	
	private Date createdTime ;
	
	private String extraPropsXML ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getVoteId() {
		return voteId;
	}

	public void setVoteId(int voteId) {
		this.voteId = voteId;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public String getExtraPropsXML() {
		return extraPropsXML;
	}

	public void setExtraPropsXML(String extraPropsXML) {
		this.extraPropsXML = extraPropsXML;
	}	

}
