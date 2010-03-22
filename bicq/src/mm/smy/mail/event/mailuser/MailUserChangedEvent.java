package mm.smy.mail.event.mailuser ;

/**
* 文件夹中的联系人的资料发生变化时的监听。
* 仅仅包含文件夹中资料的变化，不包括数目的变化。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
*/

import java.util.EventObject ;
import mm.smy.mail.MailUser ;

public class MailUserChangedEvent extends EventObject implements java.io.Serializable{
	
	protected MailUser olduser = null ;
	protected MailUser newuser = null ;
	
	/**
	* 
	* 
	* @param source the object where this event is created.
	* @param olduser the MailUser before modified
	* @param newuser the MailUser used to replace the old one. 
	*/
	public MailUserChangedEvent(Object source, MailUser olduser, MailUser newuser){
		super(source) ;
		this.olduser = olduser ; 
		this.newuser = newuser ;
	}
	
	public MailUser getOldUser(){ return olduser ; }
	
	public MailUser getNewUser(){ return newuser ; }
	
}
