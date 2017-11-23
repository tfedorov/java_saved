/**
 * 
 */
package com.tfedorov.social.twitter.processing;

import java.lang.reflect.Field;
import java.math.BigInteger;

import com.tfedorov.social.normalization.stemming.StemmingServiceImpl;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.processing.TopicInfo;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.service.TopicService;
import com.tfedorov.social.twitter.service.TopicServiceImpl;

/**
 * @author tfedorov
 * 
 */
public class TopicContextBuilder {

  private static final String TOPIC_KEYWORDS = "job, Leader";
  private static final BigInteger TOPIC_ID_STANDART = new BigInteger("1");

  public static TopicProcessingContext build(BigInteger topicId, String foundedKeyword) {
    Topic topic = new Topic();
    topic.setId(topicId);
    topic.setKeywords(TOPIC_KEYWORDS);

    //
    TopicService ts = new TopicServiceImpl();
    // set service with reflection
    try {
      Field field = ts.getClass().getDeclaredField("stemmingService");
      field.setAccessible(true);
      field.set(ts, new StemmingServiceImpl());
    } catch (Exception e) {
      e.printStackTrace();
    }
    //

    TopicInfo topInfo = ts.createTopicInfo(topic);
    TopicProcessingContext topicContext = new TopicProcessingContext(topInfo);
    topicContext.add(TopicProcessingContext.KEY_WORD_FOUND, foundedKeyword);
    return topicContext;
  }
  
  public static TopicProcessingContext buildSimple(String topicKeywords) {
	    Topic topic = new Topic();
	    topic.setId(TOPIC_ID_STANDART);
	    topic.setKeywords(topicKeywords);
	    
	    TopicService ts = new TopicServiceImpl();
	    // set service with reflection
	    try {
	      Field field = ts.getClass().getDeclaredField("stemmingService");
	      field.setAccessible(true);
	      field.set(ts, new StemmingServiceImpl());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    //	    
	    TopicInfo topInfo = ts.createTopicInfo(topic);
	    return new TopicProcessingContext(topInfo);
	    
  }
}
