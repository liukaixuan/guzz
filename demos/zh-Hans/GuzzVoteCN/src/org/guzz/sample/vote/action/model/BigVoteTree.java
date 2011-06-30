/**
 * BigVoteTree.java created at 2009-9-22 上午09:32:44 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.business.VoteTerritory;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;


/**
 * 
 * 一个投票对象的完整属性。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVoteTree implements Serializable {
	
	private BigVote bigVote ;
	
	private Map<Integer, VoteItem> voteItems = new HashMap<Integer, VoteItem>() ;
	
	private List<VoteItem> items ;
	
	private Map<Integer, VoteTerritory> voteTerritories = new HashMap<Integer, VoteTerritory>() ;
	
	private List<VoteTerritory> cities ;
	
	private List<VoteExtraProperty> extraProperties ;
	
	/**对应特殊的“海外”地区，可能为null*/
	private VoteTerritory overSeaTerritory ;
	
	/**对应特殊的“其他”地区*/
	private VoteTerritory otherTerritory ;
	
	private List<IAntiCheatPolicyChecker> antiCheckCheckers ;
	
	public VoteTerritory getVoteTerritory(int territoryId){
		Integer key = Integer.valueOf(territoryId) ;
		
		VoteTerritory value = voteTerritories.get(key) ;
		
		//TODO: if value is null, try load it from the database (maybe a new update in other servers happened).
				
		return value ;
	}
	
	public VoteItem getVoteItem(int itemId){
		Integer key = Integer.valueOf(itemId) ;
		
		VoteItem value = voteItems.get(key) ;
		
		//TODO: if value is null, try load it from the database (maybe a new update in other servers happened).
				
		return value ;
	}	
	
	public BigVote getBigVote() {
		return bigVote;
	}

	public void setBigVote(BigVote bigVote) {
		this.bigVote = bigVote;
	}
	
	public void setVoteItems(List<VoteItem> voteItems){
		this.items = voteItems ;
		
		for(VoteItem i : voteItems){
			this.voteItems.put(Integer.valueOf(i.getId()), i) ;
		}
	}
	
	public void setVoteTerritories(List<VoteTerritory> territories){
		this.cities = territories ;
		
		for(VoteTerritory v : territories){
			this.voteTerritories.put(Integer.valueOf(v.getId()), v) ;
		}
	}

	public List<VoteItem> getVoteItems() {
		return items ;
	}

	public List<VoteTerritory> getVoteTerritories() {
		return cities ;
	}

	public List<VoteExtraProperty> getExtraProperties() {
		return extraProperties;
	}

	public void setExtraProperties(List<VoteExtraProperty> extraProperties) {
		this.extraProperties = extraProperties;
	}

	public VoteTerritory getOverSeaTerritory() {
		return overSeaTerritory;
	}

	public void setOverSeaTerritory(VoteTerritory overSeaTerritory) {
		this.overSeaTerritory = overSeaTerritory;
	}

	public VoteTerritory getOtherTerritory() {
		return otherTerritory;
	}

	public void setOtherTerritory(VoteTerritory otherTerritory) {
		this.otherTerritory = otherTerritory;
	}

	public List<IAntiCheatPolicyChecker> getAntiCheckCheckers() {
		return antiCheckCheckers;
	}

	public void setAntiCheckCheckers(List<IAntiCheatPolicyChecker> antiCheckCheckers) {
		this.antiCheckCheckers = antiCheckCheckers;
	}
	
}
