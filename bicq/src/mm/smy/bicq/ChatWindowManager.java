package mm.smy.bicq ;

import java.util.* ;
import java.io.IOException ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.user.* ;

/**
* 聊天窗口管理，提供对所有聊天窗口的管理：创建，删除，隐藏，etc...
* 该类获得MainManger的引用，对消息进行必要的处理；然后与MainManger交互
* ChatWindow从该使用该类的引用发送与接收消息。
*
* 该类使用addTextMessageListener(..), addStateChangedMessageListener(....)
*
* 当ChatWindow类要求更新用户资料等信息时，调用该类的方法。
*
*
* 该类为MainManger类直接管理的类之一。负责实现聊天内容显示，发送，保存聊天记录等等。
*
* 在保存聊天纪录的时候，采用缓冲方法以提高效率。
* 注意：Guest,Host对象用数字代替，读取时要恢复。
* 
*/

public class ChatWindowManager implements TextMessageListener,StateChangedMessageListener{
	private MainManager mm = null ;
	private Hashtable chatwindows = new Hashtable(5) ; //number VS chatwindow
	private TextMessage[] textmessages = new TextMessage[10] ; //每10新的消息，保存到磁盘一次。作为缓冲
	private boolean isCacheEnable = true ; //当为真的时候，在保存时使用缓冲；否则的话（如退出程序时），保存所有的消息。
	private int i = 0 ; //记录当前缓冲区中的消息个数。


//constructors
	public ChatWindowManager(MainManager m_mm){
		mm = m_mm ;
		//添加监听器
		mm.addTextMessageListener(this) ;
		mm.addStateChangedMessageListener(this) ;
	}
//user deal....
	public Host getHost(){
		return mm.getHost() ;
		//return new Host(5000,"host_nickname") ;
	}
	
	public MainManager getMainManager(){
		return mm ;	
	}
	
//chatwindow manager.....
	/**
	*通过给定的 User对象，返回ChatWindow对象。
	*如果该ChatWindow不存在，则建立新的。
	*/
	private ChatWindow getChatWindow(User u){
//		System.out.println("getChatWindow() starts..") ;
//		System.out.println("User is null:" + (u == null) ) ;
		Integer n = new Integer(u.getNumber()) ;
		ChatWindow back = (ChatWindow) chatwindows.get(n) ;
		if(back == null){ //not exsits
//			System.out.println("chatwindow is null") ;
			back = new ChatWindow(this,u) ;
			chatwindows.put(n,back) ;
		}
//		System.out.println("getChatWindow() finished...") ;
		return back ;
	}
	
	//读取历史聊天记录,返回TextMessage的对象集
	public Vector getTextMessages(User u){
		if (u == null) return null ;
		
		Vector v = null ;
		//////////////////////////////////////////////////Maybe we should deal with the host here....... Later.
		TextMessageFile readf = new TextMessageFile(u.getNumber()) ;
		try{
			v = readf.read(u) ;
			readf.close() ;
		}catch(IOException e){
			mm.sendException("读取聊天纪录时出现IOException",e, MainManager.ERROR) ;
		}catch(ClassNotFoundException e1){
			mm.sendException("读取聊天纪录时出现ClassNotFoundException",e1,MainManager.DEBUG) ;
		}
		return v ;
	}
	
	public void closeSingeWindow(Integer i){
		if(i == null) return ;
		ChatWindow window = (ChatWindow) chatwindows.remove(i) ;
		if(window == null) return ;
		window.dispose() ;
		window = null ;
	}
	
	public Vector getTextMessages(User u,int count){
		if (u == null) return null ;
		
		Vector v = null ;
		//////////////////////////////////////////////////Maybe we should deal with the host here....... Later.
		TextMessageFile readf = new TextMessageFile(u.getNumber()) ;
		readf.setMainManager(mm) ;
		try{
			v = readf.read(u,count) ;
			readf.close() ;
		}catch(IOException e){
			mm.sendException("读取聊天纪录时出现IOException",e, MainManager.ERROR) ;
		}catch(ClassNotFoundException e1){
			mm.sendException("读取聊天纪录时出现ClassNotFoundException",e1,MainManager.DEBUG) ;
		}
		return v ;
	}
		
	private void saveTextMessage(TextMessage tm){
		//保存聊天记录，注意缓冲。在保存时，File文件处理类会自动忽略掉为null的TextMessage.
		textmessages[i] = tm ;
		i++ ;
		if(i >= textmessages.length || isCacheEnable == false){ //save
//System.out.println("saveTextMessage funtion works.") ;
			TextMessageFile savef = new TextMessageFile(this.getHost().getNumber()) ;
			try{
				savef.save(textmessages) ;
				savef.close() ;
			}catch(java.io.IOException e){
				mm.sendException("保存聊天纪录时出现IOException",e,MainManager.ERROR) ;	
			//	System.out.println("IOException while saving tm==>" + e.getMessage() ) ;
			}
			//清空缓冲区
			for(int i = 0 ; i < textmessages.length ; i++){
				textmessages[i] = null ;
			}
			i = 0 ;
		}
	}
	
	//是否缓冲消息
	public void setCacheEnabled(boolean m_c){
		isCacheEnable = m_c ;
	}
	
	/**
	* 关闭。
	* 保存缓冲区中的所有数据。
	* 设所有的ChatWindow为null, let gc works.
	*/
	public void close(){
		setCacheEnabled(false) ;
		saveTextMessage(null) ;
			
		if (chatwindows != null){
			Enumeration e = chatwindows.elements() ;
			while(e.hasMoreElements()){
				Object o = e.nextElement() ;
				o = null ;
			}
		}
	}

//能够获得指定用户所在窗口的状态。
public static final int CHATWINDOW_QUICK_MODE = 2005 ;
public static final int CHATWINDOW_FULL_MODE = 2006 ;
public static final int CHATWINDOW_HIDDEN = 2000 ;
public static final int CHATWINDOW_ICONED = 2001 ;
public static final int CHATWINDOW_MINIMIZED = 2002 ;
public static final int CHATWINDOW_MAXIMUMED = 2003 ;
public static final int CHATWINDOW_NOT_EXSIT = 2004 ;
	
	/**
	* 返回ChatWindow对象，与private .. getChatWindow(User u)相比，如果该cw不存在
	* 就返回null，而getChatWindow(User u)则自动生成新的窗口。
	*
	*/
	public ChatWindow getOutChatWindow(User u){
		if(u == null) return null ;
		
		Object temp_cw = chatwindows.get( new Integer(u.getNumber()) ) ;
		if(temp_cw == null){
			return null ;
		}
		return (ChatWindow) temp_cw ;
	}
	
//implements actions
	public void textMessageAction(TextMessage tm){ //mm will send text message here.
	
//	System.out.println("cwm has received a TextMessage:" + tm.getContent() ) ;
//	System.out.println("from:" + tm.getFrom().getNumber()) ;
//	System.out.println("to:" + tm.getTo().getNumber()) ;

		ChatWindow cw = getOutChatWindow(tm.getFrom()) ;
		if (cw != null){
			cw.sendTextMessage(tm) ;
			if (cw.isShowing() == false){
				//TODO:add some code here to tell the user this window has got a new message.
				//cw.show() ;
				mm.getMainFrame().fix() ;
			}
		}else{
			System.out.println("cwm adds an unread textmessage") ;
			this.addUnreadTextMessage(tm) ;
			tm.getFrom().incUnreadMessages() ;
			
			try{ //这儿有可能出现NullPointException，原因不明。
				mm.getMainFrame().fix() ;
			}catch(Exception e){
				//do nothing
			}
		}
		saveTextMessage(tm) ;
		return ;
	}
	public void stateChangedMessageAction(StateChangedMessage scm){
		ChatWindow cw = getOutChatWindow(scm.getFrom()) ;
		if(cw != null){
			cw.sendStateChangedMessage(scm) ;
		}
		return ;
	}
	
	/**
	* 用户可能要求打开与某个用户的聊天窗口。
	* 该窗口可能不存在，也可能已经打开
	* 该类负责选择，并且把窗口显示出来。
	* 例：用户主动与某人聊天的情况。
	*
	* 该类的引用将会出现在mm中，别的方法将会直接调用该方法。
	*
	*/
	public void showChatWindow(User u){

		ChatWindow temp_cw = getChatWindow(u) ;
		if(temp_cw != null){
			if(!temp_cw.isActive()){
				temp_cw.show() ;
			}
		}
		return ;
	}
	
/**
* For ChatWindow sends TextMessage out to the MainManager.
* 
* 
* 
*/
	public void sendOutTextMessage(TextMessage tm){
		tm.setFrom(mm.getHost()) ;
		
		System.out.println("cwm sendOutTextMessage() send out an message.") ;
		this.saveTextMessage(tm) ;
		mm.sendOutMessage(tm) ;
		return ;
	}
/*	
	public ChatWindowManager(){}
	
	public static void main(String[] args){
		User u = new User(3000,"客人") ;
		ChatWindowManager cwm = new ChatWindowManager() ;
		
		ChatWindow cw = cwm.getChatWindow(u) ;
		cw.setSize(400,400) ;
		cw.show() ;
		
	}
	
	

	
	public void test(User u){
		System.out.println("==================get guest:" + u.getNumber()) ;
		ChatWindow cw = this.getChatWindow(u) ;
		cw.setSize(300,300) ;
		cw.show() ;		
	}
	
*/	
/***********************************************************************************************************/
/**
* 缓存未读消息。
*
*
*
*/
	private LinkedList unreadmessages = new LinkedList() ;
	
	private void addUnreadTextMessage(TextMessage tm){
		if(tm == null) return ;
		
		unreadmessages.add(tm) ;
	}
	
	public TextMessage getUnreadTextMessage(User u){
		if(u == null) return null ;
		
		TextMessage temp_tm = null ;
		Iterator iter = unreadmessages.iterator() ;
		
		while(iter.hasNext()){
			temp_tm = (TextMessage) iter.next() ;
			if(temp_tm.getFrom().equals(u)){
				iter.remove() ;
				return temp_tm ;
			}			
		}
		
		return null ;
	}
/**********************************************************************************************************/

	
}


