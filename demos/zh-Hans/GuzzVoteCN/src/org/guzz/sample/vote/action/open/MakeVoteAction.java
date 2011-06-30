/**
 * MakeVoteAction.java created at 2009-9-21 下午05:08:38 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.open;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.action.model.VoterInfo;
import org.guzz.sample.vote.manager.IAntiCheatPolicyChecker;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.util.RequestUtil;
import org.guzz.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * 
 <pre>
 如果从其他编码页面投票，如gb2312页面，form需要做如下处理：
 
< SCRIPT language=JavaScript type=text/JavaScript>
	function checkValid(form){	
	document.charset="UTF-8";
	
	//check code goes here....
	
	return true ;
}
< /SCRIPT>


 < FORM id=voteForm onsubmit="return checkValid(this);" method="post" action=http://localhost:8080/vote/makeVoteAction.do accept-charset="UTF-8" target=_blank>
      
	  < INPUT name="_charset_" type="hidden" /> 
 .....

</pre>
 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MakeVoteAction implements Controller{
	
	private IBigVoteManager bigVoteManager ;
		
	private String successView ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String encoding = request.getParameter("_charset_") ;
		
		if("x-gbk".equalsIgnoreCase(encoding)){
			encoding = "gb2312" ;
		}
		
		if(StringUtil.notEmpty(encoding)){
			request.setCharacterEncoding(encoding) ;
		}
		
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;
		int cityId = RequestUtil.getParameterAsInt(request, "cityId", -1) ;
		int[] items = RequestUtil.getParameterAsIntArray(request, "items", -1) ;
			
		
		//显示方法。如果是html，则转入successView进行显示。
		String type = request.getParameter("type") ;
						
		VoterInfo info = new VoterInfo() ;
		info.setParams(request.getParameterMap()) ;
		info.setPossibleIPs(new String[]{RequestUtil.getRealIP(request), request.getRemoteAddr()}) ;
		info.setRequest(request) ;
		info.setResponse(response) ;
		
		if("html".equalsIgnoreCase(type)){
			bigVoteManager.makeAVote(voteId, items, cityId, info) ;
			
			return new ModelAndView(getSuccessView(), "voteId", Integer.valueOf(voteId)) ;
		}else{
			//xml显示。
			PrintWriter pw = response.getWriter() ;		
			
			try{
				bigVoteManager.makeAVote(voteId, items, cityId, info) ;
			}catch(Exception e){
				pw.println(error(e.getMessage())) ;
				e.printStackTrace() ;
				return null ;
			}
			
			//成功完成投票。
			pw.println(success("1")) ;
			
			return null;
		}
	}
	
	protected String error(String msg){
		return "<error>" + msg + "</error>" ;
	}
	
	protected String success(String msg){
		return "<success>" + msg + "</success>" ;
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

}
