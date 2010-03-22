package mm.smy.bicq.server.manager ;

/**
* 对StartServer的分流类之一。
* 控制所有关于ServerTextMessage的东东。
* 
* 
* @author XF
* @date 2003-11-21
* 
*/

import mm.smy.bicq.server.StartServer ;
import mm.smy.bicq.server.user.* ;
import mm.smy.bicq.server.message.* ;

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.StateChangedMessage ;

import mm.smy.bicq.server.db.BugWriter ;

import java.sql.SQLException ;

public class TextManager{
	
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	private TextDBInsert insert = null ;
	
	public TextManager(StartServer m_ss){
		ss = m_ss ;	
		manager = ss.getOnlineManager() ;
	}
	
	//接收用户发来的消息
	public void messageAction(ReceivedMessage rm){
		if( rm == null ) return ;
		
		if(rm.getType() != MessageType.TEXT_MESSAGE){
			return ;	
		}
		
		System.out.println("debug: got an text manager to:" + rm.getTo() ) ;
				
		if(rm.getTo() <= 0 || rm.getTo() == 1000){ //发送给服务器的消息，我们目前不作处理。
			rm = null ;
			return ;
		}
		
		
		OnlineUser user = manager.getOnlineUser(rm.getTo()) ;
		if(user == null){ //The special user not online, we write this record to the database.
			insert = new TextDBInsert() ;
			insert.setContent(rm.getContent()) ;
			insert.setFrom(rm.getFrom()) ;
			insert.setMintype(rm.getType()) ;
			insert.setTo(rm.getTo()) ;
			try{
				System.out.println("update result:" + insert.update() ) ;
			}catch(SQLException e){
				BugWriter.log(this,e,"向数据库中插入TextMessage失败！") ;
			}finally{
				insert.close() ;
			}
			System.out.println("debug: user not online ,we add the data to the database. finished success") ;
		}else{
		//用户在线，那人不知道~~~，可能是网络传送失败。我们直接转发，然后给发送者发送一个 秘密的StateChangedMessage(server)	
			ss.sendMessage(rm.getContent(),rm.getType(),rm.getFrom(),rm.getTo(),user.getIP(),user.getPort()) ;
			
			//获得发送用户的Online infor
			user = manager.getOnlineUser(rm.getFrom()) ;
			if(user == null){ //some thing error.
				BugWriter.log(this, new Exception("状态错误"), "收到用户：" + rm.getFrom() + " 的消息，可用户不在线。" ) ;
				return ;
			}
			
			StateChangedMessage scm = new StateChangedMessage() ;
			
			scm.setIP(user.getIP()) ;
			scm.setPort(user.getPort()) ;
			scm.setIsNotify(false) ;
			
			ss.sendMessage(scm.getByteContent(),MessageType.STATE_CHANGED_MESSAGE, 1000, rm.getFrom() ,user.getIP(), user.getPort()) ;
			
			System.out.println("debug:user online, we has resend the message to her/him.") ;
			return ;
		}
		
	}
	
	
	
	
}
