package mm.smy.bicq.message ;

import java.io.* ;
import java.util.* ;
import java.net.InetAddress ;
import mm.smy.bicq.user.*;
import mm.smy.bicq.search.TempUser ;
/**
*
* 该类的目的是传回SearchGuestResultMessage。该Message的格式为： nickname,number,portrait,gender,from,IP,state,身份验证
* 用mm.smy.bicq.search.TempUser对象表示。
*
*
*
*
*@date 2003-10-1
*@author XF
*@author e-mail:myreligion@163.com
*
*/

public class SearchGuestResultMessage implements Serializable, Message{
	
	private User from = null ;
	private User to   = null ;
	 
	private int startpos = 0 ; //开始位置，以0代表第一个
	private int guestnumbers = 0 ;	//本消息包含的 User 数目。
	
	private long flag = -1 ; //SearchGuestMessage发送过来的标志性位，我们不做任何修改，直接返回。供用户扩展功能。
	
	private Vector users = new Vector(15) ;
	
	public SearchGuestResultMessage(){}
	
//own methods
	public void addTempUser(TempUser g){
		if( g == null) return ;
		
		users.add(g) ;
		guestnumbers++ ;
		return ;		
	}
	
	public Vector getTempUsers(){
		return users ;		
	}
	
	public int getTempUserNumbers(){
		return guestnumbers ;		
	}
	
	public void setStartPos(int m_pos){
		startpos = m_pos ;
	}
	
	public int getStartPos(){
		return startpos ;
	}
	
	public void setFlag(long m_flag){
		flag = m_flag ;	
	}
	
	public long getFlag(){
		return flag ;	
	}
	
//methods implemetions	
	public byte[] getByteContent() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		DataOutputStream dos = new DataOutputStream(bout) ;
		byte[] back = null ;
		
		try{
			dos.writeLong(flag) ; //flage
			dos.writeInt(startpos) ; //start pos
			dos.writeInt(guestnumbers) ; //guest numbers
			Enumeration e = users.elements() ;
			while(e.hasMoreElements()){
				TempUser g = (TempUser) e.nextElement() ;
				dos.writeInt(g.getNumber()) ; //number
				dos.writeInt(g.getPortrait()) ; //portrait
				dos.writeInt(g.getGender()) ;     //gender
				dos.writeUTF(g.getFrom() == null?"":g.getFrom()) ; //from
				dos.writeUTF(g.getIP() == null?"":g.getIP().toString()) ; //IP
				dos.writeInt(g.getState()) ; //state
				dos.writeUTF(g.getNickname()==null?"":g.getNickname()) ;
				dos.writeInt(g.getAuth()) ;
			}
			back = bout.toByteArray() ;
		}catch(Exception e){
			System.out.println("SearchGuestResultMessage has thrown an exception while convert to byte[]==>" + e.getMessage()) ;
		}finally{
			if(dos != null){
				try{
					dos.close() ;
				}catch(Exception e){}
			}	
			if(bout != null){
				try{
					bout.close() ;
				}catch(Exception e){}
			}
			
		}
		return back ;
	}

	public void setByteContent(byte[] b) {
		if (b == null) return ;
		
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		DataInputStream dis = new DataInputStream(bin) ;
		try{
			flag         = dis.readLong() ;
			startpos     = dis.readInt() ; //start pos
			guestnumbers = dis.readInt() ; //guest numbers
			while(dis.available() > 0 ){
				TempUser g = new TempUser() ;
				g.setNumber(dis.readInt()) ; //number
				g.setPortrait(dis.readInt()) ; //portrait
				g.setGender(dis.readInt()) ;     //gender
				String tempfrom = dis.readUTF() ;
				g.setFrom(tempfrom == null?"":tempfrom) ; //from
				String temp_IP = mm.smy.text.StringFormat.formatIP( dis.readUTF() ) ;
				if(temp_IP == null || temp_IP.length() == 0){
					g.setIP(null) ;
				}else{
					InetAddress temp_IP2 = null ;
					try{
						temp_IP2 = InetAddress.getByName(temp_IP) ;
					}catch(Exception e){
						System.out.println("SGRM Failed to solve IP:" + temp_IP) ;
					}
					g.setIP(temp_IP2) ;	
				}
				g.setState(dis.readInt()) ; //state
				g.setNickname(dis.readUTF()) ;
				g.setAuth(dis.readInt()) ;
				users.add(g) ;
			}
		}catch(Exception e){
			System.out.println("SearchGuestResultMessage has thrown an exception while convert to byte[]==>" + e.getMessage()) ;
		}finally{
			if(dis != null){
				try{
					dis.close() ;
				}catch(Exception e){}
			}	
			if(bin != null){
				try{
					bin.close() ;
				}catch(Exception e){}
			}
			
		}		
		return ;
	}

	public int getType() {
		return MessageType.SEARCH_GUEST_RESULT_MESSAGE ;
	}

	public int getMinType() {
		return MessageType.SEARCH_GUEST_RESULT_MESSAGE ;
	}

	public User getFrom() {
		return from ;
	}

	public User getTo() {
		return to ;
	}
	public void setFrom(User u){
		from = u ;
	}
	public void setTo(User u){
		to = u ;
	}

	
	
	
	
	
	
}
