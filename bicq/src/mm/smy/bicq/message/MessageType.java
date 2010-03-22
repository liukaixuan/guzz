package mm.smy.bicq.message ;

/**
* the sended message types.
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/

public interface MessageType extends java.io.Serializable{
	public static final int TEXT_MESSAGE = 100 ;
	public static final int STATE_CHANGED_MESSAGE = 200 ;
	public static final int SEARCH_GUEST_MESSAGE = 300 ;
	public static final int SEARCH_GUEST_RESULT_MESSAGE = 400 ;
	public static final int USER_INFOR_MESSAGE = 500 ;
	public static final int PERMIT_MESSAGE = 600 ;
	public static final int USER_PSW_MESSAGE = 700 ;
	public static final int LOAD_GUEST_RESULT_MESSAGE = 800 ;
	public static final int ICMP_MESSAGE = 2000 ; //模拟ICMP数据报，包含一个小数据。
	public static final int REGISTER_MESSAGE = 3000 ; //注册消息
	
	public static final int OTHER_MESSAGE = 20000 ; //20000-29999属于其它消息。
	
	public static final int UNKNOWN_TYPE = 0 ;
	
}

