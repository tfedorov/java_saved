/**
 * 
 */
package com.tfedorov.social.twitter.processing.sentiments;

import java.util.Arrays;
import java.util.List;

import com.tfedorov.social.processing.ProcessingHandler;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SentimentHandlerTest {
	private static final String TWEET_TEXT = "fat hens lay few eggs german proverb";
	@Mock
	private ProcessingHandler<GeneralProcessingContext> successor;
	@InjectMocks
	private SentimentHandler sentimentHandler;
	@Mock
	private SentimentStrategy sentimentStrategy;
	
	
	private GeneralProcessingContextBuilder contextBuilder;
	List<String> wordList = getListFromCommaSepparated("fat,hens,lay,few eggs,german,proverb");

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
		contextBuilder = new GeneralProcessingContextBuilder();
		
		contextBuilder.tweetTermsWswList = wordList;
		contextBuilder.sentimentStrategy = sentimentStrategy;
	}

	@Test
	public void testProcessSimpleTweetPositive() throws Exception {

		contextBuilder.cleanTweetedText = TWEET_TEXT;
		
		GeneralProcessingContext standContext = contextBuilder.buildWithStandSimple();	
		Mockito.when(sentimentStrategy.provideSentiment(wordList,TWEET_TEXT ,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG)).thenReturn(SENTIMENT.positive);
		
		Assert.assertNull(standContext.getTweetContext().getTweetInfo().getSentiment());
		sentimentHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		Mockito.verify(sentimentStrategy).provideSentiment(wordList,TWEET_TEXT,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG);
		
		Mockito.verifyNoMoreInteractions(successor,sentimentStrategy);
		
		Assert.assertEquals(SENTIMENT.positive, standContext.getTweetContext().getTweetInfo().getSentiment());
		
		standContext = contextBuilder.buildWithStandSimple();	
		
	}
	
	@Test
	public void testProcessSimpleTweetNegative() throws Exception {
		
		contextBuilder.cleanTweetedText = TWEET_TEXT;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandSimple();	
		Mockito.when(sentimentStrategy.provideSentiment(wordList,TWEET_TEXT,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG)).thenReturn(SENTIMENT.negative);
		
		Assert.assertNull(standContext.getTweetContext().getTweetInfo().getSentiment());
		sentimentHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		Mockito.verify(sentimentStrategy).provideSentiment(wordList,TWEET_TEXT,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG);
		
		Mockito.verifyNoMoreInteractions(successor,sentimentStrategy);
		
		Assert.assertEquals(SENTIMENT.negative, standContext.getTweetContext().getTweetInfo().getSentiment());
		
		standContext = contextBuilder.buildWithStandSimple();	
		
	}
	
	@Test
	public void testProcessReTweetPositive() throws Exception {
		
		contextBuilder.cleanRetweetedText = TWEET_TEXT;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandRetweet();
		Mockito.when(sentimentStrategy.provideSentiment(wordList,TWEET_TEXT,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG)).thenReturn(SENTIMENT.negative);
		
		Assert.assertNull(standContext.getTweetContext().getTweetInfo().getSentiment());
		sentimentHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		Mockito.verify(sentimentStrategy).provideSentiment(wordList,TWEET_TEXT,GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG);
		
		Mockito.verifyNoMoreInteractions(successor,sentimentStrategy);
		
		Assert.assertEquals(SENTIMENT.negative, standContext.getTweetContext().getTweetInfo().getSentiment());
		
		standContext = contextBuilder.buildWithStandSimple();	
		
	}
	
	public void testProcessSimpleTweetNeutral() throws Exception {
		
		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		List<String> wordList = getListFromCommaSepparated("fat,hens,lay,few eggs,german,proverb");
		contextBuilder.tweetTermsWswList = wordList;
		contextBuilder.sentimentStrategy = sentimentStrategy;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandSimple();	
		Mockito.when(sentimentStrategy.provideSentiment(wordList,"",GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG)).thenReturn(SENTIMENT.neutral);
		
		Assert.assertNull(standContext.getTweetContext().getTweetInfo().getSentiment());
		sentimentHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		Mockito.verify(sentimentStrategy).provideSentiment(wordList,"",GeneralProcessingContextBuilder.STANDART_SIMPLE_LANG);
		
		Mockito.verifyNoMoreInteractions(successor,sentimentStrategy);
		
		Assert.assertEquals(SENTIMENT.neutral, standContext.getTweetContext().getTweetInfo().getSentiment());
		
		standContext = contextBuilder.buildWithStandSimple();	
		
	}
	
	
	private List<String> getListFromCommaSepparated(String commaSepList) {
		return Arrays.asList(commaSepList.split("\\s*,\\s*"));
		
	}

}
