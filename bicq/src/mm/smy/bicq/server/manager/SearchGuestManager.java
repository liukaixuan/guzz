package mm.smy.bicq.server.manager ;

/**
* 对StartServer的分流类之一。
* 控制所有关于SearchGuestMessage的东东。
* 
* 
* @author XF
* @date 2003-11-24
* 
*/

import mm.smy.bicq.server.StartServer ;
import mm.smy.bicq.server.user.* ;

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.SearchGuestMessage ;
import mm.smy.bicq.message.SearchGuestResultMessage ;

import mm.smy.bicq.server.user.SelectUserDB ;

import mm.smy.bicq.server.db.BugWriter ;

import java.util.Vector ;
import java.util.Enumeration ;

import java.sql.SQLException ;

public class SearchGuestManager{
	
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	public SearchGuestManager(StartServer m_ss){
		ss = m_ss ;	
		manager = ss.getOnlineManager() ;
	}
	
	//接收用户发来的消息
	public void messageAction(ReceivedMessage rm){
		if( rm == null ) return ;
		
		if(rm.getType() != MessageType.SEARCH_GUEST_MESSAGE){
			return ;	
		}
		
		//The requested from is not online. we just return.
		OnlineUser user = manager.getOnlineUser(rm.getFrom()) ;
		if(user == null){
			System.out.println("SearchGuestManager 收到一个请求,可是用户根本不在线.from:" + rm.getFrom()) ;
			return ;
		}
		
		//这个作为下面使用的临时查找好友在线资料的通用引用。
		OnlineUser user2 = null ;
	
		SearchGuestMessage sgm = new SearchGuestMessage() ;
		sgm.setByteContent(rm.getContent()) ;
		
		//返回给用户的消息
		SearchGuestResultMessage sgrm = new SearchGuestResultMessage() ;
		sgrm.setFlag(sgm.getFlag()) ;
		sgrm.setStartPos(sgrm.getStartPos()) ;
		
		SelectUserDB query = new SelectUserDB() ;
		
		//search friends online
		if(sgm.getMinType() == SearchGuestMessage.SEARCH_ONLINE){
			
			OnlineUser[] ous = manager.getRandomOnlineUser(10) ;
			
			if(ous != null){
				for(int i = 0 ; i < ous.length ; i++){
					sgrm.addTempUser(ous[i]) ;
				}
			}
			
			System.out.println("search online is not finished.....") ;
		}else if(sgm.getMinType() == SearchGuestMessage.SEARCH_BY_NUMBER){
			ServerGuest guest = null ;
			
			try{
				guest = query.selectByNumber(sgm.getNumber()) ;
			}catch(SQLException error){
				BugWriter.log(this, error, "查找好友：" + sgm.getNumber() + " 时发生错误。" ) ;
			}finally{
				query.close() ;	
			}
			
			
			OnlineUser u = new OnlineUser() ;
			if(guest != null){
				u.setAuth(guest.getAuth()) ;
				u.setFrom(guest.getProvince()) ;
				u.setGender(guest.getGender()) ;
				u.setNickname(guest.getNickname()) ;
				u.setNumber(guest.getNumber()) ;
				u.setPortrait(guest.getPortrait()) ;
				
				user2 = manager.getOnlineUser(u.getNumber()) ;
				if(user2 != null){
					u.setIP(user2.getIP()) ;	
					u.setPort(user2.getPort()) ;
					u.setState(user2.getState()) ;
				}
			}
			sgrm.addTempUser(u) ;
		}else if(sgm.getMinType() == SearchGuestMessage.SEARCH_BY_NICKNAME){
			Vector v = null ;
			
			try{
				v = query.selectByNickname(sgm.getNickname(),10,sgm.getStartPos()) ;	
			}catch(SQLException error){
				BugWriter.log(this, error, "查找好友：" + sgm.getNickname() + " 开始位置:" + sgm.getStartPos() + " 时发生错误。" ) ;
			}finally{
				query.close() ;	
			}
			
			//我们看看找到的用户，如果在线的话，加入IP,port,state。否则不作处理。			
			if(v != null){
				Enumeration e_nickname = v.elements() ;
				while(e_nickname.hasMoreElements()){					  
					OnlineUser nickname_ou = (OnlineUser) e_nickname.nextElement() ;	
					user2 = manager.getOnlineUser(nickname_ou.getNumber()) ;
					if(user2 != null){ //online
						nickname_ou.setIP(user2.getIP()) ;
						nickname_ou.setPort(user2.getPort()) ;
						nickname_ou.setState(user2.getState()) ;
					}
					sgrm.addTempUser(nickname_ou) ;
				}				
			}
		}else if(sgm.getMinType() == SearchGuestMessage.SEARCH_BY_GFA){
			Vector v2 = null ;
			
			try{
				v2 = query.selectByGFA(sgm.getGender(),sgm.getProvince(),sgm.getAgeFrom(),sgm.getAgeTo(),10,sgm.getStartPos()) ;
			}catch(SQLException error){
				BugWriter.log(this, error, "查找gfa性别：" + sgm.getGender() + " 来自：" + sgm.getProvince() + " 年龄下：" +  sgm.getAgeFrom() + " 年龄上：" + + sgm.getAgeTo() + " 开始位置:" + sgm.getStartPos() + " 时发生错误。" ) ;
			}finally{
				query.close() ;	
			}
			
			//我们看看找到的用户，如果在线的话，加入IP,port,state。否则不作处理。			
			if(v2 != null){
				Enumeration e_gfa = v2.elements() ;
				while(e_gfa.hasMoreElements()){					  
					OnlineUser gfa_ou = (OnlineUser) e_gfa.nextElement() ;	
					user2 = manager.getOnlineUser(gfa_ou.getNumber()) ;
					if(user2 != null){ //online
						gfa_ou.setIP(user2.getIP()) ;
						gfa_ou.setPort(user2.getPort()) ;
						gfa_ou.setState(user2.getState()) ;
					}
					sgrm.addTempUser(gfa_ou) ;
				}			
			}
		}else{ //error detected.
			System.out.println("SearchGuestManager发现错误。检测到无效的查找类型：" + sgm.getMinType() ) ;			
		}
		
		//打包数据，然后发送出去。
		
		ss.sendMessage(sgrm.getByteContent(), sgrm.getType(), 1000, user.getNumber(), user.getIP(), user.getPort()) ;
				
		System.out.println("用户请求的查找好友消息成功发出。") ;
		
	}
	
	
	
	
}
