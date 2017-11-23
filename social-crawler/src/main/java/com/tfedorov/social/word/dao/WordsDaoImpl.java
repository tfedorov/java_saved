package com.tfedorov.social.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tfedorov.social.word.Word;
import com.tfedorov.social.word.Word.WORD_TYPE;

@Repository("wordsDao")
public class WordsDaoImpl implements WordsDao {

	@Autowired
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	@Override
	public List<Word> selectWords(final WORD_TYPE type) {

		List<Word> words = new ArrayList<Word>();

		String sqlQuery = new StringBuilder("SELECT word, description FROM ")
								.append(type.name()).append(" ;").toString();
		JdbcTemplate template = new JdbcTemplate(dataSource);

		words = template.query(sqlQuery, new Object[] {},
				new RowMapper<Word>() {

			@Override
			public Word mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				Word wordToReturn = new Word(rs.getString("word"), rs
						.getString("description"), type);
				return wordToReturn;
			}

		});

		return words;
	}

	@Override
	public List<String> selectWordStrings(WORD_TYPE type) {
		List<String> words = new ArrayList<String>();

		String sqlQuery = new StringBuilder("SELECT word FROM ")
							.append(type.name()).append(" ;").toString();
		
		JdbcTemplate template = new JdbcTemplate(dataSource);

		words = template.query(sqlQuery, new Object[] {},
				new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				return rs.getString("word");
			}

		});

		return words;
	}
}
