package mm.smy.bicq.server.message ;

/**
* 插入ServerPermitMessage到数据库
* 
* 
* @author XF
* @date 2003-11-21
* 
*/

import mm.smy.bicq.server.db.ReadWriteStatement ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;

import java.util.Date ;
import java.util.Vector ;

public class PermitDBInsert{
	
	private ReadWriteStatement ro = null ;
	private PreparedStatement pstm = null ;
	
	public PermitDBInsert(){
		
	}
	
	/**
	* 添加离线ServerTextMessage消息(该消息于PermitMessage类似)。
	* 可以多次调用该函数，每次重新添加数据。使用PreparedStatement，在每次调用后不关闭连接。
	* 如果对同一消息调用多次，将会向数据库中插入重复纪录，执行后不改变 设置的ServerPermitMessage的状态。
	* 如果多次调用可以提高效率，不过注意结束该类时调用close()方法释放jdbc资源。
	* 
	* @return boolean pstm.execute()的返回结果。
	*/
	public boolean update() throws SQLException{
		if(ro == null){
			ro = new ReadWriteStatement("permit") ;
		}
		if(pstm == null)
			pstm = ro.getPreparedStatement("insert into permit(fromnumber,tonumber,type,content) values(?,?,?,?)") ;
		else
			pstm.clearParameters() ;
		pstm.setInt(1,from) ;
		pstm.setInt(2,to) ;
		pstm.setInt(3,mintype) ;
		pstm.setBytes(4,content) ;
		
		return pstm.execute() ;
		
	}
	
	/**
	* 设置一个ServerPermitMessage，替代目前该类中的ServerPermitMessage对象。
	* @param message 要插入数据库的ServerPermitMessage，如果该message==null, 直接return ;
	*/
	public void setServerPermitMessage(ServerPermitMessage message){
		if(message == null) return ;
		
		setFrom(message.getFrom()) ;
		setTo(message.getTo())	;
		setMintype(message.getMinType()) ;
		setContent(message.getByteContent()) ;
		return ;
	}
	
	private int from = -1 ;
	private int to   = -1 ;
	private int mintype = -1 ;
	private byte[] content = null ;
	
	public void setFrom(int m_from){ from = m_from ; }
	public void setTo(int m_to){ to = m_to ; }
	public void setMintype(int m_mintype) { mintype = m_mintype ; }
	public void setContent(byte[] b){ content = b ; }
	
	public int getFrom(){ return from ; }
	public int getTo() { return to ; }
	public int getMintype(){ return mintype ; }
	public byte[] getContent(){ return content ; }
	
	public void close(){
		if(ro != null)
			ro.close() ;
		if(pstm != null){
			try{
				pstm.close() ;
			}catch(Exception e){
			}
		}
		return ;
	}

		
	
	
	
}