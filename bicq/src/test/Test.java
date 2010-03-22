package test ;

import mm.smy.bicq.user.* ;
import mm.smy.bicq.user.manager.* ;

import java.util.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

public class Test extends JFrame{
	
	public static void main(String[] args){
		Test t = new Test() ;
	}
	
	private Hashtable guestgroups = null ;
	
	private void initGuestGroup(){
		guestgroups = new Hashtable() ;
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
	
	public Test(){
		this.setSize(400,400) ;
		this.show() ;
		initGuestGroup() ;
		startTest() ;
	}
	
	private void startTest(){
		GuestGroupManager ggm = new GuestGroupManager(guestgroups) ;
		ggm.show() ;
		GuestGroup gg = ggm.getChoseGuestGroup() ;
		System.out.println("gg:" + gg.getGroupname() ) ;
	}
	
	
}