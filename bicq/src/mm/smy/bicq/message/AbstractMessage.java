package mm.smy.bicq.message ;

import mm.smy.bicq.message.Message;
import mm.smy.bicq.user.*;

/**
*
*
*
*
*
*
*
*/
import java.io.* ;
import mm.smy.bicq.user.* ;

public abstract class AbstractMessage implements Serializable, Message{
	
	protected int type = MessageType.UNKNOWN_TYPE ;
	protected int mintype = MessageType.UNKNOWN_TYPE ;
	
	protected ByteArrayOutputStream bout = null ;
	protected DataOutputStream dos = null ;
	protected ByteArrayInputStream bin = null ;
	protected DataInputStream dis = null ;
	
	private User from = null ;
	private User to = null ;
	
	public int getType() {
		return type ;
	}

	public int getMinType() {
		return mintype ;
	}
	
	public void setMinType(int m_type){
		mintype = m_type ;	
	}

	public User getFrom() {
		return from ;
	}

	public User getTo() {
		return to ;
	}

	public void setFrom(User u) {
		from = u ;
	}

	public void setTo(User u) {
		to = u ;
	}

	public abstract void setByteContent(byte[] b) ;
	
	public abstract byte[] getByteContent() ;
	
	
}

