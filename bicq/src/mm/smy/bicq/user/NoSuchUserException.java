package mm.smy.bicq.user ;

/**
* save the user' encrypt infor, for encrypting messages..
* @author XF
* @e-mail myreligion@163.com
* @date   2003-8-13   
* @copyright Copyright 2003 XF All Rights Reserved.
*/
//import java.io.Serializable ;


public class NoSuchUserException extends Exception{
	public NoSuchUserException(){
		super() ;
	}
	public NoSuchUserException(String why){
		super(why);
	}
}