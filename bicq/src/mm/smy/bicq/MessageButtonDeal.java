package mm.smy.bicq ;

/**
* 提供对MainFrame中message_button的支持。
* 其实这部分代码应该放在mf中，可是因为mf代码太长了，所以暂时放在这儿。
* 该部分是mf的延续。
*
*
*
*
*
*/

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import java.util.Stack ;

import javax.swing.JButton ;

import mm.smy.bicq.message.permit.* ;
import mm.smy.bicq.message.Message ;
import mm.smy.bicq.message.MessageType ;

public class MessageButtonDeal implements ActionListener{
	private JButton messagebutton = null ;
	
	private MainManager m = null ;
	private PermitMessageManager pmm = null ;
	
	//纪录各种各样的消息类型的消息type,然后程序根据这些类型选择哪个manager处理。
	private Stack unreadmessages = new Stack() ; 
	
	public MessageButtonDeal(MainManager m_mm, JButton b){
		m = m_mm ;	
		messagebutton = b ;
		init() ;
	}
	
	private void init(){
		//加入PermitMessage的管理。
		messagebutton.setActionCommand("message") ;
		messagebutton.setName("message") ;
		messagebutton.setText("消息") ;
		
		pmm = new PermitMessageManager(this, m) ;
		return ;
	}
	
	/**
	* 如果有新的消息到达，程序调用该方法。把消息压入栈中。
	* 该方法负责把新消息的通知传送给mf
	* @param message 消息的引用。
	* 
	*/
	public void notifyNewMessage(Message message){
		int type = message.getType() ;
		unreadmessages.push(new Integer(type)) ;
		
		//通知mf有新的消息
		messagebutton.setText("新消息") ;
		messagebutton.repaint() ;
		messagebutton.invalidate() ;		
		return ;
	}
	
	public void actionPerformed(ActionEvent e){
		//打开消息
		if(unreadmessages.empty()){ //没有未读的消息，显示所有的PermitMessage历史。
			pmm.showMessage() ;//如果真的没有未读消息的话，该方法自动的显示allMessages
			return ;
		}
		
		int type = ((Integer) unreadmessages.pop()).intValue() ;
		
		if(type == MessageType.PERMIT_MESSAGE){ //PermitMessage
			pmm.showMessage() ;	
		}//TODO:在这儿添加其他的消息管理
		
		
		//最后察看是否还有未读消息，决定messagebutton的显示。
		if(unreadmessages.empty()){
			messagebutton.setText("消息") ;
			messagebutton.repaint() ;
			messagebutton.invalidate() ;
		}
		
		return ;
	}
	
	
}
