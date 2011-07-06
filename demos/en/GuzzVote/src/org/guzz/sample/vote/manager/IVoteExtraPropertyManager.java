/**
 * IVoteExtraPropertyManager.java created at 2009-10-16 上午11:22:11 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import java.util.List;

import org.guzz.sample.vote.business.VoteExtraProperty;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IVoteExtraPropertyManager {
	
	public VoteExtraProperty getExtraProperty(int propId) ;

	public void updateExtraProperty(VoteExtraProperty extraProp) ;
	
	public void addExtraProperty(VoteExtraProperty extraProp) ;
	
	public void removeExtraProperty(VoteExtraProperty extraProp) ;
	
	public List<VoteExtraProperty> listByVoteId(int voteId) ;
	
	public IUserInputValidator getUserInputValidator(String name) ;
	
	public String[] getAllValidatorNames() ;
		
}
