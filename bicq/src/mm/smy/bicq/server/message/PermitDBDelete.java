package mm.smy.bicq.server.message ;

/**
* PermitMessage的删除
* 
* 
* @author XF
* @date 2003-11-21
* 
*/

import mm.smy.bicq.server.db.ReadWriteStatement ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;

import java.util.Vector ;

public class PermitDBDelete{
	
	private ReadWriteStatement ro = null ;
	private PreparedStatement pstm = null ;
	
	public PermitDBDelete(){
		
	}
	
	/**
	* 根据用户number，删除所有 发送给 该用户的PermitMessage离线消息。
	* 可以多次调用该函数，每次重新察看数据库。使用PreparedStatement，在每次调用后不关闭连接。
	* 如果多次调用可以提高效率，不过注意结束该类时调用close()方法释放jdbc资源。
	* @param to 要删除的 发送给 的用户的BICQ号
	* @return boolean pstm.execute()的返回结果。
	*/
	public boolean deleteByNumber(int to) throws SQLException{
		if(ro == null){
			ro = new ReadWriteStatement("permit") ;	
		}
		if(pstm == null)
			pstm = ro.getPreparedStatement("delete from permit where tonumber = ? ") ;
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