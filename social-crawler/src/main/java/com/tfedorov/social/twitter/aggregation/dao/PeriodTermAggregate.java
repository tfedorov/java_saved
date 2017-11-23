package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.base.BaseDateTime;

public class PeriodTermAggregate {

  @JsonIgnore
  private BigInteger topicId;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "tag")
  private String term;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "date")
  private BaseDateTime date;

  @JsonIgnore
  private int period;

  @JsonProperty(value = "amount")
  @JsonSerialize(include = Inclusion.NON_NULL)
  private long termsCount;

  @JsonProperty(value = "topic")
  public Long getTopic() {
    return 6l;
  }

  @JsonIgnore
  private AGGREGATE_TYPE_MAPPING type;

  public PeriodTermAggregate(BigInteger topicId, String term, BaseDateTime date, int period,
      long termsCount, AGGREGATE_TYPE_MAPPING type) {
    this.topicId = topicId;
    this.term = term;
    this.date = date;
    this.period = period;
    this.termsCount = termsCount;
    this.type = type;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
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

  public BaseDateTime getDate() {
    return date;
  }

  public void setDate(BaseDateTime date) {
    this.date = date;
  }

  public long getTermsCount() {
    return termsCount;
  }

  public void setTermsCount(long termsCount) {
    this.termsCount = termsCount;
  }

  @Override
  public String toString() {
    return new StringBuilder("<br>PeriodTermAggregate [topicId=").append(topicId).append(", term=")
        .append(term).append(", termsCount=").append(termsCount).append(", date=").append(date)
        .append(", period=").append(period).append(", type=").append(type.name()).append("]")
        .toString();
  }

  public String toJsonString() {
    return new StringBuilder().append(topicId).append(",").toString();

  }

  public static enum AGGREGATE_TYPE_MAPPING {
    topic_terms_p(TopicTermAggregate.AGGREGATE_TYPE.topic_terms.name(), 50), topic_bi_terms_p(
        TopicTermAggregate.AGGREGATE_TYPE.topic_bi_terms.name(), 50 * 49 / 2), topic_tri_terms_p(
        TopicTermAggregate.AGGREGATE_TYPE.topic_tri_terms.name(), 50);

    private final String rawTable;

    private final int limit;

    private AGGREGATE_TYPE_MAPPING(String f, int limit) {
      rawTable = f;
      this.limit = limit;
    }

    public String getRawTable() {
      return rawTable;
    }

    public int getLimit() {
      return limit;
    }


  }

}
