package mm.smy.bicq.server ;

/**
* 启动服务器的类.
* 
* 
* 
* 
* 
* 
*/

import java.net.InetAddress ;

import java.io.IOException ;

import java.sql.* ;

import mm.smy.bicq.server.db.* ;

import mm.smy.bicq.Monitor ;
import mm.smy.bicq.Monitorable ;

import mm.smy.bicq.server.user.* ;

import mm.smy.bicq.message.* ;

import mm.smy.bicq.server.manager.* ;

import mm.smy.bicq.message.ReceivedMessage ;

public class StartServer implements Monitorable{
	
	private Monitor monitor = null ;
	private ServerSendMessage sm = null ;
	
	private ICMPManager icmpm = null ;
	private PermitManager permitm = null ;
	private RegisterManager registerm = null ;
	private StateChangedManager statem = null ;
	private TextManager textm = null ;
	private UserPswManager pswm = null ;
	private UserInforManager userm = null ;
	private SearchGuestManager searchm = null ;	
	
	private static int current_number = 100000 ; //申请号码已经排到的号
	
	/**
	* 获得申请的新号码。
	* 并且把当前的号自增。
	*/
	public synchronized int getNewNumber(){
		current_number++ ;
		return current_number ;			
	}
	
	private OnlineManager onlinemanager = new OnlineManager() ;
	
	public OnlineManager getOnlineManager(){
		return onlinemanager ; 		
	}
	
	public StateChangedManager getStateChangedManager(){
		return statem ;	
	}
		
	public StartServer(){
	}
	
	private void init() throws Exception{
		try{
			initSQL() ;
		}catch(SQLException error){
			BugWriter.log(this, error , "在启动时,查找号码最大值时出错." ) ;
			System.out.println("connect to mysql server meets some problem. For more, read the log file") ;
			System.exit(-2) ;
		}
		
		System.out.println("数据库初始化完成。目前号码：" + this.current_number ) ;
		
		initManager() ;
	
		InetAddress IP = InetAddress.getByName("localhost") ;
		int port = 7152 ;
		
		sm = new ServerSendMessage(5639) ;
		sm.setHost(1000) ;
		
		monitor = new Monitor(this,port) ;
		monitor.setIsInited(true) ;
		monitor.setTimeOut(1) ;
		Thread t = new Thread(monitor) ;
		t.start() ;
		
	}
	
	private void initSQL() throws SQLException{
		ReadonlyStatement ro = new ReadonlyStatement("user") ;
		
		Statement s = ro.getStatement() ;
		ResultSet rs = s.executeQuery("select number from user order by number desc") ;
		//警告：如果在上面的执行中错了问题，那么数据库资源将不会被释放！
		
		if(!rs.next()){
			this.current_number = 10000 ;
		}else{
			this.current_number = rs.getInt("number") ;	
		}
		
		ro.close() ;
		s.close() ;
		rs.close() ;
		return ;
	}
	
	private void initManager(){
		icmpm = new ICMPManager(this) ;
		permitm = new PermitManager(this) ;
		registerm = new RegisterManager(this) ;
		statem = new StateChangedManager(this) ;
		textm = new TextManager(this) ;
		pswm = new UserPswManager(this) ;
		userm = new UserInforManager(this) ;
		searchm = new SearchGuestManager(this) ;		
		return ;
	}
	
	public void start() throws Exception{
		init() ;
	}
	
	public void stop(){
		close() ;
	}
	
	public void sendReceivedMessage(ReceivedMessage rm){
		System.out.println("****************Server got a message*******************") ;
		System.out.println("from:" + rm.getFrom() ) ;
		System.out.println("to" + rm.getTo()) ;
		System.out.println("type:" + rm.getType()) ;
		
		if(rm.getType() == MessageType.USER_PSW_MESSAGE){
			pswm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.ICMP_MESSAGE){
			icmpm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.TEXT_MESSAGE){
			textm.messageAction(rm) ;
		}else if(rm.getType() == MessageType.USER_INFOR_MESSAGE){
			userm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.PERMIT_MESSAGE){
			permitm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.REGISTER_MESSAGE){
			registerm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.SEARCH_GUEST_MESSAGE){
			searchm.messageAction(rm) ;	
		}else if(rm.getType() == MessageType.STATE_CHANGED_MESSAGE){
			statem.messageAction(rm) ;	
		}else{
			System.out.println("Server: get an unsolvable message.:" + rm.getType() ) ;	
		}
		return ;
		
	}


	public void sendMonitorException(Exception e){
		//System.out.println("server stateServer has got a monitor exception==>" + e.getMessage() ) ;
	}
	
	public void close(){
		sm.close() ;
		monitor.close() ;
		
		//关闭数据库连接！
		DBConnection.getInstance().release() ;				
	}

	
	public void sendMessage(byte[] content, int type,  int from, int to, InetAddress toIP, int toPort){
		try{
			sm.send(content,type,from,to,toIP,toPort) ;
		}catch(Exception e){
			System.out.println("=================================================") ;
			System.out.println("Server IOException:" + e.getMessage()) ;
			e.printStackTrace() ;
			System.out.println("=================================================") ;			
		}
	}
	
	public static void main(String[] args) throws Exception{
		StartServer ss = new StartServer() ;	
		ss.start() ;
	}
	
}
