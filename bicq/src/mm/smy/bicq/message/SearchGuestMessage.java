package mm.smy.bicq.message ;

import mm.smy.bicq.user.*;
import java.io.* ;
import java.util.Date ;

/**
*查找好友请求消息。
*@author XF
*
*
*
*
*
*/

public class SearchGuestMessage implements Message, Serializable{
	public static final int SEARCH_ONLINE = 301 ;
	public static final int SEARCH_BY_NUMBER = 302 ;
	public static final int SEARCH_BY_NICKNAME = 303 ;
	public static final int SEARCH_BY_GFA = 304 ;
	
	private String nickname = "" ; //昵称查找
	private int    number   = -1 ; 
	private boolean online = false ; //false表示所有用户，true只查找在线的用户。
	private int gender = -1 ;        //-1表示所有用户，0女，1男
	private String province    = "" ; //长度为零代表所有。
	private int age_from = -1 ;     //最小的年龄， -1（小于零）表示没有下届
	private int age_to = -1 ; //最大的年龄。-1（小于零）表示没有上届。
	private int mintype = this.SEARCH_ONLINE ; //搜索方法，默认是在线用户
	private long flag = new Date().getTime() ; //标志位，服务器将该标识返回；因为用户可能会发出很多这样的消息，我们要知道返回的是哪个发出的。为扩展功能用。
	private int startpos = 0 ;	 //开始的位置，用于 分页 使用。
	
	private User from = null ;
	private User to   = null ;
//methods this class owns
	public void setProvince(String m_province){	province = m_province ;	}
	public String getProvince(){ return province ; }
	
	public void setNickname(String m_nickname){ nickname = m_nickname ; }
	public String getNickname(){ return nickname ; }
	
	public void setNumber( int m_number){ number = m_number ; }
	public int  getNumber(){ return number ; }
	
	public void setAge(int m_agefrom, int m_ageto){
		age_from = m_agefrom ; 
		age_to   = m_ageto   ;
	}
	public int getAgeFrom(){ return age_from ; }
	public int getAgeTo()  { return age_to   ; }
	
	public void setStartPos(int m_pos){ startpos = m_pos ; }
	public int getStartPos(){ return startpos ; }
	
	public void setFlag(long m_flag){ flag = m_flag ; }
	public long getFlag(){ return flag ; }
	
	public void setGender(int m_gender){ gender = m_gender ; }
	public int getGender(){ return gender ; }
//methods most message use

	public void setFrom(User u){ from = u ;}
	public void setTo(User u){ to = u ; }
	
	public void setMinType(int m_mintype){ mintype = m_mintype ; }
	
//commom methods...

	public int getType() {
		return MessageType.SEARCH_GUEST_MESSAGE ;
	}

	public int getMinType() {
		return mintype ;
	}

	public User getFrom() {
		return from ;
	}

	public User getTo() {
		return to ;
	}
	
	public byte[] getByteContent() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeUTF(nickname==null?"":nickname) ; //nickname
			dos.writeInt(number) ; //number
			dos.writeBoolean(online) ; //online
			dos.writeLong(flag) ; //flag
			dos.writeInt(startpos) ; //start position
			dos.writeInt(mintype) ;  //mintype
			dos.writeInt(gender) ; //gender
			dos.writeInt(age_from) ; //age from
			dos.writeInt(age_to) ;   //age to
			dos.writeUTF(province==null?"":province) ; //province.

			back = bout.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("SearchGuestMessage throw an Exception==>" + e.getMessage() ) ;
		}finally{
			if (dos != null){
				try{
					dos.close() ;
				}catch(Exception e){}
			}
			if (bout != null ){
				try{
					bout.close() ;
				}catch(Exception e){}
			}
		}
		return back ;
	}

	public void setByteContent(byte[] b) {
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		
		try{
			nickname = dis.readUTF() ;
			number   = dis.readInt() ;
			online   = dis.readBoolean() ;
			flag     = dis.readLong() ;
			startpos = dis.readInt() ;
			mintype  = dis.readInt() ;
			gender   = dis.readInt() ;
			age_from = dis.readInt() ;
			age_to   = dis.readInt() ;
			province = dis.readUTF() ;
		}catch(Exception e){
			System.out.println("SearchGuestMessage:setByteContent(byte[]) has thrown an Exception==>" + e.getMessage() ) ;
		}finally{
			if (dis != null){
				try{
					dis.close() ;
				}catch(Exception e){}
			}
			if (bin != null ){
				try{
					bin.close() ;
				}catch(Exception e){}
			}
		}				
	}
	
}
