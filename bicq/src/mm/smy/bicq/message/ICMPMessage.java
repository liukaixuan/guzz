package mm.smy.bicq.message ;
/**
* 模拟ICMP数据报，包含一个String类型的数据，和发送时间Date型。
* 在MessageType中，这儿从2000开始。
* @author XF
* @e-mail myreligion@163.com
* @date   2003-10-22   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.* ;
import java.util.Date ;
import mm.smy.bicq.message.Message;
import mm.smy.bicq.user.*;

public class ICMPMessage extends AbstractMessage implements Serializable{
	
	public static final int LOAD_ALL_GUESTS = 2001 ;
	public static final int LOAD_HOST_INFOR = 2002 ;
	public static final int LOAD_SINGLE_GUEST_INFOR = 2003 ;
	public static final int UPDATE_HOST_INFOR_RESULT = 2004 ;
	public static final int LOGIN_RESULT = 2005 ;
	public static final int REGISTER_RESULT_SUCCESS = 2006 ;
	public static final int REGISTER_RESULT_FAIL = 2007 ;
	public static final int LOGIN_TO_SERVER_SUCCESS = 2008 ;
	public static final int QUIT_BICQ = 2009 ;
	public static final int ADD_FRIEND = 2010 ;
	public static final int DELETE_FRIEND = 2011 ;
//	public static final int 
	
	protected String content = "" ; //也许以后会有人继承。
	protected Date sendtime = new Date() ;
	
	public ICMPMessage(){
		this.type = MessageType.ICMP_MESSAGE ;
	}
	
	public void setMinType(int m_type){
		mintype = m_type ;
	}
	
	public void setSendTime(Date d){
		sendtime = d ;
	}
	public Date getSendTime(){
		return sendtime ;
	}
	
	public String getContent(){ return content ; }
	public void setContent(String m_content){ content = m_content ; }
	
	public byte[] getByteContent() {
		bout = new ByteArrayOutputStream() ;
		dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeInt(mintype) ;
			dos.writeLong(sendtime==null?-1L:sendtime.getTime()) ;
			dos.writeUTF(content==null?"":content) ;
			back = bout.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("ICMPMessage throw an Exception==>" + e.getMessage() ) ;
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
		bin = new ByteArrayInputStream(b) ;
		dis = new DataInputStream(bin) ;
		
		try{
			mintype = dis.readInt() ;
			sendtime = new Date(dis.readLong()) ;
			content = dis.readUTF() ;
		}catch(Exception e){
			System.out.println("ICMPMessage:setByteContent(byte[]) has thrown an Exception==>" + e.getMessage() ) ;
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
