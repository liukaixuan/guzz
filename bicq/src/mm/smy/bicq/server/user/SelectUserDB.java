package mm.smy.bicq.server.user ;

/**
* 检索单个用户的资料。
* 按照GFA搜索数据库
* 
* 
* 
* 
*/

import mm.smy.bicq.server.db.* ;

import mm.smy.text.StringFormat ;

import java.sql.SQLException ;
import java.sql.PreparedStatement ;
import java.sql.Statement ;
import java.sql.ResultSet ;


import java.util.Date ;
import java.util.Vector ;

public class SelectUserDB{
	private static String sql = new String("select * from user where ? = ?") ;
	private PreparedStatement searchsingle = null ;
	private PreparedStatement searchbynickname = null ;
	
	private ReadonlyStatement ro = null ;
	private Statement stmt = null ;
	private ResultSet rs = null ;
	
	public SelectUserDB(){
		
	}
	
	public ServerGuest selectByNumber(int number) throws SQLException{
		init() ;
		if(searchsingle == null)
			searchsingle = ro.getPreparedStatement("select * from user where number = ?") ;
		else
			searchsingle.clearParameters() ;
			
		searchsingle.setInt(1,number) ;
		rs = searchsingle.executeQuery() ;
		
		System.out.println("++++++++++++++++++++rs is:" + rs) ;
		//System.out.println("rs.next():" + rs.next() ) ;
		
		if(rs.next()){
			System.out.println("record exsits") ;
			
			ServerGuest guest = new ServerGuest() ;
			guest.setNumber(rs.getInt("number")) ;
			guest.setNickname(StringFormat.iso2gb(rs.getString("nickname"))) ;
			guest.setAddress(StringFormat.iso2gb(rs.getString("address"))) ;
			guest.setAuth(rs.getInt("auth")) ;
		//	guest.setBirthday(rs.getDate("birthday")) ;
			if(rs.getLong("birthday") <= 0){
				guest.setBirthday(null) ;	
			}else{
				guest.setBirthday(new Date(rs.getLong("birthday"))) ;	
			}
			guest.setCounty(StringFormat.iso2gb(rs.getString("country"))) ;
			guest.setExplain(StringFormat.iso2gb(rs.getString("myexplain"))) ;
			guest.setGender(rs.getInt("gender")) ;
			guest.setHomepage(StringFormat.iso2gb(rs.getString("homepage"))) ;
			guest.setLastLoginIP(rs.getString("lastloginIP")) ;
			
			long temp_long = rs.getLong("lastlogintime") ;			
			guest.setLastLoginTime(temp_long>0?new Date(temp_long):null) ;
			
			//leavword not stored in the database
			guest.setLoginTime(new Date()) ;
			guest.setMail(StringFormat.iso2gb(rs.getString("mail"))) ;
			guest.setPortrait(rs.getInt("portrait")) ;
			guest.setProvince(StringFormat.iso2gb(rs.getString("province"))) ;
			guest.setRealname(StringFormat.iso2gb(rs.getString("realname"))) ;
			guest.setRecordID(rs.getLong("ID")) ;
			guest.setRegisterIP(rs.getString("registerIP")) ;
			
			temp_long = rs.getLong("registertime") ;
			guest.setRegisterTime(temp_long>0?new Date(temp_long):null) ;
			
			guest.setTelephone(StringFormat.iso2gb(rs.getString("telephone"))) ;
			guest.setTotalOnlineTime(rs.getLong("totalonlinetime")) ;
			guest.setZip(rs.getInt("zip")) ;
			
			guest.setPassword(rs.getString("password")) ;
			
			rs.close() ;
			return guest ;
		}
		
		rs.close() ;
		return null ;
	}

	public ServerGuest selectByRecordID(long m_recordID) throws SQLException{
		init() ;
		if(searchsingle == null)
			searchsingle = ro.getPreparedStatement(sql) ;
		else
			searchsingle.clearParameters() ;
			
		searchsingle.setString(1,"ID") ;
		searchsingle.setLong(2,m_recordID) ;
		rs = searchsingle.executeQuery() ;
		
		if(rs.next()){
			ServerGuest guest = new ServerGuest() ;
			guest.setNumber(rs.getInt("number")) ;
			guest.setNickname(StringFormat.iso2gb(rs.getString("nickname"))) ;
			guest.setAddress(StringFormat.iso2gb(rs.getString("address"))) ;
			guest.setAuth(rs.getInt("auth")) ;
			guest.setBirthday(rs.getDate("birthday")) ;
		//	if(rs.getLong("birthday") == -1){
		//		guest.setBirthday(null) ;	
		//	}else{
		//		guest.setBirthday(new Date(rs.getLong("birthday"))) ;	
		//	}
			guest.setCounty(StringFormat.iso2gb(rs.getString("country"))) ;
			guest.setExplain(StringFormat.iso2gb(rs.getString("myexplain"))) ;
			guest.setGender(rs.getInt("gender")) ;
			guest.setHomepage(StringFormat.iso2gb(rs.getString("homepage"))) ;
			guest.setLastLoginIP(rs.getString("lastloginIP")) ;
			
			long temp_long = rs.getLong("lastlogintime") ;			
			guest.setLastLoginTime(temp_long>0?new Date(temp_long):null) ;

			//leavword not stored in the database
			guest.setLoginTime(new Date()) ;
			guest.setMail(StringFormat.iso2gb(rs.getString("mail"))) ;
			guest.setPortrait(rs.getInt("portrait")) ;
			guest.setProvince(StringFormat.iso2gb(rs.getString("province"))) ;
			guest.setRealname(StringFormat.iso2gb(rs.getString("realname"))) ;
			guest.setRecordID(rs.getLong("ID")) ;
			guest.setRegisterIP(rs.getString("registerIP")) ;
			
			temp_long = rs.getLong("registertime") ;
			guest.setRegisterTime(temp_long>0?new Date(temp_long):null) ;
			
			guest.setTelephone(StringFormat.iso2gb(rs.getString("telephone"))) ;
			guest.setTotalOnlineTime(rs.getLong("totalonlinetime")) ;
			guest.setZip(rs.getInt(rs.getInt("zip"))) ;
			
			guest.setPassword(rs.getString("password")) ;
			
			rs.close() ;
			return guest ;
		}
		
		rs.close() ;
		return null ;
	}
	
	/**
	* search by nickname.精确匹配
	* 可以对这一函数进行多次调用，每次的结果将不会相互影响。同时在第一次搜索后，函数内的PreparedStatement不会关闭。
	* 可以节约一定的内存开销。
	*
	* @param nickname 要搜索的昵称
	* @param max_num 需要搜索出的最多用户
	* @param startpos 开始读取纪录的位置。如果此位置大于满足条件纪录的总数，返回null
	*
	* @return Vector对象，里面保存着OnlineUser对象。如果传入的nickname==null || max_number <= 0, return null .
	*/
	public Vector selectByNickname(String nickname, int max_num, int startpos) throws SQLException{
		if(nickname == null) return null ;
		if(max_num <= 0 ) return null ;
		
		init() ;
		
		if(searchbynickname == null){
			searchbynickname = ro.getPreparedStatement("select auth, province, gender, nickname, number ,portrait, ID from user where nickname = ? ") ;
		}
		else{
			searchbynickname.clearParameters() ;
		}
		//因为我们存到数据库中的字符是 iso，现在是gb2312，所以要修改一下。
		searchbynickname.setString(1, StringFormat.gb2iso(nickname) ) ;
		//searchbynickname.setString(1, nickname) ;
		rs = searchbynickname.executeQuery() ;
		
		Vector v = new Vector() ;
		int i = 0 ;
		
		//翻页
		if(rs.getMetaData().getColumnCount() > startpos ){
			if(startpos > 0){
				rs.absolute(startpos) ;
			}
		}else{
			rs.close() ;
			return null ;	
		}
		
		while(rs.next() && i < max_num){
			
			OnlineUser user = new OnlineUser() ;
			
			user.setAuth(rs.getInt("auth")) ;
			user.setFrom(StringFormat.iso2gb(rs.getString("province"))) ;
			user.setGender(rs.getInt("gender")) ;
			user.setNickname(StringFormat.iso2gb(rs.getString("nickname"))) ;
			user.setNumber(rs.getInt("number")) ;
			user.setPortrait(rs.getInt("portrait")) ;
			user.setRecordID(rs.getLong("ID")) ;
						
			v.add(user) ;
			i++ ;
		}
		rs.close() ;
		return v ;
	}
	
		
	private void init(){
		if(ro == null)
			ro = new ReadonlyStatement("user") ;
	}
	
	/**
	* 按照性别，省份（来自），年龄 查找。
	* 使用Statement，所以不会保存缓存。每次的调用该函数都会引起Statement对象的建立与关闭。
	* 
	* @param gender   性别：-1 任意；0 女；1 男
	* @param province 来自的省份, null或是""表示任意。
	* @param agefrom  性别下限 -1 表示没有下限
	* @param ageto    性别上限 -1 表示没有上限
	* @param maxnum   返回纪录的最大数目
	* @param startpos 开始读取纪录的位置。如果此位置大于满足条件纪录的总数，返回null
	* @return Vector 返回Vector对象，该对象的元素为OnlineUser对象。
	*/
	public Vector selectByGFA(int gender, String province, int agefrom, int ageto, int maxnum, int startpos) throws SQLException{
		init() ;
		boolean meetwhere = false ; //是否已经有过滤条件了。
		
		StringBuffer sb = new StringBuffer("select auth, province, gender, nickname, number ,portrait, ID from user") ;
		if(province == null || province.length() == 0){
		}else{
			sb.append(" where province = ") ;
			sb.append(StringFormat.gb2iso(province)) ;
			meetwhere = true ;
		}
		
		if( agefrom > 0 ){
			if(meetwhere){
				sb.append(" and birthday < ") ;
				sb.append(new Date(new Date().getYear() - agefrom , 0, 1).getTime()) ;
			}else{
				sb.append(" where birthday < ") ;
				sb.append(new Date(new Date().getYear() - agefrom , 0, 1).getTime()) ;	
				meetwhere = true ;
			}
		}
		
		if(ageto > 0 ){
			if(meetwhere){
				sb.append(" and birthday > ") ;
				sb.append(new Date(new Date().getYear() - ageto , 0, 1).getTime()) ;	
			}else{
				sb.append(" where birthday > ") ;
				sb.append(new Date(new Date().getYear() - ageto , 0, 1).getTime()) ;	
				meetwhere = true ;
			}
		}
		
		if(gender == 0 ){
			if(meetwhere){
				sb.append(" and gender = 0 ") ;	
			}else{
				sb.append(" where gender = 0 ") ;
				meetwhere = true ;	
			}
		}else if(gender == 1){
			if(meetwhere){
				sb.append(" and gender = 1 ") ;	
			}else{
				sb.append(" where gender = 1 ") ;
				meetwhere = true ;	
			}
		}
		
		System.out.println("select by gfa , sb:" + sb) ;
		
		stmt = ro.getStatement() ;
		
		rs = stmt.executeQuery(sb.toString()) ;

		//翻页
		if(rs.getMetaData().getColumnCount() > startpos){
			if(startpos > 0){
				rs.absolute(startpos) ;
			}	
		}else{
			rs.close() ;
			return null ;	
		}
		
		int i = 0 ;
		Vector v = new Vector() ;
		while(rs.next() && i < maxnum){
			OnlineUser user = new OnlineUser() ;
			
			user.setAuth(rs.getInt("auth")) ;
			user.setFrom(StringFormat.iso2gb(rs.getString("province"))) ;
			user.setGender(rs.getInt("gender")) ;
			user.setNickname(StringFormat.iso2gb(rs.getString("nickname"))) ;
			user.setNumber(rs.getInt("number")) ;
			user.setPortrait(rs.getInt("portrait")) ;
			user.setRecordID(rs.getLong("ID")) ;
						
			v.add(user) ;
			i++ ;			
		}
		
		rs.close() ;
		
		return v ;
	}
	
	public void close(){
		if(ro != null){
			ro.close() ;	
		}
		if(searchsingle != null){
			try{
				searchsingle.close() ;
			}catch(Exception e){}
		}
		if(searchbynickname != null){
			try{
				searchbynickname.close() ;	
			}catch(Exception e){}
		}
		if(stmt != null){
			try{
				stmt.close() ;	
			}catch(Exception e){}
		}
		

		
	}
	

	
	public static void main(String args[]) throws Exception{
		
	//	SelectUserDB s = new SelectUserDB() ;	
		//ServerGuest guest = s.selectByNumber(1001) ;
		
		//System.out.println("nickname:" + guest.getNickname()) ;
		
		System.out.println("year:" + new Date().getYear() ) ;
		
	}
	
	
	
	
}
