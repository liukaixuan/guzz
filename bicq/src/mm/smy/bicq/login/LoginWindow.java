package mm.smy.bicq.login ;

/**
* 登陆窗口。呵呵，现在很简洁的说，没有自动登陆之类的好东东呀！
* 
* 
* 无限的尝试，如果用户愿意退出，那就退出虚拟机。
* 直到登陆成功，然后给BICQ.class发送 host类。
* 如果发过的是null，则退出虚拟机。
* 
* 
* 
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

import java.applet.* ;
import java.net.URL ;

import mm.smy.security.Digest ;
import mm.smy.bicq.debug.BugWriter ;
import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.user.Host ;
import mm.smy.util.* ;

public class LoginWindow extends JFrame implements ActionListener,UserPswMessageListener{
	
	public LoginWindow(BICQ m_bicq, int m_port){
		bicq = m_bicq ;
		port = m_port ;
		m = m_bicq.getMainManager() ;
		initWindow() ;
		m.addUserPswMessageListener(this) ;	
	}
	
	private int port = -1 ;
	
	private JComboBox nickname = null ;
	private JPasswordField password = null ;
	private MainManager m = null ;
	private BICQ bicq = null ;
	private Host host = null ; //临时建立的Host对象，如果登陆成功的话，把该对象传入BICQ.class，指示其登陆已经成功。
	
	private int login_state = 1 ; //1:准备，2：发送请求，3：网络超时，4：登陆失败，5：服务器错误。
	private String explain = "" ; //对获得的userpswmessage的错误登陆信息的解释。
	
	private JButton submit = new JButton("登陆") ;
	private JButton register = new JButton("注册新用户") ;
	private JButton help   = new JButton("协议与帮助") ;
	
	private void initWindow(){
		this.setSize(300,300) ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					bicq.setHost(null) ;
					System.exit(0) ;
				}
			}
		) ;
		
		JPanel blank = new JPanel() ;
		
		JPanel top = new JPanel() ;
		nickname = new JComboBox() ;
		nickname.setName("nickname") ;
		nickname.setEditable(true) ;
		JLabel l1 = new JLabel("BICQ号：") ;
		l1.setLabelFor(nickname) ;
		top.add(l1) ;
		top.add(nickname) ;
		
		JPanel center = new JPanel() ;
		password = new JPasswordField(15) ;
		password.setEchoChar('*') ;
		password.setName("password") ;
		JLabel l2 = new JLabel("密码：") ;
		l2.setLabelFor(password) ;
		center.add(l2) ;
		center.add(password);
		
		submit.setName("submit") ;
		submit.setActionCommand("submit") ;
		submit.addActionListener(this) ;
		
		register.setName("register") ;
		register.setActionCommand("register") ;
		register.addActionListener(this) ;		
		
		help.setName("help") ;
		help.setActionCommand("help") ;
		help.addActionListener(this) ;
		
		JPanel buttons = new JPanel() ;
		buttons.add(submit) ;
		buttons.add(register) ;
		buttons.add(help) ;
		
		Container cp = this.getContentPane() ;
		cp.setLayout(new GridLayout(4,1)) ;
		cp.add(blank) ;
		cp.add(top) ;
		cp.add(center) ;
		cp.add(buttons) ;
		
		this.show() ;
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().trim().toLowerCase() ;
		if(command.equals("submit")){
			//提交.
			String m_nickname = (String) nickname.getSelectedItem() ;
			String m_password = password.getText() ;
			
			if(m_nickname == null || m_password == null) return ;
			int number = 0 ;
			String SHAcode = "" ;
			try{
				number = new Integer(m_nickname.trim()).intValue() ;				
			}catch(Exception e1){
				JOptionPane.showMessageDialog(this,"请输入正确的BICQ号。") ;
				System.out.println("Error:cannot convert nickname to bicq number==>" + e1.getMessage() ) ;	
				return ;
			}
			
			try{
				SHAcode = Digest.SHA_1(m_password) ;
			}catch(java.io.UnsupportedEncodingException e1){
				System.out.println("un supported encoding exception detected.==>" + e1.getMessage() ) ;	
				BugWriter.log(this,e1,"加密算法编码方式系统不支持") ;
				JOptionPane.showMessageDialog(this,"加密算法编码方式系统不支持") ;
				return ;
			}catch(java.security.NoSuchAlgorithmException e2){
				BugWriter.log(this,e2,"缺少加密算法SHA-1") ;
				System.out.println("no such algorithm exception detected==>" + e2.getMessage() ) ;	
				JOptionPane.showMessageDialog(this,"缺少加密算法SHA-1") ;
				return ;
			}
			
		//	System.out.println("***************************************************") ;
		//	System.out.println("number:" + number) ;
		//	System.out.println("password:" + SHAcode) ;
			
			//建立临时的host对象。只包含number
			host = new Host(number) ;
			//发送请求,打开监听.禁止所有无关按钮.			
			UserPswMessage message = new UserPswMessage() ;
			message.setMinType(UserPswMessage.LOGIN_REQUEST) ;
			message.setFrom(host) ;
			message.setTo(m.getServer()) ;
			message.setPassword(SHAcode) ;
			message.setPort(port) ;
			message.setExplain(new Integer(host.getNumber()).toString()) ;
			
			openListener() ;
			setFrame(2) ;
			m.sendOutMessage(message) ;
			
		}else if(command.equals("register")){
			this.dispose() ;
			RegisterWindow rw = new RegisterWindow(bicq, port) ;
			rw.show() ;			
		}else if(command.equals("help")){
			BICQ.openURL("http://nic.biti.edu.cn/vbb/showthread.php?s=&threadid=123565") ;
		}
		
	}
	
	//打开监听，主要是password message的监听。
	
	private PswWait wait ; //密码监听定时器。
	
	private void openListener(){
		SmyTimer timer = new SmyTimer() ;
		timer.setInterval(50) ;
		timer.setTotalTime(50*1000) ; //wait 50s for timeout
		wait = new PswWait(timer) ;
		timer.setTimerListener(wait) ;
		timer.startTimer() ;
		
//		m.addUserPswMessageListener(this) ;
		
	}
	
	//1:wait to fill the form 2:sending and waiting... 3:error 4:ok,success
	public void setFrame(int m_frame){
		if(m_frame == 2){
			submit.setEnabled(false) ;
			nickname.setEditable(false) ;
			password.setEditable(false) ;	
		}
		
		if(m_frame == 3){
			if(explain == null || explain.length() == 0 ){
				explain = "有错误发生，请重试" ;
			}
			JOptionPane.showMessageDialog(this,explain,"错误",JOptionPane.ERROR_MESSAGE) ;
			m_frame = -1 ;
		}else if(m_frame == 4){
			System.out.println("login in successful.....") ;
			bicq.setHost(host) ;
			this.dispose() ;
			return ;
		}
		
		if(m_frame != 2){
			submit.setEnabled(true) ;
			password.setEditable(true) ;
			nickname.setEditable(true) ;	
		}		
	}
	
//	private boolean istimeouterror ;
	
	private class PswWait implements TimerListener{
		SmyTimer timer = null ;
		
		public PswWait(SmyTimer m_timer){
			timer = m_timer ;
//			istimeouterror = true ;	
		}
		
		public void timeElapsed(){
			if(login_state == UserPswMessage.LOGIN_SUCCESS){
				timer.stopTimer() ;
				setFrame(4) ;	
			}else if(login_state == UserPswMessage.LOGIN_FAILED){
//				istimeouterror = false ;
				timer.stopTimer() ;
				setFrame(3) ;	
			}
		}
		
		public void timeOut(){
			timer.stopTimer() ;
//			if(istimeouterror){ //是不是真的网络超时带来的错误。
				explain = "网络超时，请检查网络" ;
				setFrame(3) ;
//			}
		}
	}
	
	public void dispose(){
		super.dispose() ;
		m.removeUserPswMessageListener(this) ;
	}
	
	public void userPswMessageAction(UserPswMessage message){
		System.out.println("LoginWindow got a user psw message. mintype:" + message.getMinType()) ;
		
		login_state = message.getMinType() ;
		explain = message.getExplain() ;
		
		//我们主动要求定时器处理。
//		if(wait != null){
//			wait.timeElapsed() ;
//		}
	}

/*	
	public static void main(String[] args){
		LoginWindow window = new LoginWindow(null) ;
	}
*/	
}
