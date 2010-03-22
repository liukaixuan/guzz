package mm.smy.bicq.user.manager ;

/**
* 网络操作。
* 用户文件管理，可用于启动时对guests,guestgroups的初始化。
* 提供了对好友资料保存的快捷方式。
*
* 该类处理User资料的所有网络接收；stateChanged,Permit除外。
*
* @author XF
* @also see mm.smy.bicq.user.manager.UserFileManager
* @date 2003-10-23
*
*/

import java.util.Hashtable ;
import java.util.Vector ;
import java.util.Enumeration ;
import java.util.Date ;

import mm.smy.bicq.user.* ;
import mm.smy.bicq.message.* ;
import mm.smy.bicq.MainManager ;
import mm.smy.bicq.message.UserInforMessageListener;
import mm.smy.bicq.message.MessageListener;
import mm.smy.util.*;
import mm.smy.bicq.search.TempUser ;

public class UserNetManager implements MessageListener{
	public static final int DEFAULT_INTERVAL = 50 ;
	public static final long DEFAULT_TOTAL_TIME = 20000 ; //default timeout 20s
	
	public static final int WAITING = 1 ;
	public static final int FINISHED = 2 ;
	public static final int NET_ERROR = 3 ;
	public static final int TIMEOUT = 4 ;
	public static final int UNDEFINE = 0 ;
	
	private Hashtable guestgroups = new Hashtable(6) ;
	private Hashtable guests = new Hashtable(40) ;
	private Host host = null ;
	private Host newhost = null ; //可能要求更新newhost的资料，这个作为暂存用。
	
	private Hashtable isGuestTimeOut = new Hashtable(40) ; //我们可能要求更新好友的资料，这儿发送该更新。并且标志消息是否收到。
	private int isHostTimeOut  = this.UNDEFINE ;
	private int isLoadAllMyGuestsTimeOut = this.UNDEFINE ;

	private MainManager m = null ;
	
	public UserNetManager(MainManager m_mm){
		m = m_mm ;
		this.host = m.getHost() ;
		newhost = this.host ;
		m.addMessageListener(this) ;
	}
	
	/**
	* 创建UserInforMessage，然后发送给服务器。表示更新个人资料到服务器。
	*/
	public void writeHostNet(Host m_host){
		if(m_host == null ) return ;
		
		newhost = m_host ;
		
		
		UserInforMessage message = new UserInforMessage() ;
		message.setUser(m_host) ;
		message.setFrom(host) ;
		message.setTo(m.getServer()) ;
		message.setMinType(UserInforMessage.UPDATE_HOST_INFOR) ;
		
		System.out.println("write host to net:" + (Host) message.getUser()) ;
		m.sendOutMessage(message) ;
		this.isHostTimeOut = this.WAITING ;
		
	}

	//向服务器发送下载所有好友资料的消息
	public void readNet(){
		ICMPMessage icmp = new ICMPMessage() ;
		icmp.setMinType(ICMPMessage.LOAD_ALL_GUESTS) ;
		icmp.setFrom(host) ;
		icmp.setTo(m.getServer()) ;
		m.sendOutMessage(icmp) ;
		
		this.isLoadAllMyGuestsTimeOut = this.WAITING ;
	}
	public void readGuestNet(Guest u){
		if( u == null) return ;
		
		//if(isGuestTimeOut.containsKey(new Integer(u.getNumber()))) return ;
		
		isGuestTimeOut.put(new Integer(u.getNumber()),new Integer(this.WAITING)) ;
		
		ICMPMessage icmp = new ICMPMessage() ;
		icmp.setMinType(ICMPMessage.LOAD_SINGLE_GUEST_INFOR) ;
		icmp.setFrom(host) ;
		icmp.setTo(m.getServer()) ;
		icmp.setContent(new Integer(u.getNumber()).toString()) ;
		
		m.sendOutMessage(icmp) ;
	}

	public void readHostNet(){
		ICMPMessage icmp = new ICMPMessage() ;
		icmp.setMinType(ICMPMessage.LOAD_HOST_INFOR) ;
		icmp.setContent(new Integer(host.getNumber()).toString()) ;
		m.sendOutMessage(icmp) ;
		this.isHostTimeOut = this.WAITING ;
	}

	
	public int getHosResult(){ return isHostTimeOut ; }
	
	public int getGuestResult(int m_number){
		Object o = isGuestTimeOut.get(new Integer(m_number)) ;
		if(o == null) return this.UNDEFINE ;
		return ((Integer) o).intValue() ;
	}
	
	public int getGuestResult(Guest g){
		return getGuestResult(g.getNumber()) ;	
	}
	
	//返回状态，不包含timeout，这个由用户自己判断。
	public int getLoadAllState(){
		return this.isLoadAllMyGuestsTimeOut ;	
	}
	
	public Hashtable getGuestGroups(){
		return guestgroups ;
	}
	
	public Hashtable getGuests(){
		return guests ;
	}
	
	//If the guestgroups is inited using the local data, we need to set this
	public void setGuestGroups(Hashtable h){
		guestgroups = h ;	
	}
	
	public void setGuests(Hashtable h){
		guests = h ;	
	}
	
	//implements methods
	
	public void messageAction(ReceivedMessage rm) {
		System.out.println("user net mangage receive a message.....  " + rm.getType() ) ;
		try{
			switch(rm.getType()){
				case MessageType.USER_INFOR_MESSAGE :
					UserInforMessage message = new UserInforMessage() ;
					message.setByteContent(rm.getContent()) ;
					message.setFrom(m.getGuest(rm.getFrom())) ;
					//message
					changeUserInforResult(message) ;
					break ;
				case MessageType.ICMP_MESSAGE :
					ICMPMessage icmp = new ICMPMessage() ;
					icmp.setByteContent(rm.getContent()) ;
					icmp.setFrom(m.getGuest(rm.getFrom())) ;
					icmp.setTo(m.getGuest(rm.getTo())) ;
					icmpMessageResult(icmp) ;
					break ;
				case MessageType.LOAD_GUEST_RESULT_MESSAGE :
					LoadGuestResultMessage lgrm = new LoadGuestResultMessage() ;
					lgrm.setByteContent(rm.getContent()) ;
					lgrm.setFrom(m.getGuest(rm.getFrom())) ;
					lgrm.setTo(m.getGuest(rm.getTo())) ;
					loadGuestResult(lgrm) ;
					break ;
				default:
					break ;
			}
		}catch(Exception e){
			mm.smy.bicq.debug.BugWriter.log("usernetmanager",e,"messageAction()出错，可能是类型转换出现错误") ;	
		}
		return ;
	}
	
	private void icmpMessageResult(ICMPMessage message){
		if(message == null)	return ;
		if(message.getMinType() == ICMPMessage.UPDATE_HOST_INFOR_RESULT){
			try{
				int i = new Integer(message.getContent().trim()).intValue() ;
				if(i == 1){ //好友资料成功更新，把新资料写入硬盘。
					this.isHostTimeOut = this.FINISHED ;
					m.setHost(newhost) ;
					System.out.println("update the host infor successful by UserNetManager.") ;
					System.out.println("current host:" + m.getHost()) ;
				}
				else if(i == 2) 
					this.isHostTimeOut = this.NET_ERROR ;
			}catch(Exception e){
				mm.smy.bicq.debug.BugWriter.log("usernetmanager",e,"无法将得到的ICMPMessage.content结果转换成int值，得到的值是：" + message.getContent()) ;	
			}
		}
		return ;
	}
	
	/**
	* 对User Infor update 消息的处理
	* 同时设置标记为 完成更新
	*/
	private void changeUserInforResult(UserInforMessage message){
		System.out.println("run to unt:cuir()") ;
		System.out.println("guests:" + guests) ;
		System.out.println("message.getUser():" + message.getUser()) ;
		if (message == null) return ;
		if(guests == null) return ;
		if(message.getUser() == null) return ;
		System.out.println("UserNetManager changeUserInforResult got a message, user:" + message.getUser()) ;
		
		if(message.getMinType() == UserInforMessage.UPDATE_HOST_INFOR){
			System.out.println("unm got a message to update the host's information.") ;
			Host h = (Host) message.getUser() ;
			//host = null ;
			newhost = h ;
			System.out.println("unm reports:\n\n\n\n") ;
			System.out.println("host email:" + h.getMail()) ;
			isHostTimeOut = this.FINISHED ;
			m.setHost(h) ;
			
			return ;
		}

/*		
		//更新server的方法。更理想的改进方法见suggestion.txt
		if(message.getMinType() == UserInforMessage.UPDATE_GUEST_INFOR){
			Guest g = (Guest) message.getUser() ;
			if(g.equals(m.getServer())){ //服务器
				m.setServer(g) ;
			}
		}
*/		
		//System.out.println("") ;
		Enumeration e = guests.elements() ;
		while(e.hasMoreElements()){
			User u = (User) e.nextElement() ;
			if(u.equals(message.getUser())){
				if(message.getMinType() == UserInforMessage.UPDATE_GUEST_INFOR){
					System.out.println("unm got a message to update the guest's information") ;
					Guest g = (Guest) message.getUser() ;
					//注意地址的改变，我们不能简单的覆盖一个Guest对象，因为GuestGroup里面指向的Guest内容是不会变的。
					//有两种办法，一种用新的Guest的 内容 代替原来的内容。另一种是同步的改变Guest所在GuestGroup。
					//如果有其他的类正在引用这个Guest对象的话，那么用第二种办法并不能改变原来引用的值。所以我们用
					//第一种方法。直接修改原来引用的内容。
					//guests.put(new Integer(g.getNumber()) , g) ;
					Guest oldg = m.getGuest(g.getNumber()) ;
					
					oldg = oldg.copyInfor(g) ;
					
					isGuestTimeOut.put(new Integer(u.getNumber()),new Integer(this.FINISHED)) ;
					m.getUserManager().saveGuests() ;
				}
			}
		}
		return ;
	}
	
	/**
	* 登陆的时候，我们可能会要求下载所有的用户。
	* 该消息为该请求的回答消息的解释程序。
	* 
	* 
	*/
	private void loadGuestResult(LoadGuestResultMessage message){
		if(message == null) return ;
		
		this.isLoadAllMyGuestsTimeOut = this.FINISHED ;	

			
	//	if(message.getTempUserNumbers() == 0 ) return ;
	//  该语句可能存在问题，如果用户真的一个好友都没有
	//  那么我们就会在这儿返回，而不会去试图建立默认的组。而默认组的建立使这儿完成的。
	//  这就造成了用户没有组，然后UserManager的setupGuests()函数会怀疑网络超时。
	//  因为哪儿与这儿都没有什么方法告诉用户是超时还是正常完成了。
		
		
		if(guests == null) guests = new Hashtable(40) ;
		else guests.clear() ;
		
		//init guestgroups, put the guests in the 我的好友 guestgroup
		if(guestgroups ==null) guestgroups = new Hashtable(5) ;
		
		if(guestgroups.size() == 0 ){
			GuestGroup gg1 = new GuestGroup("我的好友") ;
			gg1.setCreateTime(new Date(82,11,23)) ;
			gg1.setIsSystemic(true) ;
			GuestGroup gg2 = new GuestGroup("陌生人") ;
			gg2.setCreateTime(new Date(83,1,4)) ;
			gg2.setIsSystemic(true) ;
			GuestGroup gg3 = new GuestGroup("黑名单") ;
			gg3.setCreateTime(new Date(84,0,30)) ;
			gg3.setIsSystemic(true) ;
			
			guestgroups.put(gg1.getGroupname(),gg1) ;
			guestgroups.put(gg2.getGroupname(),gg2) ;
			guestgroups.put(gg3.getGroupname(),gg3) ;
		}
		
		GuestGroup myfriend = (GuestGroup) guestgroups.get("我的好友") ;
		if(myfriend == null){
			myfriend = new GuestGroup("我的好友") ;
			myfriend.setCreateTime(new Date(1983,2,4)) ;
			myfriend.setIsSystemic(true) ;
			guestgroups.put(myfriend.getGroupname(),myfriend) ;
		}
		
		Vector v = message.getTempUsers() ;
		Enumeration e = v.elements() ;
		while(e.hasMoreElements()){
			
			TempUser t = (TempUser) e.nextElement() ;
			if( t == null) continue ;
			
			Guest g = new Guest() ;
			g.setNumber(t.getNumber()) ;
			g.setNickname(t.getNickname()) ;
			g.setState(t.getState()) ;
			g.setIP(t.getIP()) ;
			g.setGender(t.getGender()) ;
			g.setPortrait(t.getPortrait()) ;
			g.setProvince(t.getFrom()) ;
			g.joinGuestGroup(myfriend) ;
			guests.put(new Integer(t.getNumber()),g) ;			
		}
		
		m.getUserManager().saveGuests() ;
		
	System.out.println("unm got the read net reply. setup guestgroups OK:guestgroups:" + guestgroups) ;
	System.out.println("guests：" + guests) ;			
	}
	
}
