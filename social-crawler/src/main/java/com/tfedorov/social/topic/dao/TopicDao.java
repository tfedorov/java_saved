package com.tfedorov.social.topic.dao;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;

public interface TopicDao {
  /**
   * Get all topics with status > 0
   * 
   * @return all topics with status > 0
   */
  List<Topic> getTrackedTopics();

  /**
   * Update status for topic
   * 
   * @param topicId
   * @param status
   * @return row affected
   */
  int updateStatus(BigInteger topicId, int status);

  void insertTopic(Topic topic);

  Topic getTopicById(BigInteger topicId);

  int deleteTopicById(BigInteger id);

  void updateTopic(Topic topic);

  List<Topic> getByUserId(String userId);

  List<Topic> getByUserIdSorted(String userId, TopicType type, int offset, int limit, String orderBy, boolean isDesc);

  void markAsDelete(BigInteger topicId);

  List<Topic> getTopicTypeSorted(TopicType type, int offset, int limit, String orderBy,
      boolean isDesc);

}
