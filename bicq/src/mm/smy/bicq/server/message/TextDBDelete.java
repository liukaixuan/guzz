package mm.smy.bicq.server.message ;

/**
* TextMessage的数据库读取
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

public class TextDBDelete{
	
	private ReadWriteStatement ro = null ;
	private PreparedStatement pstm = null ;
	
	public TextDBDelete(){
		
	}
	
	/**
	* 根据用户number，删除所有 发送给 该用户的TextMessage离线消息。
	* 可以多次调用该函数，每次重新察看数据库。使用PreparedStatement，在每次调用后不关闭连接。
	* 如果多次调用可以提高效率，不过注意结束该类时调用close()方法释放jdbc资源。
	* @param to 要删除的 发送给 的用户的BICQ号
	* @return boolean pstm.execute()的返回结果。
	*/
	public boolean deleteByNumber(int to) throws SQLException{
		if(ro == null){
			ro = new ReadWriteStatement("text") ;	
		}
		if(pstm == null)
			pstm = ro.getPreparedStatement("delete from textmessage where tonumber = ? ") ;
		else
			pstm.clearParameters() ;
		pstm.setInt(1,to) ;
		
		return pstm.execute() ;
		
	}
	
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