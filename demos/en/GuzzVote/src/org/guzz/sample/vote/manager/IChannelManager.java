/**
 * IChannelManager.java created at 2009-10-27 下午03:00:22 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager;

import org.guzz.sample.vote.business.Channel;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IChannelManager {
	
	public Channel getChannel(int id) ;

	public void addChannel(Channel ch, String authGroup) ;
	
	public void updateChannel(Channel ch) ;
			
}
