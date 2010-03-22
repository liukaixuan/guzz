package mm.smy.bicq.server.message ;

import java.io.* ;
import mm.smy.bicq.message.MessageType ;

/**
* 消息认证，包含加为好友请求/加为好友拒绝等等
* 该消息应该与PermitMessage.class同步。
*
* @date 2003-11-21
*
*@author XF
*@author e-mail:myreligion@163.com
*@copyright Copyright 2003 XF All Rights Reserved.
*/

public class ServerPermitMessage implements Serializable {
	public static final int PERMIT_REQUEST = 601 ; //需要身份验证
	public static final int PERMIT_SEND    = 602 ; //请求身份验证
	public static final int PERMIT_ALLOWED = 603 ; //允许加为好友
	public static final int PERMIT_REFUSED = 604 ; //拒绝请求
	
	private int from = -1 ;
	private int to   = -1 ;
	private int mintype  = MessageType.UNKNOWN_TYPE ;
	private String content = "" ; //简单留言
	
	public ServerPermitMessage(){
		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////	
	public void setMintype(int m_type){
		mintype = m_type ;
	}
	
	public void setContent(String s){
		content = s ;
	}
	
	public String getContent(){ return content ; }
	
/////////////////////////////////////////////////////////////////////////////////////////////////	
	public int getType() {	return MessageType.PERMIT_MESSAGE ;	}

	public int getMinType() { return mintype ; }

	public int getFrom() {	return from ; }

	public int getTo() { return to ; }
	
	public void setFrom(int u){ from = u ; }
	
	public void setTo(int u){ to = u ; }
	
	public byte[] getByteContent() {
		byte[] back = null ;
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		try{
			dos.writeInt(mintype) ;
			dos.writeUTF(content == null?"":content) ;
			back = bout.toByteArray() ;
		}catch(Exception e){
			System.out.println("PermitMessage throws an Exception while convert to byte[]==>" + e.getMessage() ) ;
		}finally{
			try{
				dos.close() ;
				bout.close() ;
			}catch(Exception e1){}		
			
		}
		return back ;
	}

	public void setByteContent(byte[] b) {
		if (b == null) return ;
		
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		try{
			mintype = dis.readInt() ;
			content = dis.readUTF() ;
		}catch(Exception e){
			System.out.println("PermitMessage throws an Exception while convert byte[] back ==>" + e.getMessage() ) ;
		}finally{
			try{
				dis.close() ;
				bin.close() ;
			}catch(Exception e1){
			
			}		
		}
		return ;
	}
	
}
