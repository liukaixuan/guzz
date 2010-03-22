package mm.smy.bicq.message.permit ;

/**
* 对mf中的message_button的管理；主要用于用户察看 系统通知，身份验证等等。
* 
* 
* @author XF
* @author email: myreligion@163.com
* @date 2003年11月16日
* @copyright Copyright 2003 XF　All Rights Reserverd
*/



import javax.swing.JButton ;
import javax.swing.JOptionPane ;

import java.util.Stack ;
import java.util.Enumeration ;

import java.io.IOException ;

import mm.smy.bicq.MainManager ;
import mm.smy.bicq.MessageButtonDeal ;
import mm.smy.bicq.user.Guest ;

import mm.smy.bicq.message.MessageType ;
import mm.smy.bicq.message.PermitMessage ;
import mm.smy.bicq.message.ICMPMessage ;
import mm.smy.bicq.message.PermitMessageListener ;
import mm.smy.bicq.message.PermitMessageFile ;

import mm.smy.bicq.user.manager.GuestGroupManager ;

public class PermitMessageManager implements PermitMessageListener{
	
	private Stack allmessages = null ; //this is created in the PermitMesageFile.class
	private Stack newmessages = new Stack() ;
	
	private PermitMessageFile file = null ;
	
	private MainManager m = null ;
	private MessageButtonDeal deal = null ; //mf中"消息"按钮的管理类
	
	public PermitMessageManager(MessageButtonDeal m_deal, MainManager m_mm ){
		deal = m_deal ;
		m = m_mm ;
		m.addPermitMessageListener(this) ;
		init() ;	
	}
	
	public boolean hasNewPermitMessage(){
		return (!newmessages.empty()) ;
	}
	
	public int getNewPermitMessageNumber(){
		return newmessages.size() ;	
	}
	
	private void init(){
		System.out.println("m:" + m) ;
		System.out.println("m.getHost():" + m.getHost()) ;
		file = new PermitMessageFile(m.getHost().getNumber()) ;
		try{
			allmessages = file.read() ; //获取所有以前的PermitMesage。
		}catch(IOException e){
			mm.smy.bicq.debug.BugWriter.log(this, e,"读取PermitMessage时发生IOException") ;	
		}catch(ClassNotFoundException e){
			mm.smy.bicq.debug.BugWriter.log(this, e,"读取PermitMessage时发生ClassNotFoundException") ;	
		}
		
		if(allmessages == null) allmessages = new Stack() ; //不存在纪录，新建stack
	}

	public MainManager getMainManager(){
		return m ;
	}
	
	/**
	* 显示收到的消息；如果没有新消息的话，显示所有旧的消息。
	*/
	public void showMessage(){
		if(newmessages.empty()){
			showAllMessages() ;
		}else{
			PermitMessage pm = (PermitMessage) newmessages.pop() ;
			showPermitMessage(pm) ;
		}
		return ;
	}
	
	//显示PermitMessage，用窗口显示出消息内容。
	public void showPermitMessage( PermitMessage message){
		if(message.getMinType() == PermitMessage.PERMIT_ALLOWED){ //有用户允许host把他加为好友。
			AllowWindow aw = new AllowWindow(this, (Guest) message.getFrom()) ;
			aw.show() ;	
		}else if(message.getMinType() == PermitMessage.PERMIT_REFUSED){ //  请求被拒绝
			RefuseWindow rw = new RefuseWindow(this,message) ;
			rw.show() ;
		}else if(message.getMinType() == PermitMessage.PERMIT_REQUEST){ //请求host的身份验证。
			SendReply sr = new SendReply(this, (Guest) message.getFrom(), message) ;
			sr.show() ;
		}else if(message.getMinType() == PermitMessage.PERMIT_SEND){ //有人把用户加为好友。
			javax.swing.JOptionPane.showMessageDialog(null,"用户 " + message.getFrom().getNumber() + " 把您加为好友。") ;
		}
	}
	
	//显示所有的历史消息
	public void showAllMessages(){
		JOptionPane.showMessageDialog(null,"您现在没有新消息，而且历史消息的处理代码还没有编写，所以没得看了^_^") ;
		return ;		
	}
	
	//发送PermitMessage。
	public void sendOutPermitMessage(PermitMessage pm){
		m.sendOutMessage(pm) ;
		//TODO:save this message here.
		try{
			file.save(allmessages) ;
		}catch(IOException e){
			mm.smy.bicq.debug.BugWriter.log(this,e,"读取PermitMessage时发生IOException") ;
			JOptionPane.showMessageDialog(null,"无法保存 验证消息：" + e.getMessage() , "保存资料出错", JOptionPane.ERROR_MESSAGE) ;
		}	
		
	}

	//interface methods 
		
	public void permitMessageAction(PermitMessage pm){
		if(pm == null) return ;
		
		if(pm.getFrom() == null){ //from the host itself, just return.
			return ;
		}
		
		System.out.println("PermitMessageManager client 收到一个消息:") ;
		System.out.println("pm:" + pm) ;
		System.out.println("content:" + pm.getContent() ) ;
		
		allmessages.push(pm) ;
		newmessages.push(pm) ;
		
		//如果是通过身份验证的消息，我们把好友加到用户的组里面去。
		if(pm.getMinType() == PermitMessage.PERMIT_ALLOWED){			
			GuestGroupManager ggm = new GuestGroupManager(m.getGuestGroups()) ;
			ggm.show() ;
			mm.smy.bicq.user.GuestGroup gg = ggm.getChoseGuestGroup() ;
			m.addGuest((Guest)pm.getFrom(), gg ) ;
			
			ICMPMessage addsuccess = new ICMPMessage() ;
			addsuccess.setMinType(ICMPMessage.ADD_FRIEND) ;
			addsuccess.setTo(m.getServer()) ;
			addsuccess.setContent(m.getHost().getNumber() + ":" + pm.getFrom().getNumber() ) ;
			m.sendOutMessage(addsuccess) ;
			
			m.getMainFrame().fix() ;
		}
		
		
		//TODO:加入方法告诉用户有新的消息到达。
		deal.notifyNewMessage(pm) ;
	}
	
}