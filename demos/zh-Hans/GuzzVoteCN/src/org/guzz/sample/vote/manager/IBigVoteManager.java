/**
 * IBigVoteManager.java created at 2009-9-21 下午05:06:22 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.business.VoteTerritory;
import org.guzz.sample.vote.exception.VoteException;


/**
 * 
 * 管理投票。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IBigVoteManager {
	
	public BigVote getBigVoteForUpdate(int voteId) ;
	
	public void addBigVote(BigVote vote) ;
	
	public void addAllChineseProvincesAsVoteTerritories(int voteId) ;
	
	public void addAdditionalVoteTerritoriesForAutoIP(int voteId) ;
	
	public void updateBigBote(BigVote vote) ;
	
	public void recomputeVoteCount(int voteId) ;
	
	/**
	 * 获取缓存的cacheTree对象
	 * @return 如果不存在返回null。
	 * */
	public BigVoteTree getCachedVoteTree(int voteId) ;
	
	public BigVote getCachedVote(int voteId) ;
	
	public BigVoteTree getCachedVoteTreeNoCache(int voteId) ;
	
	/**
	 * 
	 * @throws VoteException 如果投票状态异常，如投票不存在或者已经关闭，抛出错误。
	 */
	public void makeAVote(int voteId, int[] items, int cityId, VoterInfo info) throws VoteException ;
	
	public VoteItem getVoteItem(int itemId) ;
	
	public void addVoteItem(VoteItem item) ;
	
	public void updateVoteItem(VoteItem item) ;
	
	//
	
	public VoteTerritory getVoteTerritory(int territoryId) ;
	
	public void addVoteTerritory(VoteTerritory city) ;
	
	public void updateVoteTerritory(VoteTerritory city) ;	
	
	//TODO: continue here. Test this, finish slowupdate and txtLog sub system
	
}
