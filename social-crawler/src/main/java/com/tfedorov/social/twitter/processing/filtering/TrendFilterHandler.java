package com.tfedorov.social.twitter.processing.filtering;

import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TrendFilterHandler
    extends AbstractConditionalProcessingHandler<GeneralProcessingContext> {

  public TrendFilterHandler(ProcessingHandler<GeneralProcessingContext> successorTrue,
      ProcessingHandler<GeneralProcessingContext> successorFalse) {
    super(successorTrue, successorFalse);
  }

  @Override
  protected boolean processImpl(GeneralProcessingContext context) {
    return TopicType.TRENDS == context.getTopicContext().getTopicInfo().getTopic().getType();
  }

  @Override
  public Class<?> getClazz() {
    return TrendFilterHandler.class;
  }
}
