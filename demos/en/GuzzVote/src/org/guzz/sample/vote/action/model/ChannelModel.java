/**
 * ChannelModel.java created at 2009-10-27 下午02:53:38 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.model;

import java.io.Serializable;

import org.guzz.sample.vote.business.Channel;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ChannelModel implements Serializable {
	
	private Channel channel ;
	
	private boolean isNew ;
	
	public ChannelModel(){
		channel = new Channel() ;
		isNew = true ;
	}
	
	public ChannelModel(Channel channel){
		this.channel = channel ;
		isNew = false ;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
