package mm.smy.bicq.message ;

/**
* password: login and modify password
* @author XF
* 该消息如果是Login_request，将会携带用户监听端口。
* 因为只有这样，服务器才知道回复的消息该发到哪儿。
*/

import mm.smy.bicq.user.* ;
import java.io.* ;


public class UserPswMessage implements Serializable, Message {
	public static final int LOGIN_REQUEST = 701 ;
	public static final int LOGIN_SUCCESS = 702 ;
	public static final int LOGIN_FAILED = 703 ;
	public static final int MODIFY_PSW_REQUEST = 704 ;
	public static final int MODIFY_PSW_OK = 705 ;
	public static final int MODIFY_PSW_FAILED  = 707 ;
	public static final int OTHER = 706 ;
	
	private int type = MessageType.USER_PSW_MESSAGE ;
	private int mintype = OTHER ;
	private User from = null ;
	private User to = null ;
	
	private int port = -1 ; //用户使用的接收消息的端口。
	
	private String explain = "" ; //错误解释。
	
	private String password = "" ;
	private String newpassword = "" ;
//Constructors
	public UserPswMessage(){}
	
//methods only in the class
	public void setPassword(String m_password){
		password = m_password ;
	}
	public String getPassword(){
		return password ;
	}
	
	public void setNewPassword(String m_password){
		newpassword = m_password ;
	}
	public String getNewPassword(){
		return newpassword ;
	}
	
	public void setExplain(String m_explain){
		explain = m_explain ;	
	}
	
	
	public String getExplain(){
		return explain ;	
	}
	
	public int getPort(){ return port ; }
	public void setPort(int m_port){ port = m_port ; }

//methods most message needs
	public void setFrom(User u){
		from = u ;
	}
	public void setTo(User u){
		to = u ;
	}
	
	public void setMinType(int m_type){
		mintype = m_type ;
	}
	
//must methods	
	public byte[] getByteContent() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeInt(mintype) ;
			dos.writeUTF(password==null?"":password) ;
			dos.writeUTF(newpassword==null?"":newpassword) ;
			dos.writeUTF(explain==null?"":explain) ;
			dos.writeInt(port) ;
			back = bout.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("UserPswMessage has thrown an Exception==>" + e.getMessage() ) ;
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

	public int getType() {
		return type ;
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

	public void setByteContent(byte[] b) {
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		
		try{
			mintype = dis.readInt() ;
			password = dis.readUTF() ;
			newpassword = dis.readUTF() ;
			explain = dis.readUTF() ;
			port = dis.readInt() ;
		}catch(Exception e){
			System.out.println("UserPswMessage:setByteContent(byte[]) has thrown an Exception==>" + e.getMessage() ) ;
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