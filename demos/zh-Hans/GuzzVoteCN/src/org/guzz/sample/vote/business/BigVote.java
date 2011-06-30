/**
 * BigVote.java created at 2009-9-21 下午02:43:42 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;
import java.util.Date;

import org.guzz.util.DateUtil;

/**
 * 
 * 投票
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVote implements Serializable {

	public static final int VOTE_OPEN = 1 ;
	
	public static final int VOTE_CLOSED = 0 ;
	
	private int id ;
	
	/**所属频道*/
	private int channelId ;
	
	private String name ;
		
	/**投票地区选取方案。*/
	private String territoryPolicy ;
	
	/**一人一次最多投多少票*/
	private int maxItemsPerVote ;
	
	/**唯一投票人数*/
	private int votePeople ;
	
	/**总得票数*/
	private int voteNum ;
	
	/**手工追加投出票数*/
	private int addedVoteNum ;
	
	private Date createdTime ;
	
	private int status ;
	
	/**
	 * 投票开始时间
	 */
	private Date beginTime ;
	
	/**
	 * 投票结束时间
	 */
	private Date endTime ;
	

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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getStatus() {
		return status;
	}
	
	/**投票是否正常对外开放*/
	public boolean isOpenToPublicNow(){
		long now = System.currentTimeMillis() ;
		
		if(this.beginTime != null && this.beginTime.getTime() > now){ //还没开始
			return false ;
		}else if(this.endTime != null && this.endTime.getTime() < now){ //已经结束了
			return false ;
		}
		
		return this.status == VOTE_OPEN ;
	}
	
	/**投票开放时间提示串*/
	public String getOpenTimeDesc(){
		StringBuilder sb = new StringBuilder() ;
		if(this.status == VOTE_OPEN){
			sb.append("手工状态：开启") ;
		}else{
			sb.append("手工状态：关闭") ;
		}
		
		sb.append("；投票开始时间：") ;
		if(this.beginTime == null){
			sb.append("无开始时间限制") ;
		}else{
			sb.append(DateUtil.date2String(beginTime, "yyyy-MM-dd HH:mm:ss")) ;
		}
		
		sb.append("；投票结束时间：") ;
		
		if(this.endTime == null){
			sb.append("无截止时间") ;
		}else{
			sb.append(DateUtil.date2String(endTime, "yyyy-MM-dd HH:mm:ss")) ;
		}
		
		return sb.toString() ;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getMaxItemsPerVote() {
		return maxItemsPerVote;
	}

	public void setMaxItemsPerVote(int maxItemsPerVote) {
		this.maxItemsPerVote = maxItemsPerVote;
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

	public String getTerritoryPolicy() {
		return territoryPolicy;
	}
	
	public boolean isTerritoryIPAutoDetectedMode(){
		return "IP".equalsIgnoreCase(territoryPolicy) ;
	}

	public void setTerritoryPolicy(String territoryPolicy) {
		this.territoryPolicy = territoryPolicy;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public Date getBeginTime() {
		return beginTime;
	}
	
	public String getBeginTimeStr() {
		if(beginTime == null) return "" ;
		return DateUtil.date2String(beginTime, "yyyy-MM-dd HH:mm");
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public String getEndTimeStr() {
		if(endTime == null) return "" ;
		return DateUtil.date2String(endTime, "yyyy-MM-dd HH:mm");
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
