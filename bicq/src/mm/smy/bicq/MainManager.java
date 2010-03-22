package mm.smy.bicq ;

/**
* the main thread class, maintains the GuestGroups, Guests,Host
* chatting window...., and the exchanges between different parts.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-15   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.util.* ;
import java.net.InetAddress ;
import mm.smy.bicq.user.* ;
import mm.smy.bicq.search.* ;
import mm.smy.bicq.user.manager.* ;
import mm.smy.bicq.state.* ;

import mm.smy.bicq.sound.PlaySound ;

import mm.smy.bicq.login.LoginException ;
import mm.smy.bicq.message.*;

public class MainManager implements Serializable, Monitorable{
//默认组：我的好友，陌生人，黑名单	
	private Hashtable guestgroups = null ; //groupname VS GuestGroup
	private Hashtable guests = null ; // number vs Guest

	private Host host = null ;
	private Guest host2 = null ;
	
	private Guest server = new Guest(1000) ; //服务器
	
	private Monitor monitor = null ;
	private SendMessage sm  = null ;
	private ChatWindowManager cwm = null ;
	private UserNetManager unm = null ;      //用户网络资料处理
	private MainFrame mf = null ;
	private UserManager um = null ;	//内存中用户管理，包括获得，修改，删除好友，更改好友组等操作。这些操作将自动的写入硬盘（如果有必要，写入网络。）
 	private StateChangedManager scmm = null ;
 
	public static final int NO_SUCH_NUMBER = -10000 ; //没有该账号，用于登陆前向服务器发送请求时SendMessage用。
	public static final int SERVER_NUMBER =  1000 ;	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Real parts. The constructors
/***********************启动方法*************************************************************************/

	public MainManager(){
		host = new Host(this.NO_SUCH_NUMBER) ; //初始一个临时的host对象。
		host2 = new Guest(this.NO_SUCH_NUMBER) ;
	}
	
	public Guest getServer(){ return server ; }
	
	/**
	* 更新 服务器 时，仅仅更新其内容，不改地址。复制所有消息，供启动时使用。
	*/	
	public void setInitServer(Guest m_server) {
		server.copyFrom(m_server) ;
		return ;
	}
	
	/**
	* 成功登陆前的准备。打开消息监听，消息发送线程。
	*/
	public void openSocket() throws IOException, SecurityException{
		sm = new SendMessage() ;
		sm.setHost(host) ;
		sm.setServer(this.getServer().getIP(),this.getServer().getPort()) ;
		monitor = new Monitor(this) ;
		monitor.setIsInited(true) ;
		monitor.setTimeOut(1) ;
		Thread t = new Thread(monitor) ;
		t.start() ;		
	}
	
	/**
	* @param m_sendport 发送端口
	* @param m_receiveport 接收端口
	*/
	public void openSocket(int m_sendport, int m_receiveport) throws IOException, SecurityException{
		sm = new SendMessage(m_sendport) ;
		sm.setHost(host) ;
		sm.setServer(this.getServer().getIP(),this.getServer().getPort()) ;
		monitor = new Monitor(this, m_receiveport) ;
		monitor.setIsInited(true) ;
		monitor.setTimeOut(1) ;
		Thread t = new Thread(monitor) ;
		t.start() ;	
	}
	
	//初始化host对象，注意打开好友管理
	public void prepareUser() throws LoginException{
		
		unm = new UserNetManager(this) ;
		um = new UserManager(this) ;
		//打开用户资料。
		HostManager hm = new HostManager(this) ;
		Host temp_host = hm.getHost() ;
		if(temp_host != null){
			setInitHost(temp_host) ;
		}
		//打开好友资料。
		um.setupGuest() ;
		guestgroups = um.getGuestGroups() ;
		guests = um.getGuests() ;
		
		//我们将server作为一个隐藏的好友加入到组中。
		if(!guests.containsKey(new Integer(server.getNumber()))){
			System.out.println("server is not in the guests:" + guests) ;
			System.out.println("server:" + server) ;
			guests.put(new Integer(server.getNumber()), server) ;
			server.joinGuestGroup(this.getGuestGroup("我的好友")) ;	
		}
		
		//在这儿打开用户状态改变消息的监听
		scmm = new StateChangedManager(this) ;
		
	}
	
	//打开ChatWindowManager,以后可以对聊天纪录,加密算法等等进行初始化.
	public void initChatWindow(){
		cwm = new ChatWindowManager(this) ;		
		
	}
	
	//显示MainFrame
	public void showMainFrame(){
		mf = new MainFrame(this) ;
		mf.setSize(200,500) ;
		mf.setTitle(host.getNumber() + "") ;
		mf.show() ;
	}
	
	/**
	* 该函数不写文件，可是将会完全的覆盖host的所有信息
	* 主要用户启动时BICQ.class传送加密，端口等等信息。
	*
	*/
	public void setInitHost(Host m_host){
		host.copyFrom(m_host) ;	
		host2.copyFrom(host) ;	
	}
	
	public void close(){
		closeSession() ;
		System.exit(0) ;
	}
	
	public synchronized void closeSession(){
		//告诉服务器，我们退出啦！
		
		//先让主窗体消失，免得用户觉得要等呀等的。
		if(mf != null)
			mf.hide() ;
		
		if(scmm != null){
			scmm.setHostState(User.OFFLINE) ; //请求离线
			System.out.println("offline message sent OK") ;
		}else{
			System.out.println("scmm is null:" + scmm) ;
		}
		
		try{
			wait(1000) ;
		}catch(Exception e){}

		ICMPMessage message = new ICMPMessage() ;
		message.setMinType(ICMPMessage.QUIT_BICQ) ;
		message.setContent(new Integer(host.getNumber()).toString()) ;
		
		this.sendOutMessage(message) ;
		System.out.println("a成功向服务器发送　退出　请求．") ;
		
		try{
			wait(1000) ;
		}catch(Exception e){}
		
		if(cwm != null) cwm.close() ;
		cwm = null ;
		if(mf != null) mf.dispose() ;
		mf = null ;
		if(um != null) um.close() ;
		um = null ;
		if(unm != null) unm = null ;
		if(sm != null) sm.close() ;
		sm = null ;
		if(monitor != null) monitor.close() ;
		monitor = null ;		
	}
	
/**************************************************************************************************/
//usernetmanager methods
	public UserNetManager getUserNetManager(){
		return unm ;
	}
	public UserManager getUserManager(){
		return um ;	
	}
	public StateChangedManager getStateChangedManager(){
		return scmm ;	
	}
	public ChatWindowManager getChatWindowManager(){
/*		if(cwm == null){
			cwm = new ChatWindowManager(this) ;
		}
*/
		return cwm ;
	}
	public MainFrame getMainFrame(){
		return mf ;	
	}
//host methods
	public Host getHost(){
		return host ;
	}
	
	public void setHost(Host m_host){
		host.copyInfor(m_host) ;
			
		sm.setHost(host) ;
		
		//TODO:add method to share the host with the host2
		host2.copyFrom(host) ;	
		
		um.writeHostToLocal() ;
	}
	
	
	//return the host data in Guest's type.
	public Guest getHost2(){
		return host2 ;
	}
//guest methods.....

	public void setGuestGoups(Hashtable ggs){
		guestgroups = ggs ;	
	}
	
	public void setGuests(Hashtable gs){
		guests = gs ;	
	}

	public void updateGuest(Guest g){
		um.updateGuest(g) ;	
	}
	
	public Guest addGuest(Guest g, GuestGroup gg){
		return um.addGuest(g, gg) ;
	}
	
	public Guest addGuest(int m_number,String m_groupname){
		return um.addGuest(m_number,m_groupname) ;
	}
	public Guest addGuest(int m_number,GuestGroup m_gg){
		return um.addGuest(m_number,m_gg) ;
	}
	public Guest moveGuest(Guest g, GuestGroup gg){ //移动好友到新的小组
		return um.moveGuest(g,gg) ;
	}
	
	public void removeGuest(int m_number){
		um.removeGuest(m_number) ;
	}
	
	public Hashtable getGuests(){
		if(um != null)
			return um.getGuests() ;	
		return null ;
	}
	/**
	*如果是用户自己(host)，返回null.
	*如果用户不存在，创建该好友，并且加入到 陌生人 里。
	*/
	public Guest getGuest(int m_number){
		if(um == null) return null ;
		
		return um.getGuest(m_number) ;
	}
	
	/**
	*如果是用户自己(host)，返回null.
	*如果用户不存在，新建并且返回该用户，但是不加入guests中。
	*/	
	public Guest getOutGuest(int m_number){
		if(m_number == host.getNumber()) return null ;
		Guest g = (Guest) guests.get(new Integer(m_number)) ;
		
		if(g == null) return new Guest(m_number) ;	
		
		return g ;	
	}

//guestgroup methods......
	public GuestGroup getGuestGroup(String groupname){
		return um.getGuestGroup(groupname) ;
	}
	public GuestGroup addGuestGroup(String groupname){
		return um.addGuestGroup(groupname) ;
	}
	//remove
	public GuestGroup removeGuestGroup(GuestGroup m_group){
		return um.removeGuestGroup(m_group) ;	
	}
	
	public Hashtable getGuestGroups(){
		return um.getGuestGroups() ;	
	}
	
	//返回用户选择的好友将要添加到的 组， 该方法为阻止方法，目前直接返回 我的好友 组
	public GuestGroup getChoseGuestGroup(){
		 GuestGroupManager ggm = new GuestGroupManager(guestgroups) ;
		 ggm.show() ;
		 return ggm.getChoseGuestGroup() ;
		/* //I donnot know why, the above code will cause deadlock.
		*/
	}
	

//消息出口  入口。中间处理
private Vector tml   = new Vector(8) ; //text message listener
private Vector scml  = new Vector(4) ; //State changed message listener
private Vector uiml  = new Vector(4) ; //User infor message listener
private Vector upml  = new Vector(4) ; //user password message listener
private Vector sgml  = new Vector(4) ; //Search Guest Message Listener
private Vector sgrml = new Vector(4) ; //Search Guest Message Result Listener
private Vector pml   = new Vector(4) ; //Permit Message Listener.
private Vector oml   = new Vector(4) ;   //Other Message Listener
private Vector ml    = new Vector(10);  //Message Listener.
private Vector icmpml= new Vector(10);	
	//Permit Message	
	public boolean addPermitMessageListener(PermitMessageListener m_pml){
		return pml.add(m_pml) ;
	}
	public boolean removePermitMessageListener(PermitMessageListener m_pml){
		return pml.remove(m_pml) ;
	}
	
	//TextMessage	
	public boolean addTextMessageListener(TextMessageListener m_tml){
		return tml.add(m_tml) ;
	}
	public boolean removeTextMessageListener(TextMessageListener m_tml){
		return tml.remove(m_tml) ;
	}
	//State changed message
	public boolean addStateChangedMessageListener(StateChangedMessageListener m_scml){
		return scml.add(m_scml) ;
	}
	public boolean removeStateChangedMessageListener(StateChangedMessageListener m_scml){
		return scml.remove(m_scml) ;
	}
	//user information message
	public boolean addUserInforMessageListener(UserInforMessageListener m_uiml){
		return uiml.add(m_uiml) ;
	}		
	public boolean removeUserInforMessageListener(UserInforMessageListener m_uiml){
		return uiml.remove(m_uiml) ;
	}
	//user psw message
	public boolean addUserPswMessageListener(UserPswMessageListener m_upml){
		return upml.add(m_upml) ;
	}
	public boolean removeUserPswMessageListener(UserPswMessageListener m_upml){
		return upml.remove(m_upml) ;
	}
	//search guest message
	public boolean addSearchGuestMessageListener(SearchGuestMessageListener m_sgml){
		return sgml.add(m_sgml) ;
	}
	public boolean removeSearchGuestMessageListener(SearchGuestMessageListener m_sgml){
		return sgml.remove(m_sgml) ;
	}
	//search guest message
	public boolean addSearchGuestResultMessageListener(SearchGuestResultMessageListener m_sgrml){
		return sgrml.add(m_sgrml) ;
	}
	public boolean removeSearchGuestResultMessageListener(SearchGuestResultMessageListener m_sgrml){
		return sgrml.remove(m_sgrml) ;
	}
	//other message	
	public boolean addOtherMessageListener(OtherMessageListener m_messagelistener){
		return oml.add(m_messagelistener) ;
	}
	public boolean removeOtherMessageListener(MessageListener m_messagelistener){
		return oml.remove(m_messagelistener) ;
	}
	//message
	public boolean addMessageListener(MessageListener m_ml){
		return ml.add(m_ml) ;
	}
	public boolean removeMessageListener(MessageListener m_ml){
		return ml.remove(m_ml) ;
	}	
	//icmp	
	public boolean addICMPMessageListener(ICMPMessageListener m_icmpml){
		return icmpml.add(m_icmpml) ;
	}
	public boolean removeICMPMessageListener(ICMPMessageListener m_icmpml){
		return icmpml.remove(m_icmpml) ;
	}	
//the Monitor send received message.
	public void sendReceivedMessage(ReceivedMessage rm){
		System.out.println("Receive Message gets a message.") ;
		//TODO:Analyse the ReceivedMessage,dispatch it to diffent parts.
		//没有添加对消息的过滤，所以有可能消息错发给不同的用户。
		//过滤是注意to:server的消息，详细地解释参看 消息解释.txt
		int m_type = rm.getType() ;
		
		Guest m_from = this.getGuest(rm.getFrom()) ;
		System.out.println("*********mm gets message from:" + rm.getFrom()) ;
		
		//因为整个过程是单向的；不存在竞争问题，所以我们定义了一个Enumeration e 就可以啦！
		Enumeration e = null ;
		
		if (ml != null && ml.size() > 0 ){
			e = ml.elements() ;
			while(e.hasMoreElements()){
				((MessageListener) e.nextElement()).messageAction(rm) ;
			}
		}
//		System.out.println("MainManager reports tml:" + tml) ;
		switch (m_type){
			case MessageType.TEXT_MESSAGE :
				TextMessage tm = new TextMessage() ;
				tm.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				tm.setTo(host) ; //客户端
				tm.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(tml != null && tml.size() > 0){
					e = tml.elements() ;
					while(e.hasMoreElements()){
						TextMessageListener temp_tml = null ;
						temp_tml = (TextMessageListener) e.nextElement() ;
//						System.out.println("temp_tml:" + temp_tml) ;
						
						temp_tml.textMessageAction(tm) ;
					}
				}
				//发送声音
				PlaySound.play(PlaySound.MSG) ;
				break ;
			case MessageType.ICMP_MESSAGE :
				ICMPMessage icmpm = new ICMPMessage() ;
				icmpm.setFrom(this.getGuest(rm.getFrom())) ;
				icmpm.setTo(this.getHost()) ;
				icmpm.setByteContent(rm.getContent()) ;
				if(icmpml != null && icmpml.size() != 0 ){
					e = icmpml.elements() ;
					ICMPMessageListener temp_icmpl = null ;	
					while(e.hasMoreElements()){
						temp_icmpl = (ICMPMessageListener) e.nextElement() ;
						temp_icmpl.ICMPMessageAction(icmpm) ;
					}
				}
				
				//play golbal sound
				PlaySound.play(PlaySound.GLOBAL) ;
				break ;
			case MessageType.STATE_CHANGED_MESSAGE :
				StateChangedMessage scm = new StateChangedMessage() ;
				scm.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				scm.setTo(host) ; //客户端
				scm.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(scml != null && scml.size() > 0){
					e = scml.elements() ;
					while(e.hasMoreElements()){
						((StateChangedMessageListener) e.nextElement()).stateChangedMessageAction(scm) ;
						System.out.println("@#$@#$%#^$%&%$*&%^**%##$%#$MainManager: stateChangeMessage dispatch") ;
					}
				}
				break ;
			case MessageType.PERMIT_MESSAGE :
				PermitMessage pm = new PermitMessage() ;
				pm.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				pm.setTo(host) ; //客户端
				pm.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(pml != null && pml.size() > 0){
					e = pml.elements() ;
					while(e.hasMoreElements()){
						((PermitMessageListener) e.nextElement()).permitMessageAction(pm) ;
					}
				}
				PlaySound.play(PlaySound.SYSTEM) ;
				break ;				
			case MessageType.USER_INFOR_MESSAGE :
				UserInforMessage uim = new UserInforMessage() ;
				uim.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				uim.setTo(host) ; //客户端
				uim.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(uiml != null && uiml.size() > 0){
					e = uiml.elements() ;
					while(e.hasMoreElements()){
						((UserInforMessageListener) e.nextElement()).userInforMessageAction(uim) ;
					}
				}
				break ;
			case MessageType.SEARCH_GUEST_MESSAGE :
				mm.smy.bicq.message.SearchGuestMessage sgm = new mm.smy.bicq.message.SearchGuestMessage() ;
				sgm.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				sgm.setTo(host) ; //客户端
				sgm.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(sgml != null && sgml.size() > 0){
					e = sgml.elements() ;
					while(e.hasMoreElements()){
						((SearchGuestMessageListener) e.nextElement()).searchGuestMessageAction(sgm) ;
					}
				}
				break ;			
			case MessageType.SEARCH_GUEST_RESULT_MESSAGE :
				SearchGuestResultMessage sgrm = new SearchGuestResultMessage() ;
				sgrm.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				sgrm.setTo(host) ; //客户端
				sgrm.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(sgrml != null && sgrml.size() > 0){
					e = sgrml.elements() ;
					while(e.hasMoreElements()){
						((SearchGuestResultMessageListener) e.nextElement()).searchGuestResultMessageAction(sgrm) ;
					}
				}
				break ;
			case MessageType.USER_PSW_MESSAGE :
				UserPswMessage upm = new UserPswMessage() ;
				upm.setByteContent(rm.getContent()) ;
				upm.setFrom(this.getGuest(rm.getFrom())) ;
				upm.setTo(this.getHost()) ;
				if(upml != null && upml.size() != 0 ){
					e = upml.elements() ;
					while(e.hasMoreElements()){
						((UserPswMessageListener) e.nextElement()).userPswMessageAction(upm) ;
					}
				}
				break ;
			default :
			//处理OtherMessage，多态性。
				OtherMessage om = new OtherMessage() ;
				om.setFrom(this.getGuest(rm.getFrom())) ; //当为host时，此处为null
				om.setTo(host) ; //客户端
				om.setByteContent(rm.getContent()) ;
				//dispatch the message
				if(oml != null && oml.size() > 0){
					e = oml.elements() ;
					while(e.hasMoreElements()){
						((OtherMessageListener) e.nextElement()).otherMessageAction(om) ;
					}
				}
				System.out.println("Receive an unsolvable message.   Type:" + m_type) ;
				break ;
		}
		return ;
	}
	
//Exception sent from the monitor at runtime.
	public void sendMonitorException(Exception e){
		//System.out.println("Minitor has got an Exception==>" + e.getMessage() ) ;
		
	}
	
	
	public static final int ERROR   = 10 ;
	public static final int WARNING = 11 ;
	public static final int HINT    = 12 ;
	public static final int DEBUG   = 13 ;
	/**
	* Exception sent from other parts of BICQ in runntime.
	* @param message 用户指定的消息解释
	* @param e       异常引用
	* @param type    异常所属的类型
	*/
	public void sendException(String message, Exception e , int type){
		System.out.println("Received an Exception:" + message + "||type:" + type + "||Exception trace:" + e.getMessage()) ; ;	
		
	}
//send out messages.... A lot of message types...
	public synchronized void  sendOutMessage(Message message){
		try{
			sm.send(message) ;
		}catch(Exception e){
			System.out.println("Error to send out message to net") ;
		}	
	}


}


