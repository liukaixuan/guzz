package mm.smy.mail.event.mailuser ;

/**
* 文件夹中的联系人的资料发生变化时的监听。
* 仅仅包含文件夹中资料的变化，不包括数目的变化。
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
*/

public interface MailUserChangedListener extends java.io.Serializable{
	
	public void mailUserChangedAction(MailUserChangedEvent e) ;
	
}
