package mm.smy.bicq.server.message ;

/**
* TextMessage的数据库读取
* 
* 
* @author XF
* @date 2003-11-21
* 
*/

import mm.smy.bicq.server.db.ReadonlyStatement ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;

import java.util.Vector ;

public class TextDBQuery{
	
	private ReadonlyStatement ro = null ;
	private PreparedStatement pstm = null ;
	private ResultSet rs = null ;
	
	public TextDBQuery(){
		
	}
	
	/**
	* 根据用户number，查找所有 发送给 该用户的TextMessage离线消息。
	* 可以多次调用该函数，每次重新察看数据库。使用PreparedStatement，在每次调用后不关闭连接。
	* 如果多次调用可以提高效率，不过注意结束该类时调用close()方法释放jdbc资源。
	* @param to 要搜索的 发送给 的用户的BICQ号
	* @return Vector对象，里面包含ServerTextMessage消息集合。
	*/
	public Vector selectByNumber(int to) throws SQLException{
		if(ro == null){
			ro = new ReadonlyStatement("text") ;	
		}
		if(pstm == null)
			pstm = ro.getPreparedStatement("select * from textmessage where tonumber = ?") ;
		else
			pstm.clearParameters() ;
		pstm.setInt(1,to) ;
		
		rs = pstm.executeQuery() ;
		
		Vector v = new Vector() ;
		ServerTextMessage tm = null ;
		while(rs.next()){
			tm = new ServerTextMessage() ;
			tm.setByteContent(rs.getBytes("content")) ;
			tm.setFrom(rs.getInt("fromnumber")) ;
			tm.setTo(rs.getInt("tonumber")) ;
			v.add(tm) ;
		}
		
		rs.close() ;
		
		return v ;
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