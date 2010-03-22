package mm.smy.bicq.server.db ;

/**
* 只读语句的生成类. 用于创建Statement
* 因为我们可能会把不同的数据放在不同的数据库中，这儿隐藏了实际数据库的地址。
* 用户只要指定项目(name)，该类自动根据预先规定好的映射关系建立起一个对应该表的可读写的Statement对象。
* @author XF myreligion@163.com
* @date 2003-11-19
* @copyright Copyright 2003 XF All Rights Reserved
* @also see ReadonlyStatement 
*/

import java.sql.Connection ;
import java.sql.Statement ;
import java.sql.PreparedStatement ;
import java.sql.SQLException ;
import java.sql.ResultSet ;


public class ReadWriteStatement{
	private String name = "" ; //我们将根据这个名字确定使用的数据库。
	private int scroll = ResultSet.TYPE_SCROLL_INSENSITIVE ;
	
	private PreparedStatement psmt = null ;	
	private Statement stmt = null ;
	private DBConnection dbconn = null ;
	private Connection conn = null ;
		
	public ReadWriteStatement(String name){
		this.name = name ;	
	}
	
	/**
	* @param name 要创建项目的名字,该名字已经先前定义好了. 映射关系请参看 reflect.txt
	* @param type 游标类型。在ResultSet.TYPE_SCROLL_INSENSITIVE和ResultSet.TYPE_SCROLL_SENSITIVE间选择。默认为insensitive
	* 
	*/
	public ReadWriteStatement(String name, int type){
		this.name = name ;
		scroll = type ;
	}
	
	public void setScrollType(int type){
		scroll = type ;	
	}
	
	public int getScrollType(){
		return scroll ;	
	}
	
	/**
	* 根据名字name创建一个 只读 的Statement
	* 如果一个类连续两次调用该方法，将会得到两个不同的Statement实例，同时前一个statement将会被自动关闭。
	* 在用完后必须全部释放. 看close().
	*/
	public Statement getStatement() throws SQLException{
		dbconn = DBConnection.getInstance() ;
		if(conn == null){
			conn = dbconn.getConnection() ;
		}
		if(stmt != null){
			stmt.close() ;	
		}
		stmt = conn.createStatement(scroll, ResultSet.CONCUR_UPDATABLE) ;
		return stmt ;
	}

	//see above
	public PreparedStatement getPreparedStatement(String m_sql) throws SQLException{
		if(dbconn == null)
			dbconn = DBConnection.getInstance() ;
		if(conn == null){
			conn = dbconn.getConnection() ;	
		}
		if(psmt != null){
			psmt.close() ;	
		}
		psmt = conn.prepareStatement(m_sql) ;
		
		return psmt ;
		
	}
	
	public void close(){
		if (stmt != null ){
			try{
				stmt.close();
			}catch(SQLException e){
				BugWriter.log("mm.smy.vote.db.ReadWriteStatement:close():e", e , null) ;
			}
		}
		
		if (psmt != null ){
			try{
				psmt.close();
			}catch(SQLException e){
				BugWriter.log("mm.smy.vote.db.ReadonlyStatement:close():e", e , null) ;
			}
		}
		
		if (conn != null ){
			dbconn.freeConnection(conn) ;
		}
	}
	
}