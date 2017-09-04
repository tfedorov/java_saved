/**
 * 
 */
package com.game.party.step;

import java.util.Date;

import com.game.party.step.result.StepResult;

/**
 * @author taras
 *
 */
public abstract class AbstractStepBean<T extends StepResult> {

	private final Date starting;
	private final Date expiring;
	private final String userOwner;
	private T result;

	public AbstractStepBean(final String userOwner, final int expirationMilisec) {
		this.userOwner = userOwner;
		this.starting = new Date();
		this.expiring = new Date(this.starting.getTime() + expirationMilisec);
	}
	
	protected String getType(){
		return this.getClass().getName();
	}

	public String getUserOwner() {
		return userOwner;
	}

	public Date getExpiring() {
		return expiring;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}
