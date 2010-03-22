package mm.smy.bicq.state ;
/**
* 提供对StateChangedMessage的管理。
* 包括对 状态改变 时的IP更新，state更新。
* 用户主动要求离线，隐身等状况。
* 
* 以及对 离线留言 的编辑。
* 发送所有的StateChangedMessage
* 
* 该类从mm中获得host,guestgroups,guests等引用。
* 在unm,um启动完毕后启动。保存在mm中。
* 
* @author XF
* @author e-mail: myreligion@163.com
* @date 2003-11-15
* @copyright Copyright 2003 XF All Rights Reserved.
*/

import mm.smy.bicq.MainManager ;
import mm.smy.bicq.MainFrame ;

import mm.smy.bicq.message.StateChangedMessage ;
import mm.smy.bicq.message.StateChangedMessageListener ;

import mm.smy.bicq.user.User ;
import mm.smy.bicq.user.Guest ;
import mm.smy.bicq.user.Host ;

import mm.smy.bicq.sound.PlaySound ;

import java.util.Vector ;
import java.util.Enumeration ;


public class StateChangedManager implements StateChangedMessageListener{
	private MainManager m = null ;
	private Host host = null ;
	
	//constructors
	public StateChangedManager(MainManager m_mm){
		m = m_mm ;
		host = m.getHost() ;
		m.addStateChangedMessageListener(this) ;
	}
	
	//这儿的东西只是为了方便。
	public int getHostState(){
		return host.getState() ;
	}
		
	public void setTempLeaveWord(String m_word){
		host.setLeaveWord(m_word) ;
		return ;
	}
	
	//设置用户状态，注意发送StateChangedMessage数据报。
	/**
	* 如果用户是离线，首先查看是不是专门设了 TempLeaveWord，如果有的话，用这个，并把host中的设为这个。
	* 否则的话，用host里面的temp_leave_word。
	* 
	* 一开始的时候，我们不考虑 消息是否成功的发送给了服务器。就全当成功完成了。
	* 以后要添加代码，接收回执信息。
	* 在mf中也是里马设为修改的状态。
	*
	* 因为考虑到第一次发送状态失败，所以如果用户发送与第二次相同的状态；我们予以发送，以后可以屏蔽掉。
	* 
	*/
	public void setHostState( int m_state){
		
		StateChangedMessage message = new StateChangedMessage() ;
		message.setIP(null) ;
		message.setIsNotify(true) ;
		message.setMinType(m_state) ;
		message.setTempLeaveWord(m.getHost().getLeaveWord()) ;
		m.sendOutMessage(message) ;
		
		host.setState(m_state) ;	
		
		//用户状态改变，如果以前的时候用户是离线的，那么他有好多的StateChangedMessage没有刷新，还有文本消息等等。
		//我们刷新这些东西。	
		m.getMainFrame().fixAll() ;
		return ;
	}

	
	/**
	* 对好友state改变的响应。如果好友的状态发生了变化
	* 向mf发送fix()请求，这样就会刷新了。
	* GuestPanel会很好地完成他的工作。
	*
	* 因为消息可能是转发的，所以我们不去考虑getTo()的部分。
	* 同时IP部分的考虑请参看 消息解释 部分。
	*/
	public void stateChangedMessageAction(StateChangedMessage message){
		if(message == null) return ;
		
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!") ;
		
//		System.out.println("\n\nStateChangedManager::stateChangedMessageAction(..) reports:") ;
		User g =  message.getFrom() ;
		if(g == null){
//			System.out.println("StateChangedManager::stateChangedMessageAction(..)'s from is null. This is a serious error!") ;
			return ;	
		}
		System.out.println("from:" + g.getNickname()) ;
		
		//用户IP的管理
		if(message.getIP() != null){
			g.setIP(message.getIP()) ;
			g.setPort(message.getPort()) ;
		}
		
		if(message.getMinType() == User.OFFLINE){ //用户退出，设置IP为空。
			g.setIP(null) ;
		}else if(message.getMinType() == User.LEAVE){ //用户离开。
			g.setLeaveWord(message.getTempLeaveWord()) ;
		}
		
		if(g.getState() != message.getMinType()){				
			g.setState(message.getMinType()) ;
			m.getMainFrame().fix() ;
		}
				
		if(message.isNotify()){
			PlaySound.play(PlaySound.ONLINE) ;
		}
		
		
		
		System.out.println("leaveword:" + message.getTempLeaveWord() ) ;
	}
	
	//下面提供对host留言(mywords)的编辑支持。
	
	public void setMyWords(Vector v){
		if(v == null) return ;
		host.clearMyWords() ;
		
		Enumeration e = v.elements() ;
		while(e.hasMoreElements()){
			String s = (String) e.nextElement() ;
			if(s != null || s.trim().length() != 0 )
			host.appendMyWord(s) ;			
		}
		//刷新mf
		m.getMainFrame().fixState() ;
		
		return ;
	}
	
	//提供用户的编辑支持。
	public void eidtMyWords(){
		MyWordsWindow window = new MyWordsWindow(this, m.getHost() ) ;
		window.show() ;
		return ;
	}
	
	
	
	
	
}
