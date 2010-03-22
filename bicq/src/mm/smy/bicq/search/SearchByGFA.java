package mm.smy.bicq.search ;

/**
* 根据好友的 性别/所在地（省市）/年龄 查找好友。
*
*@author XF
*@copyright 偶妹妹不再偶身边，感觉好难受.....
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class SearchByGFA extends JFrame implements ActionListener{
	
	private SearchGuestManager sgm = null  ;
	
	public SearchByGFA(SearchGuestManager m_sgm){
		sgm = m_sgm ;
		init() ;
	}
	
	private int gender = -1 ;        //-1表示所有用户，0女，1男
	private String province    = "" ;
	private int age_from = -1 ;     //最小的年龄， -1（小于零）表示没有下届
	private int age_to = -1 ; //最大的年龄。-1（小于零）表示没有上届。
	
	private JLabel explain = new JLabel("高级查找") ;
	
	private JLabel label_gender = new JLabel("性别：") ;
	String[] genders = {"随便  ","女孩","男孩"} ;
	private JComboBox  combo_gender = new JComboBox(genders) ;
	
	private JLabel label_age = new JLabel("年龄：") ;
	String[] ages = {"任意", "0-14","15-18","19-25","26-35","40-99"} ;
	private JComboBox age = new JComboBox(ages) ;
	
	private JLabel label_from = new JLabel("来自：") ;
	private JTextField from = new JTextField(10) ;
	
	private JButton pre = new JButton("Pre") ;
	private JButton next = new JButton("next step") ;
	private JButton finish = new JButton("finish") ;
	
	private void init(){
		this.setTitle("Search Guest By gfa") ;
		this.setSize(400,400) ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					//System.exit(0) ;
					sgm.report(4,SearchGuestManager.STEP_CLOSE)	;
				}
			}
		) ;
		
		age.setEditable(false) ;
		combo_gender.setEditable(false) ;

/*		
		age.addItemListener(this) ;
		combo_gender.addItemListener(this) ;
*/		
		
		JPanel top = new JPanel() ;
		top.add(explain) ;
		
		JPanel center1 = new JPanel() ;
		center1.add(label_age) ;
		center1.add(age) ;
		JPanel center2 = new JPanel() ;
		center2.add(label_gender) ;
		center2.add(combo_gender) ;
		JPanel center3 = new JPanel() ;
		center3.add(label_from) ;
		center3.add(from) ;
		
		JPanel center = new JPanel() ;
		center.setLayout(new GridLayout(5,1)) ;
		center.add(new JPanel()) ;
		center.add(center1) ;
		center.add(center2) ;
		center.add(center3) ;
		center.add(new JPanel()) ;
	
		JPanel buttons = new JPanel() ;
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
		cp.add(top, BorderLayout.NORTH) ;
		cp.add(center, BorderLayout.CENTER) ;
		cp.add(buttons, BorderLayout.SOUTH) ;	
	}
	
	public void actionPerformed(ActionEvent e){

		if(e.getActionCommand().equalsIgnoreCase("pre")){
			sgm.report(4,SearchGuestManager.STEP_PREVIOUS) ;
		}else if(e.getActionCommand().equalsIgnoreCase("next")){
			this.getInfor() ; //同步用户选择的东东。
			sgm.report(4,SearchGuestManager.STEP_NEXT) ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			sgm.report(4,SearchGuestManager.STEP_FINISH) ;
		}
		return ;
	}
/*	
	public void itemStateChanged(ItemEvent e){
		System.out.println("item state changed.") ;	
		get() ;
	}
*/
	private void getInfor(){
		gender = combo_gender.getSelectedIndex() - 1 ; //很巧合的安排结果
		
		switch(age.getSelectedIndex()){ //年龄
			case 0 :
				age_from = -1 ;
				age_to = -1 ;
				break ;
			case 1 : 
				age_from = -1 ;
				age_to = 14 ;
				break ;
			case 2 :
				age_from = 15 ;
				age_to   = 18 ;
				break ;
			case 3 :
				age_from = 19 ;
				age_to   = 25 ;
				break ;
			case 4 :
				age_from = 26 ;
				age_to   = 35 ;
				break ;
			case 5 :
				age_from = 40 ;
				age_to   = 99 ;
		}
		
		province = from.getText() ;
		if(province != null){
			province.trim() ;	
		}else{
			province = "" ;	
		}
		
		//System.out.println("gender:" + gender.getSelectedItem()) ;
		//System.out.println("age:" + age_from + ", " + age_to ) ;
		//System.out.println("gender:" + gender) ;
		//System.out.println("province:" + province) ;		
	}
	
	public int getAgeFrom(){
		return age_from ; //any		
	}
	public int getAgeTo(){
		return age_to ; //any
	}
	public String getFrom(){ //所在省份
		return province ; //any
	}
	public int getGender(){
		return gender ; //any ||| 0 girl; 1 boy ; -1 anyone
	}

/*	
	public static void main(String[] args){
		SearchByGFA sbn = new SearchByGFA() ;
		sbn.show() ;
	}
	
	public SearchByGFA(){ init() ; }
	
*/
	
}
