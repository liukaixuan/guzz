package mm.smy.bicq.server.manager ;

/**
* 服务器消息分流之一。处理UserInforMessage
* 用于用户更新自己的资料。用户只能更改自己的资料，如果要更改的用户不在线（服务器无在线资料）。
* 那么该操作非法，返回错误消息。
* 
* @author XF
* @date 2003-11-24
* @copyright Copyright 2003 XF All Rights Reserved
*/

import mm.smy.bicq.server.StartServer ;

import mm.smy.bicq.server.db.BugWriter ;

import mm.smy.bicq.server.user.UpdateUserDB ;
import mm.smy.bicq.server.user.OnlineManager ;
import mm.smy.bicq.server.user.OnlineUser ;
import mm.smy.bicq.server.user.ServerGuest ;

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.UserInforMessage ;
import mm.smy.bicq.message.ICMPMessage ;

import mm.smy.bicq.user.Host ;

import java.sql.SQLException ;

public class UserInforManager{
	
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	
	public UserInforManager(StartServer m_ss){
		ss = m_ss ;
		manager = ss.getOnlineManager() ;
	}
	
	public void messageAction(ReceivedMessage rm){
		if(rm == null) return ;	
		if(rm.getType() != MessageType.USER_INFOR_MESSAGE) return ;
		
		OnlineUser user = manager.getOnlineUser(rm.getFrom()) ;
		if(user == null){ //user not log online
			//用户没有登陆，我们不知道他的地址~~~~，just return.
			System.out.println("UserInforManager Reports: 收到一个更新个人资料消息，可用户更本不在线！发送消息的用户为：" + rm.getFrom() ) ;
			return ;
		}
		
		ICMPMessage icmp = new ICMPMessage() ;
		icmp.setMinType(ICMPMessage.UPDATE_HOST_INFOR_RESULT) ;
		
		UserInforMessage message = new UserInforMessage() ;
		message.setByteContent(rm.getContent()) ;
		
		Host host = null ;
		try{
			host = (Host) message.getUser() ;
		}catch(Exception e){
			System.out.println("UserInforManager Reports: 转化成Host时出错。来自用户：" + rm.getFrom() ) ;

			icmp.setContent(new Integer(2).toString()) ;
			ss.sendMessage(icmp.getByteContent(), icmp.getType(), 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
			return ;
		}
		
		System.out.println("host:" + host) ;
		System.out.println("user in message:" + message.getUser() ) ;
		
		if(host == null) return ;
		
		ServerGuest guest = new ServerGuest() ;
		guest.setUser(message.getUser()) ;
		//在这儿加入host所特有的属性。
		guest.setAuth(host.getAuth()) ;
		
		guest.setNumber(rm.getFrom()) ; //我们重新定向，免得用户修改他人资料！！
		
		boolean success = false ;
		UpdateUserDB update = new UpdateUserDB(guest) ;
		try{
			update.update() ;
			success = true ;
		}catch(SQLException e){
			BugWriter.log(this, e, "更新用户资料时出错，用户：" + guest.getNumber() ) ;
		}finally{
			update.close() ;
		}
		
		if(success){
			icmp.setContent(new Integer(1).toString()) ;
			//修改用户在内存中的资料。
			user.setAuth(host.getAuth()) ;
			user.setFrom(host.getProvince()) ;
			user.setGender(host.getGender()) ;
			user.setNickname(host.getNickname()) ;
			user.setPortrait(host.getPortrait()) ;
		}else{
			icmp.setContent(new Integer(4).toString()) ;
		}
		
		ss.sendMessage(icmp.getByteContent(), icmp.getType(), 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
		return ;
	}
	
	
	
	
	
	
	
	
	
}
