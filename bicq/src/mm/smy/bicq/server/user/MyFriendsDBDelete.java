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

public class MyFriendsDBDelete{
	
	private ReadWriteStatement rw = null ;
	private PreparedStatement delete = null ;
	static String sql = "delete from friend where belongnumber = ? and friendnumber = ? " ;
	
	public MyFriendsDBDelete(){
		
	}
	
	/**
	* 删除好友
	* @param belongnumber 好友所属用户号码
	* @param friendnumber 好友的号码
	* @return 执行后影响的记录数。
	* @throws SQLException
	*/
	public int deleteFriend(int belongnumber, int friendnumber) throws SQLException{
		if(rw == null)
			rw = new ReadWriteStatement("friend") ;
		if(delete == null)
			delete = rw.getPreparedStatement(sql) ;
		else
			delete.clearParameters() ;
			
		delete.setInt(1, belongnumber) ;
		delete.setInt(2, friendnumber) ;
		
		return delete.executeUpdate() ;
	}
	
	
	public void close(){
		if(rw != null)
			rw.close() ;
		if(delete != null){
			try{
				delete.close() ;
			}catch(Exception e){
			}
		}
	}

}
