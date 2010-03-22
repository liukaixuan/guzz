package mm.smy.bicq.user.manager ;

/**
* 文件操作
* 用户文件管理，可用于启动时对guests,guestgroups的初始化。
* 提供了对好友资料保存的快捷方式。
*
* @author XF
* @also see mm.smy.bicq.user.manager.UserNetManager
* @date 2003-10-23
*
*/
import java.util.Hashtable ;
import java.util.Vector ;
import java.util.Enumeration ;
import java.util.Date ;

import java.io.* ;

import mm.smy.bicq.user.* ;

public class UserFileManager{
	
	private Hashtable guestgroups = null ;
	private Hashtable guests = null ;
	
	private Host host = null ;
	private boolean isFileRead = false ;
	
	public UserFileManager(Host host){
		this.host = host ;	
	}
	
	public void writeHostFile(){
		HostFile hf = new HostFile(host.getNumber()) ;
		try{
			hf.save(host) ;
		}catch(Exception e){
			mm.smy.bicq.debug.BugWriter.log(e,"保存好友资料出错，UserFileManager.class类获得异常") ;	
		}
	}
	
	public void setIsFileRead(boolean m_read){
		isFileRead = m_read ;	
	}
	
	public boolean isDataExsit(){
		if(!isFileRead) readFile() ;
		
		return 	!(guestgroups == null) ;
	}
	
	/**
	* 把文件中的Guest, GuestGroup对象全部恢复，如果有这个文件的话。
	* 如果恢复后guests, guestgroups为空，则将他们设为null. 
	*/
	private void readFile(){
		Hashtable tempgroups = null ;
		GuestGroupFile ggf = new GuestGroupFile(host.getNumber()) ;
		try{
			tempgroups = ggf.getAll() ;
//			System.out.println("after reading immediately, guestgroups:" + guestgroups) ;	
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException" + e.getMessage()) ;
		}catch(IOException e){
			System.out.println("IOException" + e.getMessage()) ;
			e.printStackTrace() ;
		}catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException" + e.getMessage()) ;
		}catch(Exception e){
			mm.smy.bicq.debug.BugWriter.log(e,"在读好友的时候出现异常，UserFileManager.class类捕获异常。") ;	
		}
		
		if(tempgroups == null || tempgroups.size() == 0) return ;
		
		//if (guests == null) guests = new Hashtable(40) ;
		
		//guestgroups.clear() ;
		//guests.clear() ;
		
		Enumeration e = tempgroups.elements() ;
		while(e.hasMoreElements()){
			GuestGroup gg = (GuestGroup) e.nextElement() ;
			guestgroups.put(gg.getGroupname(),gg) ;
			Vector v = gg.getAllGuests() ;
			System.out.println("------------------------------------------------------------------") ;
			System.out.println("gg:" + gg.getGroupname()) ;
			System.out.println("members:" + v) ;
			System.out.println("------------------------------------------------------------------") ;
			Enumeration e2 = v.elements() ;
			while(e2.hasMoreElements()){
				Guest g = (Guest) e2.nextElement() ;
				if( g == null) continue ;
				guests.put(new Integer(g.getNumber()) , g) ;	
				//g.joinGuestGroup(gg) ;
			}
		}
		
//		System.out.println("++++++++++++++++++After read file, guestgroups:" + guestgroups) ;
//		System.out.println("guests:" + guests) ;
/* guestgroups and guests are created in the unm.class, useful through all the BCIQ programme, any part cannot make it
	//null, or NullPointException will be thrown.
			
		if(guestgroups != null){
			if(guestgroups.size() == 0){
				guestgroups = null ;			
				guests = null ;
			}
		}
*/
		isFileRead = true ;		
	}	
	
	public Hashtable getGuestGroups(){
		if(!isFileRead) readFile() ;
		
		return guestgroups ;
	}
	
	public Hashtable getGuests(){
		if(!isFileRead) readFile() ;
		
		return guests ;
	}
	
	public void setGuestGroups(Hashtable ggs){
		guestgroups = ggs ;	
	}
	public void setGuests(Hashtable gs){
		guests = gs ;
	}
	
	public void saveGuests(){
		GuestGroupFile file = new GuestGroupFile(host.getNumber()) ;
		try{
			file.save(guestgroups) ;	
		}catch(Exception e){
			System.out.println("save error:" + e.getMessage()) ;	
		}
		file.close() ;
	}

/*	
	public static void main(String[] args){
		UserFileManager file = new UserFileManager(new Host(2000)) ;
		file.test() ;	
	}

	public void test(){
		
		//initGuestGroup() ;
		System.out.println("data exsits:" + this.isDataExsit()) ;
		System.out.println("********************************************created:************") ;
		System.out.println("guestgroups:" + guestgroups) ;
		System.out.println("guests:" + guests ) ;
		//this.writeHostFile() ;
		//this.saveGuest() ;
		//guestgroups = null ;
		//guests = null ;
		this.readFile() ;
		System.out.println("*******************read file******************") ;
		System.out.println("guestgroups:" + guestgroups) ;
		System.out.println("guests:" + guests ) ;
		
			
	}
	
	private void initGuestGroup(){
		if(guestgroups == null) guestgroups = new Hashtable(5) ;
		
		GuestGroup g1 = new GuestGroup("我的好友") ;
		g1.setCreateTime(new Date(1992,5,5)) ;
		g1.setIsSystemic(true) ;
		GuestGroup g2 = new GuestGroup("陌生人") ;
		g2.setCreateTime(new Date(1998,2,4)) ;
		g2.setIsSystemic(true) ;
		GuestGroup g3 = new GuestGroup("黑名单") ;
		g3.setIsSystemic(true) ;
		g3.setCreateTime(new Date(1999,3,18)) ;
	
		g1.add(new Guest(1000)) ;
		g1.add(new Guest(2000)) ;
		
		g2.add(new Guest(3000)) ;
		g2.add(new Guest(5000,"小红")) ;
		
		guestgroups.put(g1.getGroupname(),g1) ;
		guestgroups.put(g2.getGroupname(),g2) ;		
		guestgroups.put(g3.getGroupname(),g3) ;	
		
	}	
*/	
}
