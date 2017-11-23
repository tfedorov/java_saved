package com.tfedorov.social.twitter.aggregation;

import com.tfedorov.social.topic.Topic;
import org.json.JSONObject;

import twitter4j.Status;

public interface TweetsAggregationService {

  public void init();

  public void processStatus(Status status);

  public void reloadTopics();

  public void reloadWords();

  public void updateTopics();

  public int getTopicsNumber();

  public JSONObject getChainStructure();

  public void deleteTopic(Topic topic);

}
