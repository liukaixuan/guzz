package mm.smy.security ;

/**
* 身份验证失败，或是验证过期。
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
*/

public class AuthenticatorException extends Exception implements java.io.Serializable{
	public AuthenticatorException(){
		super() ;
	}
	
	public AuthenticatorException(String s){
		super(s) ;
	}	
}
