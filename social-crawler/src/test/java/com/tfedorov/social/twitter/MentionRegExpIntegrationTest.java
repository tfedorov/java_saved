package com.tfedorov.social.twitter;

import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MentionRegExpIntegrationTest
{
	@Test
	public void testRemoveMentions()
	{

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
		dataSource.setUsername("dmp");
		dataSource.setPassword("dmp01");

		TweetsAggregationDaoImpl tweetDao = new TweetsAggregationDaoImpl();
		tweetDao.setDataSource(dataSource);

//...

	}
}
