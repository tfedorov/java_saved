package com.tfedorov.social.twitter.processing.concarrency;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicLongArray;

public class ConcurrencyProtectionHandler extends
                                          AbstractChainProcessingHandler<GeneralProcessingContext> {

  private AtomicLongArray atomicLongArray = new AtomicLongArray(1000);

  public ConcurrencyProtectionHandler(
      ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {
    TopicProcessingContext topicProcessingContext =
        (TopicProcessingContext) context.get(TopicProcessingContext.TOPIC_CONTEXT);
    Topic topic = topicProcessingContext.getTopicInfo().getTopic();


    long
        tweetID =
        context.getTweetContext().getTweetInfo().getTweet()
            .getId();
    long tLong = atomicLongArray.getAndSet(
        topic.getId().intValue(), tweetID);

    if (tLong == tweetID) {
      throw new ConcurrentModificationException(
          "Found duplicate tweet processing: topic_id = '"
          + topic.getId() + "', tweet_id = '"
          + tweetID + "'; " + "Thread name: " + Thread
              .currentThread().getName());
    }
  }

  @Override
  public Class getClazz() {
    return ConcurrencyProtectionHandler.class;
  }
}
