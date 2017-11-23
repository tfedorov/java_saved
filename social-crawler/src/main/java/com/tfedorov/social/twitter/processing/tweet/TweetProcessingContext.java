package com.tfedorov.social.twitter.processing.tweet;

import com.tfedorov.social.processing.ProcessingContextImpl;

public class TweetProcessingContext extends ProcessingContextImpl {

  public static final String TWEET_CONTEXT = "tweet_context";

  protected static final String TWEET_INFO = "tweet_info";

  public static final String BLACK_WORD_FOUND = "black_word_found";

  public static final String CLEAN_TWEET_TEXT = "clean_tweet_text";

  public static final String NORMALIZED_TWEET_TEXT = "normalized_tweet_text";

  public static final String TWEET_TERMS_WSW_LIST = "tweet_terms_wsw_list";

  public static final String CLEAN_RETWEETED_TEXT = "clean_retweeted_text";

  public static final String NORMALIZED_RETWEETED_TEXT = "normalized_retweeted_text";

  public static final String NORMALIZED_RETWEETED_TEXT_WITH_INTENTS =
      "normalized_retweeted_text_with_intents";

  public static final String RETWEETED_TERMS_WSW_LIST = "retweeted_terms_ws_list";

  public static final String STEMMED_TWEET_TEXT = "stemmed_tweet_text";

  public static final String STEMMED_RETWEETED_TEXT = "stemmed_retweetedt_text";

  public static final String STEMMED_TWEET_TERMS_WSW_LIST = "stemmed_tweet_terms_wsw_list";

  public static final String PREPARED_TWEET_TEXT = "prepared_tweet_text";

  public static final String PREPARED_RETWEETED_TEXT = "prepared_retweet_text";

  public TweetProcessingContext(TweetInfo tweetInfo) {
    super();
    add(TWEET_INFO, tweetInfo);
  }

  public TweetInfo getTweetInfo() {
    return (TweetInfo) get(TWEET_INFO);
  }

  @Override
  public String getContextName() {
    return TWEET_CONTEXT;
  }

}
