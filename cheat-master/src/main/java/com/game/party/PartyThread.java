/**
 * 
 */
package com.game.party;

import java.util.Date;
import java.util.List;

import com.game.exception.GameLogicException;
import com.game.party.step.AbstractStepBean;
import com.game.party.step.BankingStepBean;
import com.game.party.step.QuestionStepBean;
import com.game.party.step.StepFactory;
import com.game.party.step.result.AnswerResult;
import com.game.party.step.result.StepResult;
import com.game.util.JsonUtil;

/**
 * @author tfedorov
 * 
 */
public class PartyThread implements Runnable {

	protected static final int BEGINING_POINTS = 100;
	public static final int THREAD_SLEEP_TIME = 1000;
	protected PartyBean threadGame;

	public PartyThread(List<String> users) {
		threadGame = new PartyBean(RoundFactory.createStartRound(users));
		RoundBean currentRound = threadGame.getCurrentRound();
		currentRound.setCurrentStep(StepFactory.createQuestion(currentRound.getCurrentUser()));

	}

	public void run() {
		System.out.println("Game is started");
		AbstractStepBean currentStep = threadGame.getCurrentRound().getCurrentStep();
		if(new Date().compareTo(currentStep.getExpiring()) < 0){
			currentStep.getClass();
		}
		try {
			Thread.sleep(THREAD_SLEEP_TIME);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public String getGameJson() throws GameLogicException {
		try {
			return JsonUtil.printGame(threadGame);
		} catch (Exception e) {
			throw new GameLogicException(e.getMessage());
		}
	}

	public void doAnswerStep(String user, String textAnswer) throws GameLogicException {
		RoundBean currentRound = threadGame.getCurrentRound();

		QuestionStepBean currentQuestion = (QuestionStepBean) currentRound.getCurrentStep();
		if (isCorrectAnser( textAnswer)) {
			int operativeScore = currentRound.getOperativeScore();
			currentRound.setOperativeScore(operativeScore == 0 ? BEGINING_POINTS : operativeScore * 3);
			currentQuestion.setResult(new AnswerResult(true, textAnswer));
		} else {
			currentRound.setOperativeScore(0);
			currentQuestion.setResult(new AnswerResult(false, textAnswer));
		}

		currentRound.setPrevStep(currentQuestion);
		String nextActiveUser = currentRound.activateNextUser();
		currentRound.setCurrentStep(StepFactory.createBankingStep(nextActiveUser));
	}

	public void doBankingStep(String user, Boolean isBanking) throws GameLogicException {
		RoundBean currentRound = threadGame.getCurrentRound();
		BankingStepBean currentBankingStep = (BankingStepBean) currentRound.getCurrentStep();

		if (isBanking) {
			currentRound.setRoundScore(currentRound.getOperativeScore());
			currentRound.setOperativeScore(0);
			currentBankingStep.setResult(new StepResult(true));

		} else {
			currentBankingStep.setResult(new StepResult(false));
		}
		currentRound.setPrevStep(currentBankingStep);
		currentRound.setCurrentStep(StepFactory.createQuestion(currentRound.getCurrentUser()));
	}

	private static boolean isCorrectAnser(final String textAnswer) {
		// TODO rewrite this
		return textAnswer.equalsIgnoreCase("aaa");
	}

}
