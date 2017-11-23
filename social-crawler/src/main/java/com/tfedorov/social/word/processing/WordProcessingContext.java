package com.tfedorov.social.word.processing;

import com.tfedorov.social.processing.ProcessingContextImpl;

public class WordProcessingContext extends ProcessingContextImpl {

	public static final String WORDS_CONTEXT = "words_context";
	
	protected static final String WORDS_INFO = "word_info"; 
	
	public WordProcessingContext(WordsInfo wordsInfo) {
		super();
		add(WORDS_INFO, wordsInfo);
	}
	
	
	public WordsInfo getWordsInfo() {
		return (WordsInfo)get(WORDS_INFO);
	}


	@Override
	public String getContextName() {
		// TODO Auto-generated method stub
		return WORDS_CONTEXT;
	}

}
