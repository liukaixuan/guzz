/**
 * Assert.java created at 2009-9-22 上午09:23:26 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.util;

import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.exception.VoteException;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class VoteAssert {
	
	public static void assertNotNull(BigVote vote, String errorMsg) throws VoteException{
		if(vote == null){
			throw new VoteException(errorMsg) ;
		}
	}
	
	public static void assertNotNull(Object obj, String errorMsg) throws VoteException{
		if(obj == null){
			throw new VoteException(errorMsg) ;
		}
	}
	
	public static void assertBigger(int value, int toCompare, String errorMsg) throws VoteException{
		if(value <= toCompare){
			throw new VoteException(errorMsg) ;
		}
	}
	
	public static void assertTrue(boolean value, String errorMsg) throws VoteException{
		if(!value){
			throw new VoteException(errorMsg) ;
		}
	}
	
	public static void assertFalse(boolean value, String errorMsg) throws VoteException{
		if(value){
			throw new VoteException(errorMsg) ;
		}
	}

	public static void assertNotNull(VoteItem item, String errorMsg) {
		if(item == null){
			throw new VoteException(errorMsg) ;
		}
	}

}
