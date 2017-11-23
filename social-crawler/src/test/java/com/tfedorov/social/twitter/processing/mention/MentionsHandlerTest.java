/**
 *
 */
package com.tfedorov.social.twitter.processing.mention;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigInteger;
import java.util.Date;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.Status;

import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicMentionAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;
import com.tfedorov.social.twitter.processing.TopicContextBuilder;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;
import com.tfedorov.social.utils.date.DateUtils;

/**
 * @author tfedorov
 */
@RunWith(MockitoJUnitRunner.class)
public class MentionsHandlerTest {

  private static final BigInteger TOPIC_ID = new BigInteger("1");

  @Mock
  private Status statusMock;
  @Mock
  private TweetsAggregationDaoImpl daoMock;

  @InjectMocks
  private MentionsHandler handler = new MentionsHandler(null);

  private Date createdeDate;
  private DateMidnight createdDateMidnight = DateUtils.convertToDateMidnight(createdeDate);

  @Test
  public void testProcessImplInsert() {
    GeneralProcessingContextBuilder builderContext = new GeneralProcessingContextBuilder();
    builderContext.tweetsAggregationDao = daoMock;
    GeneralProcessingContext context = builderContext.build(statusMock);
    context.getTweetContext().getTweetInfo().setSentiment(SENTIMENT.neutral);

    TopicProcessingContext topicContext = TopicContextBuilder.build(TOPIC_ID, "job");
    context.add(topicContext.getContextName(), topicContext);

    createdeDate = new Date();
    Mockito.when(statusMock.getCreatedAt()).thenReturn(createdeDate);

    Mockito.when(
        daoMock.updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight, SENTIMENT.neutral))
        .thenReturn(0);

    handler.processImpl(context);

    Mockito.verify(statusMock).getCreatedAt();
    Mockito.verify(daoMock).updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight,
        SENTIMENT.neutral);
    Mockito.verify(daoMock).insertAggregationTopicMention(
        Mockito.argThat(new TopicMentionAggregateMatcher()),
        Mockito.argThat(new SentimentMatcher()));

    verifyNoMoreInteractions(statusMock, daoMock);

  }

  private class SentimentMatcher extends ArgumentMatcher<SENTIMENT> {

    @Override
    public boolean matches(Object argument) {
      if (argument instanceof SENTIMENT) {
        if (argument.equals(SENTIMENT.neutral)) {
          return true;
        }
      }
      return false;
    }
  }

  private class TopicMentionAggregateMatcher extends ArgumentMatcher<TopicMentionAggregate> {

    @Override
    public boolean matches(Object argument) {
      if (argument instanceof TopicMentionAggregate) {
        TopicMentionAggregate arg = (TopicMentionAggregate) argument;
        if (!TOPIC_ID.equals(arg.getTopicId())) {
          return false;
        }

        if (1 != arg.getTweetsCount()) {
          return false;
        }

        if (!createdDateMidnight.equals(arg.getDate())) {
          return false;
        }

        // Argument match with expected
        return true;

      }
      // should not executed
      return false;
    }

  }

  @Test
  public void testProcessImplUpdate() {
    GeneralProcessingContextBuilder builderContext = new GeneralProcessingContextBuilder();
    builderContext.tweetsAggregationDao = daoMock;
    GeneralProcessingContext context = builderContext.build(statusMock);
    context.getTweetContext().getTweetInfo().setSentiment(SENTIMENT.neutral);

    TopicProcessingContext topicContext = TopicContextBuilder.build(TOPIC_ID, "job");
    context.add(topicContext.getContextName(), topicContext);

    Mockito.when(statusMock.getCreatedAt()).thenReturn(createdeDate);
    Mockito.when(
        daoMock.updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight, SENTIMENT.neutral))
        .thenReturn(1);

    handler.processImpl(context);

    Mockito.verify(statusMock).getCreatedAt();
    Mockito.verify(daoMock).updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight,
        SENTIMENT.neutral);

    verifyNoMoreInteractions(statusMock, daoMock);

  }

  @Test
  public void testProcessImplUpdateDuplicateMentions() {
    GeneralProcessingContextBuilder builderContext = new GeneralProcessingContextBuilder();
    builderContext.tweetsAggregationDao = daoMock;
    GeneralProcessingContext context = builderContext.build(statusMock);
    context.getTweetContext().getTweetInfo().setSentiment(SENTIMENT.neutral);

    TopicProcessingContext topicContext = TopicContextBuilder.build(TOPIC_ID, "Leader");
    context.add(topicContext.getContextName(), topicContext);

    Mockito.when(statusMock.getCreatedAt()).thenReturn(createdeDate);
    Mockito.when(
        daoMock.updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight, SENTIMENT.neutral))
        .thenReturn(2);

    handler.processImpl(context);

    Mockito.verify(statusMock).getCreatedAt();
    Mockito.verify(daoMock).updateAggTopicMentionIncremently(TOPIC_ID, createdDateMidnight,
        SENTIMENT.neutral);

    verifyNoMoreInteractions(statusMock, daoMock);

  }

  @Test
  public void testProcessImplNoKeyWords() {
    GeneralProcessingContextBuilder builderContext = new GeneralProcessingContextBuilder();
    builderContext.tweetsAggregationDao = daoMock;
    GeneralProcessingContext context = builderContext.build(statusMock);

    TopicProcessingContext topicContext = TopicContextBuilder.build(TOPIC_ID, null);
    context.add(topicContext.getContextName(), topicContext);

    handler.processImpl(context);

    verifyNoMoreInteractions(statusMock, daoMock);

  }

  @Test
  public void testGetClazz() {
    Assert.assertEquals(MentionsHandler.class, handler.getClazz());
  }

}
