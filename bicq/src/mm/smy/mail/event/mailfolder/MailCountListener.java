package mm.smy.mail.event.mailfolder ;

/**
* Mails in folder's count changed.
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
*/

public interface MailCountListener extends java.io.Serializable{
	public void mailAdded(MailCountEvent event) ;
	public void mailRemoved(MailCountEvent event) ;
}