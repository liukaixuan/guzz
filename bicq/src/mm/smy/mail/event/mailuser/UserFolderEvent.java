package mm.smy.mail.event.mailuser ;

/**
* 当文件夹改动设置，或是文件夹的子文件夹建立，删除时产生事件
* FOLDER_CREATED事件由被创建的文件夹的parent发送，事件参数为被创建的文件夹
* FOLDER_REMOVED事件由被删除的文件夹的parent发送，事件参数为被删除的文件夹
* FOLDER_CHANGED事件由改变的文件夹在改动前传送，然后用新的文件夹覆盖当前的文件夹，覆盖所有监听。
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
*/

import mm.smy.mail.event.FolderEventConstances ;
import mm.smy.mail.UserFolder ;

public class UserFolderEvent extends java.util.EventObject implements FolderEventConstances{
	protected int type ;
	protected UserFolder folder = null ;
	protected UserFolder newfolder = null ;

	/**
	* @param source   the object where this event is created.if is folder created event, this object will be the created folder's parent folder.
	* @param folder   the folder where the changes take affect on.
	* @param type     the event type
	*/
	
	public UserFolderEvent(Object source, UserFolder folder, int type){
		super(source) ;
		this.type = type ;
		this.folder = folder ;
	}
	
	/**
	* @param source   the object where this event is created.if is folder created event, this object will be the created folder's parent folder.
	* @param oldfolder the folder where the changes take affect on.
	* @param newfolder if this is a FOLDER_CHANGED event, this object is the folder used to replace the old one
	* @param type     the event type
	*/
	public UserFolderEvent(Object source, UserFolder oldfolder, UserFolder newfolder, int type){
		super(source) ;
		this.folder = oldfolder ;
		this.newfolder = newfolder ;
		this.type = type ;
	}
	
	public UserFolder getFolder(){ return folder ;}
	
	/**
	* only used for FOLDER_CHANGED event, else return null.
	*/
	public UserFolder getNewFolder(){ return newfolder ; }
	
	public int getType(){ return type ; }
	
}