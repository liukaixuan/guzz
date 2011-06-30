/**
 * VoteExtraPropertyManagerImpl.java created at 2009-10-16 上午11:22:47 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.manager.IUserInputValidator;
import org.guzz.sample.vote.manager.IVoteExtraPropertyManager;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteExtraPropertyManagerImpl extends GuzzBaseDao implements IVoteExtraPropertyManager {
	
	private Map<String, IUserInputValidator> userInputValidators = new HashMap<String, IUserInputValidator>() ; 
	
	private String[] validatorNames ;
	
	public IUserInputValidator getUserInputValidator(String name){
		return this.userInputValidators.get(name) ;
	}
	
	public String[] getAllValidatorNames(){
		return this.validatorNames ;
	}

	public void addExtraProperty(VoteExtraProperty extraProp) {
		super.insert(extraProp) ;
	}

	public VoteExtraProperty getExtraProperty(int propId) {
		return (VoteExtraProperty) super.getForUpdate(VoteExtraProperty.class, propId) ;
	}

	public List<VoteExtraProperty> listByVoteId(int voteId) {
		SearchExpression se = SearchExpression.forLoadAll(VoteExtraProperty.class) ;
		se.and(Terms.eq("voteId", voteId)) ;
		se.setOrderBy("id asc") ;
		
		return super.list(se) ;
	}

	public void removeExtraProperty(VoteExtraProperty extraProp) {
		super.delete(extraProp) ;
	}

	public void updateExtraProperty(VoteExtraProperty extraProp) {
		super.update(extraProp) ;
	}

	public void setUserInputValidators(List<IUserInputValidator> userInputValidators) {
		for(IUserInputValidator v : userInputValidators){
			this.userInputValidators.put(v.getName(), v) ;
		}
		
		this.validatorNames = this.userInputValidators.keySet().toArray(new String[0]) ;
	}
	
}
