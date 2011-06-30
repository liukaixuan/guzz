/**
 * AntiCheatPolicyAction.java created at 2009-10-21 下午04:55:09 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.AntiCheatPolicyModel;
import org.guzz.sample.vote.business.AntiCheatPolicy;
import org.guzz.sample.vote.manager.IAntiCheatPolicyManager;
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
public class AntiCheatPolicyAction extends SimpleFormController {

	private IAntiCheatPolicyManager antiCheatPolicyManager ;
	
	public AntiCheatPolicyAction(){
		this.setCommandName("policyForm") ;
	}	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		AntiCheatPolicyModel model = (AntiCheatPolicyModel) command ;
		AntiCheatPolicy policy = model.getPolicy() ;
		
		String name = null ;
		
		if("extraProp".equalsIgnoreCase(policy.getPolicyImpl())){
			name = "同1个" + policy.getLimitedField() + policy.getMaxLife() + "秒内最多允许投" + policy.getAllowedCount() + "票" ;
		}else if("IP".equalsIgnoreCase(policy.getPolicyImpl())){
			name = "同1个IP" + policy.getMaxLife() + "秒内最多允许投" + policy.getAllowedCount() + "票" ;
		}else if("cookie".equalsIgnoreCase(policy.getPolicyImpl())){
			name = "同1个用户" + policy.getMaxLife() + "秒内最多允许投" + policy.getAllowedCount() + "票" ;
		}
		
		policy.setName(name) ;
		
		if(model.isNew()){
			policy.setCreatedTime(new Date()) ;
			
			this.antiCheatPolicyManager.addPolicy(policy) ;
		}else{
			this.antiCheatPolicyManager.updatePolicy(policy) ;
		}
		
		return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(policy.getVoteId()));
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
				
		if(id > 0){
			AntiCheatPolicy policy = this.antiCheatPolicyManager.getPolicyById(id) ;
			VoteAssert.assertNotNull(policy, "策略不存在！") ;
			
			return new AntiCheatPolicyModel(policy) ;
		}else{
			Assert.assertBigger(voteId, 0, "缺少voteId参数") ;
			
			return new AntiCheatPolicyModel(voteId) ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "policy.policyImpl", null, "策略名不能为空！") ;
		
		ValidationUtil.rejectIfNotParamName(errors, "policy.policyImpl", null, "策略名不能包含中文和特殊字符！") ;
		
		AntiCheatPolicyModel model = (AntiCheatPolicyModel) command ;
		
		if("extraProp".equals(model.getPolicy().getPolicyImpl())){
			ValidationUtil.rejectIfEmpty(errors, "policy.limitedField", null, "限制字段不能为空！") ;
		}
		
		
		super.onBindAndValidate(request, command, errors);
	}

	public IAntiCheatPolicyManager getAntiCheatPolicyManager() {
		return antiCheatPolicyManager;
	}

	public void setAntiCheatPolicyManager(IAntiCheatPolicyManager antiCheatPolicyManager) {
		this.antiCheatPolicyManager = antiCheatPolicyManager;
	}

}
