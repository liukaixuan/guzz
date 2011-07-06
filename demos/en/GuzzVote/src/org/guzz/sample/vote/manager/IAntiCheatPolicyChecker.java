/**
 * IAntiCheatPolicyChecker.java created at 2009-9-29 下午02:29:58 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.exception.DuplicateVoteException;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IAntiCheatPolicyChecker {
	
	public String getRuleName() ;
	
	/**
	 * 检查是否允许投票。
	 * @throws DuplicateVoteException
	 */
	public void checkCanVote(BigVoteTree tree, VoterInfo info) throws DuplicateVoteException ;
	
	/**
	 * 标记已经投过一票。
	 */
	public void markOneVote(BigVoteTree tree, VoterInfo info) ;

}
