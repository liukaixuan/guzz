package mm.smy.mail.channel ;

/**
* 在由字节数组反序列化成类对象的时候可能会抛出。
* 由于在mail服务中，用户的本地资料被转化成字节数组，在加密。
* 所以如果再重新加载加密数据时密码错误，而程序没有检测出来，就会造成FormatException的抛出。
* 我们将一切反序列化的错误[数据错误，密码错误等等]，全都统一以FormatException抛出。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6
*/

public class FormatException extends Exception{
	
	public FormatException(){
		super() ;	
	}
	
	public FormatException(String s){
		super(s) ;	
	}
	
	public FormatException(Throwable t){
		super(t) ;		
	}
	
	public FormatException(String s, Throwable t){
		super(s,t) ;	
	}
		
}