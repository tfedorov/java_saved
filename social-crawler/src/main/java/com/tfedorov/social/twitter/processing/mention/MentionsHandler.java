package com.tfedorov.social.twitter.processing.mention;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicMentionAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

public class MentionsHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private Logger logger = LoggerFactory.getLogger(MentionsHandler.class);

  public MentionsHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  public Class getClazz() {
    return MentionsHandler.class;
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TweetProcessingContext twContext = context.getTweetContext();
    TopicProcessingContext topicContext = context.getTopicContext();
    ServicesContext svContext = context.getServicesContext();

    TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();

    //check context if there topic keyword was found in tweet text
    if (topicContext.get(TopicProcessingContext.KEY_WORD_FOUND) != null) {

      Topic topic = topicContext.getTopicInfo().getTopic();

      // for mentions we are interesting in tweets
      Status status = twContext.getTweetInfo().getTweet();

      DateMidnight tweetCreationDate = DateUtils.convertToDateMidnight(status.getCreatedAt());

      SENTIMENT sentiment = twContext.getTweetInfo().getSentiment();
      int
          updatedSize =
          tweetsAggregationDao.updateAggTopicMentionIncremently(topic.getId(), tweetCreationDate, sentiment);

      // if aggregate by topic_id, day exists
      if (updatedSize > 0) {

        if (updatedSize > 1) {
          logger.warn("Duplicates found in topic-mentions aggregations");
        }

        logger.debug(
            "Update on topic_mentions:" + topic.getId() + "," + tweetCreationDate.toString());
      } else {

        TopicMentionAggregate tmaN = new TopicMentionAggregate(topic.getId(), tweetCreationDate, 1);

        logger.debug("Insert into topic_mentions:" + tmaN.toString());

        tweetsAggregationDao.insertAggregationTopicMention(tmaN, sentiment);
      }

    }

  }

}
