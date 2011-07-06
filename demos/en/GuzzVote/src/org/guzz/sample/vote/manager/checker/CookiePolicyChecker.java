/**
 * CookiePolicyChecker.java created at 2009-10-23 下午05:36:14 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.checker;

import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.business.AntiCheatPolicy;
import org.guzz.sample.vote.exception.DuplicateVoteException;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;
import org.guzz.util.CookieUtil;
import org.guzz.util.StringUtil;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CookiePolicyChecker implements IAntiCheatPolicyChecker {
	
	private String cookieName = "c_atv_mk_" ;
	
	private AntiCheatPolicy policy ;
	
	private CookieUtil cookie = CookieUtil.forVersion1() ;
	
	public CookiePolicyChecker(AntiCheatPolicy policy){
		this.policy = policy ;
	}
	
	public void checkCanVote(BigVoteTree tree, VoterInfo info) throws DuplicateVoteException {
		int voteId = tree.getBigVote().getId() ;
		String name = cookieName + voteId ;
		
		String mark = cookie.readCookie(info.getRequest(), name) ;
		int value = StringUtil.toInt(mark, 0) ;
		
		if(value > policy.getAllowedCount()){
			throw new DuplicateVoteException("您已经投过 " + value + " 票，请不要反复投票。谢谢您的参与！") ;
		}
	}

	public void markOneVote(BigVoteTree tree, VoterInfo info) {
		int voteId = tree.getBigVote().getId() ;
		String name = cookieName + voteId ;
		
		String mark = cookie.readCookie(info.getRequest(), name) ;
		int newValue = StringUtil.toInt(mark, 0) + 1 ;
		
		cookie.writeCookie(info.getResponse(), name, String.valueOf(newValue), policy.getMaxLife()) ;
	}

	public String getRuleName() {
		return "cookie" ;
	}

}
