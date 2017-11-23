package com.tfedorov.social.word.processing;

import java.util.HashSet;
import java.util.Set;

public class WordsInfo {
	
	private Set<String> stopWords;
	
	private Set<String> blackWords;
	

	public WordsInfo() {
		stopWords = new HashSet<String>();
		blackWords = new HashSet<String>();
	}
	
	public WordsInfo(Set<String> stopWords, Set<String> blackWords) {
		this.stopWords = stopWords;
		this.blackWords = blackWords;
	}
	
	public Set<String> getStopWords() {
		return stopWords;
	}


	public Set<String> getBlackWords() {
		return blackWords;
	}

	
	@Override
	public String toString() {
		return new StringBuilder("WORDS_INFO{stop words:").append(stopWords)
				.append("\nblack words:").append(blackWords).append("}").toString();
	}

}
