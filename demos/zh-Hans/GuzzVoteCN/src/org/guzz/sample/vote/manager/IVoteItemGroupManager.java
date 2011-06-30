/**
 * IVoteItemGroupManager.java created at 2009-10-16 下午05:40:18 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import org.guzz.sample.vote.business.VoteItemGroup;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IVoteItemGroupManager {
	
	public void remove(VoteItemGroup group, int backupGroupId) ;
	
	public void add(VoteItemGroup group) ;
	
	public void update(VoteItemGroup group) ;
	
	public VoteItemGroup getById(int id) ;
	
}
