package mm.smy.bicq.message.permit ;

/**
* 发送加为好友的请求
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


public class SendRequest extends JFrame implements ActionListener{
	
	private JLabel lable_nickame = new JLabel("昵称：") ;
	private JTextField nickname = new JTextField(10) ;
	
	private JLabel lable_number = new JLabel("BICQ:") ;
	private JTextField number = new JTextField(10) ;
	
	private JButton portrait = new JButton() ;
	
	private JTextArea result = new JTextArea(10,8) ;
	private JScrollPane scroll_result = new JScrollPane(result) ;
	
	private JButton send = new JButton("发送请求") ;
	private JButton close = new JButton("关闭") ;
	
	private Guest to = null ;
	private PermitMessageManager smm = null ;
	
	public SendRequest(PermitMessageManager m_smm,  Guest u){
		smm = m_smm ;		
		to = u ;
		initWindow() ;
	}
	
	private void initWindow(){
		if(to == null) return ;
		
		this.setTitle("向 " + to.getNickname() + " 发送请求……" ) ;
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE) ;
		this.setSize(400,400) ;
		
		nickname.setText(to.getNickname()) ;
		number.setText(to.getNumber() + "") ;
		
		result.setText("") ;
		result.setEditable(true) ;
		
		nickname.setEditable(false) ;
		number.setEditable(false) ;
		
		send.setActionCommand("send") ;
		send.addActionListener(this) ;
		
		close.setActionCommand("close") ;
		close.addActionListener(this) ;
		
		portrait.setIcon(FaceManager.getFaceIcon(to.getPortrait(), to.getState())) ;
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
		bottom.add(this.send) ;
		bottom.add(this.close) ;
		
		Container cp = this.getContentPane() ;
		cp.add(top, BorderLayout.NORTH) ;
		cp.add(this.scroll_result, BorderLayout.CENTER) ;
		cp.add(bottom, BorderLayout.SOUTH) ;
		
	}
	
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().trim().toLowerCase() ;
		if(command.equals("send")){ //发送请求
			//Container cp = this.getContentPane() ;
			PermitMessage message = new PermitMessage() ;
			message.setTo(to) ;
			message.setContent(result.getText()) ;
			message.setMintype(PermitMessage.PERMIT_REQUEST) ;
			smm.sendOutPermitMessage(message) ;
			//我们不管消息是否发送了，是否被收到了。这些留在以后做。
			result.setText("\n\n您的请求已经发送出去了，请等待回应……") ;
			result.setEnabled(false) ;
			send.setText("完成") ;
			send.setActionCommand("close") ;
			return ;
		}else if(command.equals("close")){
			this.dispose() ;
			return ;	
		}else if(command.equals("portrait")){
			smm.getMainManager().getUserManager().showUserInfor(to) ;
			return ;
		}
	}
/*	
	public SendRequest(Guest g){
		to = g ;	
		initWindow() ;
	}
	
	public static void main(String[] args){
		SendRequest window = new SendRequest(new Guest(1000,"me")) ;
		window.show() ;	
		
	}
*/	
	
	
	
	
	
	
	
	
}
