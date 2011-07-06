/**
 * VoteItemGroupManagerImpl.java created at 2009-10-19 上午10:02:38 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.impl;

import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.business.VoteItemGroup;
import org.guzz.sample.vote.manager.IVoteItemGroupManager;
import org.guzz.transaction.WriteTranSession;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteItemGroupManagerImpl extends GuzzBaseDao implements IVoteItemGroupManager {

	public void remove(VoteItemGroup group, int newGroupId) {
		WriteTranSession tran = super.getTransactionManager().openRWTran(false) ;
		
		try{
			tran.delete(group) ;
			
			CompiledSQL sql = super.getTransactionManager().getCompiledSQLBuilder()
							  .buildCompiledSQL(VoteItem.class, 
									  "update @@" + VoteItem.class.getName() + " set @groupId = :newGroupId where @groupId = :oldGroupId") ;
						
			tran.executeUpdate(sql.bind("newGroupId", newGroupId).bind("oldGroupId", group.getId())) ;
			
			tran.commit() ;
		}catch(Exception e){
			tran.rollback() ;
		}finally{
			tran.close() ;
		}
	}

	public void add(VoteItemGroup obj) {
		super.insert(obj) ;
	}

	public VoteItemGroup getById(int id) {
		return (VoteItemGroup) super.getForUpdate(VoteItemGroup.class, id) ;
	}

	public void update(VoteItemGroup obj) {
		super.update(obj) ;
	}

}
