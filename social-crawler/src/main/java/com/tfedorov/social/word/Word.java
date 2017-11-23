package com.tfedorov.social.word;

public class Word {
	
	private String word;
	private String description;
	private WORD_TYPE type;
	

	
	public Word(String word, String description, WORD_TYPE type) {
		super();
		this.word = word;
		this.description = description;
		this.type = type;
	}

	
	public WORD_TYPE getType() {
		return type;
	}


	public void setType(WORD_TYPE type) {
		this.type = type;
	}


	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static enum WORD_TYPE {
		stop_words, black_words
        }
}
