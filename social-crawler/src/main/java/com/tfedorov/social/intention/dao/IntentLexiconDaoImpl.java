package com.tfedorov.social.intention.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.tfedorov.social.intention.IntentLexicon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("intentLexiconDaoImpl")
public class IntentLexiconDaoImpl implements IntentLexiconDao {

  private static final String INSERT_ALL_STATEMENT =
      "INSERT INTO lexicon (`term`,`qualification`) VALUES(?,?)";

  public static final String  SELECT_ALL_STATEMENT = "SELECT " + "`id`, `term`, `qualification`"
      + " FROM `lexicon`";

  @Autowired
  private DataSource dataSource;

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<IntentLexicon> getIntentLexicons() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<IntentLexicon> list =
        template.query(SELECT_ALL_STATEMENT, new Object[] {}, new IntentLexiconRowMapper());

    return list;
  }

  @Override
  public int[] insertIntentLexicons(final List<IntentLexicon> intentLexicons) {
    JdbcTemplate insert = new JdbcTemplate(dataSource);

    return insert.batchUpdate(INSERT_ALL_STATEMENT, new BatchPreparedStatementSetter() {
      private List<IntentLexicon> intent = intentLexicons;

      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {

        if (intent.get(i).getSearchTerm() != null) {
          ps.setString(1, intent.get(i).getSearchTerm());
        }
        if (intent.get(i).getQualificationsAsString() != null) {
          ps.setString(2, intent.get(i).getQualificationsAsString());
        }
      }

      @Override
      public int getBatchSize() {
        return intentLexicons.size();
      }
    });

  }

  @Override
  public List<String> getLexiconLanguages() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<String> list =
        template.query("select lang from lexicon group by lang", new RowMapper<String>() {

          @Override
          public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("lang");
          }
        });

    return list;
  }
}
