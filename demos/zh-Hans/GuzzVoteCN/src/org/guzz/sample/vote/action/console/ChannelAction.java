/**
 * ChannelAction.java created at 2009-10-27 下午02:57:02 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.console;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.GuzzContext;
import org.guzz.sample.vote.action.model.ChannelModel;
import org.guzz.sample.vote.business.Channel;
import org.guzz.sample.vote.manager.IChannelManager;
import org.guzz.sample.vote.util.ValidationUtil;
import org.guzz.service.user.AdminUserService;
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
public class ChannelAction extends SimpleFormController {

	private IChannelManager channelManager ;
	
	private AdminUserService adminUserService ;
	
	public ChannelAction(){
		this.setCommandName("channelForm") ;
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		super.onSubmit(request, response, command, errors);
		
		ChannelModel model = (ChannelModel) command ;
		Channel ch = model.getChannel() ;
		
		if(model.isNew()){
			ch.setCreatedTime(new Date()) ;
			
			this.channelManager.addChannel(ch, ch.getAuthGroup()) ;
		}else{
			this.channelManager.updateChannel(ch) ;
		}
		
		return new ModelAndView(getSuccessView());
	}

	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		int id = RequestUtil.getParameterAsInt(request, "id", -1) ;
		
		request.setAttribute("authGroups", this.adminUserService.getAvailableAuthGroups()) ;
		
		if(id > 0){
			Channel ch = this.channelManager.getChannel(id) ;
			return new ChannelModel(ch) ;
		}else{
			return new ChannelModel() ; 
		}
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ValidationUtil.rejectIfEmpty(errors, "channel.name", null, "名称不能为空") ;
		ValidationUtil.rejectIfEmpty(errors, "channel.authGroup", null, "用户组必须选择") ;
		
		super.onBindAndValidate(request, command, errors);
	}
	
	public void setGuzzContext(GuzzContext guzzContext){
		this.adminUserService = (AdminUserService) guzzContext.getService("adminUserService") ;
	}

	public IChannelManager getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(IChannelManager channelManager) {
		this.channelManager = channelManager;
	}

}
