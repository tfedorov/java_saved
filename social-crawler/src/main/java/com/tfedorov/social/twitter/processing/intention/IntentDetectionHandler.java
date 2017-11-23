package com.tfedorov.social.twitter.processing.intention;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

import com.tfedorov.social.intention.IntentString;
import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;

public class IntentDetectionHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private final Logger logger = LoggerFactory.getLogger(IntentDetectionHandler.class);

  public IntentDetectionHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  public Class getClazz() {
    return IntentDetectionHandler.class;
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TweetProcessingContext twContext = context.getTweetContext();

    IntentionProcessingContext inContext = context.getIntentContext();

    ServicesContext svContext = context.getServicesContext();

    TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();

    IntentionService intentionService = svContext.getIntentionService();

    Status status = twContext.getTweetInfo().getTweet();


    // check if there is retweeted tweet with retweets > 0 and check language for exist in our list
    if (status.isRetweet()
        && intentionService.isLanguageExist(twContext.getTweetInfo().getTweetTextLang())) {

      Status retweetedStatus = status.getRetweetedStatus();

      List<BigInteger> topicIds = new ArrayList<BigInteger>();

      // try to detect intention
      boolean isIntent = false;

      String clearRetweetedText =
          (String) twContext.get(TweetProcessingContext.CLEAN_RETWEETED_TEXT);

      String normaLizedRetweetdTextWithIntents =
          (String) twContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT_WITH_INTENTS);

      IntentString intentResult = intentionService.isIntentionString(normaLizedRetweetdTextWithIntents, clearRetweetedText);

      isIntent = intentResult.isIntention();

      String htmlText = intentResult.getResultHTML();


      if (isIntent) {

        logger.debug("INTENT: [" + retweetedStatus.getText() + "]");

        // add HTML markup text as indicator that intention found
        inContext.add(IntentionProcessingContext.TWEET_HTML_MARKUP, htmlText);


        long retweetedUserId = retweetedStatus.getUser().getId();

        // find topics by user in popular tweets
        topicIds =
            tweetsAggregationDao.checkForTrackedUsers(
                new BigDecimal(retweetedUserId).toBigIntegerExact(),
                TopicTweetAggregate.AGGREGATE_TYPE.popular_tweets);

      }

      inContext.add(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST, topicIds);

    }
  }

}
