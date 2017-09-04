package com.game.party;

import java.util.Date;
import java.util.List;

import com.game.party.step.AbstractStepBean;
import com.game.party.step.result.StepResult;

public class RoundBean {
	
	private final int roundCounter;
	private final List<String> users;
	private final Date roundStartDate;
	
	private int activeUserIndex;
	private int operativeScore;
	private int roundScore;

	private AbstractStepBean<? extends StepResult> prevStep;
	private AbstractStepBean<? extends StepResult> currentStep;
	
	protected RoundBean(final int roundCounter,final  List<String> users) {
		super();
		this.roundCounter = roundCounter;
		this.users = users;
		this.activeUserIndex = 0;
		this.roundStartDate = new Date();
	}

	public int getOperativeScore() {
		return operativeScore;
	}

	public void setOperativeScore(int operativeScore) {
		this.operativeScore = operativeScore;
	}

	public int getRoundScore() {
		return roundScore;
	}

	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
	}

	public int getRoundCounter() {
		return roundCounter;
	}

	public List<String> getUsers() {
		return users;
	}

	public Date getRoundStartDate() {
		return roundStartDate;
	}

	public int getActiveUserIndex() {
		return activeUserIndex;
	}

	public void setActiveUserIndex(int activeUserIndex) {
		this.activeUserIndex = activeUserIndex;
	}

	public String getCurrentUser() {
		return users.get(activeUserIndex);
	}
	
	public String activateNextUser() {
		if(++activeUserIndex >= users.size())
			activeUserIndex = 0;
		return getCurrentUser();
	}

	public AbstractStepBean<? extends StepResult> getPrevStep() {
		return prevStep;
	}

	public void setPrevStep(AbstractStepBean<? extends StepResult> prevStep) {
		this.prevStep = prevStep;
	}

	public AbstractStepBean getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(AbstractStepBean currentStep) {
		this.currentStep = currentStep;
	}
	
}
