package mm.smy.bicq.user ;

/**
* show the host's information
*
*@date 2004-4-10 based on InforWindow
*@author XF
*@author e-mail:myreligion@163.com
*@copyright Copyright 2003 XF All Rights Reserved.
*/
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.event.* ;
import java.util.* ;

import mm.smy.bicq.user.* ;
import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.debug.BugWriter ;
import mm.smy.util.* ;
import mm.smy.bicq.user.manager.* ;

public class HostInforWindow extends JFrame implements ActionListener, UserPswMessageListener, ICMPMessageListener{
	private Host host = null ; //the reference from the mm.
	private Host temp_host = new Host() ;
	
	private HostInforWindow outer = this ;
	
	private  MainManager m = null ;
	
	private Hashtable variables = new Hashtable(40) ;
	
	
	public static void main(String[] args){
		Host h = new Host(1000) ;
		h.setAuth(20) ;
		//h.setOnlineAction(Guest.BOX_NOTICE) ;
		HostInforWindow window = new HostInforWindow(h,null) ;
		window.show() ;
	}
	
	public HostInforWindow(Host h, MainManager m_mm){
		m = m_mm ;
		
		m.addUserPswMessageListener(this) ;
		m.addICMPMessageListener(this) ;
				
		this.host = h ; 
		
		init() ;	
		initWindow() ;
		
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					outer.dispose() ;
				}
			}	
		) ;
	}
	
	public void dispose(){
		super.dispose() ;
		m.removeUserPswMessageListener(this) ;
		m.removeICMPMessageListener(this) ;
	}
	
	//建立variables Hashtable

	private void init(){
		//user
		variables.put("number", new Integer(host.getNumber()).toString()) ;
		variables.put("nickname", host.getNickname()==null?"":host.getNickname() ) ;
		variables.put("portrait", new Integer(host.getPortrait()).toString()) ;
		variables.put("mail", host.getMail()==null?"":host.getMail() ) ;
		variables.put("realname", host.getRealname()==null?"":host.getRealname() ) ;
		variables.put("homepage", host.getHomepage()==null?"":host.getHomepage() ) ;
		variables.put("zip", new Integer(host.getZip()).toString() ) ;
		variables.put("address", host.getAddress()==null?"":host.getAddress() ) ;
		variables.put("country", host.getCountry()==null?"":host.getCountry() ) ;
		variables.put("province", host.getProvince()==null?"":host.getProvince() ) ;
		variables.put("year", new Integer(host.year).toString() ) ;
		variables.put("month", new Integer(host.month).toString() ) ;
		variables.put("day", new Integer(host.day).toString() ) ;
		variables.put("gender", new Integer(host.getGender()).toString() ) ;
		variables.put("explain", host.getExplain()==null?"":host.getExplain() ) ;
		//host
		variables.put("auth", new Integer(host.getAuth()).toString() ) ;
		variables.put("oldpassword", host.getPassword()==null?"":host.getPassword() ) ;
		variables.put("newpassword", "") ;
		variables.put("repassword", "") ;
		//TODO: add smtp information here later....		
	}
	
	private void initWindow(){
		
		this.setSize(400,400) ;
		
		ok = new JButton("修改") ;
		ok.addActionListener(this) ;
		ok.setActionCommand("modify") ;
		
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
		safe = new JPanel() ;
		psw  = new JPanel() ;
		tab.addTab("网络安全", safe) ;	
		tab.addTab("修改密码", psw) ;
			
		createSafe() ;
		createPsw() ;
		
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
		
		//修改host资料。
		if(command.equals("modify")){
			if(tab.getSelectedComponent() == psw){
				doPsw(command) ; //修改密码的处理。
				return ;
			}
			//更新host,因为我们不知道能不能更新成功；所以我们建立临时的ｈｏｓｔ
			temp_host.copyFrom(host) ;
			temp_host.setNumber(host.getNumber()) ;
			
			temp_host.setNickname((String)variables.get("nickname")) ;
			int m_portrait = string2Int((String) variables.get("portrait")) ;
			if(m_portrait >= 0){
				temp_host.setPortrait(m_portrait) ;	
			}
			temp_host.setMail((String) variables.get("mail")) ;
			temp_host.setRealname((String) variables.get("realname")) ;
			temp_host.setHomepage((String) variables.get("homepage")) ;
			int m_zip = string2Int((String) variables.get("zip")) ;
			if(m_zip >= 0 ){
				host.setZip(m_zip) ;	
			}
			temp_host.setAddress((String) variables.get("address")) ;
			temp_host.setCounty((String) variables.get("country")) ;
			temp_host.setProvince((String) variables.get("province")) ;
			temp_host.setExplain((String) variables.get("explain")) ;
			
			String m_gender = (String) variables.get("gender") ;
			if(m_gender.equalsIgnoreCase("1")){
				temp_host.setGender(1) ;	
			}else if(m_gender.equalsIgnoreCase("0")){
				temp_host.setGender(0) ;	
			}else{
				temp_host.setGender(-1) ;	
			}
			
			int m_day = string2Int((String) variables.get("day")) ;
			if(m_day > 0)
				temp_host.setDay(m_day) ;
			int m_month = string2Int((String) variables.get("month"))  ;
			if(m_month > 0 && m_month < 13)
				temp_host.setMonth(m_month) ;
			int m_year = string2Int((String) variables.get("year")) ;
			if( m_year >= 0)
				temp_host.setYear(m_year) ;
				//host parts
			int m_auth = string2Int((String) variables.get("auth")) ;
			if(m_auth > 0)
				temp_host.setAuth(m_auth) ;
				
			System.out.println("temp_host auth is set to:" + temp_host.getAuth()) ;
					
			//创建UserInfor包，发送给server，要求更新，同时等待中....
			
			ok.setEnabled(false) ;
			
			UserInforMessage message = new UserInforMessage() ;
			message.setUser(temp_host) ;
			message.setFrom(host) ;
			message.setTo(m.getServer()) ;
			message.setMinType(UserInforMessage.UPDATE_HOST_INFOR) ;
		
	//		System.out.println("write host to net:" + (Host) message.getUser()) ;
			m.sendOutMessage(message) ;

			
			System.out.println("modify pressed.") ;
			//updateView() ;
			
		}else if(command.equals("close")){ //关闭窗口
			System.out.println("close pressed") ;
			this.dispose() ;
			//更新好友资料
		}
		return ;
	}
	
	String password ;
	
	private void doPsw(String command){
		String oldpassword = "" ;
		try{
			oldpassword = mm.smy.security.Digest.SHA_1((String) variables.get("oldpassword")) ;
		}catch(Exception e){
			System.out.println("erro to encrypt") ;
			e.printStackTrace() ;
			//this shouldnot happen if user can login in.
		}
		
		String newpassword = (String) variables.get("newpassword") ;
		String repassword = (String) variables.get("repassword") ;
		
		System.out.println("old password:" + host.getPassword()) ;
		
		if((host.getPassword() == null || host.getPassword().length() == 0 )||oldpassword.equals(host.getPassword())){
			if(newpassword == null || repassword == null){ //password could not be null.
				JOptionPane.showMessageDialog(outer,"新密码不能为空","密码无效",JOptionPane.ERROR_MESSAGE) ;
				return ;
			}else if(!newpassword.equals(repassword)){
				JOptionPane.showMessageDialog(outer,"两次输入的新密码不一样","密码无效",JOptionPane.ERROR_MESSAGE) ;
				return ;
			}
			//do modify password
			try{
				newpassword = mm.smy.security.Digest.SHA_1((String) variables.get("newpassword")) ;
			}catch(Exception e){
				System.out.println("erro to encrypt") ;
				e.printStackTrace() ;
				//this shouldnot happen if user can login in.
			}
			ok.setEnabled(false) ;
			UserPswMessage upm = new UserPswMessage() ;
			upm.setMinType(UserPswMessage.MODIFY_PSW_REQUEST) ;
			upm.setPassword(oldpassword) ;
			upm.setNewPassword(newpassword) ;
			m.sendOutMessage(upm) ;
			
			password = newpassword ;
			return ;
		}else{ // old password error
			JOptionPane.showMessageDialog(outer,"旧密码错误","旧密码无效",JOptionPane.ERROR_MESSAGE) ;
		}
				
	}
	
	public void userPswMessageAction(UserPswMessage message){
		if(message == null) return ;
		if(message.getMinType() == UserPswMessage.MODIFY_PSW_FAILED){			
			JOptionPane.showMessageDialog(outer,message.getExplain(),"密码修改失败",JOptionPane.ERROR_MESSAGE) ;
			ok.setEnabled(true) ;
		}else if(message.getMinType() == UserPswMessage.MODIFY_PSW_OK){
			host.setPassword(password) ;
			JOptionPane.showMessageDialog(outer,"密码修改成功","修改密码成功",JOptionPane.PLAIN_MESSAGE) ;
			ok.setEnabled(true) ;
		}	
	}
	
	public void ICMPMessageAction(ICMPMessage message){
		if(message == null) return ;
		if(message.getMinType() == ICMPMessage.UPDATE_HOST_INFOR_RESULT){
			//	Value  : UPDATE_HOST_INFOR_RESULT
			//	content: int.toString() 1:OK  2:some packets lost, please send again. 
			//                          3. 用户根本就没有登陆纪录！ 4.更新数据库失败。
			if("1".equals(message.getContent())){
				host.copyInfor(temp_host) ; //复制到以前的引用中去。
				JOptionPane.showMessageDialog(outer,"新资料更新成功","修改资料成功",JOptionPane.PLAIN_MESSAGE) ;
			}else if("2".equals(message.getContent())){
				JOptionPane.showMessageDialog(outer,"网络不稳定或是你的个人说明太长了","修改资料失败",JOptionPane.ERROR_MESSAGE) ;
			}else if("3".equals(message.getContent())){
				JOptionPane.showMessageDialog(outer,"服务器不能确定你登陆了","修改资料失败",JOptionPane.ERROR_MESSAGE) ;
			}else if("4".equals(message.getContent())){
				JOptionPane.showMessageDialog(outer,"服务器出错，数据库更新失败","修改资料失败",JOptionPane.ERROR_MESSAGE) ;
			}
			ok.setEnabled(true) ;
		}	
	}
	
	private void updateView(){
		host = m.getHost() ;
		
		init() ;
		//initWindow() ;
		this.createBasic() ;
		this.createContact() ;
		this.createDetail() ;
		this.createPsw() ;
		this.createSafe() ;
		
		this.repaint() ;
		this.invalidate() ;
	}


/*********************************************************************************************************/	
	private int string2Int(String s){
		if( s == null || s.trim().length() == 0) return -1 ;
		
		int temp = -1 ;	
		try{
			temp = new Integer(s).intValue() ;	
		}catch(Exception e){
			return -1 ;
		}
		return temp ;
	}
	
/**********************************************************************************************************/	
	private void createBasic(){
		//头像，号，昵称，生日，省份，性别，自我介绍
		basic.removeAll() ;
		
		basic.setLayout(new GridLayout(7,1)) ;
		basic.add(getJPanel("号码：", "number", -1, false)) ;
		basic.add(getJPanel("昵称：", "nickname", -1, true)) ;
		//下面我们对头像进行处理。
		JPanel por_panel = new JPanel() ;
		por_panel.add(new JLabel("头像：                                      ")) ;
		
		box = new JComboBox() ;
		
		box.setEditable(false) ;
		box.setModel(new PortraitListModel()) ;
		box.setRenderer(new PortraitCellRender()) ;
		box.getModel().addListDataListener(new PortraitListener()) ;		
		box.setSelectedIndex(host.getPortrait() - 1) ;
		por_panel.add(box) ;	
		basic.add(por_panel) ;
		
		/////////////////////////////////////////////////////////////////////
		JLabel sex_label = new JLabel("性别：") ;
		String[] data = {"女","男","保密                                        "} ;
		JComboBox sex_box = new JComboBox(data) ;
		sex_box.setEditable(false) ;
		sex_box.getModel().addListDataListener(new SexListener()) ;
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
		
		basic.add(getJPanel("省份：", "province", -1, true)) ;
		
		JPanel panel = new JPanel() ;
			JLabel _label = new JLabel("自我介绍：") ;
			
			JTextArea _area = new JTextArea( (String) variables.get("explain") ,10,20) ;
			_area.setName("explain") ;
			_area.setEditable(true) ;
			_area.addKeyListener(new TextFieldListener(_area)) ;
			_label.setLabelFor(_area) ;
			
			JScrollPane explain_scroll = new JScrollPane(_area) ;
		//panel.add(_label) ;
		//panel.add(_area) ;
		
		//basic.add(panel) ;
		basic.add(_label) ;
		basic.add(explain_scroll) ;
		//basic.add(getJTextField("自我介绍：", "nickname", -1, true)) ;
		
	}
	private void createContact(){
		contact.removeAll() ;
		//真实姓名，邮箱，zip, address
		contact.setLayout(new GridLayout(3,1)) ;
		contact.add(getJPanel("邮箱：", "mail", -1, true)) ;
		contact.add(getJPanel("邮编：", "zip", -1, true)) ;
		contact.add(getJPanel("地址：", "address", -1, true)) ;
	}
	private void createDetail(){
		detail.removeAll() ;
		
		detail.setLayout(new GridLayout(2,1)) ;
		detail.add(getJPanel("真实姓名：", "realname", -1, true)) ;
		detail.add(getJPanel("主页：", "homepage", -1, true)) ;
		
	}
	private void createSafe(){
		safe.removeAll() ;
	//创建安全选项。
		ButtonGroup group = new ButtonGroup() ;
		JRadioButton b1 = getJRadioButton("允许任何人把我加为好友" , "auth" , Host.ALLOW_ANYONE , true ) ;
		JRadioButton b2 = getJRadioButton("需要我的验证" , "auth" , Host.MY_PERMIT , true ) ;
		JRadioButton b3 = getJRadioButton("拒绝任何人把我加为好友" , "auth" , Host.NO_DISTURB , true ) ;
		if(true){
			if(host.getAuth() ==Host.NO_DISTURB){
				b3.setSelected(true) ;	
			}else if(host.getAuth() == Host.MY_PERMIT){
				b2.setSelected(true) ;	
			}else{
				b1.setSelected(true) ;	
			}
		}
		group.add(b1) ;
		group.add(b2) ;
		group.add(b3) ;
		safe.setLayout(new GridLayout(3,1)) ;
		safe.add(b1) ;
		safe.add(b2) ;
		safe.add(b3) ;
		return ;		
	}
	private void createPsw(){
		psw.removeAll() ;
		
		psw.setLayout(new GridLayout(3,1)) ;
		psw.setAlignmentX(JLabel.LEFT_ALIGNMENT) ;
		psw.add(this.getJPasswordPanel("旧密码：", "oldpassword", -1, true) ) ;
		psw.add(this.getJPasswordPanel("新密码：", "newpassword", -1, true) ) ;
		psw.add(this.getJPasswordPanel("重复新密码：", "repassword", -1, true) ) ;
	}
	
/***********************************************************************************************************/	
	private JRadioButton getJRadioButton(String label, String name, int value,  boolean editable){
		if ( name == null || name.length() == 0 ) return null ;
		JRadioButton button = new JRadioButton(label) ;
		button.setFocusable(editable) ;
		button.setName(name) ;
		button.setActionCommand(name) ;
		if(((String)variables.get(name)).equals(new Integer(value).toString())){
			button.setSelected(true) ;
		//	System.out.println("+++++++++++++++++++++++++++++++++++++++++") ;
		//	System.out.println("radio selection:" + variables.get(name)) ;
			
		}
		button.addActionListener(new RadioActionListener(value)) ;
		return button ;
	}
	
	private class RadioActionListener implements ActionListener{
		int value = -1 ;
		
		public RadioActionListener(int m_value){
			value = m_value ;
		}
		
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand() ;
			variables.put(command,new Integer(value).toString()) ;	
		//	System.out.println("command is:" + command + "   value:" + value) ;		
		}
	}
	
	
	
	private JPanel getJPasswordPanel(String label, String name, int length, boolean editable){
		if(name == null || name.length() == 0 ) return null ;
		
		JLabel _label = new JLabel(label) ;	
		length = length>0?length:20 ;
		
		JPasswordField field = new JPasswordField(length) ;
		field.setEchoChar('\u263a') ;
		
		field.setName(name) ;
		field.addKeyListener(new TextFieldListener(field) ) ;
		_label.setLabelFor(field) ;
		
		JPanel panel = new JPanel() ;
		panel.add(_label) ;
		panel.add(field) ;
		
		return panel ;
	}

	
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
		//	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++") ;
		//	System.out.println("not in variables, name:" + name + "  value:" + variables.get(name)) ;	
		}
		
		
		
		_jtf.setEditable(editable) ;
		_jtf.addKeyListener(new TextFieldListener(_jtf)) ;
		return _jtf ;
	}
	
	private class TextFieldListener implements KeyListener{
		JComponent _jtf = null ;
		public TextFieldListener(JComponent jtf){
			_jtf = jtf ;
		}
		
		public void keyPressed(KeyEvent e){} 
		public void keyReleased(KeyEvent e){
			_jtf.invalidate() ;
			
			String temp = null ;
			if(_jtf instanceof AbstractButton){
				temp = ((AbstractButton) _jtf).getText() ;	
			}else if(_jtf instanceof JPasswordField){
				temp = ((JPasswordField) _jtf).getText() ;	
			}else if(_jtf instanceof JTextField){
				//not a good one. but now we use it.
				temp = ((JTextField) _jtf).getText() ;	
			}else if(_jtf instanceof JTextArea){
				temp = ((JTextArea) _jtf).getText() ;	
			}else if(_jtf instanceof JLabel){
				temp = ((JLabel) _jtf).getText() ;
			}
			if(temp != null){
				variables.put(_jtf.getName(),temp == null?"":temp) ;
			//	System.out.println(temp) ;
			}
			return ;			
		}
		public void keyTyped(KeyEvent e){
							
		}
	}
	
	private JLabel getJLabel(String toshow, Icon icon, Component comp){
		JLabel label = new JLabel(toshow == null?"":toshow) ;
		if(icon != null) label.setIcon(icon) ;
		label.setLabelFor(comp) ;
		return label ;
	}
/**********************************************************************************************************/

	class SexListener implements ListDataListener{ 
		public void contentsChanged(ListDataEvent e){
			int i = e.getIndex0() ;
			if(i == 0){
				variables.put("gender","0") ;
			}else if( i == 1){
				variables.put("gender", "1") ;
			}else{
				variables.put("gender", "-1") ;
			}
		}
		
		public void intervalAdded(ListDataEvent e){
		}
				
		public void intervalRemoved(ListDataEvent e){					
		}
	}

	class PortraitListener implements ListDataListener{ 
		public void contentsChanged(ListDataEvent e){
			 PortraitObject obj = (PortraitObject)box.getModel().getSelectedItem() ;
			 if(obj == null) return ;
			 variables.put("portrait", new Integer(obj.getPortrait()).toString()) ;			 	
		}
		
		public void intervalAdded(ListDataEvent e){			
		}
				
		public void intervalRemoved(ListDataEvent e){					
		}
	}

/**********************************************************************************************************/
	
	private JTabbedPane tab = new JTabbedPane() ;
	private JPanel basic = null ;
	private JPanel contact = null ;
	private JPanel detail = null ;
	private JPanel safe = null ;
	private JPanel psw = null ;
	
	private JButton ok = null ;
	private JButton cancel = null ;
	private JComboBox box = null ; //头像选择列表。
	
}
