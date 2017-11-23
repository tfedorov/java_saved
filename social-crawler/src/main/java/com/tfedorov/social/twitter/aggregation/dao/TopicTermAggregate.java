package com.tfedorov.social.twitter.aggregation.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.base.BaseDateTime;

public class TopicTermAggregate implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -9195260928276293134L;

  private BigInteger topicId;

  private String term;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private long termsCount;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private BaseDateTime date;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private AGGREGATE_TYPE type;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "tw1Id")
  private BigDecimal tw1Id;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "tw2Id")
  private BigDecimal tw2Id;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "tw3Id")
  private BigDecimal tw3Id;

  public TopicTermAggregate(BigInteger topicId, String term, BigDecimal tw1Id,
      BigDecimal tw2Id, BigDecimal tw3Id) {
    this.topicId = topicId;
    this.term = term;
    this.tw1Id = tw1Id;
    this.tw2Id = tw2Id;
    this.tw3Id = tw3Id;
  }

  public TopicTermAggregate(BigInteger topicId, String term, BaseDateTime date, long termsCount,
      AGGREGATE_TYPE type) {
    this.topicId = topicId;
    this.term = term;
    this.termsCount = termsCount;
    this.date = date;
    this.type = type;
  }

  public TopicTermAggregate(BigInteger topicId, String term, BaseDateTime date, long termsCount,
      AGGREGATE_TYPE type, BigDecimal tw1Id, BigDecimal tw2Id, BigDecimal tw3Id) {
    this.topicId = topicId;
    this.term = term;
    this.termsCount = termsCount;
    this.date = date;
    this.type = type;
    this.tw1Id = tw1Id;
    this.tw2Id = tw2Id;
    this.tw3Id = tw3Id;
  }

  public TopicTermAggregate(BigInteger topicId, String term, BaseDateTime date, long termsCount,
      BigDecimal tw1Id, BigDecimal tw2Id, BigDecimal tw3Id) {
    this.topicId = topicId;
    this.term = term;
    this.termsCount = termsCount;
    this.date = date;
    this.tw1Id = tw1Id;
    this.tw2Id = tw2Id;
    this.tw3Id = tw3Id;
  }

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


  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public long getTermsCount() {
    return termsCount;
  }

  public void setTermsCount(long termsCount) {
    this.termsCount = termsCount;
  }

  public AGGREGATE_TYPE getType() {
    return type;
  }

  public void setType(AGGREGATE_TYPE type) {
    this.type = type;
  }

  public BigDecimal getTw1Id() {
    return tw1Id;
  }

  public void setTw1_id(BigDecimal tw1Id) {
    this.tw1Id = tw1Id;
  }

  public BigDecimal getTw2Id() {
    return tw2Id;
  }

  public void setTw2_id(BigDecimal tw2Id) {
    this.tw2Id = tw2Id;
  }

  public BigDecimal getTw3Id() {
    return tw3Id;
  }

  public void setTw3_id(BigDecimal tw3Id) {
    this.tw3Id = tw3Id;
  }

  @Override
  public String toString() {
    return new StringBuilder("TopicTermAggregate [topicId=").append(topicId).append(", term=")
        .append(term).append(", termsCount=").append(termsCount).append(", date=").append(date)
        .append(", type=").append(type.name()).append("]").toString();
  }

  public static enum AGGREGATE_TYPE {
    topic_terms, topic_bi_terms, topic_tri_terms
  }

}
