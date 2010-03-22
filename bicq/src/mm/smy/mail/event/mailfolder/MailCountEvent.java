package mm.smy.mail.event.mailfolder ;

/**
* Mails in folder's count changed event.
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
*/

import java.util.EventObject ;
import mm.smy.mail.Mail ;

public class MailCountEvent extends EventObject{
	
	private Mail[] mails = null ;
	private int type = -1 ;
	
	public static final int MAIL_ADDED = 457 ;
	public static final int MAIL_REMOVED = 458 ;
	
	/**
	* @param place where the event was created.
	* @param mails affected mails
	* @param type event type, should be MAIL_ADDED or MAIL_REMOVED.
	*/
	public MailCountEvent(Object place, Mail[] mails, int type){
		super(place) ;
		this.mails = mails ;
		this.type = type ;
	}
	
	public int getType(){ return type ; }
	
	public Mail[] getMails(){ return mails ; }
	
}
