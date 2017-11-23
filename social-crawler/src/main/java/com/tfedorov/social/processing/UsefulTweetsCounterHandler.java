/**
 * 
 */
package com.tfedorov.social.processing;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.tweet.TweetInfo;

/**
 * @author tfedorov
 * 
 */
public class UsefulTweetsCounterHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  public UsefulTweetsCounterHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    // Successor should be terminal hanlder
    super(successor);
  }

  @Override
  public Class<? extends UsefulTweetsCounterHandler> getClazz() {
    return this.getClass();
  }

  /**
   * This handler should called at the end of the tweet processing Check if the tweet is useful this
   * mean saved in our db (in keyword handler or intention handler) If tweet is useful increment
   * usefull tweet counter in tracing service
   */
  @Override
  protected void processImpl(GeneralProcessingContext context) {
    TweetInfo tweetInfo = context.getTweetContext().getTweetInfo();
    if (tweetInfo.isUseful()) {
      context.getServicesContext().getTweetTracingService().incUsefullTweetsCount();
    }
  }

}
