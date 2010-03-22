package mm.smy.bicq.message ;

import mm.smy.bicq.user.* ;

public interface Message extends java.io.Serializable {
	public byte[] getByteContent() ;
	public int getType() ;
	public int getMinType() ;
	public User getFrom() ;
	public User getTo() ;
	
	public void setFrom(User u) ;
	public void setTo(User u) ;
	
	public void setByteContent(byte[] b) ;	
	
}
