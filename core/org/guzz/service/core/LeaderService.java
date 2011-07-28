/**
 * 
 */
package org.guzz.service.core;

/**
 * 
 * Leader Election Service.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface LeaderService {
	
	/**
	 * Is this machine the leader in the cluster?
	 */
	public boolean amILeader() ;

}
