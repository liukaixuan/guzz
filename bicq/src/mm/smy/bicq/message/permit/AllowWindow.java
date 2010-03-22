package mm.smy.bicq.message.permit ;

/**
* 别人允许host把他加为好友
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

import mm.smy.bicq.FaceManager ;

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class AllowWindow extends JFrame implements ActionListener{
	private JLabel lable_nickame = new JLabel("昵称：") ;
	private JTextField nickname = new JTextField(10) ;
	
	private JLabel lable_number = new JLabel("BICQ:") ;
	private JTextField number = new JTextField(10) ;
	
	private JButton portrait = new JButton() ;
	
	private JTextArea result = new JTextArea(10,8) ;
	private JScrollPane scroll_result = new JScrollPane(result) ;
	
	private JButton close = new JButton("关闭") ;
	
	private Guest from = null ;
	private PermitMessageManager smm = null ;
	
	public AllowWindow(PermitMessageManager m_smm, Guest g){
		smm = m_smm ;
		from = g ;	
		initWindow() ;
	}
	
	private void initWindow(){
		if(from == null) return ;
		
		this.setTitle("您把 " + from.getNickname() + " 加为了好友" ) ;
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE) ;
		this.setSize(400,400) ;
		
		nickname.setText(from.getNickname()) ;
		number.setText(from.getNumber() + "") ;
		
		result.setText("成功把 " +  from.getNickname() + " 加为了您的好友。") ;
		
		result.setEditable(false) ;
		nickname.setEditable(false) ;
		number.setEditable(false) ;
		
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
		bottom.add(this.close) ;
		
		Container cp = this.getContentPane() ;
		cp.add(top, BorderLayout.NORTH) ;
		cp.add(this.scroll_result, BorderLayout.CENTER) ;
		cp.add(bottom, BorderLayout.SOUTH) ;
		
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().trim().toLowerCase() ;
		if(command.equals("close")){
			this.dispose() ;
			return ;
		}else if(command.equals("portrait")){ //显示头像。
			smm.getMainManager().getUserManager().showUserInfor(from) ;
			return ;
		}
	}
	
	public static void main(String args[]){
		
		AllowWindow window = new AllowWindow(null, new Guest(10000)) ;
		window.show() ;		
	}
	
	
	
	
	
	
	
	
}