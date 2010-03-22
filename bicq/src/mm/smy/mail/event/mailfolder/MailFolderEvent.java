package mm.smy.mail.event.mailfolder ;

/**
* Mail folder event.Including folder added,removed,changed(renamed).
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
*/

import mm.smy.mail.event.FolderEventConstances ;
import mm.smy.mail.MailFolder ;

import java.util.EventObject ;

public class MailFolderEvent extends EventObject implements FolderEventConstances{
	protected int type = -1 ;
	protected MailFolder folder = null ;
	protected MailFolder newfolder = null ;

	/**
	* @param source   the object where this event is created.if is folder created event, this object will be the created folder's parent folder.
	* @param folder   the folder where the changes take affect on.
	* @param type     the event type
	*/
	
	public MailFolderEvent(Object source, MailFolder folder, int type){
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
	public MailFolderEvent(Object source, MailFolder oldfolder, MailFolder newfolder, int type){
		super(source) ;
		this.folder = oldfolder ;
		this.newfolder = newfolder ;
		this.type = type ;
	}
	
	public MailFolder getFolder(){ return folder ;}
	
	/**
	* only used for FOLDER_CHANGED event, else return null.
	*/
	public MailFolder getNewFolder(){ return newfolder ; }
	
	public int getType(){ return type ; }
	
	
	
}

