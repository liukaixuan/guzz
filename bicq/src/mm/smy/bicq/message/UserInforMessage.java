package mm.smy.bicq.message ;

/**
* User Infor
* @author XF
*
*
*
*
*/
import java.io.* ;

import mm.smy.bicq.user.* ;


public class UserInforMessage implements Serializable, Message{
	//public static final int UPDATE_INFOR = 502 ; //更新个人资料
	public static final int UPDATE_HOST_INFOR = 503 ;
	public static final int UPDATE_GUEST_INFOR = 504 ;
	
	private User from = null ;
	private User to = null ;
	private User current_user = null ;
	
	private int type = MessageType.USER_INFOR_MESSAGE ;
	private int mintype = UPDATE_GUEST_INFOR ;

//method this class holds
	/**
	*设定请求/更改资料的 用户
	*/
	public void setUser(User u){ 
		current_user = u ;
	}
	public User getUser(){
		return current_user ;
	}
	
//methods most classes use
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
		//if (current_user == null )
		//	return null ;
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(baos) ;
		byte[] back = null ;
		try{
			dos.writeInt(mintype) ;
			if(current_user != null){
				back = current_user.toBytes() ;
				dos.writeInt(back.length) ;
				dos.write(back) ;
			}
			back = baos.toByteArray() ; 			
		}catch(Exception e){
			System.out.println("UserInforMessage错误报告，在object->byte时发生错误。==〉" + e.getMessage()) ;			
		}finally{
			try{
				baos.close() ;
				dos.close() ;
			}catch(Exception e){ }		
		}
		System.out.println("back bytes length:" + back.length) ;
		return back ;
	}
	
	public void setByteContent(byte[] b) {
		if(b == null) return ;
		ByteArrayInputStream bais = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bais) ;
		
		try{
			mintype = dis.readInt() ;
			int byte_length = dis.readInt() ;
			if(byte_length > 0 ){
				byte[] temp_byte = new byte[byte_length] ;
				dis.read(temp_byte) ;
	
				if(mintype == this.UPDATE_GUEST_INFOR){
					current_user = new Guest() ;
					current_user = current_user.toObject(temp_byte) ;
				}else if(mintype == this.UPDATE_HOST_INFOR){
					current_user = new Host() ;
					current_user = current_user.toObject(temp_byte) ;
				}
			}
		}catch(Exception e){
			System.out.println("UserInforMessage错误报告，在byte->object时发生错误。==〉" + e.getMessage()) ;
		}finally{
			try{
				bais.close() ;
				dis.close() ;
			}catch(Exception e){}
		}
		System.out.println("uimessage: user:" + current_user) ;
		return ;
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


	
	
	
	
	
}



