package mm.smy.bicq.server.manager ;

/**
* 对ICMPMessage的分流处理。
* 
* 
* @author XF
* @date 2003-11-22
* 
*/

import mm.smy.bicq.message.* ;

import mm.smy.bicq.user.Guest ;
import mm.smy.bicq.user.Host ;
import mm.smy.bicq.user.User ;


import mm.smy.bicq.server.user.* ;
import mm.smy.bicq.server.message.* ;

import mm.smy.bicq.server.db.BugWriter ;

import mm.smy.bicq.server.StartServer ;

import java.util.Vector ;
import java.util.Enumeration ;

import java.sql.SQLException ;

import java.net.InetAddress ;

public class ICMPManager{
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	private InetAddress fromIP = null ;
	private int fromPort = 5201 ;
	
	public ICMPManager(StartServer m_ss){
		ss = m_ss ;
		manager	= ss.getOnlineManager() ;
	}
	
	public void messageAction(ReceivedMessage rm){
		if(rm == null) return ;
		if(rm.getType() != MessageType.ICMP_MESSAGE ) return ;
		
		int from = rm.getFrom() ;
		
		//初始化请求者的地址.
		OnlineUser user = manager.getOnlineUser(from) ;
		if(user == null){ //no online record of the online user, Sorry we will not know where to reply, drop it. 
			System.out.println("icmp manager got an user not online:" + from) ;
			return ;
		}else{
			fromIP = user.getIP() ;
			fromPort = user.getPort() ;	
		}
		
		//分析ICMP包。
		ICMPMessage message = new ICMPMessage() ;
		message.setByteContent(rm.getContent()) ;
		System.out.println("IcmpMessage MinType:" + message.getMinType()) ;
		
		if(message.getMinType() == ICMPMessage.LOAD_HOST_INFOR){
			if(message.getContent() != null && message.getContent().trim().length() > 0 )
				loadHostInfor(new Integer(message.getContent().trim()).intValue()) ;
			else
				loadHostInfor(from) ;
			return ;
			
		}else if(message.getMinType() == ICMPMessage.LOAD_SINGLE_GUEST_INFOR){
			if(message.getContent() != null && message.getContent().trim().length() > 0 )
				loadGuestInfor(new Integer(message.getContent().trim()).intValue()) ;
			return ;
		}else if(message.getMinType() == ICMPMessage.LOGIN_TO_SERVER_SUCCESS){
			//登陆成功，请发送所有离线消息。	
			sendLeaveMessage(from) ;
			return ;			
		}else if(message.getMinType() == ICMPMessage.LOAD_ALL_GUESTS){
			loadAllGuests(from) ;	
			return ;
		}else if(message.getMinType() == ICMPMessage.QUIT_BICQ){
			//发送消息给她的好友。
			int quit_temp_number = from ;
			if(message.getContent() != null && message.getContent().length() != 0){
				quit_temp_number = (new Integer(message.getContent()).intValue() ) ;	
			}
			manager.removeOnlineUser(quit_temp_number) ;
			System.out.println("用户：" + quit_temp_number + "成功退出．") ;
			
			//发送用户下线状态改变
			StateChangedManager scm = ss.getStateChangedManager() ;
			StateChangedMessage mess = new StateChangedMessage() ;
			mess.setMinType(User.OFFLINE) ;
			mess.setIsNotify(true) ;
			scm.sendStateChangedMessage(mess,quit_temp_number, true) ;
			
			System.out.println("用户成功退出消息发送给他的好友．") ;
			
			return ;
		}else if(message.getMinType() == ICMPMessage.ADD_FRIEND){
			doAddFriend(message) ;
			return ;
		}else if(message.getMinType() == ICMPMessage.DELETE_FRIEND){
			doDeleteFriend(message) ;
			return ;
		}else{
			System.out.println("*******Error:ICMPMessage Received an unsolve message::mintype:" + message.getMinType() ) ;	
		}	
		
	}
	
	private void doAddFriend(ICMPMessage message){
			System.out.println("添加好友：" + message.getContent()) ;
			String con = message.getContent() ;
			if(con == null ||con.length() < 3){
				return ;			
			}
			int mid = con.indexOf(":") ;
			if( mid == -1) return ;
			String belongs = con.substring(0,mid) ;
			String friends = con.substring(mid + 1, con.length()) ;
			System.out.println("请求者：" + belongs ) ;
			System.out.println("请求加：" + friends ) ;
			
			int belong, friend ;
			try{
				belong = new Integer(belongs.trim()).intValue() ;
				friend = new Integer(friends.trim()).intValue() ;
			}catch(Exception error){
				System.out.println("icmpmanager::doAddFriend.cannot convert number to int.==>" + error.getMessage() ) ;
				return ;	
			}
			
			MyFriendsDBInsert insert = new MyFriendsDBInsert() ;
			insert.setBelongNumber(belong) ;
			insert.setFriendNumber(friend) ;
			try{
				insert.update() ;	
				System.out.println("add friend to database success") ;				
			}catch(SQLException e){
				BugWriter.log(this, e , "添加好友时出错。申请者：" + belong + ", 申请添加用户：" + friend) ;	
			}finally{
				insert.close() ;	
			}
			
			//告诉friend有人把他加为好友。
			OnlineUser user = manager.getOnlineUser(friend) ;
			
			PermitMessage pm = new PermitMessage() ;
			pm.setMintype(PermitMessage.PERMIT_SEND) ;
			
			if(user == null){ //The special user not online, we write this record to the database.
				PermitDBInsert insertpermit = new PermitDBInsert() ;
				insertpermit.setContent(pm.getByteContent()) ;
				insertpermit.setFrom(belong) ;
				insertpermit.setMintype(pm.getType()) ;
				insertpermit.setTo(friend) ;
				try{
					insertpermit.update() ;
				}catch(SQLException e){
					BugWriter.log(this,e,"向数据库中插入PermitMessage失败！") ;
				}finally{
					insertpermit.close() ;
				}
				System.out.println("用户不在线，我们已经把他的PermitMessage写入数据库。") ;
			}else{
				//用户在线，那人不知道~~~，我们转发消息。同时告诉用户消息是从from发出的，而不是server。
				ss.sendMessage(pm.getByteContent(),pm.getType(),belong,friend,user.getIP(),user.getPort()) ;
			}
			
			return ;
		
	}
	
	private void doDeleteFriend(ICMPMessage message){
			System.out.println("删除好友：" + message.getContent()) ;
			String con = message.getContent() ;
			if(con == null ||con.length() < 3){
				return ;			
			}
			int mid = con.indexOf(":") ;
			if( mid == -1) return ;
			String belongs = con.substring(0,mid) ;
			String friends = con.substring(mid + 1, con.length()) ;
			System.out.println("请求者：" + belongs ) ;
			System.out.println("请求删除：" + friends ) ;
			
			int belong, friend ;
			try{
				belong = new Integer(belongs.trim()).intValue() ;
				friend = new Integer(friends.trim()).intValue() ;
			}catch(Exception error){
				System.out.println("icmpmanager::doDeleteFriend.cannot convert number to int.==>" + error.getMessage() ) ;
				return ;	
			}
			
			MyFriendsDBDelete delete = new MyFriendsDBDelete() ;
			try{
				delete.deleteFriend(belong, friend) ;	
			}catch(SQLException e){
				BugWriter.log( this, e , "删除好友时出错。请求者：" + belong + ", 请求删除好友：" + friend ) ;	
			}finally{
				delete.close() ;	
			}
			return ;		
	}
	
	private void loadHostInfor(int from){
		SelectUserDB select = new SelectUserDB() ;
		ServerGuest guest = null ;
		try{
			guest = select.selectByNumber(from) ;	
		}catch(SQLException e){
			select.close() ;
			BugWriter.log(this, e, "读取用户：" + from + " 的资料时出错") ;
			return ;
		}
		select.close() ;
		
		if(guest == null){ //没有该纪录。应该在这儿加入更详细的处理。
			BugWriter.log(this,new Exception("No Such Record By XF"), "用户要求读取用户：" + from + " 的登陆资料，没有遇到问题，可是没有该纪录。") ;
			return ; 
		}
		//发送消息。
		UserInforMessage message = new UserInforMessage() ;
		message.setMinType(UserInforMessage.UPDATE_HOST_INFOR) ;
			Host host = new Host() ;
			host.setAddress(guest.getAddress()) ;
			host.setAuth(guest.getAuth()) ;
			host.setCounty(guest.getCountry()) ;
			host.setExplain(guest.getExplain()) ;
			host.setGender(guest.getGender()) ;
			host.setHomepage(guest.getHomepage()) ;
			host.setLeaveWord(guest.getLeaveWord()) ;
			host.setMail(guest.getMail()) ;
			host.setNickname(guest.getNickname()) ;
			host.setNumber(guest.getNumber()) ;
			host.setPortrait(guest.getPortrait()) ;
			host.setProvince(guest.getProvince()) ;
			host.setRealname(guest.getRealname()) ;
			host.setTelephone(guest.getTelephone()) ;
			host.setZip(guest.getZip()) ;
			
			if(guest.getBirthday() != null){
				host.setYear(guest.getBirthday().getYear()) ;
				host.setMonth(guest.getBirthday().getMonth()) ;
				host.setDay(guest.getBirthday().getDay()) ;
			}
			
			
		message.setUser(host) ;
		
		ss.sendMessage(message.getByteContent(),MessageType.USER_INFOR_MESSAGE,1000,from,fromIP,fromPort) ;
		
		return ;
	}
	
	private void loadGuestInfor(int from){
		SelectUserDB select = new SelectUserDB() ;
		ServerGuest guest = null ;
		try{
			guest = select.selectByNumber(from) ;	
		}catch(SQLException e){
			select.close() ;
			BugWriter.log(this, e, "读取用户：" + from + " 的资料时出错") ;
			return ;
		}
		select.close() ;
		
		if(guest == null){ //没有该纪录。应该在这儿加入更详细的处理。
			BugWriter.log(this,new Exception("No Such Record By XF"), "用户要求读取用户：" + from + " 的登陆资料，没有遇到问题，可是没有该纪录。") ;
			return ; 
		}
		//发送消息。
		UserInforMessage message = new UserInforMessage() ;
		message.setMinType(UserInforMessage.UPDATE_GUEST_INFOR) ;
			Guest host = new Guest() ;
			host.setAddress(guest.getAddress()) ;
			host.setCounty(guest.getCountry()) ;
			host.setExplain(guest.getExplain()) ;
			host.setGender(guest.getGender()) ;
			host.setHomepage(guest.getHomepage()) ;
			host.setLeaveWord(guest.getLeaveWord()) ;
			host.setMail(guest.getMail()) ;
			host.setNickname(guest.getNickname()) ;
			host.setNumber(guest.getNumber()) ;
			host.setPortrait(guest.getPortrait()) ;
			host.setProvince(guest.getProvince()) ;
			host.setRealname(guest.getRealname()) ;
			host.setTelephone(guest.getTelephone()) ;
			host.setZip(guest.getZip()) ;
			
			if(guest.getBirthday() != null){
				host.setYear(guest.getBirthday().getYear()) ;
				host.setMonth(guest.getBirthday().getMonth()) ;
				host.setDay(guest.getBirthday().getDay()) ;		
			}
			
		//我们应改在这儿加入对在线状态的的修改
		OnlineUser user2 = manager.getOnlineUser(host.getNumber()) ;
		if(user2 != null)
			user2.setState(user2.getState()) ;
			
		message.setUser(host) ;
		
		ss.sendMessage(message.getByteContent(),MessageType.USER_INFOR_MESSAGE,1000,from,fromIP,fromPort) ;
		
		return ;		
		
		
		
	}
	
	/**
	* 用户登陆成功了，请求发送所有离线的消息。
	* Text & Permit
	* 
	* @param from 请求的用户。
	* 
	* 
	*********/
	private void sendLeaveMessage(int from){
		System.out.println("========================send left message=================") ;
		
		PermitDBQuery query = new PermitDBQuery() ;
		
		Vector v = new Vector() ;
		Enumeration e = null ;
		
		//处理离线的PermitMessage
		try{
			v = query.selectByNumber(from) ;	
		}catch(SQLException er){
			BugWriter.log(this,er,"检索用户：" + from + " 的离线Permit消息时发生错误。") ;
		}finally{
			query.close() ;	
		}
		
		if(v != null){
			e = v.elements() ;
			while(e.hasMoreElements()){
				ServerPermitMessage pm = (ServerPermitMessage) e.nextElement() ;
				ss.sendMessage(pm.getByteContent(), MessageType.PERMIT_MESSAGE, pm.getFrom(), from, fromIP, fromPort) ;				
			}
		//clear the Vector, release its resource
			v.clear() ;
		}
		
		//处理TextMessage
		TextDBQuery query2 = new TextDBQuery() ;
		try{
			v = query2.selectByNumber(from) ;	
		}catch(SQLException e2){
			BugWriter.log(this, e2, "检索用户：" + from + " 的离线TextMessage时发生错误。" ) ;	
		}finally{
			query2.close() ;
		}
		
		if(v != null){
			e = v.elements() ;
			while(e.hasMoreElements()){
				ServerTextMessage tm = (ServerTextMessage) e.nextElement() ;
				ss.sendMessage(tm.getByteContent(),MessageType.TEXT_MESSAGE, tm.getFrom(), from, fromIP, fromPort) ;				
			}
			v.clear() ;			
		}
		
		//好啦,消息发送完了~~
		//我们删了数据库中的这些东东.
		PermitDBDelete deletepermit = new PermitDBDelete() ;
		try{
			deletepermit.deleteByNumber(from) ;
			System.out.println("Permit记录删除成功。") ;
		}catch(SQLException e4){
			BugWriter.log(this, e4, "删除用户：" + from + " 的离线Permit消息时发生错误。") ;	
		}finally{
			deletepermit.close() ;	
		}
		
		TextDBDelete textpermit = new TextDBDelete() ;
		try{
			textpermit.deleteByNumber(from) ;
			System.out.println("TextMessage 纪录删除成功．") ;	
		}catch(SQLException e2){
			BugWriter.log(this, e2, "删除用户：" + from + " 的离线TextMessage时发生错误。" ) ;	
		}finally{
			textpermit.close() ;
		}
		
		//通知用户在线的好友。
		sendFriendStateOnLogin(manager.getOnlineUser(from)) ;
		
		return ;
	}

	//This method is add one 2004-4-10, no test.	
	private void sendFriendStateOnLogin(OnlineUser ou){
		MyFriendsDBQuery query = new MyFriendsDBQuery() ;
		
		Vector v = new Vector() ; 
		try{
			v = query.selectInOnline(ou.getNumber()) ; //获得from的所有好友number	
		}catch(SQLException ex){
			BugWriter.log(this, ex, "检索用户："  + ou.getNumber() + " 的所有好友号码时发生错误。" ) ;
		}finally{
			query.close() ;	
		}
		
		ServerStateChangedMessage scm = new ServerStateChangedMessage() ;
				
		Enumeration e = v.elements() ;
		while(e.hasMoreElements()){
			int number = ((Integer)e.nextElement()).intValue() ;
			OnlineUser user = manager.getOnlineUser(number) ;
			if(user == null) continue ;
			
			scm.setFrom(user.getNumber()) ;
			scm.setIP(user.getIP()) ;
			scm.setPort(user.getPort()) ;
			scm.setIsNotify(false) ;
			scm.setMinType(user.getState()) ;
			
			ss.sendMessage(scm.getByteContent(),MessageType.STATE_CHANGED_MESSAGE, user.getNumber(),ou.getNumber(),ou.getIP(),ou.getPort()) ;
		}
		System.out.println("用户上线以后他的好的状态已经全部发送了") ;
	}
	
	/**
	* 下载用户的所有好友简短资料。
	* @param from 请求下载其所有好友资料的用户号码。
	* 
	* 
	* 
	*/
	private void loadAllGuests(int from){
		MyFriendsDBQuery query = new MyFriendsDBQuery() ;
		
		Vector v = new Vector() ;
		try{
			v = query.selectInOnline(from) ; //获得from的所有好友number	
		}catch(SQLException ex){
			BugWriter.log(this, ex, "检索用户："  + from + " 的所有好友号码时发生错误。" ) ;
		}finally{
			query.close() ;	
		}
		
		LoadGuestResultMessage message = new LoadGuestResultMessage() ;
		

		if(v != null){
			
			SelectUserDB select = new SelectUserDB() ;		
			ServerGuest guest = null ;
			
			Enumeration e = v.elements() ;
			while(e.hasMoreElements()){
				int friend = ((Integer) e.nextElement()).intValue() ;
			
				//察看该用户是否在线。		
				OnlineUser user = manager.getOnlineUser(friend) ;
				
				if( user == null){ //不在线。
					try{		
						guest = select.selectByNumber(friend) ;
					}catch(SQLException ex){
						BugWriter.log(this, ex, "登陆时，读取好友：" + friend + " 的资料时发生错误。") ;
						continue ;	
					}
					user = new OnlineUser() ;
					
					//情况是这样的，如果我们删除一个用户，可是没有把他在好友表中删除
					//这个时候，这儿的guest将是null~~~。然后发生NullPointException错误
					//我们的做法是：忽略错误，然后把错误写入日志。
					if(guest == null){
						BugWriter.log(this, null, "用户在好友表中无法找到。请求用户：" + user.getNumber() + "；未找到好友：" + friend ) ;	
						continue ;
					}
					
					user.setAuth(guest.getAuth()) ;
					user.setFrom(guest.getProvince()) ;
					user.setGender(guest.getGender()) ;
					user.setNickname(guest.getNickname()) ;
					user.setNumber(guest.getNumber()) ;
					user.setPortrait(guest.getPortrait()) ;
					user.setRecordID(guest.getRecordID()) ;
						
				}
				//添加好友到message中。
				if(user != null){
					message.addTempUser(user) ;	
				}
				
			}
			//释放jdbc资源
			select.close() ;
		}else{
		//用户没有好友，这儿在客户端是个bug，将会引起 网络超时，没有读取到好友的误解。
		//为了解决这个问题，我们在这里面加入服务器，作为用户的好友。
			OnlineUser server = new OnlineUser() ;
			server.setNumber(1000) ;
			server.setNickname("BICQ服务器") ;
			server.setAuth(Host.NO_DISTURB) ;
			message.addTempUser(server) ;
		}
		
		//发送消息给用户。
		ss.sendMessage(message.getByteContent(), MessageType.LOAD_GUEST_RESULT_MESSAGE,1000,from,fromIP,fromPort) ;
		
		
	}
	
	
	
	
	
	
	
	
	
	
}
