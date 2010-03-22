package mm.smy.bicq.server.manager ;

/**
* 对StateChangedMessage的处理。该部分应该比较麻烦一点。
* 
* 
* 
* @author XF
* @date 2003-11-22
* 
* 
*/

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;

import mm.smy.bicq.server.StartServer ;

import mm.smy.bicq.server.user.OnlineManager ;
import mm.smy.bicq.server.user.OnlineUser ;
import mm.smy.bicq.server.user.MyFriendsDBQuery ;

import mm.smy.bicq.server.db.BugWriter ;

import mm.smy.bicq.message.StateChangedMessage ;

import java.util.Vector ;
import java.util.Enumeration ;

import java.sql.SQLException ;

import mm.smy.bicq.server.user.SelectUserDB ;

public class StateChangedManager{
	
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	public StateChangedManager(StartServer m_ss){
		ss = m_ss ;
		manager = ss.getOnlineManager() ;
	}
	
	
	/**
	* 完全用于消息转发
	* 并且不发送好友的IP/port
	*/
	public void messageAction(ReceivedMessage rm){
		if(rm == null) return ;
		
		if(rm.getType() != MessageType.STATE_CHANGED_MESSAGE) return ;
		
		
		if(rm.getFrom() <= 0) return ;
		
		//首先修改好友在服务器上的状态。
		OnlineUser u = manager.getOnlineUser(rm.getFrom()) ;
		if(u == null){
			//不在我们的数据库中，用户可能在登陆时网络消息发送发生错误；
			//也可能是由破解版的BICQ出现。并且没有进行身份验证而登陆了。
			//目前我们返回。
			return ;
		}	
			
		StateChangedMessage scm = new StateChangedMessage() ;
		scm.setByteContent(rm.getContent()) ;
		u.setState(scm.getMinType()) ;
		
		//我们不管她想发送给谁，我们发送给所有把她加为好友的人。
		MyFriendsDBQuery query = new MyFriendsDBQuery() ;
		Vector v = null ;
		
		try{
			v = query.selectInState(rm.getFrom()) ;	
		}catch(SQLException e){			
			BugWriter.log(this,e,"察看把用户：" + rm.getFrom() + "加为好友的用户时出现异常。") ;
		}finally{
			query.close() ;	
		}
		
		if(v == null) return ;
		
		//给她所有的在线的把她加为好友的用户发送消息。
		Enumeration e = v.elements() ;
		while(e.hasMoreElements()){
			int to = ((Integer) e.nextElement()).intValue() ;
			OnlineUser user = manager.getOnlineUser(to) ;
			if(user != null){
				System.out.println("state chagned message send to one person.") ;
				ss.sendMessage(rm.getContent(),rm.getType(),rm.getFrom(),to,user.getIP(),user.getPort() ) ;
			}			
		}
		
		//删除v
		v.clear() ;
		v = null ;
		return ;
	}
	
	/**
	* 发送statechangedmessage给用户所有好友
	* 包含用户的IP/port
	* 如果from不在线的话，直接返回。不发送任何消息。
	* @param scm  要发送的StateChangedMessage
	* @param from 状态改变的用户number
	* @param appendIP 是否追加IP/port到scm中
	*/
	public void sendStateChangedMessage( StateChangedMessage scm, int from, boolean appendIP){
		if(scm == null) return ;
		
		if(appendIP){
			OnlineUser user = manager.getOnlineUser(from) ;
			if(user == null) return ; //not online
			scm.setIP(user.getIP()) ;
			scm.setPort(user.getPort()) ;
		}
		//我们不管她想发送给谁，我们发送给所有把她加为好友的人。
		MyFriendsDBQuery query = new MyFriendsDBQuery() ;
		Vector v = null ;
		
		try{
			v = query.selectInState(from) ;	
		}catch(SQLException e){			
			BugWriter.log(this,e,"察看把用户：" + from + "加为好友的用户时出现异常。") ;
		}finally{
			query.close() ;	
		}
		
		if(v == null) return ;
		
		//给她所有的在线的把她加为好友的用户发送消息。
		Enumeration e = v.elements() ;
		while(e.hasMoreElements()){
			int to = ((Integer) e.nextElement()).intValue() ;
			OnlineUser u = manager.getOnlineUser(to) ;
			if(u != null){
				ss.sendMessage(scm.getByteContent(), MessageType.STATE_CHANGED_MESSAGE, from, to, u.getIP(), u.getPort() ) ;
			}			
		}
		
		//删除v
		v.clear() ;
		v = null ;
		
		System.out.println("state changed message&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&") ;
		
		return ;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}

