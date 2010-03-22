package mm.smy.bicq.user ;

/**
* guest groups. 好友分组
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/

import java.io.Serializable ;
import java.util.* ;

public class GuestGroup implements Serializable{
	private String groupname = "" ;
	private boolean isSystemic = false ; //是否为系统的分组
	private Vector members = new Vector() ; //group members.Store guest object
	private Date createtime = new Date() ;
	//constructor
	public GuestGroup(String m_groupname){
		groupname = m_groupname ;
	}
	public GuestGroup(){}
	
	//methods
	public Date getCreateTime(){ return createtime ; }
	
	public void setCreateTime(Date m_date) { createtime = m_date ; }
	
	public boolean equals(GuestGroup gg){
		return this.getGroupname().equalsIgnoreCase(gg.getGroupname()) ;
	}
	
	public void setGroupname(String m_groupname){groupname = m_groupname ;} 
	public void setIsSystemic(boolean m_is){isSystemic = m_is ;}
	public String getGroupname(){ return groupname ;}
	public boolean isSystemic(){ return isSystemic ;}
	
	public void invite(Guest m_guest){ 
		if (members.contains(m_guest))
			return ;
		members.add(m_guest) ;
	}
	public void add(Guest m_guest){
		invite(m_guest) ;
	}
	public void delete(Guest m_guest){
		members.remove(m_guest) ;
	}
	
	public int size(){
		return members.size() ;
	}
	
	public Vector getAllGuests(){
//		if (members == null || members.size() == 0 )
//			return null ;
		return members ;
//		Guest[] g = new Guest[members.size()] ;
//		for(int i = 0 ; i < members.size() ; i++ ){
//			g[i] = (Guest) members.elementAt(i) ;
//		}
//		
//		return g ;
	}
		
	
}
