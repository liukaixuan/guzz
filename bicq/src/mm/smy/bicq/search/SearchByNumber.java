package mm.smy.bicq.search ;

/**
* 根据好友的 number 查找好友。
*
*@author XF
*@copyright 偶妹妹不再偶身边，感觉好难受.....
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class SearchByNumber extends JFrame implements ActionListener{
	private SearchGuestManager sgm = null  ;
	
	public SearchByNumber(SearchGuestManager m_sgm){
		sgm = m_sgm ;
		init() ;
	}
	
	private JLabel explain = new JLabel("请输入好友的BICQ号：") ;
	private JTextField jtf_number = new JTextField(12) ;
	
	private JButton pre = new JButton("Pre") ;
	private JButton next = new JButton("next step") ;
	private JButton finish = new JButton("finish") ;
	
	private void init(){
		this.setTitle("Search Guest By Number") ;
		this.setSize(400,400) ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					//System.exit(0) ;
					sgm.report(2,SearchGuestManager.STEP_CLOSE)	;
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
		cp.add(jtf_number,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ;	
	}
	
	public void actionPerformed(ActionEvent e){
		//对Number进行检查，如果不满足要求。给出提示，并自动返回。
		if(e.getActionCommand().equalsIgnoreCase("next")){
			String temp_number = jtf_number.getText() ;
			if(temp_number == null) return ;
			temp_number = temp_number.trim() ;
			if(temp_number.length() == 0 ) return ;
			for(int i = 0 ; i < temp_number.length() ; i++ ){
				if(!Character.isDigit(temp_number.charAt(i))){
					explain.setText("请输入数字") ;
					jtf_number.setText("") ;
					return ;
				}
			}
			sgm.report(2,SearchGuestManager.STEP_NEXT) ;
			return ;
		}
		
		if(e.getActionCommand().equalsIgnoreCase("pre")){
			sgm.report(2,SearchGuestManager.STEP_PREVIOUS) ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			sgm.report(2,SearchGuestManager.STEP_FINISH) ;
		}
		
		
		return ;
	}
	
	public int getNumber(){
		int temp_number = -1 ;
		try{
			temp_number = new Integer(jtf_number.getText().trim()).intValue() ;
		}catch(Exception e){
			explain.setText("所输入的BICQ号无法转换成 整数 形式...") ;
		}	
		return temp_number ;
	}
/*	
	public static void main(String[] args){
		SearchByNumber sbn = new SearchByNumber() ;
		sbn.show() ;
	}
	
*/	
	
}
