/**
 * ViewVoteAction.java created at 2009-9-25 上午10:17:41 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.open;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.BigVoteTree;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * 
 * 细览某一个投票
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ViewVoteAction implements Controller{
	
	private IBigVoteManager bigVoteManager ;
	
	private String successView ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
		String type = request.getParameter("type") ;

		HashMap<String, Object> model = new HashMap<String, Object>() ;
		model.put("voteId", Integer.valueOf(voteId)) ;
		
		if(voteId > 0){
			BigVoteTree tree = bigVoteManager.getCachedVoteTree(voteId) ;
			if(tree != null){
				model.put("bigVoteTree", tree) ;
			}
		}
		
		return new ModelAndView(getSuccessView(type), model) ;
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}
	
	public String getSuccessView(String type) {
		if(type == null){
			return successView;
		}else{
			return successView + "_" + type ;
		}
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}


}
