package mm.smy.mail ;

/**
* 联系人文件夹，存放联系人或是子文件夹。
* 该类的NetItem序列化工作是用Serializable默认的writeObject实现的，暂时没有定义自己的序列化。
* @author XF  <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6-7
*/

import mm.smy.mail.channel.NetItem ;
import mm.smy.mail.channel.FormatException ;
import mm.smy.mail.event.mailuser.* ;

import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.Vector ;
import java.util.Iterator ;

import java.io.ByteArrayInputStream ;
import java.io.ByteArrayOutputStream ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;
import java.io.IOException ;


import javax.swing.JLabel ;

public class UserFolder implements java.io.Serializable,NetItem,Cloneable{
/*	
	public static void main(String[] args){
		System.out.println(UserFolder.createNewFolder(null,"aaa").getRoot().getFolderID()) ;
	}
*/	
	public static UserFolder root = null ;
	
	public UserFolder getRoot(){ return root ; }
	
	static{
		root = new UserFolder(null,"联系人地址本") ;
		root.folderID = 510275L ;
		root.setBubble(false) ;
	}
	
	protected UserFolder(UserFolder parent, String name){
		this.parent = parent ;
		this.foldername = name ;
		this.folderID = System.currentTimeMillis() ;
	}
	
	/**
	* 工厂方法，用来获得UserFolder对象。
	* 为了管理的方便，我们要求每个UserFolder的parent不为空，且都在root之下。
	* 其中root的parent is null.
	* 在建立的过程中，其父文件夹将会发出UserFolderEvent 
	* 
	* @param parent 要创建文件夹的父文件夹，如果为null，将用root代替。 
	* @param foldername 文件夹的名字
	* @see UserFolderEvent
	*/
	public static UserFolder createNewFolder(UserFolder parent, String foldername) throws UserFolderException{
		UserFolder temp = parent ; //防止下面的改动影响到传入的parent本身。
		if(temp == null)
			temp = root ;		
		UserFolder newfolder = new UserFolder(temp, foldername) ;
		temp.addChildFolder(newfolder) ;
		
		return temp ;
	}
	
	/**
	* 用户文件夹的ID，唯一的标示一个文件夹；一旦成生应该保持不变。
	*/
	public long getFolderID(){ return folderID ;}
	
	/**
	* 设置用户文件夹的ID，不允许对root文件夹进行设立。
	* @param id 用户文件夹的新ID
	* @return boolean 如果要求设立的文件夹为root，或是将要设立的id==root.getFolderID()，返回false；否则返回true.
	*/
	public boolean setFolderID(long id){
		if(this == root || id == root.getFolderID() )
			return false ;
		folderID = id ;
		return true ;
	}
	
	public String getFolderName(){ return foldername ; }
	
	public void setFolderName(String m_foldername){foldername = m_foldername ; }
	
	public UserFolder getParent(){return parent ;}
	
	public void setParent(UserFolder folder){ parent = folder ; }
	
	/**
	* 获得文件夹的图形表示，如果开始时没有定义图形界面，返回 new JLabel(foldername) ;
	* 定义图形界面，可以通过setRender(JLabel panel)实现。
	* 
	*/
	public JLabel getRender(){
		if(render == null)
			return new JLabel(foldername) ;
		else
			return render ;
	}
	
	/**
	* 定义该文件夹的图形界面。
	* @param panel 将用来显示表示this文件夹的图形界面，如果为null将会取消界面定义，按照默认的方式显示。
	*/
	public void setRender(JLabel label){
		this.render = label ;
	}
	
/*------------------MailUser---------------------------------*/	
	public Iterator getMailUsers(){
		return mailusers.iterator() ;	
	}
	
	public void addMailUsers(MailUser[] user) throws UserFolderException{	
		if(user == null) return ;
		
		mailusers.add(user) ;
		this.notifyMailUserAddedListener(user) ;
	}
	
	public void addMailUser(MailUser user) throws UserFolderException{
		MailUser[] users = {user} ;
		addMailUsers(users) ;	
	}
	
	public void removeMailUsers(MailUser[] user) throws UserFolderException{ 
		if(user == null) return ;
		
		mailusers.remove(user) ;
		this.notifyMailUserRemovedListener(user) ;
	}
	public void removeMailUser(MailUser user)throws UserFolderException{
		MailUser[] users = {user} ;
		removeMailUsers(users) ;	
	}
	
	/**
	* 更新联系人资料。
	* @param olduser 以前的资料
	* @param newuser 新的资料
	*/
	public void updateMailUser(MailUser olduser, MailUser newuser) throws UserFolderException{
		mailusers.remove(olduser) ;
		mailusers.add(newuser) ;
		this.notifyMailUserChangedListener(olduser, newuser) ;	
	}
/*------------------UserFolder---------------------------------*/	
	
	public Iterator getChildFolders(){
		return childfolders.iterator() ;	
	}
	
	public void addChildFolder(UserFolder folder) throws UserFolderException{
		if(folder == null) return ;
		
		childfolders.add(folder) ;
		this.notifyFolderAddedListener(folder) ;	
	}
	
	public void removeChildFolder(UserFolder folder) throws UserFolderException{
		if(folder == null) return ;
		
		childfolders.remove(folder) ;
		this.notifyFolderRemovedListener(folder) ;
	}
	
	/**
	* 该方法应该小心使用，新的文件夹将会彻底的覆盖旧的文件夹，并且不备份任何监听器。
	* 如果有监听器要重新注册，应在收到UserFolderEvent[FOLDER_CHANGED]后添加新的注册。
	* 该方法的第二个参数将选择是否将旧文件夹的子文件与联系人添加到新的文件夹中。
	* 如果添加，在新文件夹中将会产生MailCountChangedEvent与UserFolderEvent[FOLDER_CREATED]。
	* 上面两个事件的发生落后于旧文件夹发出的UserFolderChangedEvent
	* 旧的文件夹，即将要被取代的文件夹，为当前调用该方法的文件夹[this对象]。
	* 如果旧的文件夹为root[getParent() == null]，该方法直接返回null，不做任何操作。
	* @param newfolder 新的文件夹，用来取代旧的文件夹
	* @param savechildren 为true时保存旧文件夹的子文件夹与联系人到新的文件夹。
	* @return UserFolder 旧的文件夹。
	* @see MailCountChangedEvent UserFolderEvent
	*/
	public UserFolder updateFolder(UserFolder newfolder, boolean savechildren) throws UserFolderException{
		if(getParent() == null) return null ;
		
		getParent().removeChildFolder(this) ;
		getParent().addChildFolder(newfolder) ;			
		this.notifyFolderChangedListener(this, newfolder) ;
		
		if(savechildren){ //save oldfolder's children to the newfolder
			Iterator i = this.getChildFolders() ;
			while(i != null && i.hasNext()){
				UserFolder temp = (UserFolder) i.next() ;	
				newfolder.addChildFolder(temp) ;
				temp.setParent(newfolder) ;
			}
			
			MailUser[] users = (MailUser[]) mailusers.toArray(new MailUser[mailusers.size()]) ;
			for(int j = 0 ; j < users.length ; j++){
				users[j].setFolder(newfolder) ;
			}
			newfolder.addMailUsers(users) ;
		}
		return this ;	
	}

	
	/**
	* 设置该文件夹的事件是否会向上层文件夹传播。
	* 默认情况是向上传播。
	* 包括FolderChangedEvent与MailCountChangedEvent
	* @param eventup 是否把事件向上传播。
	* @see FolderChangedEvent MailCountChangedEvent 
	*/
	public void setBubble(boolean eventup){
		isbubble = eventup ;
	}
	
	public boolean isBubble(){ return isbubble ; }
	
	/**
	* 当该文件夹改动设置，或是该文件夹的子文件夹建立，删除时产生事件
	* 在这儿注册对这些事件的监听。
	*
	* @param listener 要监听的类 
	* @see UserFolderChangedListener
	*/
	public void addFolderListener(UserFolderListener listener){
		folderchangedlisteners.add(listener) ;
	}
	
	public void removeFolderListener(UserFolderListener listener){
		folderchangedlisteners.remove(listener) ;	
	}

	/**
	* fire MailFolder changed listeners
	* @param oldfolder 被改动的MailFolder
	* @param newfolder 该后的MailFolder
	*/
	protected void notifyFolderChangedListener(UserFolder oldfolder, UserFolder newfolder){
		UserFolderEvent event = new UserFolderEvent(this,oldfolder,newfolder,UserFolderEvent.FOLDER_CHANGED) ;
		dispatchFolderChangedEvent(event) ;	
	}
	protected void notifyFolderAddedListener(UserFolder source){
		UserFolderEvent event = new UserFolderEvent(this,source, UserFolderEvent.FOLDER_CREATED) ;
		dispatchFolderAddedEvent(event) ;
	}
	protected void notifyFolderRemovedListener(UserFolder source){
		UserFolderEvent event = new UserFolderEvent(this,source, UserFolderEvent.FOLDER_REMOVED) ;
		dispatchFolderRemovedEvent(event) ;	
	}
	
	public void dispatchFolderChangedEvent(UserFolderEvent event){
		Iterator i = this.getFolderChangedListeners() ;
		UserFolderListener listener ;
		while(i != null && i.hasNext()){
			listener = (UserFolderListener) i.next() ;
			listener.userFolderChanged(event) ;
		}
		
		if(isBubble() && getParent() != null){
			getParent().dispatchFolderChangedEvent(event) ;
		}		
	}
	
	public void dispatchFolderAddedEvent(UserFolderEvent event){
		Iterator i = this.getFolderChangedListeners() ;
		UserFolderListener listener ;
		while(i != null && i.hasNext()){
			listener = (UserFolderListener) i.next() ;
			listener.userFolderCreated(event) ;
		}
		
		if(isBubble() && getParent() != null){
			getParent().dispatchFolderAddedEvent(event) ;
		}
	}
	
	public void dispatchFolderRemovedEvent(UserFolderEvent event){
		Iterator i = this.getFolderChangedListeners() ;
		UserFolderListener listener ;
		while(i != null && i.hasNext()){
			listener = (UserFolderListener) i.next() ;
			listener.userFolderRemoved(event) ;
		}
		
		if(isBubble() && getParent() != null){
			getParent().dispatchFolderRemovedEvent(event) ;
		}	
	}
	
	/**
	* 当该文件夹中的联系人[不包括子文件夹]数目发生改变时产生
	* register such event's listener here. 
	* @see MailUserCountChangedListener
	*/
	public void addMailUserCountChangedListener(MailUserCountChangedListener listener){
		usercountlisteners.add(listener) ;
	}
	
	public void removeMailUserCountChangedListener(MailUserCountChangedListener listener){
		usercountlisteners.remove(listener) ;
	}

	public void addMailUserChangedListener(MailUserChangedListener listener){
		userchangedlisteners.add(listener) ;
	}
	
	public void removeMailUserChangedListener(MailUserChangedListener listener){
		userchangedlisteners.remove(listener) ;	
	}	
	
	/**
	* fire mailuser changed listeners
	* @param olduser 被改动的MailUser
	* @param newuser 该后的MailUser
	*/
	protected void notifyMailUserChangedListener(MailUser olduser, MailUser newuser){
		MailUserChangedEvent event = new MailUserChangedEvent(this,olduser, newuser) ;	
		dispatchMailUserChangedEvent(event) ;
	}

	protected void notifyMailUserAddedListener(MailUser[] source){
		MailUserCountChangedEvent event = new MailUserCountChangedEvent(this, source, MailUserCountChangedEvent.USER_ADDED) ;
		dispatchMailUserAddedEvent(event) ;	
	}
	
	protected void notifyMailUserRemovedListener(MailUser[] source){
		MailUserCountChangedEvent event = new MailUserCountChangedEvent(this, source, MailUserCountChangedEvent.USER_REMOVED) ;
		dispatchMailUserRemovedEvent(event) ;
	}
	
	/**
	* 将事件发送给它的监听者。同时如果 气泡 允许上浮，将事件发送给它的父类
	* 逐层往上传递。
	* @param event 要发出的事件
	* @see MailUserChangedEvent
	*/
	public void dispatchMailUserChangedEvent(MailUserChangedEvent event){
		Iterator i = this.getMailUserChangedListeners() ;
		while(i != null && i.hasNext()){
			MailUserChangedListener listener = (MailUserChangedListener) i.next() ;
			listener.mailUserChangedAction(event) ;
		}
		if(isBubble() && getParent() != null){
			getParent().dispatchMailUserChangedEvent(event) ;	
		}
	}
	
	public void dispatchMailUserAddedEvent(MailUserCountChangedEvent event){
		Iterator i = this.getMailUserCountListeners() ;
		while(i != null && i.hasNext()){
			MailUserCountChangedListener listener = (MailUserCountChangedListener) i.next() ;
			listener.mailUserCountAdded(event) ;	
		}
		if(isBubble() && getParent() != null){
			getParent().dispatchMailUserAddedEvent(event) ;	
		}
	}
	
	public void dispatchMailUserRemovedEvent(MailUserCountChangedEvent event){
		Iterator i = this.getMailUserCountListeners() ;
		while(i != null && i.hasNext()){
			MailUserCountChangedListener listener = (MailUserCountChangedListener) i.next() ;
			listener.mailUserCountRemoved(event) ;	
		}
		if(isBubble() && getParent() != null){
			getParent().dispatchMailUserRemovedEvent(event) ;	
		}
	}
	
	public Iterator getMailUserCountListeners(){
		return usercountlisteners.iterator() ;	
	}
	
	public Iterator getMailUserChangedListeners(){
		return userchangedlisteners.iterator() ;	
	}
	
	public Iterator getFolderChangedListeners(){
		return folderchangedlisteners.iterator() ;	
	}

/*-**********************implements NetItem********************************************************/
	public byte[] toBytes() throws FormatException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream(128) ;
		byte[] back = null ;
		ObjectOutputStream out = null ;
		try{
			out = new ObjectOutputStream(bout) ;
			out.writeObject(this) ;
			back = bout.toByteArray() ;
		}catch(Exception e){
			throw new FormatException(e) ;
		}finally{
			try{
				out.close() ;
				bout.close() ;
			}catch(Exception e2){}
		}		 
		return back ;
	}
	
	public Object toObject(byte[] b) throws FormatException {
		if(b == null) throw new FormatException("the given byte[] is null") ;
		
		ByteArrayInputStream bin = new ByteArrayInputStream(b) ;
		Object back = null ;
		ObjectInputStream in = null ;
		try{
			in = new ObjectInputStream(bin) ;
			back = in.readObject() ;
		}catch(Exception e){
			throw new FormatException(e) ;	
		}finally{
			try{
				in.close() ;
				bin.close() ;
			}catch(Exception e2){}
		}
		return back ;	
	}
/*-*****************************override methods***********************************************/
	/**
	* 克隆方法：按照Object.clone()进行克隆，然后将folderID改为System.currentTimeMillis().
	* @return Object 其实是UserFolder对象
	* @exception CloneNotSupportedException 其实Cloneable已经实现了，应该不会抛出^_^. 
	*/
	public Object clone() throws CloneNotSupportedException{
		UserFolder folder = (UserFolder) super.clone() ;
		folder.setFolderID(System.currentTimeMillis()) ;
		return folder ;
	}
	
	/**
	* 进行folderID的比较，相同的话返回ture, else return false.
	* if(folder == null) return false ;
	*/
	public boolean equals(UserFolder folder){
		if(folder == this) return true ;
		if(folder == null) return false ;
		return folder.getFolderID() == this.getFolderID() ;	
	}
	
	protected LinkedList usercountlisteners = new LinkedList() ;
	protected LinkedList userchangedlisteners = new LinkedList() ;
	protected LinkedList folderchangedlisteners = new LinkedList() ;
	
	private long folderID = -1 ; //唯一的标示一个文件夹，不得更改
	private UserFolder parent = root ; //默认为root
	private String foldername = null ;
	private boolean isbubble = true ; //是否将事件向上层传播。
	private JLabel render = null ;
	
	protected LinkedList mailusers = new LinkedList() ;
	protected Vector childfolders = new Vector() ;	
	
}