package mm.smy.bicq.search ;

/**
*Seach Guest step 1 .显示选择菜单。
*用户决定使用的搜索方法，输入参数。。。。。
*@author XF
*@author e-mail:myreligion@163.com
*@copyright Copyright 2003 XF All Rights Reserved.
*/
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class SearchStep1 extends JFrame implements ActionListener{
	
	private SearchGuestManager sgm = null ;

	
	private JLabel explain = new JLabel("Seach...") ;
	private JLabel blank = new JLabel("") ;
	
	private ButtonGroup group = new ButtonGroup() ;
	private JRadioButton byonline = new JRadioButton("who is online") ;
	private JRadioButton bynumber = new JRadioButton("by number") ;
	private JRadioButton bynickname = new JRadioButton("by nickname") ;
	private JRadioButton byGFA    = new JRadioButton("by gender/from/age") ;
	
	private JButton pre = new JButton("Pre") ;
	private JButton next = new JButton("next step") ;
	private JButton finish = new JButton("finish") ;
	
	private void init(){
		this.setTitle("SeachGuest") ;
		this.setSize(400,400) ;
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){	
						sgm.report(1,SearchGuestManager.STEP_CLOSE) ;
					}
				}
			) ;
		
		Panel labels = new Panel() ;
		labels.setLayout(new GridLayout(2,1)) ;
		labels.add(explain) ;
		labels.add(blank) ;
		
		Panel methods = new Panel() ;
		methods.setLayout(new GridLayout(4,1)) ;
		methods.add(byonline) ;
		methods.add(bynumber) ;
		methods.add(bynickname) ;
		methods.add(byGFA) ;
		group.add(byonline) ;
		group.add(bynumber) ;
		group.add(bynickname) ;
		group.add(byGFA) ;
		byonline.setActionCommand("byonline") ;
		bynumber.setActionCommand("bynumber") ;
		bynickname.setActionCommand("bynickname") ;
		byGFA.setActionCommand("byGFA") ;
		
		Panel buttons = new Panel() ;
		pre.setEnabled(false) ;
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
		cp.add(methods,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ;
	}
//constructors	
//	public SearchStep1(){
//		init() ;
//	}
	public SearchStep1(SearchGuestManager m_sgm){
		sgm = m_sgm ;
		init() ;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equalsIgnoreCase("next")){
			sgm.report(1,SearchGuestManager.STEP_NEXT) ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			sgm.report(1,SearchGuestManager.STEP_FINISH) ;
		}else if(e.getActionCommand().equalsIgnoreCase("pre")){
			sgm.report(1,SearchGuestManager.STEP_PREVIOUS) ;	
		}
	}
	
	public String getSelectedItem(){
		ButtonModel selecteditem = group.getSelection() ;
		if(selecteditem == null){
			System.out.println("Step one choose nothing..") ;
			System.out.println("flag1") ;
			return null ;
		}
		return selecteditem.getActionCommand() ;
	}
	
//	public static void main(String[] args){
//		SearchStep1 s1 = new SearchStep1() ;
//		s1.show() ;	
//		
//	}
	
}