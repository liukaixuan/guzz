package mm.smy.bicq.search ;

/**
* 该类显示用户的搜索过程，就是那段等待。
* 现在编号已经排到5,6啦！
*@date 2003-10-3
*@author XF
*@copyright Copyright 2003 XF All Rights Reserved
* 刚才妈妈打电话问我十一出去玩没，我说没有。然后被骂了，强烈要求出去...。
* 偶妹妹不在，出去什么意思呀？？好想妹妹呀~~~
*
*
* 使用Runnable的目的因为不用的话，程序将会在生成该部分一半的时候，转而执行sgm的wait()。使得画面不完整！
* 
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class SearchStep2 extends JFrame implements ActionListener/*,Runnable*/{
	
	

	private JLabel explain = new JLabel("搜索用户") ;
	private JLabel blank   = new JLabel("") ;
	private JLabel current = new JLabel("") ;
	
	private JButton pre = new JButton("Pre") ;
	private JButton next = new JButton("next") ;
	private JButton finish = new JButton("finish") ;
	
	private JFrame preframe = null ;
	private SearchGuestManager sgm = null ;	
	private int step = 5 ; //5 正在向服务器发送请求；6 超时，网络错误 etc...
	
	private void init(){
		this.setSize(400,400) ;
		this.setTitle("正在发送请求……") ;
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						sgm.report(step,SearchGuestManager.STEP_CLOSE) ;	
					}
				}
			) ;
		
		Panel labels = new Panel(new GridLayout(2,1)) ;
		labels.add(explain) ;
		labels.add(blank) ;
		
		pre.addActionListener(this) ;
		next.addActionListener(this) ;
		finish.addActionListener(this) ;
		pre.setActionCommand("pre") ;
		next.setActionCommand("next") ;
		finish.setActionCommand("finish") ;
		
		Panel buttons = new Panel() ;
		buttons.add(pre) ;
		buttons.add(next) ;
		buttons.add(finish) ;
		
		Container cp = getContentPane() ;
		cp.setLayout(new BorderLayout()) ;
		cp.add(labels,BorderLayout.NORTH) ;
		cp.add(current,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ; 
		
		cp.invalidate() ;
	}
	
	public void setCurrent(int m_step){//5 正在向服务器发送请求；6  超时，网络错误 etc...
		if(m_step == 5){
			next.setEnabled(false) ;
			pre.setEnabled(false) ;
			finish.setEnabled(false) ;
			current.setText("正在向服务器发送请求，请等待回应……") ;
			step = 5 ;
		}else if(m_step == 6){
			next.setEnabled(true) ;
			next.setText("完成") ;
			pre.setEnabled(true) ;
			finish.setEnabled(true) ;
			current.setText("网络超时，请检查网络是否畅通！") ;
		}
		return ;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equalsIgnoreCase("pre")){
			sgm.report(step,SearchGuestManager.STEP_PREVIOUS) ;
		}else if(e.getActionCommand().equalsIgnoreCase("next")){
			sgm.report(step,SearchGuestManager.STEP_NEXT) ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			sgm.report(step,SearchGuestManager.STEP_FINISH) ;
		}
		return ;	
	}
	
	//返回 该祯的上一祯
	public JFrame getPreFrame(){ return preframe ; }
/* //for test	
	public SearchStep2(){
		init() ;	
	}
*/
	public SearchStep2(SearchGuestManager m_sgm,JFrame preframe){
		sgm = m_sgm ;
		this.preframe = preframe ;
		init() ;
		setCurrent(5) ;
	}
	
//	public void run(){
//		init() ;	
//		setCurrent(5) ;
//	}
/* //test part		
	public static void main(String[] args){
		SearchStep2 ss2 = new SearchStep2() ;
		ss2.setCurrent(5) ;
		ss2.show() ;
	}
*/
	
}























