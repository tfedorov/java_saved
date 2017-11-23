package com.tfedorov.social.topic.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.topic.Topic;

public class TopicDaoIntegrationTest {

  private static final String COMPANY = "1";

  private static TopicDaoImpl topicDao;

  private static DriverManagerDataSource dataSource;

  @BeforeClass
  public static void setUp() {
    dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");

    topicDao = new TopicDaoImpl();
    topicDao.setDataSource(dataSource);

    System.out.println("datasource initiated...");

  }


  @Test
  public void testInsertDeleteTopic() {

    final Topic defaultTopic = createDefaultTopic();

    // insert
    topicDao.insertTopic(defaultTopic);

    // check if inserted
    Topic inserted = getTopicFromTracked(defaultTopic);
    
    assertNotNull("Topic not found after insert", inserted);
    assertEquals(Topic.STATUS_NEW, inserted.getStatus());
    assertEquals(COMPANY, inserted.getCompany());

    BigInteger insertedId = inserted.getId();

    //Check insert by id
    Topic getById = topicDao.getTopicById(insertedId);

    assertNotNull("Topic not found by id", getById);
    assertEquals(insertedId, getById.getId());

    // delete inserted
    topicDao.deleteTopicById(inserted.getId());

    // check if deleted
    inserted = getTopicFromTracked(defaultTopic);

    assertNull("Topic found after delete", inserted);

  }

  @Test
  public void testUpdateTopic() {
    final Topic defaultTopic = createDefaultTopic();

    // insert
    topicDao.insertTopic(defaultTopic);

    // check if inserted
    Topic inserted = getTopicFromTracked(defaultTopic);
    inserted.setName("changedName");
    inserted.setKeywords("changedKeywords");
    
    topicDao.updateTopic(inserted);
    
    Topic topicAfterUpdate = topicDao.getTopicById(inserted.getId());
    assertNotNull("Topic not found by id", topicAfterUpdate);

    assertEquals("changedName", topicAfterUpdate.getName());
    assertEquals("changedKeywords", topicAfterUpdate.getKeywords());
    
  }

  private Topic getTopicFromTracked(final Topic defaultTopic) {
    List<Topic> topics = topicDao.getTrackedTopics();
    Topic inserted = null;
    for (Topic lTopic : topics) {
      if (lTopic.getName().equalsIgnoreCase(defaultTopic.getName())) {
        inserted = lTopic;
      }
    }
    return inserted;
  }

  private Topic createDefaultTopic() {
    String topicName = System.currentTimeMillis() + "_topicName";

    Topic topic = new Topic();
    topic.setKeywords("keyword1");
    topic.setName(topicName);
    topic.setStatus(Topic.STATUS_NEW);
    topic.setCompany(COMPANY);
    return topic;
  }

}
