package com.tfedorov.social.twitter.sentiments.strategy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;
import com.tfedorov.social.twitter.sentiments.dao.SentimentRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SentimentDAOImpl implements SentimentDAO {
	
	/*Used in simpleSeniment strategy*/
  @Deprecated
  private static final String GET_NEGATIVE_SENTIMENTS =
      "select pattern from sentiments where s_type='n'";
  /*Used in simpleSeniment strategy*/
  @Deprecated
  private static final String GET_POSITIVE_SENTIMENTS =
      "select pattern from sentiments where s_type='p'";
  /*Used in simpleSeniment strategy*/
  @Deprecated
  private static final String GET_LANGUAGES = "select lang from sentiments group by lang";

  private static final String SELECT_ALL_STATEMENT = "SELECT id, pattern, s_type, lang FROM sentiments";
  
  @Autowired
  private DataSource dataSource;

  /*Used in simpleSeniment strategy*/
  @Deprecated
  @Override
  public List<String> loadNegativeSentimentsList() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<String> list = template.query(GET_NEGATIVE_SENTIMENTS, new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("pattern");
      }
    });

    return list;
  }

  @Override
  public List<String> loadPositiveSentimentsList() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<String> list = template.query(GET_POSITIVE_SENTIMENTS, new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("pattern");
      }
    });

    return list;
  }

  /*Used in simpleSeniment strategy*/
  @Deprecated
  @Override
  public List<String> loadSupportedLanguagesList() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<String> list = template.query(GET_LANGUAGES, new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("lang");
      }
    });

    return list;
  }

	@Override
	public List<SentimentLexicon> getSentimentLexicons() {
	    JdbcTemplate template = new JdbcTemplate(dataSource);

	    List<SentimentLexicon> list = template.query(SELECT_ALL_STATEMENT, new Object[] {}, new SentimentRowMapper());

	    return list;
	}

}
