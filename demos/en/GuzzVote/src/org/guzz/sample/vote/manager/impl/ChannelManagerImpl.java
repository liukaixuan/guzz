/**
 * ChannelManagerImpl.java created at 2009-10-27 下午03:02:21 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.manager.impl;

import org.guzz.dao.GuzzBaseDao;
import org.guzz.sample.vote.business.Channel;
import org.guzz.sample.vote.manager.IChannelManager;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ChannelManagerImpl extends GuzzBaseDao implements IChannelManager {

	public void addChannel(Channel ch, String authGroup) {
		ch.setAuthGroup(authGroup) ;
		super.insert(ch) ;
	}

	public Channel getChannel(int id) {
		return (Channel) super.getForUpdate(Channel.class, id) ;
	}

	public void updateChannel(Channel ch) {
		super.update(ch) ;
	}

}
