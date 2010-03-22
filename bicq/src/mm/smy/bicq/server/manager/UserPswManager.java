package mm.smy.bicq.server.manager ;

/**
* 对UserPswMessage的处理。
* 
* 
* @author XF
* @date 2003-11-22
* 
* 
*/

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.ReceivedMessage ;
import mm.smy.bicq.message.UserPswMessage ;

import mm.smy.bicq.server.StartServer ;

import mm.smy.bicq.server.user.OnlineManager ;
import mm.smy.bicq.server.user.OnlineUser ;
import mm.smy.bicq.server.user.SelectUserDB ;
import mm.smy.bicq.server.user.UpdateUserDB ;
import mm.smy.bicq.server.user.ServerGuest ;

import mm.smy.bicq.server.db.BugWriter ;

import mm.smy.bicq.message.StateChangedMessage ;

import java.util.Vector ;
import java.util.Enumeration ;

import java.sql.SQLException ;


public class UserPswManager{
	private StartServer ss = null ;
	private OnlineManager manager = null ;
	
	public UserPswManager(StartServer m_ss){
		ss = m_ss ;
		manager = ss.getOnlineManager() ;
	}
	
	
	public void messageAction(ReceivedMessage rm){
		if(rm == null) return ;
		if(rm.getType() != MessageType.USER_PSW_MESSAGE) return ;
		
		UserPswMessage message = new UserPswMessage() ;	
		message.setByteContent(rm.getContent()) ;
		
		if(message.getMinType() == UserPswMessage.LOGIN_REQUEST){
			doLogin(rm, message) ;
			return ;	
		}else if(message.getMinType() == UserPswMessage.MODIFY_PSW_REQUEST){
			doModify(rm, message) ; //修改密码。	
			return ;
		}else{
			System.out.println("UserPswManager: 收到一个无法处理的消息类型：" + message.getMinType()) ;
			return ;
		}
		
	}
	
	/**
	* 处理登陆请求
	* @param rm 原消息
	* @param message 原消息经过处理后的UserPswMessage消息
	* 我们把两个都传过来是为了节省一次在messageAction(。。)中已经处理的消息转换。
	*
	*
	*****/
	private void doLogin(ReceivedMessage rm, UserPswMessage message){
			SelectUserDB select = new SelectUserDB() ;
			ServerGuest guest = null ;
			try{
				guest = select.selectByNumber(new Integer(message.getExplain()).intValue()) ;
			}catch(SQLException e){
				BugWriter.log(this, e, "在登陆时，检索用户：" + rm.getFrom() + " 出现错误") ;
			}finally{
				select.close() ;	
			}
			
			UserPswMessage upm = new UserPswMessage() ;	
			//用户不存在。
			if(guest == null){
				upm.setMinType(UserPswMessage.LOGIN_FAILED) ;
				upm.setExplain("用户不存在 或是 服务器数据库系统出错。") ;
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), rm.getIP(), message.getPort() ) ;
				return ;
			}
			
			if(message.getPassword() == null){
				message.setPassword("") ;
			}
			
			if(!message.getPassword().equals(guest.getPassword())){
				upm.setMinType(UserPswMessage.LOGIN_FAILED) ;
				upm.setExplain("密码错误，请注意大小写。") ;
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), rm.getIP(), message.getPort() ) ;
				return ;
			}
			
			//察看该号是否已经登陆了
			OnlineUser user = manager.getOnlineUser(rm.getFrom()) ;	
			
			//注意：把下面的if语句划了以后就可以强制的登陆了。
/*								
			if(user != null){ //已经登陆了，一个号在申请多次登陆~~~。我们拒绝第二次的登陆。
				upm.setMinType(UserPswMessage.LOGIN_FAILED) ;
				upm.setExplain("指定账号已经登陆，登陆IP：" + user.getIP()) ;
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), rm.getIP(), message.getPort() ) ;
				return ;
			}
*/			
			//登陆成功，写入online中
			if(user == null)
				user = new OnlineUser() ;
			user.setAuth(guest.getAuth()) ;
			user.setFrom(guest.getProvince()) ;
			user.setGender(guest.getGender()) ;
			user.setIP(rm.getIP()) ;
			user.setPort(message.getPort()) ;
			user.setPortrait(guest.getPortrait()) ;
			user.setRecordID(guest.getRecordID()) ;
			user.setState(mm.smy.bicq.user.User.ONLINE) ;
			user.setNickname(guest.getNickname()) ;
			user.setNumber(guest.getNumber()) ;
			manager.addOnlineUser(user) ;
			
			//告诉用户登陆成功
			upm.setMinType(UserPswMessage.LOGIN_SUCCESS) ;
			ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
			System.out.println("登陆成功，成功消息已发送") ;
			
			//给把她加为好友的人发送StateChangedMessage
			StateChangedMessage scm = new StateChangedMessage() ;
			scm.setIP(user.getIP()) ;
			scm.setPort(user.getPort()) ;
			scm.setIsNotify(true) ;
			scm.setMinType(mm.smy.bicq.user.User.ONLINE) ;
			
			ss.getStateChangedManager().sendStateChangedMessage(scm, user.getNumber(), false) ;
			return ;
	}
	
	private void doModify(ReceivedMessage rm, UserPswMessage message){
			OnlineUser user = manager.getOnlineUser(rm.getFrom()) ;
			
			if(user == null){ //not login in
				//upm.setMinType(UserPswMessage.LOGIN_FAILED) ;
				//upm.setExplain("无法修改密码，用户：" + rm.getFrom() + " 尚未登陆！") ;
				//没有port，无法发送。我们忽略掉用户的请求~~~~。
				System.out.println("UserPswManager::无法修改密码，用户：" + rm.getFrom() + " 尚未登陆！") ;
				return ;
			}
			
			//查找用户在数据库中的资料
			SelectUserDB select = new SelectUserDB() ;
			ServerGuest guest = null ;
			try{
				guest = select.selectByNumber(rm.getFrom()) ;
			}catch(SQLException e){
				BugWriter.log(this, e, "在登陆时，检索用户：" + rm.getFrom() + " 出现错误") ;
			}finally{
				select.close() ;	
			}
			
//			System.out.println("oldpassword:" + message.getPassword()) ;
//			System.out.println("newpassword:" + message.getNewPassword()) ;
//			System.out.println("database:" + guest.getPassword()) ;
				
			UserPswMessage upm = new UserPswMessage() ;	
			//用户不存在。
			if(guest == null){
				upm.setMinType(UserPswMessage.MODIFY_PSW_FAILED) ;
				upm.setExplain("服务器数据库系统出错。在数据库中找不到用户：" + rm.getFrom()) ;
				BugWriter.log(this, new Exception("没有记录"), "我们已经在OnlineManager中找到了用户，可是在数据库中没有她的纪录。用户为：" + rm.getFrom() ) ; 
				
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
				return ;
			}
			if(message.getNewPassword() == null || message.getNewPassword().length() == 0){
				upm.setMinType(UserPswMessage.MODIFY_PSW_FAILED) ;
				upm.setExplain("修改密码失败，密码不能为空！") ;
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
				return ;
				
			}
			if(message.getPassword() == null || !message.getPassword().equals(guest.getPassword())){
				upm.setMinType(UserPswMessage.MODIFY_PSW_FAILED) ;
				upm.setExplain("修改失败：密码错误，请注意大小写。") ;
				ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
				return ;
			}
			
			//好啦，我们把新的密码写入数据。
			UpdateUserDB update = new UpdateUserDB() ;
			update.setNumber(rm.getFrom()) ;
			update.setPassword(message.getNewPassword()) ;
			try{
				update.update() ;
				update.close() ;
				upm.setMinType(UserPswMessage.MODIFY_PSW_OK) ;
			}catch(SQLException e){
				update.close() ;
				BugWriter.log(this, e, "更新用户：" + user.getFrom() + " 出错。") ;
				upm.setMinType(UserPswMessage.MODIFY_PSW_FAILED) ;
				upm.setExplain("修改失败：数据库出错。可能是密码含有非法字符。如果有问题，请和管理员联系。") ;
			}
			
			ss.sendMessage(upm.getByteContent(),MessageType.USER_PSW_MESSAGE, 1000, rm.getFrom(), user.getIP(), user.getPort() ) ;
			return ;
	}

	
	
	
	
	
	
	
}

