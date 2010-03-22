package mm.smy.bicq.server.user ;

/**
* 在线用户在内存中保存的部分数据。
* 该部分是为了优化 查找在线用户 与 察看个人资料的数据库查找。
* 该类包含用户的IP,port，不过现在是从TempUser继承来的。
* 
* @also see mm.smy.bicq.search.TempUser ;
*/

import mm.smy.bicq.search.TempUser ;
import java.io.Serializable ;

public class OnlineUser extends TempUser implements Serializable{
	
	private long ID = -1 ; //该用户在数据库的user表中所占纪录的ID标号。
	
	public OnlineUser(){	
	
	}
	
	public long getRecordID(){ return ID ; }
	
	public void setRecordID(long m_ID) { ID = m_ID ; }
	
}
