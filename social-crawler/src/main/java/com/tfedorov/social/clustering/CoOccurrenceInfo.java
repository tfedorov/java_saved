package com.tfedorov.social.clustering;

/**
 * This class is container of the pairs of the words with co-occurrences count
 * them.
 * 
 * 
 */
public class CoOccurrenceInfo {

	private String firstWord;
	
	private String secondWord;
	
	private long countConnections;

	public CoOccurrenceInfo(String firstWord, String secondWord, long countConnections) {
		super();
		this.firstWord = firstWord;
		this.secondWord = secondWord;
		this.countConnections = countConnections;
	}

	public String getFirstWord() {
		return firstWord;
	}

	public String getSecondWord() {
		return secondWord;
	}

	public long getCountLinks() {
		return countConnections;
	}
	
	
}
