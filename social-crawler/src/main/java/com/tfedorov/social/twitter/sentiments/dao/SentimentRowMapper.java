/**
 * 
 */
package com.tfedorov.social.twitter.sentiments.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author tfedorov
 *
 */
public class SentimentRowMapper implements RowMapper<SentimentLexicon> {

	@Override
	public SentimentLexicon mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		SentimentResultSetExtractor extractor = new SentimentResultSetExtractor();
		return extractor.extractData(rs);
	}

}
