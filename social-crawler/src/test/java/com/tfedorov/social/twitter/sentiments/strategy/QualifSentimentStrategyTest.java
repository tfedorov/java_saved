/**
 * 
 */
package com.tfedorov.social.twitter.sentiments.strategy;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class QualifSentimentStrategyTest {

	@Mock
	private SentimentDAO sentimentDAO;
	
	@InjectMocks
	private QualifSentimentStrategy sentiment;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sentiment = new QualifSentimentStrategy();
		sentiment.setSentimentDAO(sentimentDAO);
	}

	@Test
	public void testQualification() throws Exception {
		
		Mockito.when(sentimentDAO.getSentimentLexicons()).thenReturn(getSentListForBad());
		List<String> langList = new ArrayList<String>();
		langList.add(StemmingService.DEFAULT_LANGUAGE);
		Mockito.when(sentimentDAO.loadSupportedLanguagesList()).thenReturn(langList);
		sentiment.init();
		Mockito.verify(sentimentDAO).getSentimentLexicons();
		Mockito.verify(sentimentDAO).loadSupportedLanguagesList();
		Mockito.verifyNoMoreInteractions(sentimentDAO);
		
		//Qualifier before search term case
		SENTIMENT resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("i,am,not,bad"),
				"I am not bad", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.positive, resultSentiment);
		
		//Qualifier after search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("i,am,bad,not"),
				"I am bad not always", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.negative, resultSentiment);
		
		//No qualifier near search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("i,am,bad,man"),
				"I am bad man", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.negative, resultSentiment);
		
		//No qualifier no search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("i,am,simple,man"),
				"I am simple man", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.neutral, resultSentiment);
		
		//Qualifier no search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("bad,i,am,not"),
				"bad i am not always", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.negative, resultSentiment);
		
		//Qualifier 3 word before search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("bad,i,am,not"),
				"not word i am bad always", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.negative, resultSentiment);
	/*	
		//Qualifier 2 word before and comma search term case
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("bad,i,am,not"),
				"not, i am bad always", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.negative, resultSentiment);
	*/			
		//Another qualifier
		resultSentiment = sentiment.provideSentiment(getListFromCommaSepparated("i,am,not,bad"),
				"I am none very bad", StemmingService.DEFAULT_LANGUAGE);
		Assert.assertNotNull(resultSentiment);
		Assert.assertEquals(SENTIMENT.positive, resultSentiment);
	}
	
	private List<SentimentLexicon> getSentListForBad() {
		SentimentLexicon item = new SentimentLexicon(1L,"bad","en",false);
		List<SentimentLexicon> returnList = new ArrayList<SentimentLexicon>();
		returnList.add(item);
		return returnList;
	}
	
	private List<String> getListFromCommaSepparated(String commaSepList) {
		return Arrays.asList(commaSepList.split("\\s*,\\s*"));
	}
}
