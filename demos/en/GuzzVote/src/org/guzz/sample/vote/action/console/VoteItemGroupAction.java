/**
 * VoteItemGroupAction.java created at 2009-10-16 上午11:39:07 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.VoteItemGroupModel;
import org.guzz.sample.vote.business.VoteItemGroup;
import org.guzz.sample.vote.manager.IVoteItemGroupManager;
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
public class VoteItemGroupAction  extends SimpleFormController {

	private IVoteItemGroupManager voteItemGroupManager ;
	
	public VoteItemGroupAction(){
		this.setCommandName("voteItemGroup") ;
	}	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		VoteItemGroupModel model = (VoteItemGroupModel) command ;
		VoteItemGroup group = model.getGroup() ;
		
		if(model.isNew()){
			group.setCreatedTime(new Date()) ;
			
			this.voteItemGroupManager.add(group) ;
		}else{
			this.voteItemGroupManager.update(group) ;
		}
		
		return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(group.getVoteId()));
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
				
		if(id > 0){
			VoteItemGroup prop = this.voteItemGroupManager.getById(id) ;
			VoteAssert.assertNotNull(prop, "分组不存在！") ;
			
			return new VoteItemGroupModel(prop) ;
		}else{
			Assert.assertBigger(voteId, 0, "缺少voteId参数") ;
			
			return new VoteItemGroupModel(voteId) ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "group.name", null, "组名不能为空！") ;
		
		super.onBindAndValidate(request, command, errors);
	}

	public IVoteItemGroupManager getVoteItemGroupManager() {
		return voteItemGroupManager;
	}

	public void setVoteItemGroupManager(IVoteItemGroupManager VoteItemGroupManager) {
		this.voteItemGroupManager = VoteItemGroupManager;
	}

}
