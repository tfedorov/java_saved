/**
 * 
 */
package com.tfedorov.social.twitter.processing.intention;

import java.math.BigInteger;
import java.util.ArrayList;

import com.tfedorov.social.intention.IntentString;
import com.tfedorov.social.intention.IntentionService;
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

import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IntentDetectionHandlerTest {
	@Mock
	private ProcessingHandler<GeneralProcessingContext> successor;
	@InjectMocks
	private IntentDetectionHandler intentDetectionHandler;
	@Mock
	private IntentionService intentionService;

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
	}

	@Test
	public void testProcessSimple() throws Exception {
		GeneralProcessingContext standContext = new GeneralProcessingContextBuilder().buildWithStandSimple();	
		
		intentDetectionHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		
		Mockito.verifyNoMoreInteractions(successor);
		
		Assert.assertNull(standContext.getIntentContext().get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST));
	}
	
	@Test
	public void testProcessStandRetweet() throws Exception {
		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		contextBuilder.intentionService = intentionService;
		contextBuilder.normalizedRetweetedTextWithInt = GeneralProcessingContextBuilder.STANDART_RETWEET_NORMAL_TEXT;
		contextBuilder.cleanRetweetedText = GeneralProcessingContextBuilder.STANDART_RETWEET_ORIGINAL_TEXT;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandRetweet();	
		
		Mockito.when(intentionService.isLanguageExist(GeneralProcessingContextBuilder.STANDART_RETWEET_LANG)).thenReturn(true);
		IntentString intentResult = new IntentString();
		intentResult.setIntention(false);
		Mockito.when(intentionService.isIntentionString(GeneralProcessingContextBuilder.STANDART_RETWEET_NORMAL_TEXT,GeneralProcessingContextBuilder.STANDART_RETWEET_ORIGINAL_TEXT)).thenReturn(intentResult);
		intentDetectionHandler.process(standContext);
		
		Mockito.verify(intentionService).isIntentionString(GeneralProcessingContextBuilder.STANDART_RETWEET_NORMAL_TEXT,GeneralProcessingContextBuilder.STANDART_RETWEET_ORIGINAL_TEXT);
		
		Mockito.verify(intentionService).isLanguageExist(GeneralProcessingContextBuilder.STANDART_RETWEET_LANG);
		Mockito.verify(successor).process(standContext);
		
		Mockito.verifyNoMoreInteractions(successor,intentionService);
		
		Assert.assertNotNull(standContext.getIntentContext().get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST));

		Assert.assertEquals(new ArrayList<BigInteger>() ,standContext.getIntentContext().get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST));
	}
	
	@Test
	public void testProcessStandRetweetNotLang() throws Exception {
		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		contextBuilder.intentionService = intentionService;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandRetweet();	
		
		
		Mockito.when(intentionService.isLanguageExist(GeneralProcessingContextBuilder.STANDART_RETWEET_LANG)).thenReturn(false);
		
		intentDetectionHandler.process(standContext);
		
		Mockito.verify(successor).process(standContext);
		Mockito.verify(intentionService).isLanguageExist(GeneralProcessingContextBuilder.STANDART_RETWEET_LANG);
		
		Assert.assertNull(standContext.getIntentContext().get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST));
		
		Mockito.verifyNoMoreInteractions(successor,intentionService);
		
		Assert.assertNull(standContext.getIntentContext().get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST));

	}
	

}
