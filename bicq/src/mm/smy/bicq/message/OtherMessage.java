package mm.smy.bicq.message ;

import java.io.Serializable ;
import mm.smy.bicq.user.* ;

/**
* 设计上出现了问题，为了实现更好的可扩展性，我们加入OtherMessage的实现。
* 尽管可以用通过监视Message，然后分析自己的Message；可是总有些浪费时间
* 
* 我们通过OtherMessage为非定义的消息增加了入口。
* 所有的第二次开发用的消息都要继承该类，以实现对OtherMessage的控制。
* 否则的话，可能会出现 类 不匹配等错误，使程序出错。
* 或是根本无法实现
*
*
*/

public class OtherMessage implements Message,Serializable{
	protected byte[] b = null ;
	protected User from = null ;
	protected User to  =  null ;
	protected int mintype = MessageType.UNKNOWN_TYPE ;
	
	public byte[] getByteContent() {
		return b ;
	}

	public int getType() {
		return MessageType.UNKNOWN_TYPE ;
	}

	public int getMinType() {
		return mintype ;
	}

	public User getFrom() {
		return from ;
	}

	public User getTo() {
		return to ;
	}

	public void setFrom(User u) {
		from = u ;
	}

	public void setTo(User u) {
		to = u ;
	}

	public void setByteContent(byte[] b) {
		this.b = b ;
	}
	
}