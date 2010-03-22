package mm.smy.mail.event.mailuser ;

/**
* 当文件夹改动设置，或是文件夹的子文件夹建立，删除时产生事件
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
* @see MailFolderChangedEvent UserFolderChangedEvent
*/

public interface UserFolderListener extends java.io.Serializable{
	public void userFolderCreated(UserFolderEvent e) ;
	
	public void userFolderRemoved(UserFolderEvent e) ;
	
	public void userFolderChanged(UserFolderEvent e) ;
}