/**
 * CellPhoneValidator.java created at 2009-10-19 下午04:30:20 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.validator;

import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.exception.InvalidParamException;
import org.guzz.sample.vote.manager.IUserInputValidator;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CellPhoneValidator implements IUserInputValidator {

	public void checkValid(VoteExtraProperty extraProperty, String valueToCheck) throws InvalidParamException {
		if(valueToCheck.length() != 11){
			throw new InvalidParamException(extraProperty.getShowName() + " should be lengthen 11.") ;
		}
		
		if(valueToCheck.charAt(0) != '1'){
			throw new InvalidParamException(extraProperty.getShowName() + " should start with 1") ;
		}
	}

	public String getJsValidString(VoteExtraProperty extraProperty, String htmlFormParamName) {
		return null;
	}

	public String getName() {
		return "Cell Phone Number" ;
	}

}
