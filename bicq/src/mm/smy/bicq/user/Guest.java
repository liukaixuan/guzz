package mm.smy.bicq.user ;

/**
* the guest's information.  guest:∫√”—
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13,2003-8-15
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.net.* ;
//import mm.smy.bicq.message.* ;

public class Guest extends User implements Serializable{
	public static final int VOICE_NOTICE = 20 ;
	public static final int BOX_NOTICE   = 21 ;
	public static final int NO_NOTICE = 22 ;
	
	private GuestGroup guestgroup = null  ;
	private int onlineAction = VOICE_NOTICE ;
	private int offlineAction = VOICE_NOTICE ;
	private int messageAction = VOICE_NOTICE ;
	
	//constructors
	public Guest(){
		super() ;
	}
	public Guest(int m_number){
		super(m_number) ;
	}
	public Guest(int m_number,String m_nickname){
		super(m_number, m_nickname) ;
	}
	//methods...
	public void joinGuestGroup(GuestGroup gg){
		if (guestgroup != null)
			guestgroup.delete(this); //quit old group
		gg.invite(this) ; // join new group
		guestgroup = gg ; // update local memory data.
	}
	public void setOnlineAction(int m_oa){onlineAction = m_oa ;}
	public void setOfflineAction(int m_oa){offlineAction = m_oa ;}
	public void setMessageAction(int m_ma){messageAction = m_ma ;}
	
	public GuestGroup getGuestGroup(){return guestgroup ;}
	public int getOnlineAction(){return onlineAction ;}
	public int getOfflineAction(){return offlineAction ;}
	public int getMessageAction(){return messageAction ;}	
	
	//override
	public Guest copyFrom(Guest g){
		super.copyFrom(g) ;
		onlineAction = g.getOnlineAction() ;
		offlineAction = g.getOfflineAction() ;
		messageAction = g.getMessageAction() ;	
		guestgroup = g.getGuestGroup() ;
		return this ;
	}
	
	public Guest copyInfor(Guest g){
		super.copyInfor(g) ;
		return this ;	
	}
	
	//serializable methods
	public User toObject(byte[] b){
		super.toObject(b) ;
		return this ;		
	}
	
	public byte[] toBytes(){		
		return super.toBytes() ;		
	}	
	
}

