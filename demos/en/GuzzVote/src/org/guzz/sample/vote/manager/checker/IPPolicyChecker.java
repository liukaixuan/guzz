/**
 * IPPolicyChecker.java created at 2009-10-23 下午05:45:43 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.checker;

import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.business.AntiCheatPolicy;
import org.guzz.sample.vote.exception.DuplicateVoteException;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;
import org.guzz.service.cache.FixedLifeCountService;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IPPolicyChecker implements IAntiCheatPolicyChecker {
	
	private String prefix = "vote_@IP_" ;
	
	private AntiCheatPolicy policy ;
	
	private FixedLifeCountService fixedLifeCountService ;
	
	public IPPolicyChecker(FixedLifeCountService fixedLifeCountService, AntiCheatPolicy policy){
		this.fixedLifeCountService = fixedLifeCountService ;
		this.policy = policy ;
	}
	
	public void checkCanVote(BigVoteTree tree, VoterInfo info) throws DuplicateVoteException {
		int voteId = tree.getBigVote().getId() ;
		String key = prefix + voteId + "_" + info.getXIP() ;
				
		if(!this.fixedLifeCountService.incCountIfLess(key, 1, policy.getAllowedCount(),policy.getMaxLife())){
			throw new DuplicateVoteException("您已经投过 " + policy.getAllowedCount() + " 票，请不要反复投票。谢谢您的参与！") ;
		}
	}

	public void markOneVote(BigVoteTree tree, VoterInfo info) {
	}

	public String getRuleName() {
		return "IP" ;
	}

}
