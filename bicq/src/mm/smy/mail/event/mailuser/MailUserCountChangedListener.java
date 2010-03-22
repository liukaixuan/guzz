package mm.smy.mail.event.mailuser ;

/**
* 一个文件夹中的联系人个数变化监听，不包含子文件夹的数目变化。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
*/

public interface MailUserCountChangedListener extends java.io.Serializable{
	
	public void mailUserCountAdded(MailUserCountChangedEvent e) ;
	
	public void mailUserCountRemoved(MailUserCountChangedEvent e) ;	
	
}
