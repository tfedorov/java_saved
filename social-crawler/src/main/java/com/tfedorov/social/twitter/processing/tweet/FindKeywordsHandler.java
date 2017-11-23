package com.tfedorov.social.twitter.processing.tweet;

import java.util.List;
import java.util.regex.Pattern;

import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class FindKeywordsHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private Logger logger = LoggerFactory.getLogger(FindKeywordsHandler.class);

  public static final Pattern SPECIAL_KEYWORD_PATTERN = Pattern.compile(".*-.*");

  public FindKeywordsHandler(ProcessingHandler<GeneralProcessingContext> successorTrue) {
    super(successorTrue);
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TweetProcessingContext tweetContext = context.getTweetContext();
    TopicProcessingContext topicContext = context.getTopicContext();

    TweetInfo tweetInfo = tweetContext.getTweetInfo();
    boolean tweetInDefaulLanguage =
        StemmingService.DEFAULT_LANGUAGE.equalsIgnoreCase(tweetInfo.getTweetTextLang());
    Status tweet = tweetInfo.getTweet();

    List<Keyword> keyWords = topicContext.getTopicInfo().getParsedKeywordsLCWSList();
    // Set<String> keyWordsLCSet = topicContext.getTopicInfo().getWordsSetLCSet();

    // try to find keyword in original tweet if retweet
    if (tweet.isRetweet()) {
      // there in context should be normalized text for original tweet
      String normalizedRetweetedText =
          (String) tweetContext.get(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT);
      String stemmingRetweetedText =
          (String) tweetContext.get(TweetProcessingContext.STEMMED_RETWEETED_TEXT);
      Status retweeted = tweet.getRetweetedStatus();
      for (Keyword keyWord : keyWords) {
        // If keyword detected in normalized tweet text
        // or tweet language is english and keyword detected in stemmed tweet text
        if (normalizedRetweetedText.indexOf(keyWord.getNormalKeyword()) >= 0
            || (tweetInDefaulLanguage && stemmingRetweetedText.indexOf(keyWord.getStemmedKeyword()) >= 0)) {
          logger.debug(new StringBuilder("Found keyword{").append(keyWord)
              .append("} in retweeted[").append(retweeted.getId()).append("]RTC[")
              .append(retweeted.getRetweetCount()).append("] by [").append(tweet.getId())
              .append("]RTC[").append(tweet.getRetweetCount()).append("] \nORIGINAL{")
              .append(retweeted.getText()).append("\n NORMILIZED[").append(normalizedRetweetedText)
              .append("]} ").toString());
          //
          keywordFound(tweetInfo, topicContext, keyWord);
          //
          break;
        } else {
          /**
           * Check special keyword like "re-tweet" for exist in cleaned text (not normalized and
           * stemmed)
           */
          // check keyword and if it is word with special symbols check it on cleaned text
          if (SPECIAL_KEYWORD_PATTERN.matcher(keyWord.getNormalKeyword()).find()) {
            String cleanedTweetText =
                (String) tweetContext.get(TweetProcessingContext.PREPARED_RETWEETED_TEXT);
            // try to find keyword
            if (cleanedTweetText.indexOf(keyWord.getNormalKeyword()) >= 0) {
              //
              logger.debug(new StringBuilder("Found keyword{").append(keyWord)
                  .append("} in retweeted[").append(retweeted.getId()).append("]RTC[")
                  .append(retweeted.getRetweetCount()).append("] by [").append(tweet.getId())
                  .append("]RTC[").append(tweet.getRetweetCount()).append("] \nORIGINAL{")
                  .append(retweeted.getText()).append("\n NORMILIZED[")
                  .append(normalizedRetweetedText).append("]} ").toString());
              //
              keywordFound(tweetInfo, topicContext, keyWord);
              //
              break;
            }
          }
        }

      }

    } else {
      String normalizedTweetText =
          (String) tweetContext.get(TweetProcessingContext.NORMALIZED_TWEET_TEXT);
      String stemmedTweetText =
          (String) tweetContext.get(TweetProcessingContext.STEMMED_TWEET_TEXT);

      // try to find keyword in tweet
      for (Keyword keyWord : keyWords) {
        // If keyword detected in normalized tweet text
        // or tweet language is english and keyword detected in stemmed tweet text
        if (normalizedTweetText.indexOf(keyWord.getNormalKeyword()) >= 0
            || (tweetInDefaulLanguage && stemmedTweetText.indexOf(keyWord.getStemmedKeyword()) >= 0)) {

          logger.debug(new StringBuilder("Found keyword{").append(keyWord).append("} in tweet[")
              .append(tweet.getId()).append("]RTC[").append(tweet.getRetweetCount())
              .append("]\nORIGINAL{").append(tweet.getText()).append("}\n NORMILIZED[")
              .append(normalizedTweetText).append("]} ").toString());

          // add word found to context
          tweetInfo.markTweetAsUseful();
          topicContext.add(TopicProcessingContext.KEY_WORD_FOUND, keyWord);
          break;
        } else {
          /**
           * Check special keyword like "re-tweet" for exist in cleaned text (not normalized and
           * stemmed)
           */
          // check keyword and if it is word with special symbols check it on cleaned text
          if (SPECIAL_KEYWORD_PATTERN.matcher(keyWord.getNormalKeyword()).find()) {
            String cleanedTweetText =
                (String) tweetContext.get(TweetProcessingContext.PREPARED_TWEET_TEXT);
            // try to find keyword
            if (cleanedTweetText.indexOf(keyWord.getNormalKeyword()) >= 0) {
              //
              logger.debug(new StringBuilder("Found keyword{").append(keyWord)
                  .append("} in tweet[").append(tweet.getId()).append("]RTC[")
                  .append(tweet.getRetweetCount()).append("]\nORIGINAL{").append(tweet.getText())
                  .append("}\n NORMILIZED[").append(normalizedTweetText).append("]} ").toString());
              //
              keywordFound(tweetInfo, topicContext, keyWord);
              //
              break;
            }
          }
        }

      }
    }

  }

  private void keywordFound(TweetInfo tweetInfo, TopicProcessingContext topicContext,
      Keyword keyWord) {
    // add word found to context
    tweetInfo.markTweetAsUseful();
    topicContext.add(TopicProcessingContext.KEY_WORD_FOUND, keyWord);
  }

  @Override
  public Class getClazz() {
    return FindKeywordsHandler.class;
  }

}
