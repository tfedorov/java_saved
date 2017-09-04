package com.game.party.step;

import com.game.party.step.result.AnswerResult;

public class QuestionStepBean extends AbstractStepBean<AnswerResult> {

	private final String questionText;

	public QuestionStepBean(String userOwner, int expirationMilisec, String questionText ) {
		super(userOwner, expirationMilisec);
		this.questionText = questionText ;
	}

	public String getQuestionText() {
		return questionText;
	}

}
