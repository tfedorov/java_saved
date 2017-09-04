package com.game.party.step;

import com.game.party.step.result.StepResult;

public class BankingStepBean extends AbstractStepBean<StepResult>{

	public BankingStepBean(String userOwner, int expirationMilisec) {
		super(userOwner, expirationMilisec);
	}

}
