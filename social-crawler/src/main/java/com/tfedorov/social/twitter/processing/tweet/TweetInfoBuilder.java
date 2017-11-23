/**
 * 
 */
package com.tfedorov.social.twitter.processing.tweet;

import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

import com.tfedorov.social.utils.JsonUtils;

/**
 * @author tfedorov
 * 
 */
public final class TweetInfoBuilder {

  private TweetInfoBuilder() {}

  public static TweetInfo buildFromTwit4j(Status status) {
    TweetInfo tweetInfo = new TweetInfo(status);
    String rawJSON = DataObjectFactory.getRawJSON(status);
    if (status.isRetweet()) {
      String langOfOriginalTweet =
          JsonUtils.getFieldFromSubObject(rawJSON, "retweeted_status", "lang");
      tweetInfo.setTweetTextLang(langOfOriginalTweet);
    } else {
      String langOfTweet = JsonUtils.getInFirstChildField(rawJSON, "lang");
      tweetInfo.setTweetTextLang(langOfTweet);
    }
    return tweetInfo;
  }
}
