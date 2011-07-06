/**
 * Channel.java created at 2009-10-27 下午02:44:54 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.business;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 投票频道。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Channel implements Serializable {
	
	private int id ;
	
	private int parentId ;
	
	private String name ;
	
	/**用户组*/
	private String authGroup ;
	
	private Date createdTime ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

}
