package mm.smy.bicq.server.user ;

/**
* 查找我的好友。针对用户登陆时下载自己好友
* 以及把StateChangedMessage发送给他们做的。
* 
* 
* @author XF
* @date 2003-11-22
* 
* 
* 
* 
*/

import mm.smy.bicq.server.db.* ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;


import java.util.Date ;
import java.util.Vector ;

public class MyFriendsDBQuery{
	private PreparedStatement onlinesearch = null ;
	private PreparedStatement statesearch = null ;
	
	private ReadonlyStatement ro = null ;
	private ResultSet rs = null ;
	
	public MyFriendsDBQuery(){
		
	}
	
	/**
	* 用户状态改变时通知其好友。
	* 该函数获得把她加为好友的用户的number集合。
	* @param number 改变状态的用户的number
	* @return Vector集合。把number加为好友的用户的number[new Integer(number)]集合。
	* 
	* 
	*/
	public Vector selectInState(int number) throws SQLException{
		if(ro == null)
			ro = new ReadonlyStatement("friend") ;
		if(statesearch == null)
			statesearch = ro.getPreparedStatement("select belongnumber from friend where friendnumber = ? ") ;
		else
			statesearch.clearParameters() ;
		statesearch.setInt(1,number) ;
		
		
		rs = statesearch.executeQuery() ;
		if(rs == null) return null ;
		
		Vector v = new Vector() ;
		
		while(rs.next()){
				v.add(new Integer(rs.getInt(1))) ;	
		}
		rs.close() ;
		return v ; 
	}
	
	/**
	* 用户登陆时检索用户的好友。
	* @param number 用户的number
	* @return Vector 用户的好友的m_number构成的new Integer(m_number)集合。
	*/
	public Vector selectInOnline(int number) throws SQLException{
		if(ro == null)
			ro = new ReadonlyStatement("friend") ;
		if(onlinesearch == null)
			onlinesearch = ro.getPreparedStatement("select friendnumber from friend where belongnumber = ? ") ;
		else
			onlinesearch.clearParameters() ;
		onlinesearch.setInt(1,number) ;
		
		rs = onlinesearch.executeQuery() ;
		if(rs == null) return null ;
				
		Vector v = new Vector() ;
		while(rs.next()){
			v.add(new Integer(rs.getInt(1))) ;	
		}
		
		rs.close() ;
		
		return v ;
	}
	
	public void close(){
		if(ro != null)
			ro.close() ;
		if(onlinesearch != null){
			try{
				onlinesearch.close() ;
			}catch(Exception e){
			}
		}
		if(statesearch != null){
			try{
				statesearch.close() ;	
			}catch(Exception e){
			}
		}
		return ;
	}


		
}
