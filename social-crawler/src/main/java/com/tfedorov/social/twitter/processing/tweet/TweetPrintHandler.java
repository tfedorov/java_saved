package com.tfedorov.social.twitter.processing.tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TweetPrintHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

	private Logger logger = LoggerFactory.getLogger(TweetPrintHandler.class); 
	
	public TweetPrintHandler(ProcessingHandler<GeneralProcessingContext> successor) {
		super(successor);
	}

	@Override
	protected void processImpl(GeneralProcessingContext context) {
		TweetProcessingContext twContext = context.getTweetContext();

		logger.info(twContext.getTweetInfo().getTweet().toString());
	}

	@Override
	public Class getClazz() {
		return TweetPrintHandler.class;
	}

}
