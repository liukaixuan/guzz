package mm.smy.bicq ;

/*
* 刚刚接到妹妹的电话，她现在在火车上。没有座位，车上还那么挤，一个女孩子家……
* 而我那儿，去坐在寝室里；面对着电脑，悠悠闲闲的！
* 妹妹千里迢迢的来看偶，偶为什么就不能为她分担点痛苦哪？？
* 
* 以后要更加努力点儿，尽快完成BICQ的初级任务；以此献给偶最好妹妹--水星妹妹！
* 2003-9-30
* 
*/

/**
* 提供对 主显示窗口 的管理。
* 包括：
* 	搜索管理
* 	系统消息管理
* 	验证消息管理
*	.........
* 	
* 	Guest/GuestGroup显示与安排，他们的管理由mm处理。
*
* 该部分存在美工问题，可在以后修正
*       存在技术问题，动画效果，好友组切换等等。
* 先做为测试用吧。
*/
/**
* 现在的问题是：我们必须一个Hashtable对所有的GuestPanel进行管理。
* 告诉他们好友的状态，是不是在改变状态.
*
*
*
*
*
*
*
*/

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import java.util.* ;

import mm.smy.bicq.message.* ;
import mm.smy.bicq.user.* ;
import mm.smy.bicq.user.manager.GuestGroupManager ;
import mm.smy.bicq.search.* ;
import mm.smy.bicq.login.* ;

import mm.smy.bicq.sound.PlaySound ;

public class MainFrame extends JFrame implements ActionListener{
	
	private Hashtable guestgroups = null ;
	private GuestGroup current = null ;
	private GuestGroupDeal ggd = new GuestGroupDeal() ;//inner class
	private	ButtomDeal bd = new ButtomDeal() ; //inner class
	private GuestDeal gd = new GuestDeal() ; //inner class
	private MainManager m = null ;
	private MainFrame  outer = this ; // for inner class using
	
	private WindowTray tray = null ;
	
	private boolean withtray = false ; //是否携带快速启动的小图标。
	
	Dimension size = new Dimension(200,700) ; //mf size
	/////////////////////////////////////////////////////////////////////////
/*
	public MainFrame(){
		this.setSize(size) ;
		test() ;
	}
*/	
	public void close(){
		if(withtray) tray.close() ;
		
		m.close() ;
	}
	
	public MainFrame(MainManager m_mm){
		this.setSize(size) ;
		m = m_mm ;
		init() ;
		if(withtray)
			tray = new WindowTray(m) ;
	}
	
	public void show(){
		super.show() ;
	//	setSize(size) ;		
	}
	
	//初始化guestgroups等等资料。
	public void init(){
		
		try{
			this.setIconImage(FaceManager.loadBitmap("symbol.bmp")) ;
		}catch(Exception e){
			//load symbol failed, no case.
		}
		
		this.addWindowListener(
			new WindowAdapter(){
				public void windowActivated(WindowEvent e){}
				public void windowClosed(WindowEvent e){}
				public void windowDeactivated(WindowEvent e){}
			//	public void windowDeiconified(WindowEvent e){
			//		super.windowde
			//	}
				public void windowOpened(WindowEvent e){}
				
				public void windowClosing(WindowEvent e){
					outer.close() ;				
				}
				public void windowIconified(WindowEvent e){
					if(withtray){
						outer.hide() ;
					}
					
//					size = outer.getSize() ;
				//	windowDeiconified() ;
				//	outer.dispatchEvent(new WindowEvent(e.getWindow(),WindowEvent.WINDOW_DEICONIFIED)) ;
				//	outer.(
				}
			}
		) ;
		guestgroups = m.getGuestGroups() ;
	//	guests = m.getGuests() ;
		if(guestgroups == null){
			System.out.println("guestgroups is null whiling init the mf.") ;			
		}
		current = (GuestGroup) guestgroups.get("我的好友") ; //当前组。
		
		initWindow() ;
		
		fix() ; //重绘组信息
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
		//最顶层的任务栏，目前只有聊天功能，以后可加入许多其他的东西。
		private JButton chat = new JButton("聊天") ;
		private JButton mail = new JButton("邮件") ;
	
		//中间部分，好友的显示；好友分组的显示
		JPanel center_top = new JPanel() ;
		JPanel center_buttom = new JPanel() ;
		JPanel center_center = new JPanel() ;
		
		private JButton currentGroupButton = null ;
		JPanel center_top_withcurrent = new JPanel(new GridLayout(2,1)) ;
		JPanel JPanel_center = new JPanel() ;
	
		//最下层的，用于一些附加的功能	
		private JButton message_button = new JButton("消息") ;
		private JButton state_button = new JButton("状态") ;
		private JButton system_button = new JButton("系统") ;
		private JButton search_button = new JButton("查找") ;
	
	
		//Pop up menu .. Group
		private JPopupMenu popgroup = new JPopupMenu("popgroup") ;
		private JMenuItem popaddgroup = new JMenuItem("添加组") ;
		private JMenuItem popdeletegroup = new JMenuItem("删除组") ;
		private JMenuItem popmodifygroup = new JMenuItem("修改组") ;
		private JMenuItem bigportait = new JMenuItem("大图标") ;
		private JMenuItem smallportait = new JMenuItem("小图标") ;
		private JMenuItem search_guest = new JMenuItem("添加用户") ;
	
		//Pop up .... single guest
		JPopupMenu guestpopup  = new JPopupMenu("guestpopup") ;
		JMenuItem  sendmessage = new JMenuItem("发送消息") ;
		JMenuItem  deleteguest = new JMenuItem("删除好友") ;
		JMenuItem  showinfor   = new JMenuItem("察看资料") ;
		JMenuItem  chatlog     = new JMenuItem("聊天纪录") ;
		JMenu      moveto      = new JMenu("移动到") ;
	
	//重新标志可发送到组。	
	//@parame isFriend 当前选择的用户是不是好友。
	public void fixMoveTo(boolean isFriend){
		//在这儿我们初始化好友移动到别的组。
		moveto.removeAll() ;
		Enumeration ggs = m.getGuestGroups().keys() ;
		while(ggs.hasMoreElements()){
			String groupname = ggs.nextElement().toString() ;
			JMenuItem item = new JMenuItem(groupname) ;
			item.addActionListener(gd) ;
			item.setActionCommand("moveto@" + groupname) ;
			if(current == null ||current.getGroupname().equals("黑名单") || current.getGroupname().equals("陌生人")){
				if(groupname.equals("黑名单") || groupname.equals("陌生人"))
					item.setEnabled(true) ;
				else 
					item.setEnabled(false) ;
			}
			if(current != null && groupname.equals(current.getGroupname())){
				item.setEnabled(false) ;
			}
			moveto.add(item) ;
		}
	}
	
	private void initWindow(){
			sendmessage.addActionListener(gd) ;
			sendmessage.setActionCommand("sendmessage") ;
			
			deleteguest.addActionListener(gd) ;
			deleteguest.setActionCommand("deleteguest") ;
			
			showinfor.addActionListener(gd) ;
			showinfor.setActionCommand("showinfor") ;
			
			chatlog.addActionListener(gd) ;
			chatlog.setActionCommand("chatlog") ;
			
			guestpopup.add(sendmessage) ;
			guestpopup.add(deleteguest) ;
			guestpopup.add(showinfor) ;
			guestpopup.add(chatlog) ;
			guestpopup.add(moveto) ;

	//	JPopupMenu.setDefaultLightWeightPopupEnabled(false) ;

		//pop up menu ... Group init
			popaddgroup.setActionCommand("addgroup") ;
			popaddgroup.addActionListener(ggd) ;
			
			popdeletegroup.setActionCommand("deletegroup") ;
			popdeletegroup.addActionListener(ggd) ;
			
			popmodifygroup.setActionCommand("modifygroup") ;
			popmodifygroup.addActionListener(ggd) ;
			
			bigportait.setActionCommand("bigportrait") ;
			bigportait.addActionListener(ggd) ;
			
			smallportait.setActionCommand("smallportrait") ;
			smallportait.addActionListener(ggd) ;
			
			//注意：该item使用ButtonDeal的处理函数！
			search_guest.setActionCommand("search") ;
			search_guest.addActionListener(bd) ;
			
			popgroup.add(popaddgroup) ;
			popgroup.add(popdeletegroup) ;
			popgroup.add(popmodifygroup) ;
			popgroup.add(bigportait) ;
			popgroup.add(smallportait) ;
			popgroup.add(search_guest) ;
			
			//最顶层的任务栏，目前只有聊天功能，以后可加入许多其他的东西。
			JPanel top = new JPanel() ;
			top.add(chat) ;
			top.add(mail) ;
		
			//中间部分，好友的显示；好友分组的显示
			//外壳
		
			JPanel_center.setLayout(new BorderLayout()) ;
			JScrollPane scroll_guestgroups = new JScrollPane(JPanel_center) ;	
			//分三部分:上面的组;当前组成员;下面的组.

			JScrollPane scroll_center_center = new JScrollPane(center_center) ;
		
		

		
			JPanel_center.add(center_top_withcurrent,BorderLayout.NORTH) ;
			JPanel_center.add(scroll_center_center,BorderLayout.CENTER) ;
			JPanel_center.add(center_buttom,BorderLayout.SOUTH) ;
		
			//JPanel_center.addMouseListener(ggd) ;
		
		//最下层的，用于一些附加的功能
		
		search_button.setActionCommand("search") ;
		search_button.addActionListener(bd) ;
		state_button.setActionCommand("state") ;
		state_button.addActionListener(bd) ;
		
		//message_button is managered by a single class: mm.smy.bicq.MessageButtonDeal.class
		message_button.addActionListener(new MessageButtonDeal(m, message_button)) ;
		
		system_button.setActionCommand("system") ;
		system_button.addActionListener(bd) ;
		
		JPanel buttom = new JPanel(new GridLayout(2,2)) ;
		buttom.add(message_button.getText(), message_button) ;
		buttom.add( state_button.getText(), state_button) ;		
		buttom.add(system_button.getText(), system_button) ;
		buttom.add(search_button.getText(), search_button) ;
		

		Container cp = this.getContentPane() ;
		cp.setLayout(new BorderLayout()) ;
		cp.add(top,BorderLayout.NORTH) ;
		cp.add(scroll_guestgroups,BorderLayout.CENTER) ;
		cp.add(buttom,BorderLayout.SOUTH) ;
		
		//初始化其他部分,状态初始化.
		this.initState() ;
		this.initSystem() ;
		
	}
	
	/**
	* 刷新所有mf上的东西，例如验证消息，好友组，请求等等。
	*
	*
	*
	*
	*/
	public void fixAll(){
		fixState() ;
		fix() ;
	}
	
	public void fixState(){
		initState() ;
	}
	
	/**
	* 刷新好友组，好友状态等等。
	*
	*
	*/
	public void fix(){
		if(current == null) return ;
		center_top.removeAll() ;
		center_center.removeAll() ;
		center_buttom.removeAll() ;
		
		Enumeration e = guestgroups.elements() ;
		Vector min = new Vector() ;
		Vector max = new Vector() ;
		GuestGroup tempgroup = null ;
		while(e.hasMoreElements()){
			tempgroup = (GuestGroup) e.nextElement() ;
			if(tempgroup.getCreateTime().after(current.getCreateTime())){
				min.add(tempgroup) ;
			}else if(tempgroup.getCreateTime().before(current.getCreateTime())){
				max.add(tempgroup) ;
			}
		}
		
		JButton temp_button = null ;
		//做center_top
		if(max.size() < 1 ){
			System.out.println("总的组数为0，max太小。可能有问题出现") ;
		}else{
			center_top.setLayout(new GridLayout(max.size(),1)) ;
			for(int i = 0 ; i< max.size() ; i++){
				tempgroup = (GuestGroup) max.elementAt(max.size() - i - 1) ;	
				temp_button = new JButton(tempgroup.getGroupname()) ;
				temp_button.setActionCommand(tempgroup.getGroupname()) ;
				temp_button.addMouseListener(ggd) ; /////////////////////////////////////////////////
				temp_button.addActionListener(ggd) ;				
				center_top.add(temp_button) ;
			}
			/*
			e = max.elements() ;
			while(e.hasMoreElements()){
				tempgroup = (GuestGroup)e.nextElement() ;
				temp_button = new JButton(tempgroup.getGroupname()) ;
				temp_button.setActionCommand(tempgroup.getGroupname()) ;
				temp_button.addMouseListener(ggd) ; /////////////////////////////////////////////////
				temp_button.addActionListener(ggd) ;				
				center_top.add(temp_button) ;
			}
			*/
		}
		//draw current
		
		center_center.setLayout(new GridLayout(current.size() + 1 ,1)) ;	
		currentGroupButton = null ;
		currentGroupButton = new JButton(current.getGroupname()) ;		
		//System.out.println("paiting current:" + current.getGroupname()) ;
		currentGroupButton.setText(current.getGroupname()) ;
		currentGroupButton.addActionListener(ggd) ;
		currentGroupButton.addMouseListener(ggd) ;
		
		center_top_withcurrent.removeAll() ;
		center_top_withcurrent.add(center_top) ;
		center_top_withcurrent.add(currentGroupButton) ;
		center_top_withcurrent.repaint() ;
		center_top_withcurrent.validate() ;
		//绘制center_center
		/***************************************************************************************************/		
		
	
		Guest temp_guest = null ;
		GuestPanel temp_gp = null ; //用来代表每个Guest的单位。
		if(current.size() > 0){
			e = current.getAllGuests().elements() ;
			while(e.hasMoreElements()){
				temp_guest = (Guest) e.nextElement() ;
				//temp_button = new JButton(temp_guest.getNumber() + "") ;
				
				//我们不打算显示 服务器 作为用户的好友。所以隐蔽掉
				if(temp_guest.equals(m.getServer())) continue ;
				
				
				temp_gp = new GuestPanel(temp_guest,false) ;
				temp_gp.addActionListener(this) ;
				temp_gp.addMouseListener(gd) ;
				center_center.add(temp_gp.getText(),temp_gp) ;
			}
		}else{
			center_center.add(new Label("")) ;
		}
		
		center_center.addMouseListener(ggd) ;
		
		//绘制center_buttom
		
		if(min.size() > 0){
			center_buttom.setLayout(new GridLayout(min.size(),1)) ;
			
			for(int i = 0 ; i < min.size(); i++){
				tempgroup = (GuestGroup) min.elementAt(min.size() - i - 1 ) ;	
				temp_button = new JButton(tempgroup.getGroupname()) ;
				temp_button.setActionCommand(tempgroup.getGroupname()) ;
				temp_button.addMouseListener(ggd) ; /////////////////////////////////////////////////
				temp_button.addActionListener(ggd) ;
				center_buttom.add(temp_button) ;
			}
		}else{
			center_buttom.add(new Label("")) ;
		}
		JPanel_center.validate() ;
		this.repaint() ;
		this.invalidate() ;
	}

	
	
	//显示聊天窗口。这儿是对Guest按钮的监听。
	public void actionPerformed(ActionEvent e){
		System.out.println("ActionEvent.commandline:" + e.getActionCommand()) ;
		int temp_number = 0 ;
		try{
			temp_number = new Integer(e.getActionCommand().trim()).intValue() ;
		}catch(Exception e1){
			System.out.println("no such user " + e.getActionCommand() + "==>" + e1.getMessage()) ;
			return ;
		}
		//if( m == null){
		//	m = new MainManager() ;
		//}
//		System.out.println("*********************actionPerformed, temp_number is " + temp_number ) ;
	//	User temp = m.getGuest(temp_number) ;
	//	System.out.println("getGuest(int) gets is null:" + (temp==null)) ;
//		m.getChatWindowManager().showChatWindow(m.getGuest(temp_number)) ;
				
	}
	
//	public static void main(String[] args){
//		MainManager mm = new MainManager() ;
//		MainFrame mf = new MainFrame(mm) ;
//		MainFrame mf = new MainFrame() ;
		//mf.setSize(400,700) ;
//		mf.show() ;
//	}
	
	//这儿是个内部类，它的用处就是解决 好友组的问题[切换，右键等等]
	class GuestGroupDeal implements ActionListener,MouseListener{
		public GuestGroupDeal(){}
		
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand() ;
			GuestGroupManager ggm = null ;
			//add methods to deal with the right click popupMenu choosing!
			if(command.equalsIgnoreCase("addgroup")){
				//addgroup
				ggm = new GuestGroupManager(guestgroups) ;
				ggm.show() ;
				outer.fix() ;
				return ;
			}else if(command.equalsIgnoreCase("deletegroup")){
				//delete group
				ggm = new GuestGroupManager(guestgroups) ;
				ggm.show() ;
				outer.fix() ;
				return ;
			}else if(command.equalsIgnoreCase("modifygroup")){
				//modify group
				ggm = new GuestGroupManager(guestgroups) ;
				ggm.show() ;
				outer.fix() ;
				return ;	
			}else if(command.equalsIgnoreCase("bigportrait")){
				//show guests in big portrait mode
				return ;
			}else if(command.equalsIgnoreCase("smallportrait")){
				//show guests in small portrait mode.
				return ;
			}
			
			//click the groupname to change the current group.
			//This will be the last to be deal.
			String pre = current.getGroupname() ;
			
			GuestGroup inner_gg = null ;			
			if(!pre.equalsIgnoreCase(command)){
				inner_gg = (GuestGroup) guestgroups.get(command) ;
				if(inner_gg == null){
					System.out.println("Inner class GroupDeal found new such guesgroup:" + command) ;
					return ;
				}
				current = inner_gg ;
				//play sound
				PlaySound.play(PlaySound.FOLDER) ;
				System.out.println("Group Button pressed,groupname:" + inner_gg.getGroupname()) ;
				fix() ;					
			}
			return ;	
		}
		
		public void mouseClicked(MouseEvent e){
			//System.out.println("mouse event captured:" + e.paramString() ) ;
			center_center.repaint() ;////////////////////////////////////////////////////////////////////////
			
			if(e.getButton() == MouseEvent.BUTTON3){
		//	if(e.isPopupTrigger()){
			try{
				//if(e.getSource().getClass().isInstance(Class.forName("javax.swing.JButton"))){
					popgroup.show((JComponent)e.getSource(), e.getX(), e.getY()) ;
				//	System.out.println("Group Button2 pressed trigger:" + e.paramString()) ;
				//}else{
				//	popgroup.show((JPanel)e.getSource(),e.getX(),e.getY()) ;
				//}
			}catch(Exception error){
				System.out.println("mouseClicked(..) throw exception:" + error.getMessage() ) ;	
			}
			}
		
		}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		
	}
/****************************************************************************************************/	
	/**
	* 提供对下面四个按钮的处理。
	* search_button, system_button, message_button, state_button
	* 
	*/
	/**
	* 为了实现对state按钮的显示，我们用一个JPopupMenu。
	* 名字为statepopup
	*
	*
	*
	*/
	JPopupMenu statepopup = new JPopupMenu("state") ;
	JMenu  temp_leave = new JMenu("离开") ;
	
	private void initState(){
		statepopup.removeAll() ;
		
		addStatePopupMenu(statepopup,null,"上线            ","online") ;
		addStatePopupMenu(statepopup,null,"下线","offline") ;
		statepopup.add(temp_leave) ;
		addStatePopupMenu(statepopup,null,"隐身","hide") ;
		
		//离开原因
		temp_leave.removeAll() ;
		
		Vector reasons = m.getHost().getAllMyWords() ;
		if(reasons == null || reasons.size() == 0){
			addStatePopupMenu(temp_leave,null,"吃饭去了","temp_leave") ;
			addStatePopupMenu(temp_leave,null,"出去有事，马上回来","temp_leave") ;
			addStatePopupMenu(temp_leave,null,"工作中……","temp_leave") ;
		}else{
			Enumeration temp_e = reasons.elements() ;
			while(temp_e.hasMoreElements()){
				String s = (String) temp_e.nextElement() ;
				addStatePopupMenu(temp_leave,null,s,"temp_leave") ;	
			}	
		}
		addStatePopupMenu(temp_leave,null,"编辑","temp_leave") ; //编辑用户留言。
		
		temp_leave.repaint() ;
		temp_leave.invalidate() ;
		
		statepopup.repaint() ;
		statepopup.invalidate() ;
		
	}
	
	JPopupMenu systempopup = new JPopupMenu("system") ;
	JMenu systemclothes = new JMenu("更改皮肤") ;
	JMenu systemhelp = new JMenu("帮助") ;
	JMenu systemmanager = new JMenu("资料管理") ;
	
	private void initSystem(){
		systempopup.add(systemhelp) ;
		systempopup.add(systemmanager) ;
		systempopup.add(systemclothes) ;
		addStatePopupMenu(systempopup,null,"更改用户","changeuser") ;
		addStatePopupMenu(systempopup,null,"个人资料","personal") ;
		addStatePopupMenu(systempopup,null,"系统设定","settings") ;	
		addStatePopupMenu(systempopup,null,"注册向导","register") ;
		addStatePopupMenu(systempopup,null,"退出","quit") ;
		
		addStatePopupMenu(systemclothes,null,"默认","clothes") ;
		systemclothes.addSeparator() ;
		addStatePopupMenu(systemclothes,null,"粉红色的回忆","clothes") ;
		addStatePopupMenu(systemclothes,null,"机器猫小叮当","clothes") ;
		addStatePopupMenu(systemclothes,null,"Windows 2006","clothes") ;
		
		addStatePopupMenu(systemhelp,null,"用户向导","help") ;
		addStatePopupMenu(systemhelp,null,"建议与意见","help") ;
		addStatePopupMenu(systemhelp,null,"皮肤设计","help") ;
		addStatePopupMenu(systemhelp,null,"功能扩展","help") ;
		addStatePopupMenu(systemhelp,null,"编程人员向导","help") ;
		addStatePopupMenu(systemhelp,null,"源代码与文档","help") ;
		addStatePopupMenu(systemhelp,null,"参与人员","help") ;
		
		addStatePopupMenu(systemmanager,null,"好友管理","manager") ;
		addStatePopupMenu(systemmanager,null,"聊天记录管理","manager") ;
		addStatePopupMenu(systemmanager,null,"系统消息管理","manager") ;		
		
	}
	
	private void addStatePopupMenu(JComponent des , Icon icon, String text, String command){
		JMenuItem item = new JMenuItem(text,icon) ;
		if(command != null) item.setActionCommand(command) ;
		
		item.addActionListener(bd) ;
		des.add(item) ;
		return ;
	}
	
/****************************************************************************************************/
//对窗口最底部四个按钮的管理。
	
	class ButtomDeal implements ActionListener{
		public ButtomDeal(){}
		
		private SearchGuestManager sgm = null ;
		
		public void actionPerformed(ActionEvent e){
			String inner_command = e.getActionCommand().trim().toLowerCase() ;
			
			if(e.getSource() instanceof JButton){
				JButton temp_button = (JButton) e.getSource() ;
				if(inner_command.equals("search")){ //search command is pressed.
					if(sgm == null){
						sgm = new SearchGuestManager(m) ;
					}else{
						if(sgm.isWorking()) return ;
						sgm.rework() ;	
					}
				}else if(inner_command.equals("message")){
					//message process.....	
				}else if(inner_command.equals("state")){
					statepopup.show(temp_button, temp_button.getX() - temp_button.getWidth() , temp_button.getY() ) ;				
				}else if(inner_command.equals("system")){
					systempopup.show(temp_button, 0 , 0) ;
				}
				
				return ;
			}
			
			
			if(e.getSource() instanceof JMenuItem){
				JMenuItem temp_item = (JMenuItem)e.getSource() ;
				
				//处理state改变时的消息
				if(inner_command.equals("online")){
					state_button.setText("在线") ;
					m.getStateChangedManager().setHostState(User.ONLINE) ;
					
				}else if(inner_command.equals("offline")){
					state_button.setText("离线") ;
					m.getStateChangedManager().setHostState(User.OFFLINE) ;
				}else if(inner_command.equals("hide")){
					state_button.setText("隐身") ;
					m.getStateChangedManager().setHostState(User.HIDDEN) ;
				}else if(inner_command.equals("temp_leave")){
					String temp_word = temp_item.getText() ;
					if(temp_word.equalsIgnoreCase("编辑")){
						m.getStateChangedManager().eidtMyWords() ;
						return ;	
					}
					state_button.setText(temp_word) ;
					m.getStateChangedManager().setTempLeaveWord(temp_word) ;
					m.getStateChangedManager().setHostState(User.LEAVE) ;
					//System.out.println("bd reports command:" + temp_item.getText()) ;
				}
				
				//处理system的消息。
				if(inner_command.equals("changeuser")){ //更改用户
					//System.out.println("change user") ;
				//	m.closeSession() ;
				//	BICQ bicq = new BICQ() ;
					
				}else if(inner_command.equals("personal")){ //更改个人资料
					m.getUserManager().showUserInfor(m.getHost()) ;
				}else if(inner_command.equals("settings")){ //设定
					BICQ.exec("notepad " + System.getProperty("user.dir") + "/server.ini") ;
				}else if(inner_command.equals("register")){ //注册向导

				}else if(inner_command.equals("quit")){
					m.close() ;
				}else if(inner_command.equals("clothes")){
					
				}else if(inner_command.equals("help")){
					BICQ.openURL("http://nic.biti.edu.cn/vbb/showthread.php?s=&threadid=123565") ;
				}else if(inner_command.equals("manager")){
					
				}
				
				
			}
		}
	}
/*********************************************************************************************************/

	/**
	* 对Guest的处理
	*
	*
	*/
/*	
	JPopupMenu guestpopup  = new JPopupMenu("guestpopup") ;
	JMenuItem  sendmessage = new JMenuItem("发送消息") ;
	JMenuItem  deleteguest = new JMenuItem("删除好友") ;
	JMenuItem  showinfor       = new JMenuItem("察看资料") ;
	JMenuItem  chatlog     = new JMenuItem("聊天纪录") ;
*/
	
	class GuestDeal implements ActionListener,MouseListener{
		private int inner_current_guestnumber = MainManager.NO_SUCH_NUMBER ;
		
		//对guestpop的JMenuItem的事件进行处理。
		public void actionPerformed(ActionEvent e){
				String command = e.getActionCommand().trim().toLowerCase() ;
				System.out.println("********:ActionCommand:" + command) ;
				
				if(command.equals("sendmessage")){
					//发送消息
					m.getChatWindowManager().showChatWindow(m.getGuest(inner_current_guestnumber)) ;										
				}else if(command.equals("deleteguest")){
					m.removeGuest(inner_current_guestnumber) ;
					fix() ;
				}else if(command.equals("showinfor")){
					System.out.println("ShowInfor Debug:") ;
					System.out.println("m.getGuest():" + m.getGuest(inner_current_guestnumber)) ;
					System.out.println("m.getUserManager():" + m.getUserManager()) ;
					m.getUserManager().showUserInfor(m.getGuest(inner_current_guestnumber)) ;					
				}else if(command.equals("chatlog")){
					
				}else if(command.startsWith("moveto@")){
					GuestGroup gg = m.getGuestGroup(command.substring(7,command.length())) ;
					System.out.println() ;
					if(gg == null) return ;
					Guest g = m.getGuest(inner_current_guestnumber) ;
					if(g == null) return ;
					m.moveGuest(g, gg) ;
					outer.fix() ;
				}
				
				center_center.repaint() ;
				return ;		
		}
		
		public void mouseClicked(MouseEvent e){
			//double click will open the chatwindow directly.
			if(e.getClickCount() >= 2){
				if(inner_current_guestnumber <= 0 ) return ;
				m.getChatWindowManager().showChatWindow(m.getGuest(inner_current_guestnumber)) ;
				//System.out.println("open chatwindow with :" + this.inner_current_guestnumber) ;	
				return ;
			}
			
			Guest g = m.getGuest(inner_current_guestnumber) ;
			if(g == null) return ;
			
			GuestGroup _gg = g.getGuestGroup() ;
			if(_gg == null || "陌生人".equalsIgnoreCase(_gg.getGroupname()) || "黑名单".equals(_gg.getGroupname())) 
				outer.fixMoveTo(false) ;
			else
				outer.fixMoveTo(true) ;
			
			if(e.getButton() == MouseEvent.BUTTON3){
				guestpopup.show((JComponent)e.getSource(),e.getX(),e.getY()) ;	
			}
			
			try{
				inner_current_guestnumber = new Integer(((JButton)e.getSource()).getActionCommand().trim()).intValue() ;
			}catch(Exception e1){
				mm.smy.bicq.debug.BugWriter.log(e1,"无法转换成整数，传入的用户bicq号为：" + ((JButton)e.getSource()).getActionCommand().trim() ) ;	
			}
		}
		
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){
			//((JComponent) e.getSource()).repaint() ;	
		}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){
				
		}
	}
	
	
	
	
}

