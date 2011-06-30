/**
 * BigVoteInterpreter.java created at 2009-10-29 下午03:00:21 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.util.List;

import org.guzz.GuzzContext;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.interpreter.SEBusinessInterpreter;
import org.guzz.orm.mapping.FirstColumnDataLoader;
import org.guzz.orm.se.InTerm;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.sample.vote.exception.VoteException;
import org.guzz.service.user.AdminUser;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVoteInterpreter extends SEBusinessInterpreter implements GuzzContextAware {

	TransactionManager tm = null ;
	
	CompiledSQL cs ;
	
	//覆盖基类方法，基类方法就是直接抛出异常。
	protected Object explainOtherTypeConditon(ObjectMapping mapping, Object limitTo) {
		//根据传入的后台用户对象，返回查询此用户有权限投票的查询条件。
		if(limitTo instanceof AdminUser){
			AdminUser a = (AdminUser) limitTo ;
			if(a.isSystemAdmin()){ //超级管理员，允许所有。不需要添加条件。
				return null ;
			}else{
				
				ReadonlyTranSession session = tm.openDelayReadTran() ;
				
				try{
					//TODO: add support for many groups
					BindedCompiledSQL bsql = cs.bind("authGroup", a.getAuthGroups()[0]) ;
					bsql.setRowDataLoader(FirstColumnDataLoader.newInstanceForReturnType("int")) ;
					
					List<Integer> cids = session.list(bsql, 1, 50) ;
					
					if(cids.isEmpty()){
						throw new VoteException("您没有被授权管理任何频道") ;
					}			
					
					return new InTerm("channelId", cids) ;
				}finally{
					session.close() ;
				}
			}
		}
		
		return super.explainOtherTypeConditon(mapping, limitTo);
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		tm = guzzContext.getTransactionManager() ;
		
		String sql = "select @id from @@" + Channel.class.getName() + " where @authGroup = :authGroup" ;
		
		cs =  tm.getCompiledSQLBuilder().buildCompiledSQL(Channel.class, sql) ;
	}

	

}
