package com.tfedorov.social.twitter.sentiments.strategy;

import java.util.List;

import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;

public interface SentimentDAO {
	@Deprecated
	List<String> loadNegativeSentimentsList();

	@Deprecated
	List<String> loadPositiveSentimentsList();

	List<String> loadSupportedLanguagesList();

	List<SentimentLexicon> getSentimentLexicons();

}
