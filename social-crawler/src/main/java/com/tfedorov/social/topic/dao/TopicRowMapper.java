package com.tfedorov.social.topic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfedorov.social.topic.Topic;
import org.springframework.jdbc.core.RowMapper;

public class TopicRowMapper implements RowMapper<Topic>{

	public Topic mapRow(ResultSet resultset, int i) throws SQLException {
	    TopicResultSetExtractor extractor = new TopicResultSetExtractor ();
	    return extractor.extractData(resultset);
	}
}
