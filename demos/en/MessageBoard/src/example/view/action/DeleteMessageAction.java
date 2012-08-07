package example.view.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.jdbc.ObjectBatcher;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;
import example.business.User;

public class DeleteMessageAction implements Controller {
	
	private GuzzContext guzzContext ;
	
	private SlowUpdateService slowUpdateService ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		
		if("POST".equals(request.getMethod())){//Batch delete
			int[] ids = RequestUtil.getParameterAsIntArray(request, "ids", 0) ;
			
			if(ids.length == 0){
				return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
			}
			
			List<Message> msgs = null ;
			
			//load the Messages to delete.
			SearchExpression se = SearchExpression.forLoadAll(Message.class) ;
			se.setTableCondition(userId) ;
			se.and(Terms.in("id", ids)) ;
			
			//read from slave db.
			ReadonlyTranSession read = guzzContext.getTransactionManager().openDelayReadTran() ;
			try{
				msgs = read.list(se) ;
			}finally{
				read.close() ;
			}
			
			//Open write connections to the master db.
			WriteTranSession write = guzzContext.getTransactionManager().openRWTran(false) ;
			try{
				//Perform Batch operation.
				ObjectBatcher batcher = write.createObjectBatcher() ;
				batcher.setTableCondition(userId) ;
			
				for(Message msg : msgs){
					batcher.delete(msg) ;					
				}
				
				batcher.executeUpdate() ;
				
				write.commit() ;
			}catch(Exception e){
				write.rollback() ;
				
				throw e ;
			}finally{
				write.close() ;
			}
			
			//dec the message count
			this.slowUpdateService.updateCount(User.class, null, "messageCount", userId, -msgs.size()) ;
		}else{
			//Delete one message
			int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
			
			//auto-commit
			WriteTranSession write = guzzContext.getTransactionManager().openRWTran(true) ;
			
			try{
				Guzz.setTableCondition(userId) ;
				Message msg = (Message) write.findObjectByPK(Message.class, msgId) ;
				
				if(msg != null){
					write.delete(msg) ;
					
					//dec the message count
					this.slowUpdateService.updateCount(User.class, null, "messageCount", userId, -1) ;
				}
			}finally{
				write.close() ;
			}
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

	public SlowUpdateService getSlowUpdateService() {
		return slowUpdateService;
	}

	public void setSlowUpdateService(SlowUpdateService slowUpdateService) {
		this.slowUpdateService = slowUpdateService;
	}

}
