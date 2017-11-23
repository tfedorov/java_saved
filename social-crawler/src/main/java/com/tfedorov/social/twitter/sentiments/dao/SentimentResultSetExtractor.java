/**
 * 
 */
package com.tfedorov.social.twitter.sentiments.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author tfedorov
 *
 */
public class SentimentResultSetExtractor implements ResultSetExtractor<SentimentLexicon> {
	@Override
	public SentimentLexicon extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		boolean isPositiv = "p".equalsIgnoreCase(rs.getString("s_type"));
		SentimentLexicon resultSentiment = new SentimentLexicon(
				rs.getLong("id"), rs.getString("pattern"),
				rs.getString("s_type"), isPositiv);

		return resultSentiment;
	}
}
