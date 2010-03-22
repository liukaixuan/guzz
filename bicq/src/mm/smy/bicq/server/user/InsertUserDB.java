package mm.smy.bicq.server.user ;

/**
* 插入用户到数据库。
* 
* 
* 
* 
* 
*/

import mm.smy.bicq.server.db.* ;
import java.util.Date ;

import mm.smy.text.StringFormat ;

import java.sql.Statement ;
import java.sql.SQLException ;

import java.net.InetAddress ;

public class InsertUserDB{
	private ServerGuest guest = null ;
	private StringBuffer sb = null ; //sql语句
	
	private ReadWriteStatement rw = null ;
	private Statement stmt = null ;
	
	public InsertUserDB(){}
	
	public InsertUserDB(ServerGuest m_guest){
		guest = m_guest ;	
	}
	
	public void setServerGuest(ServerGuest m_guest){
		guest = m_guest ;	
		return ;
	}
	
	/**
	* 将数据真实加入到数据库。
	*/
	public int update() throws SQLException{
		init() ;
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$") ;
		System.out.println("the sql statement we creat is:" + sb.toString() ) ;
		if(sb == null) return -1 ;
		
		if(rw == null){
			rw = new ReadWriteStatement("user") ;		
		}
		if(stmt == null){
			stmt = rw.getStatement() ;
		}
		
		return stmt.executeUpdate(sb.toString()) ;		
	}
	
	public void close(){
		if(rw != null){
			rw.close() ;
		}
		
		if(stmt != null){
			try{
				stmt.close() ;
				stmt = null ;
			}catch(Exception e){
			}
		}
	}
	
	//把ServerGuest的Fields做成sql语句
	private void init() throws SQLException{
		if(guest.getNumber() == ServerGuest.UNDEFINE_INT) throw new SQLException("InsertUserDB.class::number非法,请输入正确的number") ;
		
		sb = new StringBuffer() ;
		sb.append("insert into user (number,password,nickname,gender,portrait,address,zip, country, province, myexplain, birthday,telephone, homepage, realname, mail, auth,lastloginIP, lastlogintime,registerIP,registerTime, totalonlinetime) values(") ;
	//	sb.append("insert into user values(") ;
		
//	sb.append("insert into user2(number,password,nickname) values(") ;
		sb.append(guest.getNumber() ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getPassword(), null)) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getNickname(),"\"\"") ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getGender(), -1) ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getPortrait(), 0) ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getAddress(), "\"\"") ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getZip(), 0) ) ;	
		sb.append(", ") ;
		sb.append(doNull(guest.getCountry(),"\"\"") ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getProvince(),"\"\"") ) ;
		sb.append(", ") ;		
		sb.append(doNull(guest.getExplain(),"\"\"") ) ;	
		sb.append(", ") ;
		sb.append(doNull(guest.getBirthday(), -1) ) ;	
		sb.append(", ") ;
		sb.append(doNull(guest.getTelephone(), "\"\"")) ;
		sb.append(", ") ;		
		sb.append(doNull(guest.getHomepage(),"\"\"") ) ;
		sb.append(", ") ;			
		sb.append(doNull(guest.getRealname(),"\"\"") ) ;
		sb.append(", ") ;	
		sb.append(doNull(guest.getMail(), "\"\"") ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getAuth(), mm.smy.bicq.user.Host.ALLOW_ANYONE) ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getLastLoginIP(), "\"\"") ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getLastLoginTime(), new Date().getTime() ) ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getRegisterIP(), "\"\"") ) ;
		sb.append(", ") ;		
		sb.append(doNull(guest.getRegisterTime(), new Date().getTime() ) ) ;
		sb.append(", ") ;
		sb.append(doNull(guest.getTotalOnlineTime(), 0) ) ;

		sb.append(")") ;
		
		
		return ;
	}
	
	private int doNull(int n, int def){
		if(n ==  ServerGuest.UNDEFINE_INT)	
			return def ;
		return n ;
	}
	
	private String doNull(String s, String def){
		if( s== null) return def ;
		if( s.equals(ServerGuest.UNDEFINE_STRING)) return def ;
		return "\"" +  StringFormat.gb2iso(s) + "\"" ;
	}
	
	private long doNull(long l, long def){
		if(l == ServerGuest.UNDEFINE_LONG) return def ;	
		return l ;
	}
	
	private long doNull(Date d, long def){
		if(d == null) return def ;
		if(d.equals(ServerGuest.UNDEFINE_DATE)) return def ;
		
		return d.getTime() ;
	}
	
	private String doNull(InetAddress ip, String def){
		if(ip == null) return def ;
		
		return "\"" +  mm.smy.text.StringFormat.formatIP(ip) + "\"" ;
	}
	
	
	public static void main(String[] args) throws Exception{
		ServerGuest g = new ServerGuest() ;
		g.setNumber(2009) ;
		g.setNickname("xiao ling 22222") ;
		g.setPassword("79cf577i943448ah07567h75bj025j9d663a1e28") ;
		g.setMail("mail@mail.com") ;
		InsertUserDB insert = new InsertUserDB(g) ;
		insert.update() ;
		return ;
	}
	
	
	
	
	
	
}
