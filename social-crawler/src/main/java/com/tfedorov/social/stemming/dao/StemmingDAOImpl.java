package com.tfedorov.social.stemming.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.tfedorov.social.normalization.stemming.StemmingResult;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Repository("stemmingDAO")
public class StemmingDAOImpl implements StemmingDAO {

	private static final String DELETE_USELESS = "delete from stemming where id IN ( select id  from  ( " + 
	"select id from stemming  " + 
	"join (select stemmed_word from stemming where stemmed_word = word and lang = ? and insert_date between ? and ?) as similar  " + 
	"on similar.stemmed_word = stemming.stemmed_word " + 
	"where (select count(id) from stemming where stemmed_word = similar.stemmed_word and lang = ?) <= 1 limit 1000" + 
	") as subSelect);"; 

	  private static final String SELECT_BY_WORD =
	      "select word, stemmed_word , modification_date, lang from stemming where word = ? ";

  @Autowired
  private DataSource dataSource;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public int[] addNewStemmedWord(final Map<StemmingResult, Integer> stemmingResult) {
    final List<StemmingResult> keys = new ArrayList<StemmingResult>(stemmingResult.keySet());
    EtmPoint perfPoint = getPerformancePoint(".addNewStemmedWord()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template
          .batchUpdate(
              "insert into stemming(word,stemmed_word,lang,count,modification_date) values(left(?,50),left(?,50),?,?,now())",
              new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                  StemmingResult sr = keys.get(i);
                  ps.setString(1, sr.getWord());
                  ps.setString(2, sr.getStemmedWord());
                  ps.setString(3, sr.getLanguage());
                  ps.setInt(4, stemmingResult.get(sr));
                }

                @Override
                public int getBatchSize() {
                  return keys.size();
                }
              });
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int[] updateStemmedWord(final StemmingResult[] stemmingResult) {
    EtmPoint perfPoint = getPerformancePoint(".updateStemmedWord()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template
          .batchUpdate(
              "update stemming set count=count+1, modification_date = now() where word=? and stemmed_word=? and lang=?",
              new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                  StemmingResult sr = stemmingResult[i];
                  ps.setString(1, sr.getWord());
                  ps.setString(2, sr.getStemmedWord());
                  ps.setString(3, sr.getLanguage());
                }

                @Override
                public int getBatchSize() {
                  return stemmingResult.length;
                }
              });
    } finally {
      perfPoint.collect();
    }
  }

  @Deprecated
  @Override
  public String loadTopWordByStemmed(String stemmedWord) {
    EtmPoint perfPoint = getPerformancePoint(".loadTopWordByStemmed()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template
          .queryForObject(
              "select t1.word, t1.stemmed_word from stemming as t1 left join stemming as t2 on t1.stemmed_word=t2.stemmed_word and t1.count<t2.count where t1.stemmed_word = ? and t2.count is null",
              new Object[] {stemmedWord}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                  return rs.getString("word");
                }
              });
    } finally {
      perfPoint.collect();
    }
  }

  @Deprecated
  @Override
  public Map<String, String> loadTopWordsListByStemmed(List<String> stemmedWordsList, final String language) {
    EtmPoint perfPoint = getPerformancePoint(".loadTopWordsListByStemmed()");
    try {
      NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("wordList", stemmedWordsList);
      map.put("lang", language);

      return template
          .query(
              "select t1.word, t1.stemmed_word from stemming as t1 left join stemming as t2 on t1.stemmed_word=t2.stemmed_word and t1.count<t2.count where t1.lang = :lang AND t1.stemmed_word in (:wordList) AND t2.count is null",
              map, new ResultSetExtractor<Map<String, String>>() {
                @Override
                public Map<String, String> extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                  Map<String, String> result = new HashMap<String, String>();
                  while (rs.next()) {
                    result.put(rs.getString("stemmed_word"), rs.getString("word"));
                  }
                  return result;
                }
              });
    } finally {
      perfPoint.collect();
    }
  }
  
  @Override
	public List<StemmingBean> loadMapByTopStemmedList(List<String> stemmedWordsList, final String language) {
		EtmPoint perfPoint = getPerformancePoint(".loadTopWordsListByStemmed()");
		try {
			NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("wordList", stemmedWordsList);
			map.put("lang", language);

			return template.query("select stemmed_word, word, count from stemming where lang = :lang AND stemmed_word in (:wordList)", map,
					new ResultSetExtractor<List<StemmingBean>>() {

						@Override
						public List<StemmingBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
							List<StemmingBean> result = new ArrayList<StemmingBean>(2500);
							while (rs.next()) {
								result.add(new StemmingBean(rs.getString("stemmed_word"), rs.getString("word"), rs.getInt("count")));
							}
							return result;
						}

					});
		} finally {
			perfPoint.collect();
		}
	}

  @Override
  public List<StemmingResult> getStemmingByWords(String originalForm) {
    EtmPoint perfPoint = getPerformancePoint(".getStemmingByWords()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.query(SELECT_BY_WORD, new Object[] {originalForm},
          new RowMapper<StemmingResult>() {

            @Override
            public StemmingResult mapRow(ResultSet rs, int rowNum) throws SQLException {

              return new StemmingResult(rs.getString("word"), rs.getString("stemmed_word"), rs
                  .getString("lang"), DateUtils.convertToDateTime(rs
                  .getTimestamp("modification_date")));
            }
          });
    } finally {
      perfPoint.collect();
    }
  }

  public int cleanUselessSteamLimited(DateTime from, DateTime to, String lang) {

    EtmPoint perfPoint = getPerformancePoint(".cleanUselessSteamLimited()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(DELETE_USELESS, new Object[] {lang, DateUtils.convertToTimeStamp(from),
              DateUtils.convertToTimeStamp(to), lang});
    } finally {
      perfPoint.collect();
    }
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(StemmingDAOImpl.class.toString())
        .append(name).toString());
  }

	@Override
	public List<String> getStemmingLangsByCount() {
		EtmPoint perfPoint = getPerformancePoint(".getStemmingLangsByCount()");
		try {
		      JdbcTemplate template = new JdbcTemplate(dataSource);
		      return template.query("select lang, COUNT(id) as lang_freq from stemming group by lang order by lang_freq desc",
		          new RowMapper<String>() {

		            @Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {

		              return rs.getString("Lang");
		            }
		          });
		} finally {
			perfPoint.collect();
		}
	}

}
