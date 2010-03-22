package mm.smy.mail ;

/**
* 用户文件夹异常。
* 
* @author XF  <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6-7
*/

public class MailFolderException extends Exception implements java.io.Serializable{
	
	public static final int READ_FORBID = 2345 ;
	public static final int WRITE_FORBID = 2346 ;
	public static final int ACCESS_FORBID = 2347 ;
	public static final int MAIL_TYPE_ERROR = 2348 ;
	public static final int NO_HOLD_MAILS = 2350 ;
	public static final int NO_HOLD_FOLDERS = 2351 ;

	
	private int type  ;
	
	public MailFolderException(String s){
		super(s) ;
	}
	
	public MailFolderException(String s, int type){
		super(s) ;
		this.type = type ;
	}
	
	public MailFolderException(int type){
		super() ;
		this.type = type ;
	}
	
	public MailFolderException(Throwable t, String s, int type){
		super(s, t) ;
		this.type= type ;
	}
	
	public MailFolderException(Throwable t, int type){
		super(t) ;
		this.type = type ;
	}
	
	public int getType(){ return type ; }
	
	/**
	* 获得对异常的系统解释，该解释主要依赖于构造函数中传入的int type类型。
	* 当type无法识别的时候，返回给定的解释，如果没有解释，使用系统默认的无法识别消息。
	* @return String 系统解释
	*/
	public String getSystemExplain(){
		switch(type){
			case READ_FORBID :
				return explains[0] ;
			case WRITE_FORBID :
				return explains[1] ;
			case ACCESS_FORBID :
				return explains[2] ;
			case NO_HOLD_MAILS :
				return explains[3] ;
			case NO_HOLD_FOLDERS :
				return explains[4] ;
			case MAIL_TYPE_ERROR :
				return explains[5] ;
			default:
				return super.getMessage() == null?explains[6]:super.getMessage() ;
		}
	}
	
	private static String[] explains = {
		"Cannot read from the special folder",
		"Cannot write to the special folder",
		"Cannot access to the special folder",
		"The folder cannot hold mails",
		"The folder cannot hold subfolders",
		"The folder doesn't accept the given mail type",
		"Unknown exception type, Please Contact Us, Thank You!"
	} ;
	
	
}
