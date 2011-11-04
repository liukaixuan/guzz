/**
 * 
 */
package org.guzz.dao;

import java.util.List;

import org.guzz.test.User;


/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IUserDao {
	
	public User getById(int id) ;
	
	public void insert(User u) ;
	
	public void update(User u) ;
	
	public void delete(User u) ;
	
	public long countAll() ;
	
	public void insertFailed(User u) ;
		
	public void clearTable() ;
	
	public List getIso() ;
	
	public void insertTimeout(User u) throws Exception ;
	
	public User findByUserName(String userName) ;

}
