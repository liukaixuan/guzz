package mm.smy.mail ;

/**
* 邮件联系人.
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6
* @see JavaMailUser BicqMailUser
*/

import javax.mail.Address ;

import mm.smy.mail.channel.NetItem ;

public interface MailUser extends java.io.Serializable, NetItem{
	
	/**
	* 获得联系人的图形表示，用于不同类型的账号的用户在一块儿处理。
	* 同时该图形表示定义了对账户的联系人自定义操作。
	* @return JLabel， 为了便于树型显示。
	*/
	public javax.swing.JLabel getRender() ;
	
	/**
	* 获得联系人所在的地址本上的文件夹，为了方便同组用户的处理。 
	* 不应该进行实际序列化。建议仅仅序列化folderID以减小可能的网络传送。
	*/
	public UserFolder getFolder() ;
	
	/**
	* 设置联系人所在的文件夹。
	*/
	public void setFolder(UserFolder folder) ;
	
	/**
	* 对邮件发送者的简单描述，例如javamail描述成"XF<myreligion@163.com>"
	* bicqmail描述成"加伊<123456>"等等。要求尽量可以通过该字段在地址本上找到该用户。
	* 或是确定该用户不在地址本上。
	*/
	public String getShortExplain() ;
	
	/**
	* 获得Address对象，为了照顾JavaMail而设立的。
	* 对于BicqMail, Address.toString()返回bicq号。
	* @see BicqMail Address
	*/
	public Address getAddress() ;
	
	/**
	* 复制，主要目的是为了通过邮件发送联系人。可以是描述联系人的字符串，或是插件什么的。
	*/
	public Object copy() ;
}
