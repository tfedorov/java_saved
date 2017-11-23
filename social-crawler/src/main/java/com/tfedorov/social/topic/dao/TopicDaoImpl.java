package com.tfedorov.social.topic.dao;

import java.math.BigInteger;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Repository("topicDao")
public class TopicDaoImpl implements TopicDao {

  public static final String SELECT_ALL_TRACKED_STATEMENT = "SELECT "
      + " id, name, keywords, status,  ctime, mtime, cuser, muser, company, type"
      + " FROM topic where status >= 0 ";

  public static final String INSERT_STATEMENT =
      "INSERT INTO topic (name,keywords,status,cuser,company,ctime,mtime) VALUES (?,?,?,?,?,now(), now())";

  public static final String SELECT_BY_ID_STATEMENT =
      "SELECT id, name, keywords, status, ctime, mtime, cuser, muser, company, type FROM topic where id = ?";

  public static final String SELECT_TRACKED_BY_USER_ID_STATEMENT =
      "SELECT id, name, keywords, status, ctime, mtime, cuser, muser, company, type FROM topic where cuser=? and status>=0";

  public static final String SELECT_ALL_BY_TYPE_STATEMENT =
      "SELECT id, name, keywords, status, ctime, mtime, cuser, muser, company, type FROM topic where type=? and status>=0 order by ";

  public static final String SELECT_TRACKED_FILTERED_BY_USER_ID_STATEMENT =
      "SELECT id, name, keywords, status, ctime, mtime, cuser, muser, company, type FROM topic where cuser=? and type=? and status>=0 order by ";

  public static final String UPDATE_TOPIC_STATEMENT =
      "UPDATE topic SET name = ?, keywords = ?, muser = ?, mtime = now() WHERE id = ?";

  public static final String UPDATE_MARK_DELETED_TOPIC_STATEMENT = "UPDATE topic SET status="
      + Topic.STATUS_DELETED + " WHERE id = ?";

  public static final String UPDATE_TOPIC_STATUS_STATEMENT =
      "UPDATE topic SET status = ? , muser='<crawler>', mtime = now() WHERE id = ? and status <> ?";

  @Autowired
  private DataSource dataSource;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<Topic> getTrackedTopics() {

    EtmPoint perfPoint = getPerformancePoint(".getTrackedTopics()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);

      List<Topic> list =
          template.query(SELECT_ALL_TRACKED_STATEMENT, new Object[] {}, new TopicRowMapper());
      return list;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int updateStatus(BigInteger topicId, int status) {

    EtmPoint perfPoint = getPerformancePoint(".updateStatus()");
    try {

      JdbcTemplate update = new JdbcTemplate(dataSource);

      return update.update(UPDATE_TOPIC_STATUS_STATEMENT, new Object[] {status, topicId, status});

    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void insertTopic(Topic topic) {
    EtmPoint perfPoint = getPerformancePoint(".insertTopic()");
    try {
      JdbcTemplate insert = new JdbcTemplate(dataSource);
      insert.update(
          INSERT_STATEMENT,
          new Object[] {topic.getName(), topic.getKeywords(), topic.getStatus(),
              topic.getCreateUserName(), topic.getCompany()});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public Topic getTopicById(BigInteger topicId) {
    EtmPoint perfPoint = getPerformancePoint(".getTopicById()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      Topic topic =
          template.queryForObject(SELECT_BY_ID_STATEMENT, new Object[] {topicId},
              new TopicRowMapper());
      return topic;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int deleteTopicById(BigInteger topicId) {
    EtmPoint perfPoint = getPerformancePoint(".deleteTopicById()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      String sqlQuery = "DELETE FROM topic WHERE id = ?;";
      return template.update(sqlQuery, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void updateTopic(Topic topic) {
    EtmPoint perfPoint = getPerformancePoint(".updateTopic()");
    try {
      JdbcTemplate update = new JdbcTemplate(dataSource);
      update.update(UPDATE_TOPIC_STATEMENT, new Object[] {topic.getName(), topic.getKeywords(),
          topic.getModifydUserName(), topic.getId()});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<Topic> getByUserId(String userId) {
    EtmPoint perfPoint = getPerformancePoint(".getByUserId()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      List<Topic> list =
          template.query(SELECT_TRACKED_BY_USER_ID_STATEMENT, new Object[] {userId},
              new TopicRowMapper());
      return list;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<Topic> getTopicTypeSorted(TopicType type, int offset, int limit, String orderBy,
      boolean isDesc) {
    EtmPoint perfPoint = getPerformancePoint(".getTopicTypeSorted()");
    try {
      StringBuilder statement = new StringBuilder(SELECT_ALL_BY_TYPE_STATEMENT);
      statement.append(TopicSortingFields.valueOf(orderBy).getField());
      statement.append(" ");
      if (isDesc) {
        statement.append("desc");
      } else {
        statement.append("asc");
      }
      if (limit >= 0 & offset >= 0) {
        statement.append(" limit " + limit);
        statement.append(" offset " + offset);
      }
      JdbcTemplate template = new JdbcTemplate(dataSource);
      List<Topic> list =
          template.query(statement.toString(), new Object[] {type.name()}, new TopicRowMapper());
      return list;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<Topic> getByUserIdSorted(String userId, TopicType type, int offset, int limit,
      String orderBy, boolean isDesc) {

    EtmPoint perfPoint = getPerformancePoint(".getByUserIdSorted()");
    try {
      StringBuilder statement = new StringBuilder(SELECT_TRACKED_FILTERED_BY_USER_ID_STATEMENT);
      statement.append(TopicSortingFields.valueOf(orderBy).getField());
      statement.append(" ");
      if (isDesc) {
        statement.append("desc");
      } else {
        statement.append("asc");
      }
      if (limit >= 0 & offset >= 0) {
        statement.append(" limit " + limit);
        statement.append(" offset " + offset);
      }
      JdbcTemplate template = new JdbcTemplate(dataSource);
      List<Topic> list =
          template.query(statement.toString(), new Object[] {userId, type.name()},
              new TopicRowMapper());
      return list;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void markAsDelete(BigInteger topicId) {
    EtmPoint perfPoint = getPerformancePoint(".markAsDelete()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      template.update(UPDATE_MARK_DELETED_TOPIC_STATEMENT, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }

  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TopicDaoImpl.class.toString()).append(
        name).toString());
  }
}
