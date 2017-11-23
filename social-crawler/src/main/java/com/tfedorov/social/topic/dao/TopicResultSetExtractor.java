package com.tfedorov.social.topic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfedorov.social.utils.date.DateUtils;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;

public class TopicResultSetExtractor implements ResultSetExtractor<Topic> {

  // id, name, keywords, status, ctime, mtime, cuser, muser, company, type

  @Override
  public Topic extractData(ResultSet rs) throws SQLException {
    Topic topic = new Topic();
    topic.setId(rs.getBigDecimal("id").toBigInteger());
    topic.setName(rs.getString("name"));
    topic.setKeywords(rs.getString("keywords"));
    topic.setStatus(rs.getInt("status"));
    topic.setCreated(DateUtils.convertToDateTime(rs.getTimestamp("ctime")));
    topic.setModified(DateUtils.convertToDateTime(rs.getTimestamp("mtime")));
    topic.setCreateUserName(rs.getString("cuser"));
    topic.setModifydUserName(rs.getString("muser"));
    topic.setCompany(rs.getString("company"));
    topic.setType(TopicType.valueOf(rs.getString("type")));
    return topic;
  }
}
