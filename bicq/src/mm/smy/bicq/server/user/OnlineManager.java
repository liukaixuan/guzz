package mm.smy.bicq.server.user ;

/**
* 提供对在线好友的管理。
*
*
*
*
*
*/
import java.util.* ;

public class OnlineManager{
	
	private Hashtable onlines = new Hashtable() ; //number VS OnlineUser. 很明显这儿应该用AVL Tree，以后改写。
	
	
	public OnlineManager(){
		
	}
	
	public void addOnlineUser(OnlineUser user){
		if(user == null) return ;
		onlines.put(new Integer(user.getNumber()),user) ;
		return ;
	}
	
	public void removeOnlineUser(int number){
		onlines.remove(new Integer(number)) ;
		return ;	
	}
	
	public OnlineUser getOnlineUser( int number){
		return (OnlineUser) onlines.get(new Integer(number)) ;
	}
	
	public boolean isExsit(OnlineUser user){
		return onlines.containsKey(new Integer(user.getNumber())) ;	
	}
	
	/**
	* 返回随机产生的OnlineUser[]对象。
	* @param num 返回的个数，可能会不足要求的个数，因为会考虑到效率的问题。
	*/
	public OnlineUser[] getRandomOnlineUser(int num){
		if(num <= 0 ) return new OnlineUser[0] ;
		if(num >= onlines.size())
			return (OnlineUser[]) onlines.values().toArray(new OnlineUser[0]) ;
		
		//我们按照顺序返回，并不是随机的^_^		
		OnlineUser[] t = new OnlineUser[num] ;
		
		Vector v = new Vector(onlines.values()) ;
		
		try{
			for(int i = 0 ; i < num ; i++){
				t[i] = (OnlineUser) v.get(i) ;			
			}
		}catch(Exception e){ //可以在查找的同时有用户下线，导致越界。
			//
		}
		return t ;		
	}
	
	
}
