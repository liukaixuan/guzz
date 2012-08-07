package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.service.core.SlowUpdateService;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;

public class VoteMessageAction implements Controller {
	
	private SlowUpdateService slowUpdateService ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
		String type = request.getParameter("type") ;

		if("yes".equals(type)){
			//public void updateCount(Class domainClass, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteYes", msgId, 1) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteScore", msgId, 10) ;
		}else{
			this.slowUpdateService.updateCount(Message.class, userId, "voteNo", msgId, 1) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteScore", msgId, -8) ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public SlowUpdateService getSlowUpdateService() {
		return slowUpdateService;
	}

	public void setSlowUpdateService(SlowUpdateService slowUpdateService) {
		this.slowUpdateService = slowUpdateService;
	}

}
