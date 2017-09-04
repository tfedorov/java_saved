package com.game.party;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.game.exception.GameLogicException;
import com.game.party.step.AbstractStepBean;
import com.game.party.step.QuestionStepBean;
import com.game.party.step.result.AnswerResult;

public class PartyThreadTest {
	
	private static final String TEST_USER_1 = "123456";
	private static final String TEST_USER_2 = "623451";
	
	private PartyThread testedThread;
	private static List<String> list = new ArrayList<String>();
	{
		list.add(TEST_USER_1);
		list.add(TEST_USER_2);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		testedThread = new PartyThread(list);
	}

	@Test
	public void testCreate() throws Exception {

		PartyBean resultGame = testedThread.threadGame;
		
		Assert.assertEquals(0, resultGame.getTotalScore());

		RoundBean resultRound = resultGame.getCurrentRound();
		Assert.assertEquals(0, resultRound.getOperativeScore());
		Assert.assertEquals(0, resultRound.getRoundScore());
		
		Assert.assertEquals(0, resultRound.getActiveUserIndex());
		Assert.assertEquals(TEST_USER_1, resultRound.getCurrentUser());
		Assert.assertEquals(list, resultRound.getUsers());
		
		Assert.assertNull(resultRound.getPrevStep());
		
		AbstractStepBean resultQuestion = resultRound.getCurrentStep();
		Assert.assertEquals(TEST_USER_1, resultQuestion.getUserOwner());

	}
	
	@Test
	public void testDoAnswerCorrectBanking() throws Exception {
		testedThread.run();
		
		AbstractStepBean firstQuestion = testedThread.threadGame.getCurrentRound().getCurrentStep();
		
		Assert.assertTrue(firstQuestion instanceof QuestionStepBean);
		
		testedThread.doAnswerStep(TEST_USER_1, "aaa");
		
		PartyBean resultGame = testedThread.threadGame;
		
		Assert.assertEquals(0, resultGame.getTotalScore());

		RoundBean resultRound = resultGame.getCurrentRound();
		//Check increasing of operative score
		Assert.assertEquals(PartyThread.BEGINING_POINTS, resultRound.getOperativeScore());
		Assert.assertEquals(0, resultRound.getRoundScore());
		
		//Index of the current user should increased
		Assert.assertEquals(1, resultRound.getActiveUserIndex());
		//Active user should be changed
		Assert.assertEquals(TEST_USER_2, resultRound.getCurrentUser());
		Assert.assertEquals(list, resultRound.getUsers());
		
		//Prev Question field should appear
		Assert.assertNotNull(resultRound.getPrevStep());
		//Prev Question field should be equal previus active question
		Assert.assertEquals(firstQuestion,resultRound.getPrevStep());
		
		//Prev Question Result field should appear
		Assert.assertNotNull(resultRound.getPrevStep().getResult());
		//Prev Question result should be answer
		Assert.assertEquals(AnswerResult.class,resultRound.getPrevStep().getResult().getClass());
		//Answer was succes
		Assert.assertTrue(resultRound.getPrevStep().getResult().isSuccesFullyResult());
		
		AbstractStepBean secondQuestion = resultRound.getCurrentStep();
		Assert.assertEquals(TEST_USER_2, secondQuestion.getUserOwner());
		
		testedThread.doBankingStep(TEST_USER_2, true);
		//Active user should be changed
		Assert.assertEquals(TEST_USER_2, resultRound.getCurrentUser());
		//Check increasing of operative score
		Assert.assertEquals(0, resultRound.getOperativeScore());
		Assert.assertEquals(PartyThread.BEGINING_POINTS, resultRound.getRoundScore());
	
	}

}
