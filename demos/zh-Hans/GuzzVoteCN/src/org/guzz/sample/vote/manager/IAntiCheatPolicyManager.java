/**
 * IAntiCheatPolicyManager.java created at 2009-10-21 下午04:42:01 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import java.util.List;

import org.guzz.sample.vote.business.AntiCheatPolicy;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IAntiCheatPolicyManager {
	
	public AntiCheatPolicy getPolicyById(int id) ;
	
	public void addPolicy(AntiCheatPolicy policy) ;
	
	public void updatePolicy(AntiCheatPolicy policy) ;
	
	public void removePolicy(AntiCheatPolicy policy) ;
	
	public List<AntiCheatPolicy> listPolicies(int voteId) ;
	
	/**
	 * 如果没有策略，返回null
	 */
	public List<IAntiCheatPolicyChecker> listPolicyCheckers(int voteId) ;

}
