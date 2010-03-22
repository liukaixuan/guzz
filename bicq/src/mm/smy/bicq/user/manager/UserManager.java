package mm.smy.bicq.user.manager ;

/**
* 对Guest的处理。
*
* @author XF
*
*
*
*/
import java.io.* ;
import java.util.* ;

import mm.smy.bicq.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.login.LoginException ;

import mm.smy.bicq.user.* ;
import mm.smy.util.* ;

public class UserManager{
	
	private MainManager m = null ;
	private Host host = null ;
	
	private Hashtable guestgroups = null ;
	private Hashtable guests = null ;
	
	private UserFileManager filemanager = null ;
	private UserNetManager netmanager = null ;
	
	private boolean istimeout = false ;
	private int loadGuestState = UserNetManager.UNDEFINE ;
	
	public UserManager(MainManager m_mm){
		m = m_mm ;
		host = m.getHost() ;
		filemanager = new UserFileManager(host) ;
		netmanager = m.getUserNetManager() ;
		
		//用unm的guestgroups,guests最为所有的好友对象地址
		
		guestgroups = netmanager.getGuestGroups() ;
		guests = netmanager.getGuests() ;
		
		filemanager.setGuestGroups(guestgroups) ;
		filemanager.setGuests(guests) ;
	}
	
	//first put
	
	//把目前的host写入硬盘.
	public void writeHostToLocal(){
		filemanager.writeHostFile() ;
	}
	
	/**
	* 创建Guest的资料，首先读入loacal data；如果不存在的话，读网络。
	* 
	* 该方法为中断方法，方法一直等待，直到读到消息，或是超时，此时置标记为istimeout = true ;
	* m类应该查看是否超时，已决定下一步应该怎么做！
	*/
	public void setupGuest() throws LoginException{
		filemanager.setIsFileRead(false) ;
		this.istimeout = false ;
		
		if(isLocalDataExsit()){
			initLocalData() ;
			//把数据给unm
			//netmanager.setGuestGroups(guestgroups) ;
			//netmanager.setGuests(guests) ;
		}
		
		if(guestgroups == null || guestgroups.size() == 0){//local data not exsits, read from net...
//					System.out.println(" whiling setupGuest in um, UserNetManager is:" + netmanager) ;
			SmyTimer timer = new SmyTimer() ;
			timer.setTimerListener(new WaitInitGuest(timer)) ;
			timer.setInterval(UserNetManager.DEFAULT_INTERVAL) ;
			timer.setTotalTime(UserNetManager.DEFAULT_TOTAL_TIME) ;
			timer.startTimer() ;
			
			this.loadGuestState = netmanager.WAITING ;
			
			netmanager.readNet() ;//读取所有好友资料。
//			System.out.println("read from net start:" + netmanager.getLoadAllState() ) ;
			
			
			while(loadGuestState == netmanager.WAITING){
				try{
					synchronized(this){
						wait(500) ;	
					}
				}catch(Exception e){
					System.out.println("Exception in setupGuest() in um") ;						
				}

//				System.out.println("do nothing but loop " + loadGuestState) ;
			}
//			System.out.println("Waiting is:" + netmanager.WAITING ) ;
			//System.out.println("loop should go on:" + (loadGuestState)) ;
//			System.out.println("read from net end:" + netmanager.getLoadAllState() ) ;
			
			//下面的两行现在是不需要的,因为在构造函数中我们已经让所有的地址指向了unm。
			guestgroups = netmanager.getGuestGroups() ;
			guests      = netmanager.getGuests() ;
			
//			System.out.println("read from netmanager: guestgroups:" + guestgroups) ;
//			System.out.println("guests:" + guests) ;
			
			if(guestgroups == null || guestgroups.size() == 0 ){ //wang luo cao shi.
				LoginException e = new LoginException("网络超时，检查网络，请察看日志") ;
				mm.smy.bicq.debug.BugWriter.log("UserManager.java", e, "读取guestgroups时网络错误，标志：" + netmanager.getLoadAllState() ) ;
				throw e  ;
				//在这儿添加关于网络超时的处理，例如提示用户，退出程序等等。
			}
			//下面的两行现在是不需要的,因为在构造函数中我们已经让所有的地址指向了unm。
			//filemanager.setGuestGroups(guestgroups) ;
			//filemanager.setGuests(guests) ;
		}
		
		if (guests == null){			
			//guests = new Hashtable(40) ;
			throw new LoginException("guests在um中为空，这是不应该出现的，说明程序初始化错误；应为程序设计错误！") ;
		}
		//下面的两行现在是不需要的,因为在mm的构造函数中我们已经让地址指向了unm。
		//m.setGuestGoups(guestgroups) ;
		//m.setGuests(guests) ;
		
		return ;		
	}
	
	public boolean isTimeOut(){
		return istimeout ;	
	}
	
	private class WaitInitGuest implements TimerListener{
		private SmyTimer timer = null ;
		
		public WaitInitGuest(SmyTimer m_timer){
			timer = m_timer ;
			//timer.startTimer() ;
		}
		
		public void timeElapsed(){
			if(netmanager.getLoadAllState() == netmanager.FINISHED){
				loadGuestState = netmanager.FINISHED ;
				istimeout = false ;
				timer.stopTimer() ;	
			}
		}
		
		public void timeOut(){
			loadGuestState = netmanager.TIMEOUT ;
			istimeout = true ;
			timer.stopTimer() ;	
		}		
	}

/*********************************************************************************************/
	public void initLocalData(){
		//如果不存在，得到的全是null
		System.out.println("init local data................................................"); 
		//这儿只是强迫filemanager执行private 函数读取 文件纪录。
		filemanager.getGuestGroups() ;
		filemanager.getGuests() ;
	}
	
	public boolean isLocalDataExsit(){
		return filemanager.isDataExsit() ;
	}

	
/*********************************************************************************************/
	
	public void showUserInfor(Guest g){
		if(g == null) return ;
	//	InforWindow window = new InforWindow(g,m) ;
		GuestInforWindow window = new GuestInforWindow(g, m) ;
		window.show() ;
		return ;
	}
	public void showUserInfor(Host h){
		if(h == null) return ;
//		InforWindow window = new InforWindow(h,m) ;
	System.out.println("trying to show the host's information:" + h) ;
		HostInforWindow window = new HostInforWindow(h,m) ;
		window.show() ;
		return ;
	}
	
	public Hashtable getGuestGroups(){
		return guestgroups ;
	}
	
	public Hashtable getGuests(){
		return guests ;
	}
	
//guest methods.....

	public void updateGuest(Guest g){
		if(g == null) return ;
		guests.put(new Integer(g.getNumber()),g) ;
	}
	
	public Guest addGuest(Guest g, GuestGroup gg){
		if(g == null) return null;
		if(gg == null) gg = getGuestGroup("我的好友") ;
		
		Guest newguest = (Guest) guests.get(new Integer(g.getNumber())) ;
		if ( newguest == null ){
			newguest = g ;
			newguest.joinGuestGroup(gg) ;
			//put the newguest in the memory
			guests.put(new Integer(g.getNumber()), newguest) ;
		}else{
			newguest.joinGuestGroup(gg) ;
			return newguest ; 
		}
		//Here we write all the GuestGroup information into the 'number/guestgroup.bicq' file.
		saveGuests() ;
		return newguest;			
	}
	
	public Guest addGuest(int m_number,String m_groupname){
		if(m_groupname == null) m_groupname = "我的好友" ;
		
		return addGuest(m_number, getGuestGroup(m_groupname)) ;
	}
	public Guest addGuest(int m_number,GuestGroup m_gg){
		if(m_gg == null) return null ;
		
		Guest newguest = (Guest) guests.get(new Integer(m_number)) ;
		if ( newguest == null ){
			newguest = new Guest(m_number) ;
			newguest.joinGuestGroup(m_gg) ;
			//put the newguest in the memory
			guests.put(new Integer(m_number),newguest) ;
		}else{
			return newguest ; 
		}
		//Here we write all the GuestGroup information into the 'number/guestgroup.bicq' file.
		saveGuests() ;
		return newguest;
	}
	
	public Guest moveGuest(Guest g, GuestGroup gg){ //移动好友到新的小组
		if ( !guestgroups.containsValue(gg) ){ //如果组不存在，就直接返回
			return g ;
		}
		
		if(gg.getGroupname().equals("陌生人") || gg.getGroupname().equals("黑名单")){
			//用户可能要求删除用户。
			GuestGroup oldgroup = g.getGuestGroup() ;
			if(oldgroup != null){
				if(!"陌生人".equals(oldgroup.getGroupname()) && !"黑名单".equals(oldgroup.getGroupname())){
					//删除用户
					removeGuest(g.getNumber()) ;
				}				
			}
		}
		
		g.joinGuestGroup(gg) ;
		return g ;
	}
	
	public void removeGuest(int m_number){
		//Guest g = (Guest) guests.get(new Integer(m_number)) ;
		//if(g == null) return ;
		Guest g = (Guest) guests.remove(new Integer(m_number)) ;
		
		if(g != null){
			g.getGuestGroup().delete(g) ;
			g = null ; //删除
			saveGuests() ;
			//告诉服务器删除好友。
			ICMPMessage deleteicmp = new ICMPMessage() ;
			deleteicmp.setMinType(ICMPMessage.DELETE_FRIEND) ;
			deleteicmp.setContent(m.getHost().getNumber() + ":" + m_number ) ;
			m.sendOutMessage(deleteicmp) ;
			System.out.println("删除好友消息发送成功。") ;
		}
		return ;
	}
	/**
	*如果是用户自己(host)，返回null.
	*如果用户不存在，创建该好友，并且加入到 陌生人 里。
	*/
	public Guest getGuest(int m_number){
		System.out.println("==========um is requested to solve the guest:" + m_number) ;
		if(m_number <= 0) {
			return m.getServer() ; //返回Server的地址。
		}

		if(m_number == m.getServer().getNumber()) return m.getServer() ;
		if(m_number == host.getNumber()) return m.getHost2() ; //发送者就是接收者，这儿只是为了测试，在真正的程序中应该慎重考虑！ 
		
		System.out.println("guests is:" + guests) ;
		if(guests == null) return null ; //在刚启动时，好友为空；没有这个，消息处理在寻找Guest时将会出错。
		
		Guest g = (Guest) guests.get(new Integer(m_number)) ;
		if (g == null) { //不是偶的好友，放到陌生人里面。
			//Guest newguest = new Guest(m_number) ;
			//newguest.joinGuesgGroup(getGuestGroup("陌生人")) ; //put this in the addGuest method.
			System.out.println("^^^^^^^^^^^^^^add " + m_number + " to stanger guestgroup.") ;
			g = addGuest(m_number, getGuestGroup("陌生人")) ;
		}
		return g ;
	}

//guestgroup methods......
	/**
	*@param groupname 返回指定的组。如果组名不存在，新建并返回改组。
	*                 对大小写，组名字前后有无空格不敏感。
	*
	*/
	public GuestGroup getGuestGroup(String groupname){
		return (GuestGroup) guestgroups.get(groupname) ;
	}
	public GuestGroup addGuestGroup(String groupname){
		if (guestgroups.containsKey(groupname.trim().toLowerCase())){
			return getGuestGroup(groupname) ;
		}
		GuestGroup gg = new GuestGroup(groupname) ;
		gg.setIsSystemic(false) ;
		
		this.saveGuests() ;

		return gg ;
	}
	//remove
	public GuestGroup removeGuestGroup(GuestGroup m_group){
		m_group = (GuestGroup) guestgroups.remove(m_group.getGroupname()) ;
		
		saveGuests() ;
		
		return m_group ;
	}
	
	
	/**
	* 关闭方法，把新的guests, guestgroups 数据填入硬盘
	* 以后可能会填入 服务器
	* 
	* 释放所有guest占用的资源。关闭好友管理线程。
	*/
	
	public void close(){
		try{
			GuestGroupFile ggf = new GuestGroupFile(host.getNumber()) ;
			ggf.save(guestgroups) ;
		}catch(FileNotFoundException e){
			//This should not happen....
			System.out.println("系统错误：guestgroup.bicq无法找到，在保存组时-->"+ e.getMessage()) ;
		}catch(IOException e){
			//文件操作错误，应该跳出程序，或告诉用户。
			System.out.println("系统错误：在保存guestgroup时出现IOException==>"+ e.getMessage() ) ;	
		}
		
		this.writeHostToLocal() ;
		
		if(guests !=null ){
//			System.out.println("guests is not null when we close the bicq:" + guests) ;
			guests.clear() ;
		}
		if(guestgroups != null){
//			System.out.println("guestgroups is not null when we close the bicq:" + guestgroups) ;	
			guestgroups.clear() ;
		}
		
		guests = null ;
		guestgroups = null ;
		
		return ;		
	}
	
	public void saveGuests(){
		try{
			GuestGroupFile ggf = new GuestGroupFile(host.getNumber()) ;
		//	System.out.println("\n\n\num saveGuests() reports:") ;
		//	System.out.println("guests.50282717") ;
		//	Guest g = (Guest) guests.get(new Integer(50282717)) ;
		//	System.out.println("nickname:" + g.getNickname()) ;
		//	System.out.println("nickname:" + g.getMail()) ;
		//	System.out.println("nickname:" + g.getExplain()) ;
		//	System.out.println("nickname:" + g.getCountry()) ;
		//	System.out.println("nickname:" + g.getProvince()) ;
			
			ggf.save(guestgroups) ;
		}catch(FileNotFoundException e){
			//This should not happen....
			System.out.println("系统错误：guestgroup.bicq无法找到，在保存组时-->"+ e.getMessage()) ;
		}catch(IOException e){
			//文件操作错误，应该跳出程序，或告诉用户。
			System.out.println("系统错误：在保存guestgroup时出现IOException==>"+ e.getMessage() ) ;	
		}
	}
	
	
	
}
