package com.tfedorov.social.twitter.processing.terms;

import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TweetTrendTermsHandler extends TweetTermAggregationHandler {

  public TweetTrendTermsHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  public Class<?> getClazz() {
    return TweetTrendTermsHandler.class;
  }

  @Override
  protected boolean processTriTerms() {
    return false;
  }
}
