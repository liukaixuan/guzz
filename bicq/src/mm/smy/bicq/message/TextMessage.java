package mm.smy.bicq.message ;
/**
* TextMessage. 文本聊天内容，可以设立加密标志
* @author XF
*
*
*
*/
import mm.smy.bicq.user.*;
import java.io.* ;
import java.util.Date ;


public class TextMessage implements Message,Serializable{
	private String content = "" ; //聊天文本内容
	private boolean security = false ; //文本是否加密
	private int type = MessageType.TEXT_MESSAGE ;
	private int mintype = type ; //小类型
	private Date sendTime = new Date() ;  //消息发送时间
	
	private User from = null ;
	private User to = null ;
	
	public TextMessage(){
		
	}
	
	public byte[] getByteContent() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeBoolean(security) ;
			dos.writeInt(mintype) ;
			dos.writeLong(sendTime.getTime()) ;
			dos.writeUTF(content==null?"":content) ;
			back = bout.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("TextMessage throw an Exception==>" + e.getMessage() ) ;
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
			security = dis.readBoolean() ;
			mintype = dis.readInt() ;
			sendTime = new Date(dis.readLong()) ;
			content = dis.readUTF() ;
		}catch(Exception e){
			System.out.println("TextMessage:setByteContent(byte[]) has thrown an Exception==>" + e.getMessage() ) ;
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
//methods defined by this class
	public boolean isSecurity(){
		return security ;
	}
	public void setSecurity(boolean m_security){
		security = m_security ;
	}
	
	public String getContent(){
		return content ;
	}
	public void setContent(String m_content){
		content = m_content ;
	}
	
	public Date getReceivedTime(){
		return sendTime ;
	}
	public void setReceivedTime(long milsec){
		sendTime = new Date(milsec) ;
	}
	
//methods maybe all messages need.	
	public void setMinType(int m_mintype){
		mintype = m_mintype ;
	}
	
	public void setFrom(User u){
		from = u ;
	}
	public void setTo(User u){
		to = u ;
	}
			
		
}