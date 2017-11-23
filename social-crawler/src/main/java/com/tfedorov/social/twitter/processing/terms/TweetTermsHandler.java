package com.tfedorov.social.twitter.processing.terms;

import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TweetTermsHandler extends TweetTermAggregationHandler {

  public TweetTermsHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  public Class<?> getClazz() {
    return TweetTermsHandler.class;
  }
}
