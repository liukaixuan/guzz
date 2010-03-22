package mm.smy.bicq.message ;

import mm.smy.bicq.user.* ;
import java.io.* ;
import java.net.InetAddress ;


public class StateChangedMessage extends AbstractMessage implements Message,Serializable{
	
	private static final String NO_IP = "*^%~__n#$#o%#$2yFAkt$" ; //为了便于传送，对外是透明的
	private static final int NO_PORT = 5201 ;  //为了便于传送；规定：如果IP是null，port无效。
	
	private String leave_word = "" ; //暂时离开时的留言消息
	
	private InetAddress IP = null ; //用户的IP
	private int port = this.NO_PORT ; //用户的监听端口。
	
	private boolean isNotify = false ; //是否应该提醒用户用户状态改变了。这主要用户在运行时用户改变状态通知她/他的好友。
	
	public StateChangedMessage(){
		this.type = MessageType.STATE_CHANGED_MESSAGE ;
		this.mintype = User.OFFLINE ;
	}
	
//notify infor
	public boolean isNotify(){
		return isNotify ;
	}
	public void setIsNotify(boolean m_notify){
		isNotify = m_notify ;
	}
	
//IP and port.
	public void setIP(InetAddress m_IP){
		IP = m_IP ;	
	}
	public InetAddress getIP(){
		return IP ;	
	}
	
	public void setPort(int m_port){
		port = m_port ;	
	}
	
	public int getPort(){
		return port ;
	}
	
//methods for this type of message

	public String getTempLeaveWord(){
		return leave_word ;
	}
	public void setTempLeaveWord(String temp_word){
		leave_word = temp_word ;
	}
	
//the must do methods.
	public byte[] getByteContent() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeInt(mintype) ;
			dos.writeUTF(leave_word==null?"":leave_word) ;
			dos.writeBoolean(isNotify) ;
			dos.writeUTF(IP==null?this.NO_IP:IP.toString()) ;
			dos.writeInt(port) ;
			back = bout.toByteArray() ;
			
		}catch(Exception e){
			System.out.println("StateChangedMessage throws an Exception==>" + e.getMessage() ) ;
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
			mintype = dis.readInt() ;
			leave_word = dis.readUTF() ;
			isNotify = dis.readBoolean() ;
			
			String tempIP = dis.readUTF() ;
			if(tempIP.length() == 0)
				IP = null ;
			else if(tempIP.equals(this.NO_IP)){
				//没有IP段。	
				IP = null ;
			}else{
				//为了解决像"/211.68.47.45"之类的IP无法解析问题，我们保留纯数字的IP地址。
				//例如把"Cathy/211.68.47.45"修改为"211.68.47.45"
				try{
					//System.out.println("orginal IP is:" + tempIP) ;
					IP = InetAddress.getByName(mm.smy.text.StringFormat.formatIP(tempIP)) ;	
				}catch(Exception e){
					System.out.println("cannot solve the IP:" + IP + " in StateChangedMessage==>" + e.getMessage() ) ;
				}
			}
			
			port = dis.readInt() ;
			
		}catch(Exception e){
			System.out.println("StateChangedMessage:setByteContent(byte[]) has thrown an Exception==>" + e.getMessage() ) ;
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
		
		return ;		
	}
	
/******************************Test IP cutting****************************************/	
/*	
	public static void main(String[] args){
		String tempIP = "" ;
		int start = tempIP.indexOf("/") ;
		if(start != -1){
			tempIP = tempIP.substring(start + 1 ,tempIP.length()) ;	
		}
		System.out.println("finished:" + tempIP) ;
	}
	
*/	
	
	
}