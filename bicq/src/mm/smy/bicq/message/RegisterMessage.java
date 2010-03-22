package mm.smy.bicq.message ;


import java.io.* ;
import mm.smy.bicq.user.User ;

public class RegisterMessage extends AbstractMessage implements Serializable, Message{
	public static final int REGISTER_NEW_USER = 3001 ;
	
	public RegisterMessage(){
		this.mintype = this.REGISTER_NEW_USER ;
		this.type = MessageType.REGISTER_MESSAGE ;
	}
	
	private String password = "" ;
	
	private User user = null ;
	
	private int port = -1 ;
	
	public void setPort(int m_port){
		port = m_port ; 	
	}
	
	public int getPort(){
		return port ; 	
	}
	
	public void setPassword(String m_password){
		password = m_password ;	
	}
	public String getPassword(){ return password ; }
	
	public void setUser(User u){ user = u ; }
	
	public User getUser(){ return user ; }
	
	public void setByteContent(byte[] b){
		if( b == null ) return ;
		
		bin = new ByteArrayInputStream(b) ;
		dis = new DataInputStream(bin) ;
		int length ;
		try{
			port = dis.readInt() ;
			password = dis.readUTF() ;	
			length = dis.readInt() ;
			byte[] temp = new byte[length] ;
			dis.read(temp) ;
			user = new User() ;
			user =  user.toObject(temp) ;
		}catch(Exception e){
			mm.smy.bicq.debug.BugWriter.log(this,e,"setByteContent(byte[]) error.") ;	
		}finally{
			if(bin != null){
				try{
					bin.close() ;	
				}catch(Exception e1){}
			}
			if(dis != null){
				try{
					dis.close() ;	
				}catch(Exception e2){}
			}			
		}
		return ;
	}
	
	public byte[] getByteContent(){
		if(user == null) return null ;
		
		bout = new ByteArrayOutputStream() ;
		dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		try{
			dos.writeInt(port) ;
			dos.writeUTF(password==null?"":password) ;	
			byte[] b = user.toBytes() ;
			dos.writeInt(b.length) ;
			dos.write(b) ;
			back = bout.toByteArray() ;
		}catch(Exception e){
			mm.smy.bicq.debug.BugWriter.log(this,e,"error while convert getBytes.") ;	
		}finally{
			if(bout != null){
				try{
					bout.close() ;	
				}catch(Exception e1){}
			}
			if(dos != null){
				try{
					dos.close() ;	
				}catch(Exception e2){}
			}			
		}
		return back ;
	}
	
	
	
}
