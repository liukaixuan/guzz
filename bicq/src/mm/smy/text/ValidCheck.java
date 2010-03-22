package mm.smy.text;

/**
* valid check
* @author: XF
* @author: e-mail: myreligion@163.com
* @date  : 2003年7月26日
* @copyright: XF 2003 All rights reserved.
*/


public class ValidCheck{

	public static boolean isDate(String m_date){
		return true ;
	}
	
	/**
	* 检查传入的字串是否是邮件格式 a@b.c
	* 如果传入的字串中包含有不可构成文件名的字符，return false
	*
	* @param mail 要检查的邮件地址字串。
	* @see ValidCheck.canBeFileName(String validString)
	*/
	public static boolean isMail(String mail){
		if(!canBeFileName(mail)) return false ;
		
		if(mail == null || mail.trim().length() < 5 ) return false ;		
		int place1 = mail.indexOf("@") ;
		int place2 = mail.lastIndexOf(".") ;
		if(place1 >= place2) return false ;
		if(place1 == -1 || place2 == -1) return false ;
		if(place1 == 0) return false ;
		if(mail.charAt(place1+1) == '.') return false ;
		return true ;		
	}
	
	/**
	* 判断是否全是由数字组成。如果字串为空,return false
	* @param m_number 要检验的字串。
	*/
	public static boolean isNumber(String m_number){
	  if(m_number == null || m_number.length() == 0)
			return false ;		
      byte[] tempbyte=m_number.getBytes();
      for(int i=0;i<m_number.length();i++) {
          //by=tempbyte[i];
          if((tempbyte[i]<48)||(tempbyte[i]>57)){
              return false;
          }
      }
      return true;
	}
  /**
   * 判断字符串是否为只包括字母和数字,字串为空return false 
   * @param validString 要判断的字符串
   * @return boolen值，true或false
   */
  public static boolean isChar(String validString){
      if(validString == null || validString.length() == 0)
			return false ;	
      byte[] tempbyte=validString.getBytes();
      for(int i=0;i<validString.length();i++) {
          //  by=tempbyte[i];
          if((tempbyte[i]<48)||((tempbyte[i]>57)&(tempbyte[i]<65))||(tempbyte[i]>122)||((tempbyte[i]>90)&(tempbyte[i]<97))) {
              return false;
          }
      }
      return true;
  }

  /**
   * 判断字符串是否只包括字母，如果字串为空返回false
   * @param validString 要判断的字符串
   * @return boolen值，true或false
   */
  public static boolean isLetter(String validString){
    if(validString == null || validString.length() == 0)
		return false ;
    byte[] tempbyte=validString.getBytes();
    for(int i=0;i<validString.length();i++) {
        //by=tempbyte[i];
        if((tempbyte[i]<65)||(tempbyte[i]>122)||((tempbyte[i]>90)&(tempbyte[i]<97))) {
            return false;
        }
    }
    return true;
  }
  
  /**
  * 检查传入的字符是否可以构成一个文件名[Windows系统]，我们假设这些字符为安全的字符，
  * 可以用来形成用户名，密码，邮箱之类的东西。
  * 如果请求的字符串为null或是.length == 0, return false ;
  * 如果字符串中包含以下字符 / \ : * ? " < > |  return false ;
  * 否则返回true
  * @param validString 请求检验的字串。
  */
  public static boolean canBeFileName(String validString){  	
  	if(validString ==null || validString.length() == 0) return false ;
  	
  	byte[] bytes = validString.getBytes() ;
  	for(int i = 0 ; i < bytes.length ; i++ ){
  		byte b = bytes[i] ;
  		if(b == '/' || b == '\\' || b == ':' || b == '*' || b == '?' || b == '\"' || b == '<' || b == '>' || b ==  '|' )	
  			return false ;
  	}
  	return true ;
  }


}