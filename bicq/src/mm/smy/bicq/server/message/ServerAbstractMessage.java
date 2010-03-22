package mm.smy.bicq.server.message ;

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

import mm.smy.bicq.message.MessageType ;

public abstract class ServerAbstractMessage implements Serializable{
	
	protected int type = MessageType.UNKNOWN_TYPE ;
	protected int mintype = MessageType.UNKNOWN_TYPE ;
	
	
	protected int from = -1 ;
	protected int to = -1 ;
	
	public int getType() {
		return type ;
	}

	public int getMinType() {
		return mintype ;
	}
	
	public void setMinType(int m_type){
		mintype = m_type ;	
	}

	public int getFrom() {
		return from ;
	}

	public int getTo() {
		return to ;
	}

	public void setFrom(int u) {
		from = u ;
	}

	public void setTo(int u) {
		to = u ;
	}

	public abstract void setByteContent(byte[] b) ;
	
	public abstract byte[] getByteContent() ;
	
	
}

