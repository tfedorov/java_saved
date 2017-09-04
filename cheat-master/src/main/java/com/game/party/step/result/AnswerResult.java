/**
 * 
 */
package com.game.party.step.result;

/**
 * @author taras
 *
 */
public class AnswerResult extends StepResult {

	private final String resultText;

	public AnswerResult(boolean succesFullyResult, String resultText) {
		super(succesFullyResult);
		this.resultText = resultText;
	}

	public String getResultText() {
		return resultText;
	}

}
