package com.tfedorov.social.twitter.service;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.topic.processing.TopicInfo;

public interface TopicService {

  void asyncTopicUpdate(Topic updatedTopic);

  TopicInfo createTopicInfo(Topic topic);

  List<Topic> getByUserIdSorted(String userId, int offset, int limit, String orderBy, boolean isDesc);

  void insertTopic(Topic topic);

  Topic getTopicById(BigInteger topicId);

  List<Topic> getTypeTopics(TopicType type, int offset, int limit, String orderBy, boolean isDesc);

  void markTopicAsDeleted(Topic topicToDelete);

  void asyncTopicAggregationClean(Topic topicToDelete);
}
