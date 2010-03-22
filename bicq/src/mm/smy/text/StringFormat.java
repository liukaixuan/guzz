package mm.smy.text ;

/**
* 字串处理。
* 
* @author XF
* @date 2003-11-26
* 
* 
* 
*/

import java.io.UnsupportedEncodingException ;

public class StringFormat{
	
	public static String iso2gb(String str) { //转iso->gb，用于将乱码转成中文。database,post,get
      if (str != null) {
          byte[] tmpbyte=null;
          try {
              tmpbyte=str.getBytes("ISO8859_1");
          }
          catch (UnsupportedEncodingException e) {
              System.out.println("mm.smy.util.StringFommat:iso2gb(String)-->" + e.getMessage());
          }
          try {
              str=new String(tmpbyte,"GBK");
          }
          catch(UnsupportedEncodingException e) {
              System.out.println("mm.smy.util.StringFommat:iso2gb(String)-->" + e.getMessage());
          }
      }
      return str;
	}

	public static String gb2iso(String string){ //转gb->iso，用于存储与数据库中
      if (string != null) {
          byte[] tmpbyte=null;
          try {
              tmpbyte=string.getBytes("GBK");
          }
          catch(UnsupportedEncodingException e1) {
              System.out.println("mm.smy.util.StringFommat:gb2iso(String):e1-->"+e1.getMessage());
          }
          try {
              string=new String(tmpbyte,"ISO8859_1");
          }
          catch(UnsupportedEncodingException e2) {
              System.out.println("mm.smy.util.StringFommat:gb2iso(String):e1-->"+e2.getMessage());
          }
      }
      return string;
	}
	
	/**
	* 为了解决像"/211.68.47.45"之类的IP无法解析问题，我们保留纯数字的IP地址。
	* 例如把"Cathy/211.68.47.45"修改为"211.68.47.45"
	* 
	* @param ip 要转换的IP
	* @return 转换完成的IP
	*/
	public static String formatIP(String ip){
		if(ip == null|| ip.length() == 0) return ip ;

		int start = ip.indexOf("/") ;
		String temp_ip = null ;
		if(start != -1){
			temp_ip = ip.substring(start + 1 ,ip.length()) ;	
		}
		
		return temp_ip ;		
	}
	
	public static String formatIP(java.net.InetAddress ip){
		if(ip == null) return null ;
		
		return formatIP(ip.toString()) ;	
	}
	
	
	
	
	
	
	
}