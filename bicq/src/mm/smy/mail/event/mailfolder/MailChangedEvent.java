package mm.smy.mail.event.mailfolder ;

/**
* Mail folder events.
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
*/

import java.util.EventObject ;
import mm.smy.mail.Mail ;

public class MailChangedEvent extends EventObject{
	private int type = -1 ;
	private Mail mail = null ;
	
	/**
	* 头发生了变化，例如contentType, mailType, Flags等变化了。
	*/
	public static final int HEADER_CHANGED = 94 ;
	
	/**
	* 身份验证发生了变化。
	*/
	public static final int AUTH_CHANGED = 95 ;
	
	/**
	* 邮件正文，标题，发送者，接收者等发生了变化时发出。 
	*/
	public static final int CONTENT_CHANGED = 96 ;
	
	/**
	* @param place where the event was created.
	* @param mail  the affected mail. This mail is the mail after modified.
	* @param type   the type of the event
	*/
	public MailChangedEvent(Object place, Mail mail, int type){
		super(place) ;
		this.mail = mail ;
		this.type = type ;
	}
	
	public Mail getMail(){ return mail ; }
	
	public int getType(){ return type ; }
	
}