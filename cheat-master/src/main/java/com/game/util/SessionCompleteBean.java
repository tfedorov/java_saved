/**
 * 
 */
package com.game.util;

import java.util.List;

/**
 * @author tfedorov
 *
 */
public class SessionCompleteBean{
	
	private boolean isCompleted;
	private String session;
	private List<String> users;
	public SessionCompleteBean(String session, List<String> users) {
		super();
		this.isCompleted = true;
		this.session = session;
		this.users = users;
	}
	public boolean isCompleted() {
		return isCompleted;
	}
	public String getSession() {
		return session;
	}
	public List<String> getUsers() {
		return users;
	}
	
}	
