package mm.smy.bicq.login ;

/**
* 登陆时出现错误，如无法获得host，guestgroups信息等等。
* 可能为网络不通等等。
*
* @author XF
*
* @date 2003-10-31
*
*/

public class LoginException extends Exception{
	
	public LoginException(){
		super() ;	
	}
	
	public LoginException(String message){
		super(message) ;
	}
	
}
