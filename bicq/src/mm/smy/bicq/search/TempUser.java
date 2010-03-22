package mm.smy.bicq.search ;

/**
* 该类的目的是暂时的保存SeachGuestResultMessage转换出的临时User资料，因为该资料包含了Host对象的东西，而
* 添加的是Guest对象，所以给转换带来了麻烦。而且用Host对象作中间变量的话，太浪费内存了。
* 所以引入的该临时类存放。该类包含的User资料如下：
*                                                number,portrait,gender,from,IP,state,auth(身份验证)
*
* @author XF
* @author e-mail:myreligion@163.com
* @date 2003-10-6
*
*/

import java.net.InetAddress ;
import mm.smy.bicq.user.* ;

public class TempUser implements java.io.Serializable{
	public TempUser(){}	
	
	public int getNumber(){ return number ; }
	public int getPortrait(){ return portrait ; }
	public int getGender(){ return gender ; }
	public String getFrom(){ return from ; }
	public InetAddress getIP(){ return IP ; }
	public int getState() { return state ; }
	public int getAuth() { return auth ; }
	public String getNickname() { return nickname ; }
	public int getPort(){ return port ; }
	
	public void setNumber( int m_number) { number = m_number ; }
	public void setPortrait(int m_portrait){ portrait = m_portrait ; }
	public void setGender(int m_gender ) { gender = m_gender ; }
	public void setFrom(String m_from ){ from = m_from ; }
	public void setIP(InetAddress m_IP){ IP = m_IP ; }
	public void setState( int m_state){ state = m_state ; }
	public void setAuth(int m_auth) { auth = m_auth ; }
	public void setNickname(String m_nickname) { nickname = m_nickname ; }
	public void setPort(int m_port){ port = m_port ; }
	
	
//private:
	private int number = 0 ;
	private int portrait = 0 ;
	private int gender = -1 ;
	private String from = null ;
	private InetAddress IP = null ;
	private int port = 5201 ;
	private int state = User.OFFLINE ;
	private int auth = Host.ALLOW_ANYONE ; 	
	private String nickname = "" ;
}

