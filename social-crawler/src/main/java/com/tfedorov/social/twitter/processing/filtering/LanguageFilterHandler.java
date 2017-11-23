package com.tfedorov.social.twitter.processing.filtering;

import java.util.Set;

import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextImpl;

public class LanguageFilterHandler extends AbstractConditionalProcessingHandler<GeneralProcessingContext> {

	public LanguageFilterHandler(ProcessingHandler<GeneralProcessingContext> successorTrue,
			ProcessingHandler<GeneralProcessingContext> successorFalse) {
		super(successorTrue, successorFalse);
	}

	@Override
	protected boolean processImpl(GeneralProcessingContext context) {
		
		Set<String> languagesSupported = (Set<String>) context.get(GeneralProcessingContextImpl.LANGUAGES_SUPPORTED);
		String language = context.getTweetContext().getTweetInfo().getTweetTextLang().toLowerCase();
		
		return languagesSupported.contains(language);
	}

	@Override
	public Class<?> getClazz() {
		return LanguageFilterHandler.class;
	}
}
