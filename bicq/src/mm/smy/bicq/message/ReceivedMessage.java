package mm.smy.bicq.message ;

/**
* This class is a lower level message type.
* Represents for the Moniter.class. For it donnot hava any Host/Guest
* data, and we donnot want waste time to collect...,JUST use the user
* 's number to represent it. This message can only be recognised by
* Main Thread Class, who will do some job to convert it to common message.
*@author XF
*@author e-mail:myreligion@163.com
*@date 2003-8-15
*@copyright Copyright 2003 XF All Rights Reserved.
*/
import java.io.Serializable ;
import java.net.InetAddress ;

public class ReceivedMessage implements Serializable{
	private int from = -1 ;
	private int to   = 0 ;
	private int type   = MessageType.UNKNOWN_TYPE ; 
	private long hashcode  = -1 ;
	private byte[] content = null ;
	private transient InetAddress fromIP = null ;
	private transient int fromPort = 5200 ;
//	private String clip = "" ; //纪录已获得消息的片断
	
	public ReceivedMessage(){}
	
	public int getFrom(){return from ;}
	public int getTo(){ return to ;}
	public int getType(){ return type ;}
	public long getHashcode(){ return hashcode ; }
	public byte[] getContent(){return content ;}
	public InetAddress getIP(){ return fromIP ;}
	public int getPort(){ return fromPort ; }
	
	public void setFrom(int m_from){ from = m_from ; } 
	public void setTo(int m_to){ to = m_to ; }
	public void setType(int m_type) { type = m_type ; }
	public void setHashcode(long m_hashcode) { hashcode = m_hashcode ; }
	public void setContent(byte[] b){ content = b ; }
	public void setIP(InetAddress m_IP){ fromIP = m_IP ; }
	public void setPort(int m_port) { fromPort = m_port ; }
	
	
	
	//methods deal with the clip
/*	public boolean isClipFull(int maxnumber){ // is all message received.
		//message starts at 1
		if (clip.length() == 0 || clip == null)
			return false ;
		int j = 1 ;
		for (int i = 0 ; i < clip.length() ; i++ ){
			if (clip.charAt(i) == ','){
				j++ ;
			}
		}
		if (j >= maxnumber)
			return true ;
		
		return false ;
	}
	public void addClip(int currentpage){
		String current = new Integer(currentpage).toString() ;
		if (clip.length() == 0 || clip == null ){
			clip = current ;
			return ;
		}
		if (clip.indexOf(current) == -1){ //make sure we have not add the same
			clip = clip + "," + current ;
		}
		return ;
	}
	public String getClip(){
		return clip ;
	}
*/


		
}
	