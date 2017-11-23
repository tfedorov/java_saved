package com.tfedorov.social.twitter.processing.filtering;

import java.util.regex.Pattern;

import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.word.processing.NormalizedTweetText;
import com.tfedorov.social.word.processing.WordProcessor;

/**
 * Tweet text filtering and normalization
 * 
 * Set of regexp used on twitter https://github.com/twitter/twitter-text-rb/blob/
 * master/lib/twitter-text/regex.rb
 * 
 * 
 */
public class NormalizationFilterHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {
  public static final Pattern ALLOWED_SPEC_CHARS = Pattern.compile("[\\-\\'_0-9]+");

  public NormalizationFilterHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TweetProcessingContext twContext = context.getTweetContext();
    // prepare terms list and normalized text
    NormalizedTweetText normalTweetText =
        WordProcessor.normalizeText(twContext.getTweetInfo().getTweet(), twContext.getTweetInfo()
            .getTweetTextLang(), context.getServicesContext().getStemmingService());

    // add to context
    twContext.add(TweetProcessingContext.CLEAN_TWEET_TEXT, normalTweetText.getCleanText());
    twContext
        .add(TweetProcessingContext.NORMALIZED_TWEET_TEXT, normalTweetText.getNormalizedText());
    twContext.add(TweetProcessingContext.TWEET_TERMS_WSW_LIST, normalTweetText.getTextTerms());
    twContext.add(TweetProcessingContext.STEMMED_TWEET_TEXT,
        normalTweetText.getStemmedNormalizedText());
    twContext.add(TweetProcessingContext.STEMMED_TWEET_TERMS_WSW_LIST,
        normalTweetText.getStemmedTextTerms());
    twContext.add(TweetProcessingContext.PREPARED_TWEET_TEXT, normalTweetText.getPreparedText());

    // ---- do the same for original tweet if this is retweet (will be reused on mentions,
    // intentions)
    if (twContext.getTweetInfo().getTweet().isRetweet()) {
      //
      IntentionService intentionService = context.getServicesContext().getIntentionService();

      // normalization for keyword matching
      NormalizedTweetText normalRetweetText =
          WordProcessor.normalizeText(twContext.getTweetInfo().getTweet().getRetweetedStatus(),
              twContext.getTweetInfo().getTweetTextLang(), context.getServicesContext()
                  .getStemmingService());
      //
      String normalizedWithoutTermsRetweetedText =
          WordProcessor.normalizeTextWithoutTerms(normalRetweetText.getCleanText(),
              intentionService.getAllIntentTrms());
      //
      twContext.add(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT,
          normalRetweetText.getNormalizedText());
      twContext.add(TweetProcessingContext.CLEAN_RETWEETED_TEXT, normalRetweetText.getCleanText());
      twContext.add(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT_WITH_INTENTS,
          normalizedWithoutTermsRetweetedText);
      twContext.add(TweetProcessingContext.STEMMED_RETWEETED_TEXT,
          normalRetweetText.getStemmedNormalizedText());
      twContext.add(TweetProcessingContext.PREPARED_RETWEETED_TEXT, normalRetweetText.getPreparedText());
    }

  }


  @Override
  public Class<?> getClazz() {
    return NormalizationFilterHandler.class;
  }

}
