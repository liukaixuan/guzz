/**
 * ZipCodeValidator.java created at 2009-10-16 下午02:27:26 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.validator;

import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.exception.InvalidParamException;
import org.guzz.sample.vote.manager.IUserInputValidator;
import org.guzz.util.StringUtil;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ZipCodeValidator implements IUserInputValidator {

	public void checkValid(VoteExtraProperty extraProperty, String valueToCheck) throws InvalidParamException {
		if(valueToCheck.length() != 6){
			throw new InvalidParamException(extraProperty.getShowName() + " 's length is 6.") ;
		}
		
		int zipCode = StringUtil.toInt(valueToCheck) ;
		
		if(zipCode > 999999 || zipCode < 100000){
			throw new InvalidParamException(extraProperty.getShowName() + " 's lenth is 6.") ;
		}
	}

	public String getJsValidString(VoteExtraProperty extraProperty, String htmlFormParamName) {
		return null;
	}

	public String getName() {
		return "Chinese Zip Code" ;
	}

}
