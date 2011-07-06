/**
 * VoteTerritoryAction.java created at 2009-9-22 下午04:17:53 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.VoteTerritoryModel;
import org.guzz.sample.vote.business.VoteTerritory;
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
public class VoteTerritoryAction extends SimpleFormController {

	private IBigVoteManager bigVoteManager ;
	
	public VoteTerritoryAction(){
		this.setCommandName("cityForm") ;
	}	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		VoteTerritoryModel model = (VoteTerritoryModel) command ;
		VoteTerritory city = model.getCity() ;
		
		if(model.isNew()){
			this.bigVoteManager.addVoteTerritory(city) ;
		}else{
			this.bigVoteManager.updateVoteTerritory(city) ;
		}
		
		return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(city.getVoteId()));
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
		
		if(id > 0){
			VoteTerritory city = this.bigVoteManager.getVoteTerritory(id) ;
			VoteAssert.assertNotNull(city, "投票地区不存在！") ;
			
			return new VoteTerritoryModel(city) ;
		}else{
			if(voteId < 0){
				throw new VoteException("缺少voteId参数") ;
			}
			
			return new VoteTerritoryModel(voteId) ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "city.name", null, "名称不能为空！") ;
		
		super.onBindAndValidate(request, command, errors);
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}

}
