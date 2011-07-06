/**
 * BigVoteAction.java created at 2009-9-22 下午02:11:54 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.BigVoteModel;
import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.sample.vote.util.ValidationUtil;
import org.guzz.sample.vote.util.VoteAssert;
import org.guzz.util.DateUtil;
import org.guzz.util.RequestUtil;
import org.guzz.util.StringUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * 
 * 添加或者修改BigVote
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigVoteAction extends SimpleFormController {

	private IBigVoteManager bigVoteManager ;
	
	public BigVoteAction(){
		this.setCommandName("bigVoteForm") ;
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		BigVoteModel model = (BigVoteModel) command ;
		BigVote vote = model.getVote() ;
		
		if(model.isNew()){
			vote.setCreatedTime(new Date()) ;
			
			this.bigVoteManager.addBigVote(vote) ;
			
			if(model.isAddChineseProvinces()){
				this.bigVoteManager.addAllChineseProvincesAsVoteTerritories(vote.getId()) ;
			}
			
			//如果是基于IP的维护，增加“海外”和“其他”2个选项。
			if(vote.isTerritoryIPAutoDetectedMode()){
				this.bigVoteManager.addAdditionalVoteTerritoriesForAutoIP(vote.getId()) ;
			}
			
		}else{
			this.bigVoteManager.updateBigBote(vote) ;
			this.bigVoteManager.recomputeVoteCount(vote.getId()) ;
		}
		
		return new ModelAndView(getSuccessView());
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int voteId = RequestUtil.getParameterAsInt(request, "id", -1) ;
		
		HashMap<String, String> territoryPolicies = new HashMap<String, String>() ;
		territoryPolicies.put("city", "Users choose their areas") ;
		territoryPolicies.put("IP", "Choose area by IP") ;
		request.setAttribute("territoryPolicies", territoryPolicies) ;
		
		if(voteId > 0){
			BigVote vote = bigVoteManager.getBigVoteForUpdate(voteId) ;
			VoteAssert.assertNotNull(vote, "投票不存在！") ;
			
			return new BigVoteModel(vote) ;
		}else{
			return new BigVoteModel() ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "vote.name", null, "名称不能为空") ;
		ValidationUtil.rejectIntSmaller(errors, "vote.channelId", 1, null, "频道必须选择") ;
		
		BigVoteModel model = (BigVoteModel) command ;
		BigVote vote = model.getVote() ;
		
		//判断时间格式正确，并设置到bigvote对象中。		
		if(StringUtil.notEmpty(model.getStartTime())){
			Date beginTime = DateUtil.stringToDate(model.getStartTime(),"yyyy-MM-dd HH:mm") ;
			if(beginTime == null){
				errors.rejectValue("startTime", null, null, "日期格式错误，格式为：2009-10-06 22:28");
			}else{
				beginTime.setSeconds(0) ;
				vote.setBeginTime(beginTime) ;
			}
		}else{
			vote.setBeginTime(null) ;
		}
		
		if(StringUtil.notEmpty(model.getEndTime())){
			Date endTime = DateUtil.stringToDate(model.getEndTime(),"yyyy-MM-dd HH:mm") ;
			if(endTime == null){
				errors.rejectValue("endTime", null, null, "日期格式错误，格式为：2009-10-06 22:28");
			}else{
				endTime.setSeconds(59) ;
				vote.setEndTime(endTime) ;
			}
		}else{
			vote.setEndTime(null) ;
		}
	
		super.onBindAndValidate(request, command, errors);
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}
	
}
