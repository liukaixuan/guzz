package mm.smy.bicq.debug ;

/**
* 当产生调试性的错误时，我们用该类纪录。
*
* @author XF
* @atthor e-mail:myreligion@163.com
* @title 夏日烟愁
* @date 2003-10-17
* @copyright Copyright XF 2003 All Rights Reserved
*
*/

public class BugWriter{
	
	/**
	* log the explains
	* @param from 错误的来源类及出错的方法。
	* @param exception 被抛出的异常
	* @param myexplain 编程人员添加的对该异常的解释。
	*/	
	public static void log(String from, Exception exception, String myexplain){
		System.out.println(myexplain + "===============>" + exception.getMessage() ) ;
	}
	public static void log(Exception e){
		System.out.println("Exception==>" + e.getMessage() ) ;
	}
	public static void log(Exception e, String myexplain){
		System.out.println(myexplain + "==>" + e.getMessage() ) ;	
	}
	
	public static void log(Object from, Exception e, String myexplain){
		System.out.println( from.getClass().getName() + "||"  + myexplain + "==>" + e.getMessage() ) ;		
	}
	
	
	
}
