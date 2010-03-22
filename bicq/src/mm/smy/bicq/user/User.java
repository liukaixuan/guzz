package mm.smy.bicq.user ;

/**
* user information
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.net.InetAddress ;
import mm.smy.bicq.message.* ;

public class User implements Serializable, Cloneable{
	public static final int ONLINE = 10 ; //在线
	public static final int OFFLINE = 11 ; //不在线
	public static final int HIDDEN = 12 ; //隐身
	public static final int LEAVE = 13 ; //离开
	public static final int TEMP_OFFLINE = 14 ; //登陆后，离线
	
	protected transient String temp_leave_word = "" ;//用户选择离开时的留言
	
	public String getLeaveWord(){
		return temp_leave_word ;
	}
	public void setLeaveWord(String m_leave_word){
		temp_leave_word = m_leave_word ;
	}
	//copy methods
	public User copyFrom(User u){
		if( u == null) return null;
		
		temp_leave_word = u.getLeaveWord() ;
		IP = u.getIP() ;
		port = u.getPort() ;
		number = u.getNumber() ;
		nickname = u.getNickname() ;
		state = u.getState() ;
		previousstate = u.getPreviousState() ;
		portrait = u.getPortrait() ;
		mail = u.getMail() ;
		realname = u.getRealname() ;
		homepage = u.getHomepage() ;
		zip = u.getZip() ;
		address = u.getAddress() ;
		country = u.getCountry() ;
		province = u.getProvince() ;
		year = u.getYear() ;
		month = u.getMonth() ;
		day = u.getDay() ;
		explain = u.getExplain() ;
		gender = u.getGender() ;
		unreadmessages = u.getUnreadMessages() ;
		return this;
	}

	/**
	* 更新好友资料，不过只更新网络传送的UserInforMessage部分。
	* 该函数可以有效地解决 用户状态 在下在新的资料后被破坏，尤其是IP,port等信息。
	*/
	public User copyInfor(User src){
		if(src == null) return this ;
		
		number = src.getNumber() ;
		gender = src.getGender() ;
		nickname = src.getNickname() ;
		portrait = src.getPortrait() ;
		mail = src.getMail() ;
		realname = src.getRealname() ;
		homepage = src.getHomepage() ;
		telephone = src.getTelephone() ;
		zip = src.getZip() ;
		address = src.getAddress() ;
		country = src.getCountry() ;
		province = src.getProvince() ;
		explain = src.getExplain() ;
		year = src.getYear() ;
		month = src.getMonth() ;
		day = src.getDay() ;
		
		return this ;		
	}
		
	//the user's net connect infor
	protected transient InetAddress IP = null ;
	protected transient int port = 5201 ;
	
	public void setIP(InetAddress m_IP){ IP = m_IP ;} 
	public void setPort(int m_port) { port = m_port ; }
	
	public InetAddress getIP(){return IP ; }
	public int getPort(){return port ;}

	//basic infor
	protected int number = 0 ;
	protected String nickname = "" ;
	protected int state = OFFLINE ;
	//先前的状态，因为有些程序要求检测状态的变化。
	//所以加入该字段，当state变化时自动保存上次的state
	//用户可以选择让这两个state做暂时的一样，一使程序不会重复检测。
	protected int previousstate = this.OFFLINE ; 
	
	protected int portrait = 0 ; //头像文件名称
	protected String mail = "" ;
	//constructor
	public User(){}
	public User(int m_number){number = m_number ;}
	public User(int m_number,String m_nickname){
		number = m_number ;
		nickname = m_nickname ;
		//nickname must be not null or empty,Or we will use NUMBER to fill it.
		if (nickname == null || nickname.length() == 0)
			nickname = new Integer(number).toString() ;		
	}	
	//methods	
	public String getNickname(){
		if(nickname == null || nickname.length() == 0 )
			return new Integer(number).toString() ;
		return nickname ;
	}
	public int getNumber(){return number;} 
	//两种状态。
	public int getState(){ return state ; }
	public int getPreviousState(){ return previousstate ; } 
	
	public int getPortrait() { return portrait ;}
	public String getMail() { return mail ;}
	
	public void setNickname(String m_nickname){
		if (m_nickname.length() == 0 || m_nickname == null)
			return ;// no null or empty nickname is allowed.
		nickname = m_nickname ;
	}
	public void setNumber(int m_number){ 
		number = m_number ;
		//we check the valid of nickname here....
	 	if(nickname == null || nickname.length() == 0){
	 		nickname = new Integer(number).toString() ;
	 	}
	}
	public void setState(int m_state){
		previousstate = state ; //自动的保存上次的state.
		state = m_state ; 
	}
	//设置上次的状态
	public void setPreviousState( int m_state){
		previousstate = m_state ;
	}
	
	
	public void setPortrait(int m_portrait){portrait = m_portrait;}
	public void setMail(String m_mail){ mail = m_mail ;}
	
	//after a long thought, I think we should put this in the main memory.
	protected String realname = "" ;
	protected String homepage = "" ;
	protected String telephone = "" ;
	protected int zip = 0 ;
	protected String address = "" ;
	protected String country = "" ;
	protected String province = "" ;
	protected String explain = "" ;
	protected int year  = 0 ;
	protected int month = 0 ;
	protected int day   = 0 ;
	protected int gender = -1 ; //0:girl  ; 1:boy
	
	// the following is some methods to support this main memory waste rubblish!
	public int getGender(){ return gender ; }
	public String getRealname(){return realname ;}
	public String getHomepage(){return homepage ;}
	public String getTelephone(){return telephone ;}
	public int getZip(){ return zip ;}
	public String getAddress(){return address ;}
	public String getCountry(){return country ;}
	public String getProvince(){return province ;}
	public String getExplain(){return explain ;}
	public int getYear(){return year ;}
	public int getMonth(){return month ;}
	public int getDay(){return day ;}
	
	public void setGender(int m_gender){ gender = m_gender ; }
	public void setRealname(String m_realname){realname = m_realname ;}
	public void setHomepage(String m_homepage){homepage = m_homepage ;}
	public void setTelephone(String m_telephone){telephone = m_telephone ;}
	public void setZip(int m_zip){zip = m_zip ;}
	public void setAddress(String m_address){address = m_address ;}
	public void setCounty(String m_country){country = m_country ;}
	public void setProvince(String m_province){province = m_province ;}
	public void setExplain(String m_explain){explain = m_explain ;}
	public void setYear(int m_year){year = m_year ;}
	public void setMonth(int m_month){month = m_month ;}
	public void setDay(int m_day){day = m_day ;}
/**************************************************************************************************************/	
	//The following THREE methods is to deal with unread messages of this user.
	protected transient int unreadmessages = 0 ; //no unread message
	
	/**
	*unread messages +1 ; if the current unread messages is minus, set it to 1 .
	*/
	public void incUnreadMessages(){
		if(unreadmessages < 0) unreadmessages = 0 ;
		
		unreadmessages++ ;	
	}
	
	public void decUnreadMessages(){
		if(unreadmessages <= 0 ) return ;
		
		unreadmessages-- ;	
	}
	
	public boolean hasUnreadMessages(){
		return ( unreadmessages > 0 ) ;	
	}
	
	public int getUnreadMessages(){
		return unreadmessages ;
	}

/*************************************************************************************************************/

	//overrided methods
	public boolean equals(User m_user){
		if(m_user == null) return false ;
		
		return number == m_user.getNumber() ;
	}
	public boolean equalsIngoreCase(User m_user){
		if(m_user == null) return false ;
		
		return this.equals(m_user) ;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer() ;
		
		sb.append("<number = ") ;
		sb.append(number) ;	
		sb.append(",nickname = ") ;
		sb.append(nickname) ;	
		sb.append(",temp_leave_word = ") ;
		sb.append(temp_leave_word) ;	
		sb.append(",state = ") ;
		sb.append(state) ;	
		sb.append(",previousstate = ") ;
		sb.append(previousstate) ;
		sb.append(",portrait = ") ;
		sb.append(portrait) ;	
		sb.append(",mail = ") ;
		sb.append(mail) ;	
		sb.append(",realname = ") ;
		sb.append(realname) ;	
		sb.append(",homepage = ") ;
		sb.append(homepage) ;	
		sb.append(",telephone = ") ;
		sb.append(telephone) ;	
		sb.append(",zip = ") ;
		sb.append(zip) ;	
		sb.append(",country = ") ;
		sb.append(country) ;	
		sb.append(",province = ") ;
		sb.append(province) ;	
		sb.append(",year = ") ;
		sb.append(year) ;	
		sb.append(",month = ") ;
		sb.append(month) ;	
		sb.append(",day = ") ;
		sb.append(day) ;	
		sb.append(",unreadmessages = ") ;
		sb.append(unreadmessages) ;	
		sb.append(">") ;
		
		return sb.toString() ;	
	}
	
	
	//serializable
	public User toObject(byte[] b){
		if (b == null || b.length == 0)
			return null ;
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		try{
			number = dis.readInt() ;
			state = dis.readInt() ;
			this.setNickname(dis.readUTF()) ;
			portrait = dis.readInt() ;
			mail = dis.readUTF() ;
			
			realname = dis.readUTF() ;
			homepage = dis.readUTF() ;
			telephone = dis.readUTF() ;
			zip = dis.readInt() ;
			address = dis.readUTF() ;
			country = dis.readUTF() ;
			province = dis.readUTF() ;
			explain = dis.readUTF() ;
			
			year = dis.readInt() ;
			month = dis.readInt() ;
			day = dis.readInt() ;
			
			gender = dis.readInt() ;
		}catch(Exception e){
			System.out.println("mm.smy.bicq.user.User:toObject() has thrown an Exception==>" + e.getMessage()) ;
		}finally{
			if (bin != null){
				try{
					bin.close() ;
				}catch(Exception e){}
			}
			if (dis != null){
				try{
					dis.close() ;
				}catch(Exception e){}
			}
		}
		
		return this ;
	}
	public byte[] toBytes(){
		ByteArrayOutputStream bo = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bo) ;		
		byte[] back = null;
		try{			
			dos.writeInt(number) ; //number
			dos.writeInt(state) ;
			dos.writeUTF(nickname==null?"":nickname) ; //nickname
			dos.writeInt(portrait) ;
			dos.writeUTF(mail==null?"":mail) ;
		
			dos.writeUTF(realname==null?"":realname) ;
			dos.writeUTF(homepage==null?"":homepage) ;
			dos.writeUTF(telephone==null?"":telephone) ;
			dos.writeInt(zip) ;
			dos.writeUTF(address==null?"":address) ;
			dos.writeUTF(country==null?"":country) ;
			dos.writeUTF(province==null?"":province) ;
			dos.writeUTF(explain==null?"":explain) ;
		
			dos.writeInt(year) ;
			dos.writeInt(month) ;
			dos.writeInt(day) ;	
			
			dos.writeInt(gender) ;
			
			back = bo.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("mm.smy.bicq.user.User:toBytes() has thrown an exception:" + e.getMessage()) ;
		}finally{
			if (bo!= null){
				try{
					bo.close() ;
				}catch(Exception e){}
			}
			
			if (dos != null){
				try{
					dos.close() ;
				}catch(Exception e){}
			}
		}
	
		return back ;
	}


						
}
		

