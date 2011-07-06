/**
 * VoteExtraPropertyAction.java created at 2009-10-16 上午11:39:07 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.VoteExtraPropertyModel;
import org.guzz.sample.vote.business.VoteExtraProperty;
import org.guzz.sample.vote.manager.IVoteExtraPropertyManager;
import org.guzz.sample.vote.util.ValidationUtil;
import org.guzz.sample.vote.util.VoteAssert;
import org.guzz.util.Assert;
import org.guzz.util.RequestUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class VoteExtraPropertyAction extends SimpleFormController {

	private IVoteExtraPropertyManager voteExtraPropertyManager ;
	
	public VoteExtraPropertyAction(){
		this.setCommandName("voteProp") ;
	}	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		VoteExtraPropertyModel model = (VoteExtraPropertyModel) command ;
		VoteExtraProperty prop = model.getProp() ;
		
		if(model.isNew()){
			this.voteExtraPropertyManager.addExtraProperty(prop) ;
		}else{
			this.voteExtraPropertyManager.updateExtraProperty(prop) ;
		}
		
		return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(prop.getVoteId()));
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
		
		request.setAttribute("validatorNames", this.voteExtraPropertyManager.getAllValidatorNames()) ;
		
		if(id > 0){
			VoteExtraProperty prop = this.voteExtraPropertyManager.getExtraProperty(id) ;
			VoteAssert.assertNotNull(prop, "属性不存在！") ;
			
			return new VoteExtraPropertyModel(prop) ;
		}else{
			Assert.assertBigger(voteId, 0, "缺少voteId参数") ;
			
			return new VoteExtraPropertyModel(voteId) ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "prop.paramName", null, "参数名不能为空！") ;
		ValidationUtil.rejectIfEmpty(errors, "prop.showName", null, "名称不能为空！") ;
		
		ValidationUtil.rejectIfNotParamName(errors, "prop.paramName", null, "参数名不能包含中文和特殊字符！") ;
		
		super.onBindAndValidate(request, command, errors);
	}

	public IVoteExtraPropertyManager getVoteExtraPropertyManager() {
		return voteExtraPropertyManager;
	}

	public void setVoteExtraPropertyManager(IVoteExtraPropertyManager voteExtraPropertyManager) {
		this.voteExtraPropertyManager = voteExtraPropertyManager;
	}

}
