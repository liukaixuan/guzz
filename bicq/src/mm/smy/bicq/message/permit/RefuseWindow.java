package mm.smy.bicq.message.permit ;

/**
* 别人拒绝host的窗口。
* 
* 
* 
* 
* 
* 
* 
*/

import mm.smy.bicq.user.Guest ;
import mm.smy.bicq.user.User  ;

import mm.smy.bicq.message.PermitMessage ;

import mm.smy.bicq.FaceManager ;

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class RefuseWindow extends JFrame implements ActionListener{
	private JLabel lable_nickame = new JLabel("昵称：") ;
	private JTextField nickname = new JTextField(10) ;
	
	private JLabel lable_number = new JLabel("BICQ:") ;
	private JTextField number = new JTextField(10) ;
	
	private JButton portrait = new JButton() ;
	
	private JTextArea result = new JTextArea(10,8) ;
	private JScrollPane scroll_result = new JScrollPane(result) ;
	
	private JButton add = new JButton("加为好友") ;
	private JButton close = new JButton("关闭") ;
	
	private PermitMessage pm = null ;
	private Guest from = null ;
	private PermitMessageManager smm = null ;
	
	public RefuseWindow(PermitMessageManager m_smm, PermitMessage m_pm){
		smm = m_smm ;
		pm = m_pm ;
		if(pm == null) return ;
		
		from = (Guest) pm.getFrom() ;
		initWindow() ;
	}
/*	
	public RefuseWindow(PermitMessage m_pm){
		pm = m_pm ;
		if(pm == null) return ;
		
		from = pm.getFrom() ;
		initWindow() ;
	}
*/	
	private void initWindow(){
		if(from == null) return ;
		
		this.setTitle(from.getNickname() + " 拒绝了您把他/他加为好友" ) ;
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE) ;
		this.setSize(400,400) ;
		
		nickname.setText(from.getNickname()) ;
		number.setText(from.getNumber() + "") ;
		
		result.setText(from.getNickname() + "拒绝您将她/他加为好友\n\n\n" + "拒绝理由：" + pm.getContent()) ;
		
		result.setEditable(false) ;
		nickname.setEditable(false) ;
		number.setEditable(false) ;
		
		add.setActionCommand("add") ;
		add.addActionListener(this) ;
		
		close.setActionCommand("close") ;
		close.addActionListener(this) ;
		
		portrait.setIcon(FaceManager.getFaceIcon(from.getPortrait(), from.getState())) ;
		portrait.setActionCommand("portrait") ;
		portrait.addActionListener(this) ;
		
		//布局。
		JPanel top = new JPanel() ;
		top.add(this.lable_number) ;
		top.add(this.number) ;
		top.add(this.lable_nickame) ;
		top.add(this.nickname) ;
		top.add(this.portrait) ;
		
		JPanel bottom = new JPanel() ;
		bottom.add(this.add) ;
		bottom.add(this.close) ;
		
		Container cp = this.getContentPane() ;
		cp.add(top, BorderLayout.NORTH) ;
		cp.add(this.scroll_result, BorderLayout.CENTER) ;
		cp.add(bottom, BorderLayout.SOUTH) ;
		
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().trim().toLowerCase() ;
		if(command.equals("add")){ //加为好友。
			SendRequest request = new SendRequest(smm,from) ;
			request.setVisible(true) ;
			System.out.println("chang to SendRequest Window.") ;
			this.dispose() ;
			return ;
		}else if(command.equals("close")){
			this.dispose() ;
			return ;
		}else if(command.equals("portrait")){ //显示头像。
			smm.getMainManager().getUserManager().showUserInfor(from) ;
			return ;
		}
	}
/*	
	public static void main(String args[]){
		PermitMessage message = new PermitMessage() ;
		message.setFrom(new User(6777)) ;
		message.setContent("No reason, hahaha") ;
		message.setMintype(PermitMessage.PERMIT_REFUSED) ;
		
		RefuseWindow window = new RefuseWindow(message) ;
		window.show() ;		
	}
*/	
	
	
	
	
	
	
	
	
}

