package mm.smy.mail.event ;

/**
* 定义了FolderChanged的一些公共常量。无操作。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
* @see MailFolderChangedEvent UserFolderChangedEvent
*/


public interface FolderEventConstances extends  java.io.Serializable{
	/**
	* 文件夹建立事件，其实就是父文件夹添加子文件夹的事件。
	* 该事件由父文件夹发出，事件中包含的的文件夹指向新建的文件夹。 
	*/
	public static final int FOLDER_CREATED = 56 ;
	
	/**
	* 文件夹删除事件，由被删文件夹的父文件夹发出
	* 事件中指向被删除的文件夹 
	*/
	public static final int FOLDER_REMOVED = 57 ;
	
	/**
	* 文件夹更改设置，由被更改的文件夹发出事件。
	* 具体请参考个具体的实现类。
	*/
	public static final int FOLDER_CHANGED = 58 ;	
}