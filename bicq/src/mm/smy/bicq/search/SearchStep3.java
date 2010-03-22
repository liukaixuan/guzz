package mm.smy.bicq.search ;

import java.util.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.table.* ;

import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.user.* ;

import mm.smy.bicq.user.manager.GuestGroupManager ;


/**
* 搜索完毕，成功完成。显示搜索的结果。
* ss2取消。上一步将转到搜索参数选择。
* 下一步将添加好友[这儿情况由该类统一管理，以减轻sgm的负担。最后传给sgm成功添加好友的对象。
* 该部分负责请求验证消息的发送，但是具体的回送消息处理由sgm完成。
* 为了减轻sgm的负担，该类将拥有MainManager的引用。可以直接发送消息，但是不允许监听消息！！以统一管理消息。
* 该类拥有它上上一画面的引用，用于sgm对"上一步"进行处理。"完成"就是关闭一切，结束searchguest。"下一步"由该类自己处理。
* 我们的任务是发送请求！
*
* 该类排队到 7 
*/

public class SearchStep3 extends JFrame implements ActionListener{
	private MainManager m = null ;
	private SearchGuestManager sgm = null ;
	private SearchGuestResultMessage sgrm = null ;
	
	private DealAdd da = null ;
	
	Vector tempusers = null ;
	/********************************************************/
	public SearchStep3(SearchGuestResultMessage m_sgrm){
		sgrm = m_sgrm ;
		tempusers = sgrm.getTempUsers() ;
		init() ;
	}
	public static void main(String[] args){
		/*SearchGuestResultMessage sgrm = new SearchGuestResultMessage() ;
		for(int i = 0 ; i < 10 ; i++ ){
			TempUser tu = new TempUser() ;
			tu.setNickname("nickname" + i) ;
			tu.setNumber(1000 + i*10) ;
			tu.setFrom("from" + i) ;
			tu.setState(User.OFFLINE) ;
			tu.setAuth(Host.MY_PERMIT) ;
			sgrm.addTempUser(tu) ;	
		}
		
		MainManager m = new MainManager() ;
		SearchGuestManager sgm = new SearchGuestManager(m) ;
		SearchStep3 ss3 = new SearchStep3(m,sgm,sgrm) ;	
		ss3.show() ;
		System.out.println("finished.") ;
		*/
		System.out.println("ggm debug starts:") ;

		GuestGroupManager ggm = new GuestGroupManager(new java.util.Hashtable()) ;
		ggm.show() ;
		System.out.println(ggm.getChoseGuestGroup()) ;
	}
	//*******************************************************
	
	public SearchStep3(){
		tempusers = sgrm.getTempUsers() ;
		init() ;
	}
	public SearchStep3(MainManager m_mm,SearchGuestManager m_sgm, SearchGuestResultMessage m_sgrm){ //用给定的tempuser对象组 显示收到的用户。
		m = m_mm ;
		sgm = m_sgm ;
		sgrm = m_sgrm ; 
		tempusers = sgrm.getTempUsers() ;
		System.out.println("TempUsers.size():" + tempusers.size()) ;
		init()  ;
	}

	private JLabel explain = new JLabel("查找结果") ;
	private JTable result = null ;
	private JScrollPane scroll_result = null ;
	
	private JButton detail = new JButton("资料") ; //显示好友的详细资料
	private JButton uppage = new JButton("上一页") ;
	private JButton downpage = new JButton("下一页") ;
	
	private JButton pre = new JButton("上一步")  ;
	private JButton next = new JButton("下一步") ; 
	private JButton finish = new JButton("完成") ;
	
	private void init(){
		this.setSize(400,400) ;
		this.setTitle("搜索结果……") ;
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					if(da != null){
						da.dispose() ;	
					}
					sgm.report(7,SearchGuestManager.STEP_CLOSE) ;
				}
			}
		) ;
		
		//init JTable result...
		System.out.println("********************************************* 11") ;
		Enumeration e = tempusers.elements() ;
		Vector rows = new Vector(11) ;
		Vector head = new Vector(4) ;
		head.add("号") ;
		head.add("昵称") ;
		head.add("性别") ;
		//row.add("年龄") ;
		head.add("来自") ;
		//rows.add(row) ;
		TempUser tu = null ;
		Vector row = null ;
		while(e.hasMoreElements()){
			tu = (TempUser) e.nextElement() ;
			row = new Vector(4) ;
			row.add(new Integer(tu.getNumber())) ;
			row.add(tu.getNickname()) ;
			row.add(new Integer(tu.getGender())) ;
			row.add(tu.getFrom()) ;
			rows.add(row) ;
		}
		///////////////////////////////////////////////////////////// init components
		System.out.println("********************************************* 12") ;
		result = new JTable(rows,head)  ;
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
		//result.setCellSelectionEnabled(false) ;
		result.setDragEnabled(false) ;
		
		//result.setPreferredScrollableViewportSize(new Dimension(300,300)) ;
		scroll_result = new JScrollPane(result) ;
		
		Panel labels = new Panel() ;
		labels.add(explain) ;		
		
		Panel centerbuttons = new Panel() ;
		detail.setActionCommand("detail") ;
		detail.addActionListener(this) ;
		uppage.setActionCommand("uppage") ;
		uppage.addActionListener(this) ;
		downpage.setActionCommand("downpage") ;
		downpage.addActionListener(this) ;
		centerbuttons.add(detail) ;
		centerbuttons.add(uppage) ;
		centerbuttons.add(downpage) ;
		System.out.println("********************************************* 13") ;
		Panel center = new Panel(new BorderLayout()) ;
		center.add(scroll_result,BorderLayout.CENTER) ;
		center.add(centerbuttons,BorderLayout.SOUTH) ;
		
		Panel buttons = new Panel() ;
		buttons.add(pre) ;
		buttons.add(next) ;
		buttons.add(finish) ;
		
		Container cp = this.getContentPane() ;
		cp.setLayout(new BorderLayout()) ;
		cp.add(labels,BorderLayout.NORTH) ;
		cp.add(center,BorderLayout.CENTER) ;
		cp.add(buttons,BorderLayout.SOUTH) ;
		
		pre.setActionCommand("pre") ;
		next.setActionCommand("next") ;
		finish.setActionCommand("finish") ;
		System.out.println("********************************************* 14") ;
		pre.addActionListener(this) ;
		next.addActionListener(this) ;
		finish.addActionListener(this) ;
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equalsIgnoreCase("pre")){
			if(da != null){
				da.dispose() ;	
			}
			sgm.report(7,SearchGuestManager.STEP_PREVIOUS) ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			if(da != null){
				da.dispose() ;	
			}
			sgm.report(7,SearchGuestManager.STEP_FINISH) ;
		}
		
		//其他的情况由该类自己解决！
		if(e.getActionCommand().equalsIgnoreCase("uppage")){
			//上一页	
			
		}else if(e.getActionCommand().equalsIgnoreCase("downpage")){
			//下一页
		}
		
		////////////////////////////////////////////////////////////////////
		//获取选择用户
		int chooseline = result.getSelectedRow() ;
		TempUser chooseuser = null ;
			
		System.out.println("chooseline:" + chooseline) ;
		if(chooseline < 0){ //select nothing at all
			explain.setText("请选择先一个好友！") ;
			return ;
		}else{
			Enumeration en = tempusers.elements() ;
			while(en.hasMoreElements()){
				TempUser tu = (TempUser) en.nextElement() ;
				System.out.println("choose number is: " + result.getValueAt(chooseline,0) ) ;
				
				if(tu.getNumber() == ((Integer) result.getValueAt(chooseline,0)).intValue()){
					chooseuser = tu ;
				}	
			}
		}
		
		if(chooseuser == null) return ;
		
		Guest guest = new Guest() ;
		guest.setNumber(chooseuser.getNumber()) ;
		guest.setNickname(chooseuser.getNickname()) ;
		guest.setIP(chooseuser.getIP()) ;
		guest.setPort(chooseuser.getPort()) ;
		guest.setProvince(chooseuser.getFrom()) ;
		guest.setGender(chooseuser.getGender()) ;
		guest.setPortrait(chooseuser.getPortrait()) ;
		guest.setState(chooseuser.getState()) ;
		
		if(e.getActionCommand().equalsIgnoreCase("next")){
			if(da == null){
				da = new DealAdd(m,this,sgm) ;
			}
			da.setTo(guest) ;
			da.setCurrent(chooseuser.getAuth()) ;
			da.show() ;
		}else if(e.getActionCommand().equalsIgnoreCase("detail")){
			//显示指定用户的详细资料。
			GuestInforWindow window_detail = new GuestInforWindow(guest, m) ;
			window_detail.show() ;
			return ;
		}
		
	}

}

class DealAdd extends JFrame implements ActionListener{
	private MainManager m = null ;
	private SearchStep3 ss3 = null ;
	private SearchGuestManager sgm = null ;
	private int current = Integer.MIN_VALUE ;
	private Guest toadd = null ;
		
	private JButton pre = new JButton("上一步") ;
	private JButton next = new JButton("下一步") ;
	private JButton finish = new JButton("完成") ;
	
	private Label explain = new Label() ;
	private TextArea leaveword = new TextArea(10,30) ;
	private JScrollPane scroll_leaveword = new JScrollPane(leaveword) ;
	
	public DealAdd(MainManager m_mm, SearchStep3 m_ss3, SearchGuestManager m_sgm ){
		m = m_mm ;
		ss3 = m_ss3 ;
		ss3.hide() ;
		sgm = m_sgm ;
		init() ;
	}
	public void setCurrent(int m_current){
		current = m_current ;
		paintWindow() ;	
	}
	public void setTo(Guest u){
		toadd = u ;
	}
	
	private void init(){
		this.setSize(400,400) ;
		this.setTitle("添加好友") ;
		
		top.add(explain) ;
		
		pre.setActionCommand("pre") ;
		pre.addActionListener(this) ;
		next.setActionCommand("next") ;
		next.addActionListener(this) ;
		finish.setActionCommand("finish") ;
		finish.addActionListener(this) ;
		
		buttom.add(pre) ;
		buttom.add(next) ;
		buttom.add(finish) ;
	}
	
	private Panel top = new Panel() ;
	private Panel center = new Panel() ;
	private Panel buttom = new Panel() ;
	
	
	private void paintWindow(){
		
		System.out.println("current:" + current) ;
		System.out.println("toadd:" + toadd) ;
		
		center.removeAll() ;		
		
		Container cp = this.getContentPane() ;
		cp.removeAll() ;
		//////////////////////////////////////////////////////////////////////////////////////////////
		PermitMessage pm = new PermitMessage() ;
		if(current == Host.ALLOW_ANYONE){
			
				//发送消息给服务器，好友添加成功。
				ICMPMessage addsuccess = new ICMPMessage() ;
				addsuccess.setMinType(ICMPMessage.ADD_FRIEND) ;
				addsuccess.setContent(m.getHost().getNumber() + ":" + toadd.getNumber() ) ;
				m.sendOutMessage(addsuccess) ;
			
			/* 该部分由服务器发送。
				pm.setMintype(PermitMessage.PERMIT_SEND) ;
				pm.setFrom(m.getHost()) ;
				pm.setTo(toadd) ;
				m.sendOutMessage(pm) ;
			*/
			
			//the followwing 3 lines will cause deadlock
			//
			//System.out.println("gg:" + gg.getGroupname() ) ;
			//m.addGuest(toadd,gg) ;
			
			/*下面的代码是为了让用户能够选择用那个组，可是现在有问题。
				GuestGroupManager ggm = new GuestGroupManager(m.getGuestGroups()) ;
				ggm.show() ;
				
				
				GuestGroup gg = ggm.getChoseGuestGroup() ;
			*/
				GuestGroup gg = m.getGuestGroup("我的好友") ;
				m.addGuest(toadd, gg) ;
				
				explain.setText("添加成功") ;
				m.getMainFrame().fix() ;
				
		}else if(current == Host.NO_DISTURB){
			pm = null ;
			explain.setText("您要添加的好友拒绝任何人把她/他加为好友，请以后在试。") ;			
		}else if(current == Host.MY_PERMIT){
			explain.setText("对方要求身份验证：") ;	
			center.add(scroll_leaveword) ;
		}else{
			explain.setText("无法添加，对方表示不明确。可能是系统数据库出错。请报告这个错误给我们，谢谢。") ;	
		}
		
		cp.setLayout(new BorderLayout()) ;
		cp.add(top,BorderLayout.NORTH) ;
		cp.add(center,BorderLayout.CENTER) ;
		cp.add(buttom,BorderLayout.SOUTH) ;
		
		cp.invalidate() ;
	}
	

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equalsIgnoreCase("pre")){
			this.hide() ;	
			ss3.show() ;
		}else if(e.getActionCommand().equalsIgnoreCase("finish")){
			//////////////////////////////////////////////////????????????????????????????????????care!!!!!!!!!!!!!!!!!
			sgm.report(7,SearchGuestManager.STEP_FINISH) ;
			this.dispose() ;
		}else if(e.getActionCommand().equalsIgnoreCase("next")){
			if(current == Host.MY_PERMIT){ //发送身份验证
				PermitMessage pm = new PermitMessage() ;
				pm.setMintype(PermitMessage.PERMIT_REQUEST) ;
				pm.setFrom(m.getHost()) ;
				pm.setTo(toadd) ;
				pm.setContent(leaveword.getText()) ;
				m.sendOutMessage(pm) ;
				
				//repaint compoments
				center.removeAll() ;
				explain.setText("请求已经发送，请等待回应……") ;
				
				this.getContentPane().invalidate() ;
			}
			else{
				this.hide() ;	
				ss3.show() ;
			}
		}
	}
	
}