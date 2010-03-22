package mm.smy.bicq.server.manager ;

/**
* 对StartServer的分流类之一。
* 控制所有关于PermitMessage的东东。
* 
* 
* @author XF
* @date 2003-11-22
* 
*/

import mm.smy.bicq.server.StartServer ;
import mm.smy.bicq.server.user.* ;
import mm.smy.bicq.server.message.PermitDBInsert ;

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.PermitMessage ;

import mm.smy.bicq.server.db.BugWriter ;

import java.sql.SQLException ;

public class PermitManager{
	
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	private PermitDBInsert insert = null ;
	
	public PermitManager(StartServer m_ss){
		ss = m_ss ;	
		manager = ss.getOnlineManager() ;
	}
	
	//接收用户发来的消息
	public void messageAction(ReceivedMessage rm){
		if( rm == null ) return ;
		
		if(rm.getType() != MessageType.PERMIT_MESSAGE){
			return ;	
		}
				
		if(rm.getTo() <= 0 || rm.getTo() == 1000){ //发送给服务器的消息，我们目前不作处理。
			rm = null ;
			return ;
		}
		
		OnlineUser user = manager.getOnlineUser(rm.getTo()) ;
		if(user == null){ //The special user not online, we write this record to the database.
			insert = new PermitDBInsert() ;
			insert.setContent(rm.getContent()) ;
			insert.setFrom(rm.getFrom()) ;
			insert.setMintype(rm.getType()) ;
			insert.setTo(rm.getTo()) ;
			try{
				insert.update() ;
			}catch(SQLException e){
				BugWriter.log(this,e,"向数据库中插入PermitMessage失败！") ;
			}finally{
				insert.close() ;
			}
			System.out.println("用户不在线，我们已经把他的PermitMessage写入数据库。") ;
		}else{
			//用户在线，那人不知道~~~，我们转发消息。同时告诉用户消息是从from发出的，而不是server。
			ss.sendMessage(rm.getContent(),rm.getType(),rm.getFrom(),rm.getTo(),user.getIP(),user.getPort()) ;
			System.out.println("服务器pm调试：") ;
			System.out.println("用户在线，发送消息到IP:" + user.getIP() ) ;
			System.out.println("端口：" + user.getPort() ) ;
			return ;
		}
		
	}
	
	
	
	
}
