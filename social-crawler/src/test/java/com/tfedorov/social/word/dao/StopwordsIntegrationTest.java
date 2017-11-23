package com.tfedorov.social.word.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.word.Word;

public class StopwordsIntegrationTest {

	WordsDaoImpl stopwordsDao;

	@Test
	public void testStopwordsDao() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
		dataSource.setUsername("dmp");
		dataSource.setPassword("dmp01");

		stopwordsDao = new WordsDaoImpl();
		stopwordsDao.setDataSource(dataSource);

		List<Word> stopwords = stopwordsDao
				.selectWords(Word.WORD_TYPE.black_words);

		for (Word sw : stopwords) {
			System.out.println(sw.getWord());
		}
	}
}
