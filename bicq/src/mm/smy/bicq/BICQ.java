package mm.smy.bicq ;

/**
* 启动方法。
* 首先启动mm，然后打开LoginWindow，该方法将临时设立host对象。
* 如果选择注册的话，host一直为空。注意SendMessage中的对空操作。
* 如果登陆成功的话，LoginWindow将会告诉BICQ方法，然后bicq对mm进行必要的初始化
* 然后显示结果，打开主窗体。向服务器发送启动完毕消息。这个时候服务器会把
* 系统消息，验证消息，留言等等发送到mm处。
* 该类退出，将控制交给mm。
*
*/

import mm.smy.bicq.MainManager ;
import mm.smy.bicq.login.* ;
import mm.smy.bicq.user.* ;
import mm.smy.bicq.debug.* ;
import mm.smy.bicq.* ;

import mm.smy.bicq.message.ICMPMessage ;

import javax.swing.JOptionPane ; 
import java.io.* ;
import java.util.Properties ;
import java.net.InetAddress ;

public class BICQ{
	private MainManager m = null ;
	private LoginWindow loginwindow = null ;
	private Host host = null ;
	private Guest server = null ;
	
	private int debug = 0 ;

	private int sendport = 1245 ;
	private int receiveport = 7545 ;

	public BICQ(){
		if(debug == 1){
			sendport = 5203 ;
			receiveport = 5204 ;
		}
		
		load() ;
		init() ;
	}

	private String serverIP = null ;
	private int serverPort = -1 ;

	private void load(){
		Properties p = new Properties() ;
		try{
			FileInputStream fis = new FileInputStream("server.ini") ;
			p.load(fis) ;
			serverIP = p.getProperty("serverIP") ;
			try{
				serverPort = new Integer(p.getProperty("serverPort").trim()).intValue() ;
			}catch(Exception e){
				System.out.println("cannot solve the server port in the server.ini file.") ;
			}
			fis.close() ;
		}catch(Exception e){
			System.out.println("Error:" + e.getMessage() ) ;
		}
		System.out.println("serverIP:" + serverIP) ;
		System.out.println("serverPort:" + serverPort) ;
		if(serverIP == null || serverIP.trim().length() == 0)
			serverIP = "211.68.47.45" ;
		if(serverPort <1 || serverPort > 65535){
			serverPort = 7152 ;
		}
	}

	private void init(){
		m = new MainManager() ;

		Guest server = new Guest(1000) ;
		InetAddress IP = null ;

		try{
			IP = InetAddress.getByName(serverIP) ;
			System.out.println("IP :" + IP) ;
		}catch(Exception e){
			System.out.println("Cannot resolve the server IP:" + serverIP + "==>" + e.getMessage() ) ;
			return ;
		}

		server.setIP(IP) ;
		server.setPort(this.serverPort) ;

		m.setInitServer(server) ;
		System.out.println("run to here.") ;
		try{
			m.openSocket(sendport, receiveport) ;
		}catch(IOException e1){
			System.out.println("IOException was catched==>" + e1.getMessage() ) ;
			JOptionPane.showMessageDialog(null,"IO错误，注意一次只能打开一个BICQ==〉" + e1.getMessage(), "端口错误",JOptionPane.ERROR_MESSAGE) ;
			m.close() ;
			System.exit(-1) ;
		}catch(SecurityException e2){
			System.out.println("SecurtiyException ==>" + e2.getMessage() ) ;
			JOptionPane.showMessageDialog(null,"安全错误，您所设立的安全级别不允许BICQ运行，请和管理员联系。==〉" + e2.getMessage(), "安全错误",JOptionPane.ERROR_MESSAGE) ;
			m.close() ;
			System.exit(-1) ;
		}

		loginwindow = new LoginWindow(this, receiveport) ;
		loginwindow.show() ;
		System.out.println("set host") ;
		//setHost(new Host(3000)) ;
	}

	//打开MainManager的启动方法，整理host,guestgroups等信息。启动MainFrame，如果中间有错误发生，提示用户并且打开 登陆窗口 。
	public void setHost(Host h){
		if(h == null){ //退出虚拟机
			m.close() ;//由loginwindow退出。
			return ;
		}
		host = h ;
		m.setInitHost(host) ;

		try{
			System.out.println("prepare user.....") ;
			m.prepareUser() ;
		}catch(LoginException e){
			BugWriter.log(this,e,"") ;
			//发送退出消息。
			if(m != null)
				m.closeSession() ;
			
			JOptionPane.showMessageDialog(null,"运行时错误：您已经成功登陆，可是：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE) ;
			if(loginwindow == null) loginwindow = new LoginWindow(this, receiveport) ;

			loginwindow.setFrame(1) ;
			loginwindow.show() ;
			return ;
		}

		m.initChatWindow() ;

		//告诉服务器启动成功完成
		ICMPMessage loginok = new ICMPMessage() ;
		loginok.setMinType(ICMPMessage.LOGIN_TO_SERVER_SUCCESS) ;
		loginok.setContent(new Integer(m.getHost().getNumber()).toString()) ;
		m.sendOutMessage(loginok) ;

		System.out.println("show main") ;
		//打开主窗口MainFrame.class
		m.showMainFrame() ;
		loginwindow.dispose() ;

		return ;
	}

	public MainManager getMainManager(){
		return m ;
	}

	public static void main(String[] args){
		BICQ bicq = new BICQ() ;
	//	openURL("http://nic.biti.edu.cn/vbb/showthread.php?s=&threadid=123565") ;
//		exec("calc") ;
	}
	
	public static void openURL(String url){
			exec("cmd /c start " + url) ;
		//	Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler http://nic.biti.edu.cn") ;
			//also can be: rundll32 url.dll, FileProtocolHandler http://nic.biti.edu.cn		
	}
	
	public static void exec(String command){
		try{
			Runtime.getRuntime().exec(command) ;
			System.out.println("exect:" + command) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}		
	}







}


