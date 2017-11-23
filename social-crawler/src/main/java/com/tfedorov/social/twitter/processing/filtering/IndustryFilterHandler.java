package com.tfedorov.social.twitter.processing.filtering;

import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class IndustryFilterHandler
    extends AbstractConditionalProcessingHandler<GeneralProcessingContext> {

  public IndustryFilterHandler(ProcessingHandler<GeneralProcessingContext> successorTrue,
      ProcessingHandler<GeneralProcessingContext> successorFalse) {
    super(successorTrue, successorFalse);
  }

  @Override
  protected boolean processImpl(GeneralProcessingContext context) {
    return TopicType.INDUSTRY == context.getTopicContext().getTopicInfo().getTopic().getType();
  }

  @Override
  public Class<?> getClazz() {
    return IndustryFilterHandler.class;
  }

}
