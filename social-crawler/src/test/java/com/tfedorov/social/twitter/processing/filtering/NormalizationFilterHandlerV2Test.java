/**
 * 
 */
package com.tfedorov.social.twitter.processing.filtering;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class NormalizationFilterHandlerV2Test {
	@Mock
	private ProcessingHandler<GeneralProcessingContext> successor;
	@InjectMocks
	private NormalizationFilterHandler normalizationFilterHandlerV2;
	@Mock
	private IntentionService intentionServiceMock;
	
	private static final String INTENT_TERMS = "*buy, affordable, amount, astronomical, at cost, bargain basement, bargain*, best deal*, best offer, budget*, c.o.d., can, cash, cash only, cents, charge, cheap, clearance, competitive, competitive pricing, consider*, contemplat*, cost*, cost-effective, could, could change, could consider, could do, could go, could like, could transfer, could use, coupon*, credit, cut-rate, dealer cost, discount*, dollar*, down payment, easy on the pocket, economical, exorbitant, expensive, extravagant, finance, finances, financial plan*, give you, going to, good deal*, great deal*, hard cash, high end, high-priced, if, if I, inexpensive, intend, intended, intent, intention, interest, inventory, invoice, keen on, liquidation, looking to, low end, low-priced, manufacturer cost, manufacturers suggested price, mark up, markdown, marked down, market value, match prices, moderate, monetary, money, money back, moneys worth, must have, need, needed, needs, over-priced, overpriced, overstock, penalty, price cut, price*, prohibitive, ready money, reasonable, reasonably priced, rebat*, reduce*, reduction, refund*, retail, ridiculous, rip off, rip-off, roll back, sale*, sky-high, spending money, steep, sum, super deal*, thinking about, thinking of, through the roof, total, under-priced, underpriced, value, very expensive, very high, want, wanted, wholesale, will, would change, would consider, would do, would go, would have, would like, would transfer, would use, wouldn't like";

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
		normalizationFilterHandlerV2 = new NormalizationFilterHandler(successor);
	}

	@Test
	public void testProcessImpl() throws Exception {
		
		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		contextBuilder.intentionService = intentionServiceMock;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandRetweet();		
		
		Set<String> intetnTerms = new HashSet<String>(getListFromCommaSepparated(INTENT_TERMS));
		Mockito.when(intentionServiceMock.getAllIntentTrms()).thenReturn(intetnTerms);

		normalizationFilterHandlerV2.process(standContext);

		Mockito.verify(intentionServiceMock).getAllIntentTrms();
		Mockito.verify(successor).process(standContext);
		Mockito.verifyNoMoreInteractions(successor,intentionServiceMock);
		
		TweetProcessingContext resultingTweetContext = standContext.getTweetContext();

		Assert.assertEquals("RT @aaadriiannn: My last day of running school. #bittersweet", resultingTweetContext.get(TweetProcessingContext.CLEAN_TWEET_TEXT));
		Assert.assertEquals(" rt my last day of running school  #bittersweet  @aaadriiannn ", resultingTweetContext.get(TweetProcessingContext.NORMALIZED_TWEET_TEXT));	
		Assert.assertEquals(getListFromCommaSepparated("rt, my, last, day, of, running, school"),resultingTweetContext.get(TweetProcessingContext.TWEET_TERMS_WSW_LIST));
		Assert.assertEquals(" rt my last day of run school  #bittersweet  @aaadriiannn ",resultingTweetContext.get(TweetProcessingContext.STEMMED_TWEET_TEXT));
		Assert.assertEquals(getListFromCommaSepparated("rt, my, last, day, of, run, school"),resultingTweetContext.get(TweetProcessingContext.STEMMED_TWEET_TERMS_WSW_LIST));
		
		Assert.assertEquals(" my last day of running school  #bittersweet ", resultingTweetContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT));
		Assert.assertEquals("My last day of running school. #bittersweet", resultingTweetContext.get(TweetProcessingContext.CLEAN_RETWEETED_TEXT));
		Assert.assertEquals(" my last day of running school #bittersweet ", resultingTweetContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT_WITH_INTENTS));
		Assert.assertEquals(" my last day of run school  #bittersweet ", resultingTweetContext.get(TweetProcessingContext.STEMMED_RETWEETED_TEXT));
	}
	
	@Test
	public void testProcessImplSimple() throws Exception {
		
		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		contextBuilder.intentionService = intentionServiceMock;
		GeneralProcessingContext standContext = contextBuilder.buildWithStandSimple();		
		
		Set<String> intetnTerms = new HashSet<String>(getListFromCommaSepparated(INTENT_TERMS));
		Mockito.when(intentionServiceMock.getAllIntentTrms()).thenReturn(intetnTerms);

		normalizationFilterHandlerV2.process(standContext);

		//Mockito.verify(intentionServiceMock).getAllIntentTrms();
		Mockito.verify(successor).process(standContext);
		Mockito.verifyNoMoreInteractions(successor,intentionServiceMock);
		
		TweetProcessingContext resultingTweetContext = standContext.getTweetContext();

		Assert.assertEquals("Fat hens lay few eggs. - German Proverb", resultingTweetContext.get(TweetProcessingContext.CLEAN_TWEET_TEXT));
		Assert.assertEquals(" fat hens lay few eggs german proverb ", resultingTweetContext.get(TweetProcessingContext.NORMALIZED_TWEET_TEXT));	
		Assert.assertEquals(getListFromCommaSepparated("fat, hens, lay, few, eggs, german, proverb"),resultingTweetContext.get(TweetProcessingContext.TWEET_TERMS_WSW_LIST));
		Assert.assertEquals(" fat hen lay few egg german proverb ",resultingTweetContext.get(TweetProcessingContext.STEMMED_TWEET_TEXT));
		Assert.assertEquals(getListFromCommaSepparated("fat, hen, lay, few, egg, german, proverb"),resultingTweetContext.get(TweetProcessingContext.STEMMED_TWEET_TERMS_WSW_LIST));
		
		Assert.assertNull(resultingTweetContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT));
		Assert.assertNull(resultingTweetContext.get(TweetProcessingContext.CLEAN_RETWEETED_TEXT));
		Assert.assertNull(resultingTweetContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT_WITH_INTENTS));
		Assert.assertNull(resultingTweetContext.get(TweetProcessingContext.STEMMED_RETWEETED_TEXT));
	}	

	private List<String> getListFromCommaSepparated(String commaSepList) {
		return Arrays.asList(commaSepList.split("\\s*,\\s*"));
	}

}
