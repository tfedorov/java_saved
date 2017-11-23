package com.tfedorov.social.topic.processing;

import java.util.List;
import java.util.Set;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.twitter.processing.tweet.Keyword;

public class TopicInfo {

  private Topic topic;


  /**
   * words or phrases in lower case separated by {@link TOPIC_KEYWORDS_SEPARATOR} and surrounded by
   * white spaces
   */
  private List<Keyword> parsedKeywordsLCWSList;

  /**
   * unique set of certain words
   */
  private Set<String> wordsSetLCSet;
  private Set<String> stemmedWordsSetLCSet;



  public TopicInfo(Topic topic, List<Keyword> parsedKewordsLCWSList, Set<String> wordsSetLCSet,
      Set<String> stemmedWordsSetLCSet) {
    super();
    this.topic = topic;
    this.parsedKeywordsLCWSList = parsedKewordsLCWSList;
    this.wordsSetLCSet = wordsSetLCSet;
    this.stemmedWordsSetLCSet = stemmedWordsSetLCSet;

  }



  public Topic getTopic() {
    return topic;
  }


  public List<Keyword> getParsedKeywordsLCWSList() {
    return parsedKeywordsLCWSList;
  }


  public Set<String> getWordsSetLCSet() {
    return wordsSetLCSet;
  }


  @Override
  public String toString() {
    return new StringBuilder("\nTOPIC_INFO{topic:").append(topic).append("Key Words LCWS List:")
        .append(parsedKeywordsLCWSList).append("Words LC Set:").append(wordsSetLCSet)
        .append("Stemmed Words LC Set:").append(stemmedWordsSetLCSet).append("}").toString();
  }



  public Set<String> getStemmedWordsSetLCSet() {
    return stemmedWordsSetLCSet;
  }



}
