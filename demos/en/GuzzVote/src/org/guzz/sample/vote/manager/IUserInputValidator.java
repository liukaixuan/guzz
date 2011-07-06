/**
 * IUserInputValidator.java created at 2009-10-16 下午02:12:27 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.exception.InvalidParamException;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IUserInputValidator {
	
	/**名称必须全局唯一，可以为中文。*/
	public String getName() ;
	
	/**
	 * 此方法只负责验证规则。<br>
	 * @param valueToCheck 调用此方法前自动trim()，并且不会为空。<br>
	 * 参数是否属于@param extraProperty.validValues 取值范围内验证也不必考虑。
	 * */
	public void checkValid(VoteExtraProperty extraProperty, String valueToCheck) throws InvalidParamException ;

	/**
	 * 生成用于前台验证的js代码。生成的代码作为form.onSubmit()函数的一部分。
	 * 
	 * @param extraProperty
	 * @param 前台表单参数的名称
	 */
	public String getJsValidString(VoteExtraProperty extraProperty, String htmlFormParamName) ;
	
}
