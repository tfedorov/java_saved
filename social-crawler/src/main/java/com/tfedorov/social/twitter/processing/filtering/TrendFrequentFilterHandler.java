package com.tfedorov.social.twitter.processing.filtering;

import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TrendFrequentFilterHandler
    extends AbstractConditionalProcessingHandler<GeneralProcessingContext> {

  public TrendFrequentFilterHandler(ProcessingHandler<GeneralProcessingContext> successorTrue,
      ProcessingHandler<GeneralProcessingContext> successorFalse) {
    super(successorTrue, successorFalse);
  }

  @Override
  protected boolean processImpl(GeneralProcessingContext context) {
    return context.getServicesContext().getTweetTracingService().canProcessTrend();
  }

  @Override
  public Class<?> getClazz() {
    return TrendFrequentFilterHandler.class;
  }
}
