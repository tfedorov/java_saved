package com.tfedorov.social.intention.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tfedorov.social.intention.Purchase;

@Repository("intention2LevelDaoImpl")
public class Intention2LevelDaoImpl implements Intention2LevelDao {

  private static final String INSERT_ALL_STATEMENT =
      "INSERT INTO level_2_lexicon (`primaryTest`,`secondaryTest`,`categoryLevel1`) VALUES(?,?,?)";

  private static final String SELECT_ALL_STATEMENT = "SELECT "
      + "`id`, `primaryTest`, `secondaryTest`, `categoryLevel1`" + " FROM `level_2_lexicon`";

  @Autowired
  private DataSource dataSource;

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<Purchase> getLevel2Lexicons() {
    JdbcTemplate template = new JdbcTemplate(dataSource);

    List<Purchase> list =
        template.query(SELECT_ALL_STATEMENT, new Object[] {}, new Intention2LevelRowMapper());

    return list;
  }

  @Override
  public int[] insertLevel2Lexicons(final List<Purchase> intentLexicons) {
    JdbcTemplate insert = new JdbcTemplate(dataSource);

    return insert.batchUpdate(INSERT_ALL_STATEMENT, new BatchPreparedStatementSetter() {
      private List<Purchase> intent = intentLexicons;
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (intent.get(i).getPrimaryTest() != null) {
          ps.setString(1, intent.get(i).getPrimaryTest());
        }
        if (intent.get(i).getSecondaryTest() != null) {
          ps.setString(2, intent.get(i).getSecondaryTest());
        }
        if (intent.get(i).getCategoryLevel1() != null) {
          ps.setString(3, intent.get(i).getCategoryLevel1());
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
        template.query("select lang from level_2_lexicon group by lang", new RowMapper<String>() {

          @Override
          public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("lang");
          }
        });

    return list;
  }
}
