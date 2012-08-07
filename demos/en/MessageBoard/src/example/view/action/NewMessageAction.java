/**
 * 
 */
package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.GuzzContext;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;
import example.business.User;

public class NewMessageAction implements Controller {
	
	private GuzzContext guzzContext ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		String content = request.getParameter("content") ;

		Message msg = new Message() ;
		msg.setContent(content) ;
		msg.setCreatedTime(new java.util.Date()) ;
		
		//close auto-commit
		WriteTranSession write = guzzContext.getTransactionManager().openRWTran(false) ;
		
		try{
			User user = (User) write.findObjectByPK(User.class, userId) ;
			user.setMessageCount(user.getMessageCount() + 1) ;
			
			msg.setUserId(userId) ;
			
			write.insert(msg, userId) ;
			write.update(user) ;
			
			write.commit() ;
		}catch(Exception e){
			write.rollback() ;
			
			throw e ;
		}finally{
			write.close() ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

}
