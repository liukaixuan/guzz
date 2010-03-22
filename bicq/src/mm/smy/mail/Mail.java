package mm.smy.mail ;

/**
* 邮件接口，该邮件是广义的定义，指一切可以传送的信息。
* 目前主要是普通的电子邮件与BICQ消息/邮件。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6
* @see JavaMail BicqMail
*/

import java.io.Serializable ;
import java.util.Date ;
import java.util.Vector ;

import mm.smy.mail.channel.NetItem ;
import mm.smy.security.HashAuthenticator ;
import mm.smy.security.AuthenticatorException ;

import javax.mail.Flags ;
import javax.mail.Flags.Flag ;
import javax.mail.Message ;

public interface Mail extends Serializable, NetItem{
	
	/**
	* get the mailtype, making the differences between services.
	* like "mail/javamail" "mail/bicqmail" , case insensitive.
	* We suggestted using lower case.
	*/
	public String getMailType() ;
	
	/**
	* the contentType, can contains encoding.if no encoding, we use "gb2312".
	* eg: "text/plain", "text/html; charset=gb2312" are both accepted.
	* case insensitive. 
	* This give us a chance to handle different formatted contents.
	*/
	public String getContentType() ;
	
	/**
	* the subject of the mail
	*/
	public String getSubject() ;
	
	/**
	* the sent time of the mail
	*/	
	public Date getSentTime() ;
	
	/**
	* the recieved time of the mail
	*/	
	public Date getReceivedTime() ;
	
	/**
	* the priority of the mail
	* if not defined, -1 will be returned.
	*/	
	public int getPriority() ;
	
	/**
	* a mark of an email, for convience to search.
	* And a small Icon is suggested to indicate this. 
	*/
	public int getMark() ;
	
	/**
	*  set the mark of a mail
	*/
	public void setMark(int mark) ;
	
	/**
	* The folder which holds this message's ID.
	* To avoid big Serializable.
	*/
	public long getFolderID() ;
	
	/**
	* Flags of the mail
	* we use Flags.Flag.FLAGGED to indicate this message is already saved to server.
	* @see javax.mail.Flags 
	*/
	public Flags getFlags() ;
	
	/**
	* set Flag to the mail. if the second param is false, we cancel the first param's flag
	* @param flag the flag of message
	* @param set set the flag or cancel it
	*/
	public void setFlag(Flags.Flag flag, boolean set) ;
	
	/**
	* get the senders
	*/
	public MailUser[] getSender() ;
	
	public MailUser[] getRecipients(Message.RecipientType type) ;
	
	/**
	* get the rough size, this is not precise.
	* Just to give the user an idea.
	*/
	public int getSize() ;
	
	/**
	* if contains attachments
	*/
	public boolean hasAttachments() ;
	
	/**
	* fetch the attchments.if doesn't contain, return null
	*
	* @return javax.mail.Part 's Vector
	* @see javax.mail.Part
	*/
	public Vector getAttachments() ;
	
	/**
	* 返回该邮件的验证消息，为了支持所有消息的加密。
	* @return HashAuthenticator 如果没有验证，返回null
	*/
	public HashAuthenticator getAuthenticator() ;
	
	/**
	* 设置邮件身份验证。在设置的过程中我们<b>建议</b>不改变原验证类的地址到新的验证实例。
	* 而仅仅是把新验证HashAuthenticator的资料复制给原来的。By value 传值。
	* 
	* @param newauth 新的HashAuthenticator资料存放地址，如果为null，取消该邮件的密码验证。
	* @param oldpassword 旧密码，提供修改前验证。
	* @param newpassword 修改后的新密码，如果为null，将会取消密码[不一定取消验证]。
	* @exception AuthenticatorException 如果提供的旧密码错误则抛出。
	*/
	public void setAuthenticator(HashAuthenticator auth, char[] oldpassword, char[] newpassword) throws AuthenticatorException;
	
}