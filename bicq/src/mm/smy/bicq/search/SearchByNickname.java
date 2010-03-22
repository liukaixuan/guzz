package mm.smy.bicq.search ;

/**
* 根据好友的 number 查找好友。
*@date 2003-10-3
*@author XF
*@copyright 偶妹妹不再偶身边，感觉好难受.....
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class SearchByNickname extends JFrame implements ActionListener{
	private SearchGuestManager sgm = null  ;
	
	public SearchByNickname(SearchGuestManager m_sgm){
		sgm = m_sgm ;
		init() ;
	}
	
	private JLabel explain = new JLabel("请输入好友的昵称：") ;
	private JTextField jtf_nickname = new JTextField(12) ;
	
	private JButton pre = new JButton("Pre") ;
	private JButton next = new JButton("next step") ;
	private JButton finish = new JButton("finish") ;
	
	private void init(){
		this.setTitle("Search Guest By Nickname") ;
		this.setSize(400,400) ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					//System.exit(0) ;
					sgm.report(3,SearchGuestManager.STEP_CLOSE)	;
				}	
			}	
		) ;
		
		
		Panel labels = new Panel() ;
		labels.add(explain) ;
	
		Panel buttons = new Panel() ;
		pre.addActionListener(this) ;
		pre.setActionCommand("pre") ;
		next.setFocusable(true) ;
		next.setActionCommand("next") ;
		next.addActionListener(this) ;
		finish.setActionCommand("finish") ;
		finish.addActionListener(this) ;
		buttons.add(pre) ;
		buttons.add(next) ;
		buttons.add(finish) ;	
		
		Container cp = getContentPane() ;
		cp.setLayout(new BorderLayout()) ;
		cp.add(labels,BorderLayout.NORTH) ;
		cp.add(jtf_nickname,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ;	
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equalsIgnoreCase("pre")){
			sgm.report(3,SearchGuestManager.STEP_PREVIOUS) ;
			return ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			sgm.report(3,SearchGuestManager.STEP_FINISH) ;
			return ;
		}
		
		
		//对Number进行检查，如果不满足要求。给出提示，并自动返回。
		
		String temp_nickname = jtf_nickname.getText() ;
		if(temp_nickname == null) return ;
		temp_nickname = temp_nickname.trim() ;
		if(temp_nickname.length() == 0 ) return ;
		for(int i = 0 ; i < temp_nickname.length() ; i++ ){
			if(!Character.isLetter(temp_nickname.charAt(i))){
				explain.setText("请输入合法的用户昵称") ;
				jtf_nickname.setText("") ;
				return ;
			}
		}
		sgm.report(3,SearchGuestManager.STEP_NEXT) ;
		return ;
	}
	
	public String getNickname(){
		return jtf_nickname.getText() ;
	}

/*	
	public static void main(String[] args){
		SearchByNickname sbn = new SearchByNickname() ;
		sbn.show() ;
	}
	
	public SearchByNickname(){ init() ; }
	
*/	
	
}
