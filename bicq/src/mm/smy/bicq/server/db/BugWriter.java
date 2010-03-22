package mm.smy.bicq.server.db;
/**
* Log error/waring information.
* @author: XF
* @author: e-mail: myreligion@163.com
* @date  : 2003Äê7ÔÂ17ÈÕ
* @copyright: XF 2003 All rights reserved.
*/

public class BugWriter{

	public static void log(String formpackage, Exception e ,String message){
		System.out.println(formpackage + (e!=null?e.getMessage():"null")  + message) ; //for test
	}


	public static void log(Object from , Exception e , String message){
		System.out.println(from.getClass().getName() + e + message) ; //for test
		if(e != null){
			System.out.println("===============================error===========================") ;
			e.printStackTrace() ;
			System.out.println("===============================error finished==================") ;
		}
	}


}