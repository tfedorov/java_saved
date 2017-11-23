package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;

import com.tfedorov.social.utils.JsonDateSerializer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.base.BaseDateTime;

public class TopicMentionAggregate {

  @JsonProperty(value = "id")
  private BigInteger topicId;

  @JsonProperty(value = "amount")
  private long tweetsCount;

  @JsonProperty(value = "date")
  private BaseDateTime date;

  @JsonProperty(value = "positive")
  private int positiveMentions;

  @JsonProperty(value = "negative")
  private int negativeMentions;

  public TopicMentionAggregate(BigInteger topicId, BaseDateTime date, long tweetsCount) {
    this.topicId = topicId;
    this.tweetsCount = tweetsCount;
    this.date = date;
  }

  public TopicMentionAggregate(BigInteger topicId, BaseDateTime date, long tweetsCount, int positiveMentions, int negativeMentions) {
    this.topicId = topicId;
    this.tweetsCount = tweetsCount;
    this.date = date;
    this.positiveMentions = positiveMentions;
    this.negativeMentions = negativeMentions;
  }

  @JsonProperty(value = "topic")
  public Long getTopic() {
    return 6l;
  }

  @JsonSerialize(using = JsonDateSerializer.class)
  public BaseDateTime getDate() {
    return date;
  }

  public void setDate(BaseDateTime date) {
    this.date = date;
  }

  public BigInteger getTopicId() {
    return topicId;
  }

  public void setTopicId(BigInteger topicId) {
    this.topicId = topicId;
  }

  public long getTweetsCount() {
    return tweetsCount;
  }

  public void setTweetsCount(long tweetsCount) {
    this.tweetsCount = tweetsCount;
  }

  public int getPositiveMentions() {
    return positiveMentions;
  }

  public void setPositiveMentions(int positiveMentions) {
    this.positiveMentions = positiveMentions;
  }

  public int getNegativeMentions() {
    return negativeMentions;
  }

  public void setNegativeMentions(int negativeMentions) {
    this.negativeMentions = negativeMentions;
  }

  @Override
  public String toString() {
    return new StringBuilder("TopicMentionAggregate [topicId=").append(topicId)
        .append(", tweetsCount=").append(tweetsCount).append(", date=")
        .append(date + "]").toString();
  }

}
