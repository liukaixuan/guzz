/**
 * 
 */
package org.guzz.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.guzz.dao.GuzzBaseDao;
import org.guzz.dao.GuzzWriteCallback;
import org.guzz.exception.GuzzException;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.test.User;
import org.guzz.transaction.WriteTranSession;
import org.springframework.transaction.TransactionDefinition;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class UserDaoImpl extends GuzzBaseDao implements IUserDao{
	
	public User getById(int id){
		return (User) super.getForUpdate(User.class, id) ;
	}

	public void insert(User u) {
		super.insert(u) ;
	}

	public void update(User u) {
		super.update(u) ;
	}

	public void delete(User u) {
		super.delete(u) ;
	}
		
	public void insertFailed(User u) {
		this.insert(u) ;
		throw new RuntimeException("failed on expect!") ;
	}
	
	public long countAll(){
		SearchExpression se = SearchExpression.forClass(User.class) ;
		
		return super.count(se) ;
	}
		
	public List getIso() {
		return this.getWriteTemplate().executeWrite(
				new GuzzWriteCallback<List>() {

					public List doWrite(WriteTranSession session) throws GuzzException, SQLException {
						LinkedList result = new LinkedList() ;
						Connection conn = session.createJDBCTemplate(User.class).getConnection() ;
						
						Assert.assertFalse(conn.getAutoCommit()) ;
						Assert.assertEquals(conn.getTransactionIsolation(), TransactionDefinition.ISOLATION_SERIALIZABLE) ;
												
						result.add(conn) ;
						
						return result;
					}
				}
		) ;
	}
	
	public void insertTimeout(User u) throws Exception {
		this.insert(u) ;
		
		synchronized(this){
			this.wait(10 * 1000) ;
		}
		
		this.getById(1) ;
	}
	
	public void clearTable(){
		final CompiledSQL cs = this.getTransactionManager().getCompiledSQLBuilder()
							.buildCompiledSQL(User.class, "truncate table @@" + User.class.getName()) ;
		
		this.getWriteTemplate().executeWrite(new GuzzWriteCallback<Integer>() {

			public Integer doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.executeUpdate(cs.bindNoParams()) ;
			}
			
		}) ;
		
	}	

	public User findByUserName(String userName) {
		SearchExpression se = SearchExpression.forClass(User.class) ;
		se.and(Terms.eq("userName", userName)) ;
		
		return (User) super.findObject(se) ;
	}

}
