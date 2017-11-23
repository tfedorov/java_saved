package com.tfedorov.social.topic.processing;

import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.processing.AbstractIterableProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextImpl;

public class TopicIterationHandler extends AbstractIterableProcessingHandler<TopicProcessingContext, GeneralProcessingContext> {

	public TopicIterationHandler(ProcessingHandler<GeneralProcessingContext> repeatedSucessor,
			ProcessingHandler<GeneralProcessingContext> exitSuccessor) {
		super(repeatedSucessor, exitSuccessor);
	}

	@Override
	protected List<TopicProcessingContext> getContextsList(GeneralProcessingContext generalContext){
		
		//TODO: revisit code to prepare contextslist once after topics update
		List<TopicInfo> list = (List<TopicInfo>)generalContext.get(GeneralProcessingContextImpl.TOPICS_INFO_LIST);
		
		List<TopicProcessingContext> contextsList = new ArrayList<TopicProcessingContext>(list.size());
		
		for (TopicInfo topicInfo: list) {
			contextsList.add(new TopicProcessingContext(topicInfo));
		}
		
		return contextsList;
	}

	@Override
	public Class getClazz() {
		return TopicIterationHandler.class;
	}

}
