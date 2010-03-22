package mm.smy.mail.channel ;

/**
* Bicq标准的自己序列化方法，主要为了避免使用Java的序列化方法而带来的巨大的数据量对网络的压力。
* 从而期望提高网络利用率与程序运行时网络资源的瓶颈。
* 
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/6
*/

public interface NetItem extends java.io.Serializable{
	/**
	* 用对象的关键部分组成一个字节数组。
	* @exception FormatException 该异常为所有异常的通用表示，其时大部分应该是IOException
	*/
	public byte[] toBytes() throws FormatException;	
	/**
	* 用给定的字节数组反序列化成一个对象。
	*/
	public Object toObject(byte[] b) throws FormatException;
}