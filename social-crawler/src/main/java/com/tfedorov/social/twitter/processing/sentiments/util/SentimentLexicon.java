/**
 * 
 */
package com.tfedorov.social.twitter.processing.sentiments.util;

/**
 * @author tfedorov
 * 
 */
public class SentimentLexicon {

	private long id;
	private String searchTerm;

	private String lang;
	private boolean positiv;

	public SentimentLexicon(long id, String searchTerm, String lang,
			boolean positiv) {
		this.id = id;
		this.searchTerm = searchTerm;
		this.lang = lang;
		this.positiv = positiv;
	}

	public long getId() {
		return id;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String getLang() {
		return lang;
	}

	public boolean isPositiv() {
		return positiv;
	}

}
