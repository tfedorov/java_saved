package com.tfedorov.social.topic.processing;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TopicPrintHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

	private Logger logger = LoggerFactory.getLogger(TopicPrintHandler.class); 
	
	public TopicPrintHandler(ProcessingHandler<GeneralProcessingContext> successor) {
		super(successor);
	}

	@Override
	protected void processImpl(GeneralProcessingContext context) {

		TopicProcessingContext tpContext = context.getTopicContext();

		logger.info(tpContext.getTopicInfo().getTopic().toString());
	}

	@Override
	public Class getClazz() {
		return TopicPrintHandler.class;
	}

}
