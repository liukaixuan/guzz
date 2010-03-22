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

import mm.smy.bicq.server.db.ReadWriteStatement ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;

import java.util.Date ;

public class MyFriendsDBInsert{
	
	private ReadWriteStatement rw = null ;
	private PreparedStatement insert = null ;
	static String sql = "insert into friend (friendnumber, belongnumber, addtime) values(?,?,?)" ;
	
	private int belong = -1 ;
	private int friend = -1 ;
	private long addtime  = Long.MIN_VALUE ;
	
	public MyFriendsDBInsert(){
		
	}
	
	/**
	* @return PreparedStatement.executeUpdate()
	*/
	public int update() throws SQLException{
		if(belong < 0 || friend < 0 ){
			throw new SQLException("参数错误：在MyFriendsDBInsert中传入的参数错误。belongnumber:" + belong + ", friendnumber:" + friend ) ;	
		}
		
		if(rw == null)
			rw = new ReadWriteStatement("friend") ;
		if(insert == null)
			insert = rw.getPreparedStatement(sql) ;
		else
			insert.clearParameters() ;
		insert.setInt(1,friend) ;
		insert.setInt(2,belong) ;
		insert.setLong(3, (addtime != Long.MIN_VALUE)?addtime:new Date().getTime() ) ;
		
		return insert.executeUpdate() ;
	}
	
	public void setBelongNumber(int m_number){
		belong = m_number ;	
	}
	
	public void setFriendNumber(int m_number){
		friend = m_number ;	
	}
	
	public void setAddTime(long m_time){
		addtime = m_time ;	
	}
	
	public long getAddTime(){
		return addtime ;	
	}
	
	public int getBelongNumber(){
		return belong ;	
	}
	
	public int getFriendNumber(){
		return friend ;	
	}
	
	public void close(){
		if(rw != null)
			rw.close() ;
		if(insert != null){
			try{
				insert.close() ;
			}catch(Exception e){
			}
		}
	}


		
}
