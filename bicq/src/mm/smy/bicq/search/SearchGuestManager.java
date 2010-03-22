package mm.smy.bicq.search ;

import mm.smy.bicq.* ;
import mm.smy.bicq.user.* ;
import mm.smy.bicq.message.* ;

import mm.smy.util.* ;

import java.util.* ;
/**
* 提供对Seach Guest的统一管理。该类将有主类成生一个实例，然后所有的新建消息都会发送给该实例。
* 由该类自己决定怎么处理请求。
*
* 目前的做法是 只有一个实例运行！
* 维护SearchGuestMessage 与 SearchGuestResultMessage 的运行！
*
*/
import javax.swing.* ;

public class SearchGuestManager implements SearchGuestResultMessageListener{
	public static final String STEP_CLOSE = "close" ;
	public static final String STEP_PREVIOUS   = "previous" ;
	public static final String STEP_NEXT  = "next" ;
	public static final String STEP_FINISH = "finish" ;
	
	private SearchStep1 ss1 = null ;
	private SearchStep2 ss2 = null ;
	private SearchStep3 ss3 = null ;
	private SearchByNumber s_number = null ;
	private SearchByNickname s_nickname = null ;
	private SearchByGFA s_gfa = null ;
	
	private boolean isworking = false ; //当前的查找是否正在运行，以避免多个实例同时存在！
	
	private JFrame currentframe = null ; //当前的祯
	
	private SearchGuestManager out = this ; //当前类指针，用于内部类调用。
	
	private SearchGuestMessage sgm = new SearchGuestMessage() ;
	private SearchGuestResultMessage sgrm = null ; //保存返回的message对象。
	
	private MainManager m = null ;
	
	private int timeout = 20000 ; //等待超时 2秒
	
	
	public 	SearchGuestManager(MainManager m_mm){
		m = m_mm ;
		init() ;
	}
	/*
	public SearchGuestManager(){
		init() ;		
	}
	public static final void main(String[] args){
		SearchGuestManager sgm = new SearchGuestManager() ;
		
	}
	*/
	private void init(){
		if(m != null){
//			m.addSearchGuestMessageListener(this) ;
			m.addSearchGuestResultMessageListener(this) ;
		}
		ss1 = new SearchStep1(this) ;
		ss1.show() ;
		isworking = true ;
		return ;
	}
	
	/**
	* 当finish()调用以后，我们可能还要重新搜索。
	* 该方将会重新构建sgm。
	*
	*
	*
	*
	*/
	public void rework(){
		ss1 = null ;
		ss1 = new SearchStep1(this) ;	
		ss1.show() ;
		isworking = true ;	
	}
	
	public boolean isWorking(){
		return isworking ;
	}

//与各step的通讯，以调控个步骤的协同工作。
/**
* @param m_step 第几步工作，从1开始算起。1代表：SearchStep1   2代表：SearchByNumber 3代表：SearchByNickname 5代表：SearchByGFA[Gender/From/Age]
* @param m_actioncommand 用户作出的相应，对应按钮的actionCommand。
*/

	
	public void report(int m_step,String m_actioncommand){
		System.out.println("Step" + m_step + " reports:" + m_actioncommand ) ;
		if(m_actioncommand == null) return ;
		
		switch(m_step){
			case 1 : //step 1
				if (ss1 == null){
					ss1 = new SearchStep1(this) ;
				 	ss1.show() ;
				 	currentframe = ss1 ;
				 	return ;
				 }
				

				 if(m_actioncommand.equals(this.STEP_NEXT)){
					String temp_ss1 = ss1.getSelectedItem() ;
					if(temp_ss1 == null) return ; //用户什么也没有选择，我们直接返回，什么也不去做！
					
					ss1.hide() ;
					
				 	if(temp_ss1.equalsIgnoreCase("byonline")){
				 		sgm.setMinType(SearchGuestMessage.SEARCH_ONLINE) ;
				 		currentframe = ss1 ;
				 		send() ;
				 		return ;
				 	}else if(temp_ss1.equalsIgnoreCase("bynumber")){
				 		if(s_number == null) s_number = new SearchByNumber(this) ;
				 		currentframe = s_number ;
				 		s_number.show() ;
				 	}else if(temp_ss1.equalsIgnoreCase("bynickname")){
				 		if(s_nickname == null) s_nickname = new SearchByNickname(this) ;
				 		currentframe = s_nickname ;
				 		s_nickname.show() ;
				 	}else if(temp_ss1.equalsIgnoreCase("byGFA")){
				 		if(s_gfa == null) s_gfa = new SearchByGFA(this) ;
				 		currentframe = s_gfa ;
				 		s_gfa.show() ;
				 	}
				 	return ;
				}
				ss1.hide() ;
				finish() ; //quit the search.
				break ;
			case 2 : //search by number.
				if(m_actioncommand.equals(this.STEP_NEXT)){
					int temp_number = s_number.getNumber() ;
					if(temp_number <= 0) return ; //有错误发生
					
					sgm.setMinType(SearchGuestMessage.SEARCH_BY_NUMBER) ;
					sgm.setNumber(temp_number) ;
					s_number.hide() ;
					send() ;
					return ;
				}
				if(m_actioncommand.equals(this.STEP_PREVIOUS)){
					if(s_number != null ) s_number.hide() ;
					ss1.show() ;
					return ;
				}
				
				finish() ;
				break ;
				
			case 3 : //search by nickname
				if(m_actioncommand.equals(this.STEP_NEXT)){	
					String temp_nickname = s_nickname.getNickname() ;
					if(temp_nickname == null ) return ; //有错误发生
					temp_nickname = temp_nickname.trim() ;
					if (temp_nickname.length() == 0 ) return ;
					
					sgm.setMinType(SearchGuestMessage.SEARCH_BY_NICKNAME) ;
					sgm.setNickname(temp_nickname) ;
					s_nickname.hide() ;
					send() ;
					return ;
				}
				if(m_actioncommand.equals(this.STEP_PREVIOUS)){
					if(s_nickname != null ) s_nickname.hide() ;
					ss1.show() ;
					return ;
				}
				
				finish() ;
				break ;	
			case 4 : //search by gfa
				if(m_actioncommand.equals(this.STEP_NEXT)){
					sgm.setMinType(SearchGuestMessage.SEARCH_BY_GFA) ;
					sgm.setAge(s_gfa.getAgeFrom(),s_gfa.getAgeTo()) ;
					sgm.setProvince(s_gfa.getFrom()) ;
					sgm.setGender(s_gfa.getGender()) ; //0 girl; 1 boy ; -1 anyone
					s_gfa.hide() ;
					send() ;
					return ;
				}
				if(m_actioncommand.equals(this.STEP_PREVIOUS)){
					s_gfa.hide() ;
					ss1.show() ;
					return ;
				}
				
				finish() ;
				break ;
			case 5 : //connect and commuicate with the server....
				//nothing will send here now....
				//Maybe later, we will use this.
			case 6 : //net error/timeout...
				if(m_actioncommand.equals(this.STEP_PREVIOUS)){
					System.out.println("ss2 is null:" + (ss2==null)) ;
					System.out.println("currentframe is null :" + (currentframe == null)) ;
					ss2.hide() ;
					//ss2.getPreFrame().show() ; 
					currentframe.show() ;
					return ;
				}
				finish() ;
				break ;
			case 7 : //searchstep3, search OK, show result......
				if(m_actioncommand.equals(this.STEP_PREVIOUS)){
					ss3.hide() ;
					currentframe.show() ;
					return ;
				}
				finish() ;
				break ; 
			default:
				return ;
		}
		return ;
	}
	
	//send the message out. check its valid...
	private void send(){
		
		ss2 = new SearchStep2(this,currentframe) ;
		ss2.show() ;
		
		//send out the message
		sgm.setFrom(m.getHost()) ;
		sgm.setTo(m.getServer()) ;
		m.sendOutMessage(sgm) ;
		
		System.out.println("message sends out....MessageType:" + sgm.getMinType()) ;
		//wait.. until timeout
		
		SmyTimer timer = new SmyTimer() ;
		timer.setTimerListener(new WaitReply(timer)) ;
		timer.setTotalTime(timeout) ;
		timer.setInterval(timeout/100) ;
		timer.startTimer() ;

	}
	
	
	//等待收到消息，如果真得无法收到的话，就报告超时。
	private class WaitReply implements TimerListener{
		private SmyTimer timer = null ;
		
		public WaitReply(SmyTimer m_timer){
			timer = m_timer ;	
		}
		
		public void timeElapsed(){
			if(sgrm != null){ //收到服务器发来的消息。ss2结束，显示ss3.
				timer.stopTimer() ;
				
				ss2.hide() ;
				ss3 = new SearchStep3(m, out, sgrm) ;
				ss3.show() ;
			}
		}
		
		public void timeOut(){
			timer.stopTimer() ;
			
			ss2.setCurrent(6) ;
			ss2.repaint() ;
		}
		
	}
	
	/**
	* 用户要求退出 搜索
	* 做清理工作。
	*/
	public void finish(){
		System.out.println("finish() is invoked!!!!!!!") ;
		if(s_number != null){
			s_number.hide() ;
			s_number = null ;
		}
		if(s_nickname != null){
			s_nickname.hide() ;
			s_nickname = null ;
		}
		if(s_gfa != null){
			s_gfa.hide() ;
			s_gfa = null ;
		}
		if(ss1 != null){
			ss1.hide() ; 
			ss1 = null ;
		}
		if(ss2 != null){
			ss2.hide() ;
			ss2 = null ;
		}
		if(ss3 != null){
			ss3.hide() ;
			ss3 = null ;
		}
		
		isworking = false ;
		return ;
	}
	
	
//implements methods
//	public void searchGuestMessageAction(SearchGuestMessage sgm){
//		System.out.println("sgm has received a searchguestmessage....") ;
//		
//		return ;
//	}
	public void searchGuestResultMessageAction(SearchGuestResultMessage sgrm){
		this.sgrm = sgrm ;		
		System.out.println("sgm has received a seachguestresultmessage...") ;
		
		return ;
	}
}

