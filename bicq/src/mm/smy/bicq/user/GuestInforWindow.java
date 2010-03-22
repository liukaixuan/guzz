package mm.smy.bicq.user ;

/**
* 该类为InforWindow的更加合理化版本。我们把非用户自身的更新放入一个新的类中
* 该类包含自己的更新方法，提高封装性。
*
* @author XF
* @author e-mail:myreligion@163.com
* @date 2003-12-27 Thanksgiving Day!
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import java.util.* ;

import mm.smy.bicq.user.* ;
import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.debug.BugWriter ;
import mm.smy.util.* ;
import mm.smy.bicq.user.manager.* ;

public class GuestInforWindow extends JFrame implements ActionListener, UserInforMessageListener{
	private Guest guest = new Guest() ;
	private Host host = new Host() ;
	
	private GuestInforWindow outer = this ;
	
	private  MainManager m = null ;
	
	private Hashtable variables = new Hashtable(40) ;
	
	
	public static void main(String[] args){
		
		Guest g = new Guest(2000) ;
		g.setNickname("guest") ;
		g.setAddress("地址") ;
		//h.setOnlineAction(Guest.BOX_NOTICE) ;
		InforWindow window = new InforWindow(g,null) ;
		window.show() ;
	}
	
	public GuestInforWindow(Guest u, MainManager m_mm){
		m = m_mm ;
		
		guest = guest.copyFrom(u) ;
		init() ;	
		initWindow() ;
		this.setSize(400,400) ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					outer.dispose() ;
				}
			}
		) ;
	}
	
	//建立variables Hashtable

	private void init(){
		//user
		variables.put("number", new Integer(guest.getNumber()).toString()) ;
		variables.put("nickname", guest.getNickname()==null?"":guest.getNickname() ) ;
		variables.put("portrait", new Integer(guest.getPortrait()).toString()) ;
		variables.put("mail", guest.getMail()==null?"":guest.getMail() ) ;
		variables.put("realname", guest.getRealname()==null?"":guest.getRealname() ) ;
		variables.put("homepage", guest.getHomepage()==null?"":guest.getHomepage() ) ;
		variables.put("zip", new Integer(guest.getZip()).toString() ) ;
		variables.put("address", guest.getAddress()==null?"":guest.getAddress() ) ;
		variables.put("country", guest.getCountry()==null?"":guest.getCountry() ) ;
		variables.put("province", guest.getProvince()==null?"":guest.getProvince() ) ;
		variables.put("year", new Integer(guest.year).toString() ) ;
		variables.put("month", new Integer(guest.month).toString() ) ;
		variables.put("day", new Integer(guest.day).toString() ) ;
		variables.put("gender", new Integer(guest.getGender()).toString() ) ;
		variables.put("explain", guest.getExplain()==null?"":guest.getExplain() ) ;
	}
	
	private void initWindow(){
		
		ok = new JButton("更新") ;
		ok.addActionListener(this) ;
		ok.setActionCommand("update") ;	
		
		cancel = new JButton("关闭") ;
		cancel.setActionCommand("close") ;
		cancel.addActionListener(this) ;
					
		basic = new JPanel() ;
		contact = new JPanel() ;
		detail = new JPanel() ;
		
		createBasic() ;
		createContact() ;
		createDetail() ;
		
		tab.addTab("基本资料", basic) ;
		tab.addTab("联系方法", contact) ;
		tab.addTab("详细资料", detail) ;
		
		JPanel buttons = new JPanel() ;
		buttons.add(ok) ;
		buttons.add(cancel) ;
				
		Container cp = this.getContentPane() ;
		cp.add(tab,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ;		
		
	}
	
	//对ok,cancal 按钮的处理。
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand().toLowerCase().trim() ;
			
		if(command.equals("close")){ //关闭窗口
			//关闭窗口对消息的监听，如果有的话。
			m.removeUserInforMessageListener(this) ;
			
			this.dispose() ;
			
		}else if(command.equals("update")){ //更新好友资料
			System.out.println("update..") ;
			//让ok变灰
			ok.setEnabled(false) ;
			
			SmyTimer timer = new SmyTimer() ;
			timer.setInterval(UserNetManager.DEFAULT_INTERVAL) ;
			timer.setTotalTime(UserNetManager.DEFAULT_TOTAL_TIME + 1000) ;
			timer.setTimerListener(new UpdateGuestListener(timer)) ;
			timer.startTimer() ;
			
			//添加消息监听
			m.addUserInforMessageListener(this) ;
			System.out.println("添加好友监听消息") ;
			
			//发送消息请求
			ICMPMessage icmp = new ICMPMessage() ;
			icmp.setMinType(ICMPMessage.LOAD_SINGLE_GUEST_INFOR) ;
			icmp.setTo(m.getServer()) ;
			icmp.setContent(new Integer(guest.getNumber()).toString()) ;
		
			m.sendOutMessage(icmp) ;
			
			
			System.out.println("InforWindow sends a messge to download the guest:" + guest.getNumber() + " 's information.") ;
		}
		
	}
	
/*********************************************************************************************************/
//下载好友新的资料
	private class UpdateGuestListener implements TimerListener{
		private SmyTimer timer = null ;
		public UpdateGuestListener(SmyTimer m_timer){ timer = m_timer ;} 
		
		public void timeElapsed(){
			if(success_finish){
				timer.stopTimer() ;
				//删除监听
				m.removeUserInforMessageListener(outer) ;
				System.out.println("移除好友监听") ;
			}
		}
		
		public void timeOut(){
			timer.stopTimer() ;
			//删除监听
			m.removeUserInforMessageListener(outer) ;
			System.out.println("移除好友监听") ;
			ok.setEnabled(true) ;
			JOptionPane.showMessageDialog(outer,"网络超时，更新失败！","失败",JOptionPane.ERROR_MESSAGE) ;		
		}
		
	}
	
	private boolean success_finish = false ; 
	
	public void userInforMessageAction(UserInforMessage message){
		if(message == null) return ;
		if(message.getMinType() != UserInforMessage.UPDATE_GUEST_INFOR) return ;
		
		
		User temp = message.getUser() ;
		System.out.println("收到好友更新消息：" + temp.getNumber() ) ;
		if(temp.getNumber() != guest.getNumber() ) return ; //不是发送给我们的。返回。
		System.out.println("呵呵，是我们要的。") ;
		
		guest.copyInfor(((Guest) temp)) ;
		success_finish = true ;
		updateView() ;
		m.getMainFrame().fix() ;
		ok.setEnabled(true) ;
		return ;
	}
	
	private void updateView(){
		init() ;
		//initWindow() ;
		this.createBasic() ;
		this.createContact() ;
		this.createDetail() ;
		
		this.repaint() ;
		this.invalidate() ;
	}


	private void createBasic(){
		//头像，号，昵称，生日，省份，性别，自我介绍
		basic.removeAll() ;
		
		basic.setLayout(new GridLayout(7,1)) ;
		basic.add(getJPanel("号码：", "number", -1, false)) ;
		basic.add(getJPanel("昵称：", "nickname", -1, false)) ;
		
		//下面我们对头像进行处理。
		JPanel por_panel = new JPanel() ;
		por_panel.add(new JLabel("头像：  ")) ;
		
		JLabel icon = new JLabel("                                        ") ;
		icon.setIcon(FaceManager.getFaceIcon(guest.getPortrait(), guest.getState())) ;
		por_panel.add(icon) ;	
		basic.add(por_panel) ;
		
		/////////////////////////////////////////////////////////////////////
		JLabel sex_label = new JLabel("性别：") ;
		String[] data = {"女","男","保密                                          "} ;
		JComboBox sex_box = new JComboBox(data) ;
		sex_box.setEnabled(false) ;
		if(host.getGender() == 0){
			sex_box.setSelectedIndex(0) ;
		}else if(host.getGender() == 1){
			sex_box.setSelectedIndex(1) ;
		}else{
			sex_box.setSelectedIndex(2) ;
		}
		sex_label.setLabelFor(sex_box) ;
		JPanel sex_panel = new JPanel() ;
		sex_panel.add(sex_label) ;
		sex_panel.add(sex_box) ;		
		basic.add(sex_panel) ;

		
//		basic.add(getJPanel("头像：", "portrait", -1, false)) ;
//		basic.add(getJPanel("性别：", "gender",-1,false)) ;
		basic.add(getJPanel("省份：", "province", -1, false)) ;
		
		JPanel panel = new JPanel() ;
			JLabel _label = new JLabel("自我介绍：") ;
			
			JTextArea _area = new JTextArea( (String) variables.get("explain") ,10,20) ;
			_area.setName("explain") ;
			_area.setEditable(false) ;
			_label.setLabelFor(_area) ;
			
			JScrollPane explain_scroll = new JScrollPane(_area) ;
		
		basic.add(_label) ;
		basic.add(explain_scroll) ;
		
	}
	private void createContact(){
		contact.removeAll() ;
		//真实姓名，邮箱，zip, address
		contact.setLayout(new GridLayout(3,1)) ;
		contact.add(getJPanel("邮箱：", "mail", -1, false)) ;
		contact.add(getJPanel("邮编：", "zip", -1, false)) ;
		contact.add(getJPanel("地址：", "address", -1, false)) ;
	}
	private void createDetail(){
		detail.removeAll() ;
		
		detail.setLayout(new GridLayout(2,1)) ;
		detail.add(getJPanel("真实姓名：", "realname", -1, false)) ;
		detail.add(getJPanel("主页：", "homepage", -1, false)) ;
		
	}
	
/***********************************************************************************************************/	
	
	private JPanel getJPanel(String label, String name, int length, boolean editable){
		JPanel panel = new JPanel() ;
		JTextField _jtf = getJTextField(name,length,editable) ;
		panel.add(getJLabel(label,null,_jtf)) ;
		panel.add(_jtf) ;
		
		return panel ;
	}
	
	private JTextField getJTextField(String name, int length, boolean editable){
		if(name == null || name.length() == 0) return null ;
		
		JTextField _jtf = new JTextField(20) ;
		_jtf.setName(name) ;
		if(length > 0)
			_jtf.setColumns(length) ;
		
		String defaultvalue = (String) variables.get(name) ;
		if(defaultvalue != null && defaultvalue.length() != 0){
			_jtf.setText(defaultvalue) ;
		}else{
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++") ;
			System.out.println("not in variables, name:" + name + "  value:" + variables.get(name)) ;	
		}		
		
		_jtf.setEditable(editable) ;
		
		return _jtf ;
	}
	
	private JLabel getJLabel(String toshow, Icon icon, Component comp){
		JLabel label = new JLabel(toshow == null?"":toshow) ;
		if(icon != null) label.setIcon(icon) ;
		label.setLabelFor(comp) ;
		return label ;
	}
/**********************************************************************************************************/
	
	private JTabbedPane tab = new JTabbedPane() ;
	private JPanel basic = null ;
	private JPanel contact = null ;
	private JPanel detail = null ;
	
	private JButton ok = null ;
	private JButton cancel = null ;
	
}
