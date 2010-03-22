package mm.smy.mail ;

/**
* 邮箱，存放邮件使用，不包含对邮件等的网络，文件等操作。
* 所有操作基于内存中进行。
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/7
*/

import mm.smy.mail.channel.NetItem ;
import mm.smy.mail.channel.FormatException ;
import mm.smy.security.HashAuthenticator ;
import mm.smy.security.AuthenticatorException ;


import mm.smy.mail.event.mailfolder.* ;

import java.io.Serializable ;
import java.util.Vector ;
import java.util.Iterator ;
import java.util.Enumeration ;

import java.security.NoSuchAlgorithmException ;

public class MailFolder implements NetItem, Serializable{
	
	public static MailFolder localroot = null ;
	public static MailFolder INBOX, SENTBOX, DRAFTBOX, TRASHBOX, SERVERBOX ;
	public static int HOLDS_FOLDERS = 456 ;
	public static int HOLDS_MAILS   = 457 ;
	public static int HOLDS_ANY     = 458 ;
	
	static{
		
		
	}
	
	public static UserFolder getFolder(String foldername){ return null ;}
	
	public static UserFolder getFolder(long folderID){ return null ; }
	
	public static MailFolder createNewFolder(MailFolder parent, String name, int type) throws MailFolderException, AuthenticatorException{
		if(type != HOLDS_ANY && type != HOLDS_FOLDERS && type != HOLDS_MAILS)
			throw new MailFolderException("Mail folder type unexpected.") ;
		MailFolder folder = new MailFolder(name, type, false)  ;
		if(parent == null)
			localroot.addChildFolder(folder) ;
		else
			parent.addChildFolder(folder) ;
		return folder ;
	}
	
	/**
	* @param name 文件夹名
	* @param type 文件夹类型
	* @param issystemic 是否为系统文件夹
	*/
	protected MailFolder(String name, int type, boolean issystemic){
		foldername = name ;
		this.type = type ;
		this.systemic = issystemic ;
	}
	public void setMailContext(MailContext mc){
		this.mc = mc ;
	}
	
	
	public MailFolder getRoot(){ return localroot ; }
	
	public MailFolder getParent(){ return parent ; }
	
	/**
	* @see UserFolder.isBubble()
	*/
	public boolean isBubble(){ return isbubble ; }
	
	/**
	* @see UserFolder.setBubble(boolean isbubble)
	*/
	public void setBubble(boolean m_bubble){ isbubble = m_bubble ; }
	
	public void addChildFolder(MailFolder folder) throws MailFolderException, AuthenticatorException{
		if(needAuthenticator(ADDFOLDER)) throw new AuthenticatorException("No rights to Add new subfoder") ;
		
		if(acceptType(this.HOLDS_FOLDERS)){
			childfolders.add(folder) ;
			this.notifySubfolderAddedListener(folder) ;
		}else{
			throw new MailFolderException(MailFolderException.NO_HOLD_FOLDERS) ;
		}
	}
	
	public void removeChildFolder(MailFolder folder) throws MailFolderException, AuthenticatorException{
		if(needAuthenticator(REMOVEFOLDER)) throw new AuthenticatorException("No rights to Remove subfolder") ;
				
		if(childfolders != null){
			if(!folder.isSystemic()){
				childfolders.remove(folder) ;
				this.notifySubfolderRemovedListener(folder) ;
			}else{
				throw new MailFolderException("系统文件夹，不能删除。") ;
			}
		}
	}
	
	
	public void addMail(Mail m) throws MailFolderException, AuthenticatorException{
		Mail[] mails = {m} ;
		addMails(mails) ;
	}
	
	public void addMails(Mail[] m) throws MailFolderException, AuthenticatorException{
		if(!acceptType(this.HOLDS_MAILS)) throw new MailFolderException(MailFolderException.NO_HOLD_MAILS) ;
		if(needAuthenticator(WRITE)) throw new AuthenticatorException("Append mail to folder denied, Authenticator needed.") ;
		for(int i = 0 ; i < m.length ; i++){
			mails.add(m[i]) ;
		}
		this.notifyMailAddedListener(m) ;
	}
	
	public String getFolderName(){ return foldername ; }	
	public void setFolderName(String m_name){ foldername = m_name ; }
	
	public long getFolderID(){ return folderID ; }
	
	/**
	* 如果文件夹是系统的文件夹，则不允许该操作，返回false。
	* 否则返回true。
	* 系统文件夹默认包括：INBOX, SENTBOX, DRAFTBOX, TRASHBOX, SERVERBOX
	*/
	public boolean setFolderID(long m_folderID){
		if(isSystemic()) return false ;
	
		folderID = m_folderID ;
		return true ;
	}
	
	/**
	* 是否为系统文件夹
	*/
	public boolean isSystemic(){ return systemic ; }	
	protected void setSystemic(boolean issystemic){ systemic = issystemic ; }
	
	/**
	* 该文件夹是否为指定类型的文件夹。
	* 例如：this.type = HOLDS_ANY, 当用HOLDS_FOLDERS，HOLDS_MAILS请求时都返回真。
	* @param m_type 请求类型，必须是HOLDS_ANY，HOLDS_FOLDERS，HOLDS_MAILS中的一个，否则返回false。
	*/
	public boolean acceptType(int m_type){
		if(type == HOLDS_ANY) return true ;
		return m_type == type ;
	}
	
	public int getType(){ return type ; }
	
	/**
	* 对该文件夹进行搜索，找到所有符合条件的项目并且返回。
	* 由于是对Mail进行查找，而javax.mail.search.SerchTerm都是进行Message查找，两者不在兼容。
	* 在我们的程序中将使用自定义的mm.smy.mail.search.SearchTerm，使用方法与javax包中的类似。
	* @param term 用于搜索的搜索项，如果是null，返回所有邮件。
	* @return Vector 包含Mail对象的Vector
	* @see mm.smy.mail.search.SearchTerm
	*/
//	public Vector search(mm.smy.mail.search.SearchTerm term){
//		return null ;
//	}
	
	/**
	* 设置新的文件夹验证。在设置的过程中我们不改变原验证类的地址到新的验证实例。
	* 而仅仅是把新验证HashAuthenticator的资料复制给原来的。By value 传值。
	* 
	* @param newauth 新的HashAuthenticator资料存放地址，如果为null，取消该文件夹的密码验证。
	* @param oldpassword 旧密码，提供修改前验证。
	* @param newpassword 修改后的新密码，如果为null，将会取消密码[不一定取消验证]。
	* @exception AuthenticatorException 如果提供的旧密码错误则抛出。
	*/
	public void setAuthenticator(HashAuthenticator newauth, char[] oldpassword, char[] newpassword) throws AuthenticatorException{
		if(auth == null){
			auth = newauth ;
			return ;
		}
		if(auth.isPasswordOK(oldpassword)){
			if(newauth != null){
				try{
				auth.setPassword(newpassword) ;
				}catch(NoSuchAlgorithmException e){
					throw new AuthenticatorException("系统错误，HashAuthenticator加密算法丢失") ;
				}catch(java.io.UnsupportedEncodingException e2){
					throw new AuthenticatorException("系统缺少ISO-8859-1编码，请确认并安装，否则加密系统无法运行。") ;
				}
				auth.setEchoOn(newauth.isEchoOn()) ;
				auth.setPromt(newauth.getPromt()) ;
			}
			return ;
		}
		throw new AuthenticatorException("密码错误或是包含不合法字符") ;
	}
	
	public HashAuthenticator getAuthenticator(){ return auth ; }
	
	/**
	* 请求在下次点击的时候要求验证，如果没有身份验证或密码为空，该方法效果。
	* @param action 操作名称，参看needAuthenticator(String)，增加"all"字段。
	* @return boolean true:成功的设置了下次请求密码。false:设置失败。
	*/
	public boolean nextNeedPassword(String action){
		password = null ;
		return needAuthenticator(action) ;
	}
	
	/**
	* 是否需要验证的判断。
	* 目前我们的标准是在下面情况下不要验证：1.该文件夹没有定义验证 2.所需验证的密码为空 3.用户选择在启动时验证一次而这一次已经成功地通过了验证。
	* @return boolean 是否需要验证
	* @param action 操作。包括"listmail","write","delete","rename","addfolder","removefolder","listfolder"
	* 各操作含义：
	* listmail: 读取文件中的文件列表，同时具有了对文件中未加密邮件的读取权利。
	* write: 往该文件夹中写入新的邮件
	* delete:删除文件中的邮件
	* rename:更改文件夹设置，该过程不会自动地把旧文件夹的Authenticator应用于旧文件夹上。
	* addfolder:建立子文件夹
	* removefolder:删除子文件夹
	* listfolder:列出子文件夹目录
	*/
	
	public static final String LISTMAIL = "listmail" ;
	public static final String WRITE = "write" ;
	public static final String DELETE = "delete" ;
	public static final String RENAME = "rename" ;
	public static final String ADDFOLDER = "makefolder" ;
	public static final String REMOVEFOLDER = "removefolder" ;
	public static final String LISTFOLDER = "listfolder" ;
	
	public boolean needAuthenticator(String action){
		if(!"listmail".equalsIgnoreCase(action)) return false ; //如果不是"read"操作，我们直接返回false，目前不提供支持。
		
		if(auth == null) return false ;
		if(auth.isPasswordOK(null)) return false ;
		if(password != null) return false ;
		return true ;
	}
	
	//关闭文件夹
	public void close(){
		//close connections like tcp, jdbc...
	}
	
	public Iterator getMails() throws AuthenticatorException {
		if(needAuthenticator(LISTMAIL)) throw new AuthenticatorException("you have no rights to list mails in this folder") ;
		return mails.iterator() ;
	}
	public Iterator getChildFolders()throws AuthenticatorException {
		if(needAuthenticator(LISTFOLDER)) throw new AuthenticatorException("Access subfolders denied.") ;
		return childfolders.iterator() ;		
	}
	
/*---------------------listeners manager---------------------------------------*/
	//文件夹事件
	public void addFolderListener(MailFolderListener listener){
		folderlisteners.add(listener) ;
	}
	public void removeFolderListener(MailFolderListener listener){
		folderlisteners.remove(listener) ;
	}
	//邮件改变
	public void addMailChangedListener(MailChangedListener listener){
		mailchangelisteners.add(listener) ;
	}
	public void removeMailChangedListener(MailChangedListener listener){
		mailchangelisteners.remove(listener) ;
	}
	//邮件数目变化, 添加/删除邮件
	public void addMailCountListener(MailCountListener listener){
		mailcountlisteners.add(listener) ;
	}
	public void removeMailCountListener(MailCountListener listener){
		mailcountlisteners.remove(listener) ;
	}
	
	/**
	* 把本文件夹子文件夹的添加事件传递出去。
	* @param source 被添加的文件夹。
	*/
	protected void notifySubfolderAddedListener(MailFolder source){
		MailFolderEvent event = new MailFolderEvent(this, source, MailFolderEvent.FOLDER_CREATED ) ;
		dispatchFolderAddedEvent(event) ;
	}	
	public void dispatchFolderAddedEvent(MailFolderEvent event){
		Enumeration e = folderlisteners.elements() ;
		while(e.hasMoreElements()){
			MailFolderListener listener = (MailFolderListener) e.nextElement() ;
			listener.mailFolderCreated(event) ;		
		}
		//dispatch to the parent
		if(getParent() != null || isBubble()){
			getParent().dispatchFolderAddedEvent(event) ;			
		}
	}
	
	/**
	* 把本文件夹子文件夹的删除事件传递出去。
	* @param source 被删除的文件夹。
	*/
	protected void notifySubfolderRemovedListener(MailFolder source){
		MailFolderEvent event = new MailFolderEvent(this, source, MailFolderEvent.FOLDER_REMOVED ) ;
		dispatchFolderRemovedEvent(event) ;
	}
	public void dispatchFolderRemovedEvent(MailFolderEvent event){
		Enumeration e = folderlisteners.elements() ;
		while(e.hasMoreElements()){
			MailFolderListener listener = (MailFolderListener) e.nextElement() ;
			listener.mailFolderRemoved(event) ;		
		}
		//dispatch to the parent
		if(getParent() != null || isBubble()){
			getParent().dispatchFolderRemovedEvent(event) ;			
		}
	}
	
	/**
	* 把本文件夹的变动传播出去
	* @param newfolder 本文件夹即将被改成的样子
	*/
	protected void notifyFolderChangedListener(MailFolder newfolder){
		MailFolderEvent event = new MailFolderEvent(this, this , newfolder, MailFolderEvent.FOLDER_CHANGED) ;
		dispatchFolderChangedEvent(event) ;
	}
	public void dispatchFolderChangedEvent(MailFolderEvent event){
		Enumeration e = folderlisteners.elements() ;
		while(e.hasMoreElements()){
			MailFolderListener listener = (MailFolderListener) e.nextElement() ;
			listener.mailFolderChanged(event) ;		
		}
		//dispatch to the parent
		if(getParent() != null || isBubble()){
			getParent().dispatchFolderChangedEvent(event) ;			
		}		
	}
	/////////////////mail part listeners//////////////////////////////////
	
	/**
	* 邮件改变事件。
	* @param mail 改动以后的邮件
	* @param type 改动类型
	* @see MailChangedEvent
	*/
	protected void notifyMailChangedListener(Mail mail, int type){
		MailChangedEvent event = new MailChangedEvent(this, mail, type) ;
		dispatchMailChangedEvent(event) ;
	}	
	public void dispatchMailChangedEvent(MailChangedEvent event){
		Enumeration e = mailchangelisteners.elements() ;
		while(e.hasMoreElements()){
			MailChangedListener listener = (MailChangedListener) e.nextElement() ;
			listener.mailChangedAction(event) ;
		}
		//bubble to the parent
		if(getParent() != null && isBubble()){
			getParent().dispatchMailChangedEvent(event) ;
		}
	}
	
	protected void notifyMailAddedListener(Mail[] mails){
		MailCountEvent event = new MailCountEvent(this, mails, MailCountEvent.MAIL_ADDED) ;
		dispatchMailAddedEvent(event) ;
	}	
	public void dispatchMailAddedEvent(MailCountEvent event){
		Enumeration e = mailcountlisteners.elements() ;
		while(e.hasMoreElements()){
			MailCountListener listener = (MailCountListener) e.nextElement() ;
			listener.mailAdded(event) ;
		}
		//bubble to the parent
		if(getParent() != null && isBubble()){
			getParent().dispatchMailAddedEvent(event) ;
		}
	}
	
	protected void notifyMailRemovedListener(Mail[] mails){
		MailCountEvent event = new MailCountEvent(this, mails, MailCountEvent.MAIL_REMOVED) ;
		dispatchMailRemovedEvent(event) ;
	}
	public void dispatchMailRemovedEvent(MailCountEvent event){
		Enumeration e = mailcountlisteners.elements() ;
		while(e.hasMoreElements()){
			MailCountListener listener = (MailCountListener) e.nextElement() ;
			listener.mailRemoved(event) ;
		}
		//bubble to the parent
		if(getParent() != null && isBubble()){
			getParent().dispatchMailRemovedEvent(event) ;
		}
	}
	
	/**
	* 文件夹事件所有监听器
	*/
	protected Vector folderlisteners = new Vector() ;
	protected Vector mailchangelisteners = new Vector() ;
	protected Vector mailcountlisteners = new Vector() ;


/*---------------------implements NetItem--------------------------------------*/
	public byte[] toBytes() throws FormatException{
		return null ;
	}
	
	public Object toObject(byte[] b) throws FormatException{
		return null ;
	}
	
	private Vector mails = new Vector() ;
	private Vector childfolders = new Vector() ;
	
	private String foldername = "new folder" ; //folder name
	private long folderID = -1 ;
	private boolean systemic = false ; //whether this folder is systemic
	protected int type ;
	protected MailContext mc = null ;
	private HashAuthenticator auth = null ;//身份验证，支持单个文件夹的加密
	private char[] password = null ; //该文件夹的密码，如果该内容为空请求密码
	private boolean password_everyclick = true ; //每次显示该文件夹的内容都要求密码。
	
	private boolean isbubble = true ; 
	private MailFolder parent = null ;
	
}
