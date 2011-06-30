/**
 * VoteException.java created at 2009-9-21 下午05:30:06 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.exception;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteException extends RuntimeException{

	public VoteException(){
		super() ;
	}
	
	public VoteException(String msg){
		super(msg) ;
	}
	
}
