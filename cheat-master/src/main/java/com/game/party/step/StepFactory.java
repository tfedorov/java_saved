package com.game.party.step;

import java.util.Random;

public class StepFactory {

	public static QuestionStepBean createQuestion(String currentUser) {
		return new QuestionStepBean(currentUser, 7000, "repeate " + new Random(999));
	}

	public static BankingStepBean createBankingStep(String userOwner) {
		return new BankingStepBean(userOwner, 1000);
	}

}
