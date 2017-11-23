package com.tfedorov.social.twitter.processing.trace;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

public class DumpTweetHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  public DumpTweetHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  public Class getClazz() {
    return DumpTweetHandler.class;
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {
    TweetProcessingContext twContext = context.getTweetContext();
    ServicesContext svContext = context.getServicesContext();

    TweetTracingService traceService = svContext.getTweetTracingService();

    traceService.trace();

    traceService.dumpTweetToFile(twContext.getTweetInfo().getTweet());

  }

}
