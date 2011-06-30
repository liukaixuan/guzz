/**
 * AntiCheatPolicyManagerImpl.java created at 2009-10-21 下午04:48:59 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.impl;

import java.util.LinkedList;
import java.util.List;

import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.sample.vote.business.AntiCheatPolicy;
import org.guzz.sample.vote.exception.VoteException;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;
import org.guzz.sample.vote.manager.IAntiCheatPolicyManager;
import org.guzz.sample.vote.manager.checker.CookiePolicyChecker;
import org.guzz.sample.vote.manager.checker.ExtraPropPolicyChecker;
import org.guzz.sample.vote.manager.checker.IPPolicyChecker;
import org.guzz.service.cache.FixedLifeCountService;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AntiCheatPolicyManagerImpl extends GuzzBaseDao implements IAntiCheatPolicyManager {
		
	private FixedLifeCountService fixedLifeCountService ;

	public void addPolicy(AntiCheatPolicy policy) {
		super.insert(policy) ;
	}

	public AntiCheatPolicy getPolicyById(int id) {
		return (AntiCheatPolicy) super.getForUpdate(AntiCheatPolicy.class, id) ;
	}

	public void removePolicy(AntiCheatPolicy policy) {
		super.delete(policy) ;
	}

	public void updatePolicy(AntiCheatPolicy policy) {
		super.update(policy) ;
	}

	public List<AntiCheatPolicy> listPolicies(int voteId) {
		SearchExpression se = SearchExpression.forLoadAll(AntiCheatPolicy.class) ;
		se.and(Terms.eq("voteId", voteId)) ;
		se.setOrderBy("id asc") ;
		
		return super.list(se) ;
	}
	
	public List<IAntiCheatPolicyChecker> listPolicyCheckers(int voteId){
		List<AntiCheatPolicy> acps =  listPolicies(voteId) ;
		
		if(acps == null || acps.isEmpty()){
			return null ;
		}
		
		LinkedList<IAntiCheatPolicyChecker> cs = new LinkedList<IAntiCheatPolicyChecker>() ;
		
		for(int i = 0 ; i < acps.size() ; i++){
			AntiCheatPolicy acp = acps.get(i) ;
			String impl = acp.getPolicyImpl() ;
			
			if("cookie".equalsIgnoreCase(impl)){
				cs.addLast(new CookiePolicyChecker(acp)) ;
			}else if("IP".equalsIgnoreCase(impl)){
				cs.addLast(new IPPolicyChecker(this.fixedLifeCountService, acp)) ;
			}else if("extraProp".equalsIgnoreCase(impl)){
				cs.addLast(new ExtraPropPolicyChecker(this.fixedLifeCountService, acp)) ;
			}else{
				throw new VoteException("unknown anti cheat policy impl:" + impl) ;
			}
		}
		
		return cs ;
	}
	
	public void startup(){
		fixedLifeCountService = (FixedLifeCountService) this.getGuzzContext().getService("fixedLifeCountService") ;
	}

}
