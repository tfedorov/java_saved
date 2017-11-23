package com.tfedorov.social.word.dao;

import java.util.List;

import com.tfedorov.social.word.Word;
import com.tfedorov.social.word.Word.WORD_TYPE;

public interface WordsDao {


	public List<Word> selectWords(WORD_TYPE type);
	
	public List<String> selectWordStrings(WORD_TYPE type);
}
