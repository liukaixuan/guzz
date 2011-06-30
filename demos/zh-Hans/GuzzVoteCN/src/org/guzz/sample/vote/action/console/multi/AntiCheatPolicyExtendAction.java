/**
 * AntiCheatPolicyExtendAction.java created at 2009-10-22 上午11:46:58 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console.multi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.business.AntiCheatPolicy;
import org.guzz.sample.vote.manager.IAntiCheatPolicyManager;
import org.guzz.util.Assert;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class AntiCheatPolicyExtendAction extends MultiActionController {
	
	private IAntiCheatPolicyManager antiCheatPolicyManager ;
	
	private String listView ;
	
	/**
	 * 根据策略号删除1个反作弊策略。
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id") ;
		
		AntiCheatPolicy acp = this.antiCheatPolicyManager.getPolicyById(id) ;
		
		Assert.assertResouceNotNull(acp, "策略不存在！") ;
		
		this.antiCheatPolicyManager.removePolicy(acp) ;
		
		return new ModelAndView(getListView(), "voteId", acp.getVoteId()) ;
	}

	public String getListView() {
		return listView;
	}

	public void setListView(String listView) {
		this.listView = listView;
	}

	public IAntiCheatPolicyManager getAntiCheatPolicyManager() {
		return antiCheatPolicyManager;
	}

	public void setAntiCheatPolicyManager(IAntiCheatPolicyManager antiCheatPolicyManager) {
		this.antiCheatPolicyManager = antiCheatPolicyManager;
	}

}
