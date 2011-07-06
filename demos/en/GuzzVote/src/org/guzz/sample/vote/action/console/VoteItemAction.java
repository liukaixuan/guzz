/**
 * VoteItemAction.java created at 2009-9-22 下午04:07:29 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.VoteItemModel;
import org.guzz.sample.vote.business.VoteItem;
import org.guzz.sample.vote.exception.VoteException;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.sample.vote.util.ValidationUtil;
import org.guzz.sample.vote.util.VoteAssert;
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
public class VoteItemAction extends SimpleFormController {

	private IBigVoteManager bigVoteManager ;
	
	public VoteItemAction(){
		this.setCommandName("voteItemForm") ;
	}	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		VoteItemModel model = (VoteItemModel) command ;
		VoteItem item = model.getItem() ;
		
		item.setName(item.getName().trim()) ;
		
		if(model.isNew()){
			this.bigVoteManager.addVoteItem(item) ;
		}else{
			this.bigVoteManager.updateVoteItem(item) ;
		}
		
		return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(item.getVoteId()));
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
		
		if(id > 0){
			VoteItem item = this.bigVoteManager.getVoteItem(id) ;
			VoteAssert.assertNotNull(item, "投票项不存在！") ;
			
			return new VoteItemModel(item) ;
		}else{
			if(voteId < 0){
				throw new VoteException("缺少voteId参数") ;
			}
			
			return new VoteItemModel(voteId) ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "item.name", null, "名称不能为空！") ;
		
		super.onBindAndValidate(request, command, errors);
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}
	
}

