package mm.smy.mail ;

/**
* 邮件客户的环境变量。有点儿类似于MainManager类。在这儿进行各个Account的初始化。
* 虽然我们期望邮件服务器能够独立于BICQ而运行，可是现在还达不到这种能力，主要是因为
* 很多东西要求BICQ服务器提供支持，也需要BICQ的组件。
* 我们把与BICQ联系紧密的地方尽量放在了该类中。
*
* @author XF <a href="mailto:myreligion@163.com">myreligion@163.com</a>
* @date 2004/2/8
* @see mm.smy.bicq.MainManager
*/

import java.util.Properties ;

public class MailContext implements java.io.Serializable{
	
	/**
	* 各项外部初始化完毕，请求开启邮件客户端。
	* 
	*/
	public void startService(){
		
	}
	
	/**
	* 获得MailContext对象。参数Properties应该包含以下项目。
	* <b>initfile</b> :该文件指定了服务器初始化的文件地址。
	* <pre>该文件中包含三项：mail:邮件保存文件地址，默认为username.mail
	*                        account:账号保存地址，默认为username.account
	*                        contact:JavaMail地址本，该部分由JavaMail的Account自己指定，这儿只是为了方便。默认为username.contact
	* </pre>
	* 我们对上面的三个文件提供加密支持。
	* <b>username</b> :用户名，要求为String对象，在Bicq中用bicq号；用来命名个文件夹。
	* <b>password</b> :密码，该密码为明文密码！！！以提供对Bicq各使用文件的统一加密。
	* password并不是简单的String对象，而是mm.smy.security.HashAuthenticator对象。
	* <b>userfolder</b>默认的使用文件夹，如果initfile使用失败[如为null或是中间项目残缺错误等]
	* 我们将会使用该文件夹。该文件夹还用来备份各个资料，因此要求不为空。
	* 如果在指定的文件夹中已经含有所要项目，而initfile没有指定，将会使用这些项目。
	* 如果这些项目不合法，将会创建新命名的文件，然后修改Properties对象。
	* 总之，经过该方法后，传入的函数将会被修改成包含可用内容的格式。
	* 如果传入的参数中包含有不合法且不能自动更正的成分，返回null.
	* 该类不是single模式，每次返回新的实例或是null.
	* <b>mm</b>MainManager的实例，这儿只是照顾性的Apater模式，期望以后改变。
	* 用来和BICQ服务器通信。
	* @param Properties p :初始化环境
	* @return MailContext :除initfile可以为null，其他任何项为空都将返回null。
	*/
	public static MailContext getMailContext(Properties p){		
		return null ;
	}
	
	
	
	
	
	
}
