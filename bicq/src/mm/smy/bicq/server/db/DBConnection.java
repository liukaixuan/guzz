package mm.smy.bicq.server.db;

/**
* Database conncetion
* @author: 
* @author: 
* @date  : 2003年7月17日
* @copyright:
*/

import java.sql.* ;
import java.util.Vector ;
import java.util.Enumeration ;
import java.util.Properties ;
import java.io.FileInputStream ;

import com.mysql.jdbc.Driver ;

import mm.smy.bicq.server.db.BugWriter ;

public class DBConnection{
	 private int checkedOut ; //已经使用的连接数。
	 private Vector freeConnections = new Vector();//可以使用的连接
	 
	 private static DBConnection instance = null ; //
	 private static int clients = 0 ; //目前连接到数据库的连接数
	
	 private String URL = null ;
	 private String user = null ;
	 private String password = null ;
	 private int maxConns = 0 ;
	 private int initConns = 0 ;
	 private int timeOut = 5 ;
	 
	public static synchronized DBConnection getInstance(){
		if(instance == null){
			try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			}catch(Exception e){
				System.out.println("unable to load driver by XF ==>" + e.getMessage() ) ;
				return null;	
			}
			
			String connurl = null ;
			int maxconn = 10 ;
			int initconn = 5 ;
			int timeout = 5 ; //second
			
			Properties p = new Properties() ;
			try{
				FileInputStream fis = new FileInputStream("conf/server-config.ini") ;
				p.load(fis) ;				
				try{
					String dbserverIP = p.getProperty("dbserverIP") ;
					String dbserverPort = p.getProperty("dbserverPort").trim() ;
					String dbusername = p.getProperty("dbusername") ;
					String dbpassword = p.getProperty("dbpassword") ;
					String dbdatabase = p.getProperty("dbdatabase") ;
					
					maxconn = new Integer(p.getProperty("maxConns").trim()).intValue() ;
					initconn = new Integer(p.getProperty("initConns").trim()).intValue() ;
					timeout = new Integer(p.getProperty("timeOut").trim()).intValue() ;
					
					connurl = "jdbc:mysql://" + dbserverIP + ":" + dbserverPort + "/" + dbdatabase
							+ "?user=" + dbusername + "&password=" + dbpassword ;
				}catch(Exception e){
					System.out.println("Cannot load init file.") ;
					e.printStackTrace() ;
				}
				fis.close() ;
				}catch(Exception e){
					System.out.println("Error:" + e.getMessage() ) ;
					return null ;
				}

			
			//instance = new DBConnection("jdbc:mysql://211.68.47.45:3306/bicq?user=root&password=root", null, null, 10, 5, 5) ;
			instance = new DBConnection(connurl, null, null, maxconn, initconn, timeout) ;
			return instance ;
		}
		return instance ;
	}

	private DBConnection( String URL, String user, String password, int maxConns, int initConns, int timeOut ){
		this.URL = URL ;
		this.user = user ;
		this.password = password ;
		this.maxConns = maxConns ;
		this.initConns = initConns ;
		this.timeOut  = timeOut > 0 ? timeOut:5 ;
		
		initPool(initConns) ;

	}

	private void initPool(int initConns){
		for(int i = 0; i<initConns;i++ ){
			try{
				Connection pc = newConnection() ;
				freeConnections.addElement(pc) ;
			}catch( SQLException e ){
				BugWriter.log("mm.smy.bicq.server.db.DBConnection:initPool(int)", e , "SQLException" ) ;
			}
		}
	}

	public Connection getConnection() throws SQLException{
			return getConnection(timeOut*1000) ;
	}

	private synchronized Connection getConnection(long timeout){
		//Get a pooled connection from the cache/a new one.
		//Dealing timeout, waiting, notifyAll....

		long startTime = System.currentTimeMillis();
		long remaining = timeout ;
		Connection conn = null ;
		
		try{
			while( (conn = getPooledConnection()) == null ){
				try{
					wait(remaining) ;
				}catch(InterruptedException e){ //timeout, record for further init conns.

				}

				remaining = timeout - (System.currentTimeMillis() - startTime) ;
				if(remaining <= 0 ){//time has expired
					BugWriter.log("mm.smy.bicq.server.db.DBConnection:getConnection(long)", new Exception("本人自己抛出的异常，因为数据库的连接池太小") , "time-out, no connection available。 Not enough connection!" ) ;
				}
			}
		}catch(SQLException e){
			BugWriter.log("mm.smy.bicq.server.db..DBConnection:: private getConnection()", e , "SQLException") ;	
		}


		//Check if the Connection is still OK
		if(!isConnectionOK(conn)){
			//Bad Connection, try again with the remaining timeout.
			BugWriter.log("mm.smy.bicq.db.DBConnection:getConnection(long)" , new Exception("获得的数据库连接是错误的!!!") , "BadConnection The connectin got is bad!!!!!!" ) ;
			return getConnection(remaining) ;
		}

		checkedOut++ ;
		return conn ;
	}

	private boolean isConnectionOK(Connection conn){
		Statement testStmt = null ;
		try{
			if(!conn.isClosed()){
				testStmt = conn.createStatement() ;
				testStmt.close() ;
			}
			else{
				return false ;
			}
		}catch(SQLException e){
			if(testStmt != null){
				try{
					testStmt.close();
				}catch(SQLException e2)
					{}
			}
			//bad one. can record....
			return false ;
		}
		return true ;
	}

	private Connection getPooledConnection() throws SQLException{
		Connection conn = null ;
		if (freeConnections.size() > 0 ){
			//pick up the first Connection in the Vector.
			conn = (Connection) freeConnections.firstElement() ;
			freeConnections.removeElementAt(0) ;
		}
		else{
			conn = newConnection() ;
		}

		return conn ;
	}

	private Connection newConnection() throws SQLException{
		Connection conn = null ;
		
		if ( user == null ) {
			conn = DriverManager.getConnection(URL);
		}
		else{
			conn = DriverManager.getConnection(URL,user,password) ;
		}
		
		return conn;
	}

	public synchronized void freeConnection(Connection conn){ // return the connection back.
		//put the connection at the end
		freeConnections.addElement(conn) ;
		checkedOut-- ;
		notifyAll();
	}

	public synchronized void release(){
		Enumeration allConnections = freeConnections.elements() ;
		while(allConnections.hasMoreElements()){
			Connection con = (Connection) allConnections.nextElement() ;
			try{
				con.close() ;
			}catch(SQLException e){
				BugWriter.log("mm.smy.bicq.server.db..DBConnection:release()", e , "SQLException") ;
			}
		}
		freeConnections.removeAllElements() ;
	}

	
	public static void main(String[] args) throws SQLException{
		DBConnection conn = DBConnection.getInstance() ;
		System.out.println("success to finish by XF") ;	
		System.out.println("connection:" + conn.getConnection()) ;
	}

}