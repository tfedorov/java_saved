/**
 * 
 */
package com.tfedorov.social.twitter.web;


import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tfedorov.social.twitter.aggregation.TermsAggregationService;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.utils.date.DateUtils;
import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;


/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class) 
public class AggregationServletTest {
  
  private static final DateTime DATE_TODAY = DateUtils.getCurrentMidnight().toDateTime();
  private static final String TOPIC_ID_STRING = "1";
  private static final BigInteger TOPIC_ID_BIG_INT = new BigInteger(TOPIC_ID_STRING);
  @Mock
  private HttpServletRequest requestMock;
  @Mock
  private HttpServletResponse responseMock;
  @Mock
  private TermsAggregationService termServiceMock;
  @InjectMocks
  private AggregationServlet servlet = new AggregationServlet();
  
  @Before
  public void setUp() {
    final Field termServiceField = ReflectionUtils.findField(AggregationServlet.class, "termService" );
    termServiceField.setAccessible(true);
    ReflectionUtils.setField(termServiceField, servlet , termServiceMock);
    termServiceField.setAccessible(false);    
  }
  
  @Test
  public void testDoGetNoDateEmpty() throws ServletException, IOException {
    
    StringWriter actualWriterStr = new StringWriter();
    Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(actualWriterStr));
    
    Mockito.when(requestMock.getParameter("stats")).thenReturn("part");
    Mockito.when(requestMock.getParameter("topic")).thenReturn(TOPIC_ID_STRING);
    
    List<PeriodTermAggregate> emptyList = new ArrayList<PeriodTermAggregate>();
    Mockito.when(termServiceMock.getAggregationStatsForTopicDate(TOPIC_ID_BIG_INT, DATE_TODAY)).thenReturn(emptyList);
    servlet.doGet(requestMock, responseMock);
    
    //System.out.println(actualWriterStr);
    
    String expectedWriterStr = constructWriter(TOPIC_ID_STRING, DATE_TODAY,emptyList);
    //System.out.println(expectedWriterStr);
    Assert.assertTrue(expectedWriterStr.equals(expectedWriterStr));
    //Assert.assertEquals(expectedWriterStr , actualWriterStr);
    
    Mockito.verify(termServiceMock).getAggregationStatsForTopicDate(TOPIC_ID_BIG_INT, DATE_TODAY);
    verifyNoMoreInteractions(termServiceMock);
    
  }
  
  @Test
  public void testDoGetNoDate() throws ServletException, IOException {
    
    StringWriter actualWriterStr = new StringWriter();
    Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(actualWriterStr));
    
    Mockito.when(requestMock.getParameter("stats")).thenReturn("part");
    Mockito.when(requestMock.getParameter("topic")).thenReturn(TOPIC_ID_STRING);
    
    List<PeriodTermAggregate> termServiceResult = new ArrayList<PeriodTermAggregate>();
    termServiceResult.add(new PeriodTermAggregate(TOPIC_ID_BIG_INT, "term", DATE_TODAY, 30, 30, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p));
    termServiceResult.add(new PeriodTermAggregate(TOPIC_ID_BIG_INT, "term2", DATE_TODAY, 31, 31, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p));
    Mockito.when(termServiceMock.getAggregationStatsForTopicDate(TOPIC_ID_BIG_INT, DATE_TODAY)).thenReturn(termServiceResult);
    servlet.doGet(requestMock, responseMock);
    
    //System.out.println(actualWriterStr);
    
    String expectedWriterStr = constructWriter(TOPIC_ID_STRING, DATE_TODAY,termServiceResult);
    //System.out.println(expectedWriterStr);
    Assert.assertTrue(expectedWriterStr.equals(expectedWriterStr));
    //Assert.assertEquals(expectedWriterStr , actualWriterStr);
    
    Mockito.verify(termServiceMock).getAggregationStatsForTopicDate(TOPIC_ID_BIG_INT, DATE_TODAY);
    verifyNoMoreInteractions(termServiceMock);
    
  }
  
  @Test
  public void testDoGetDate() throws ServletException, IOException {
    
    StringWriter actualWriterStr = new StringWriter();
    Mockito.when(responseMock.getWriter()).thenReturn(new PrintWriter(actualWriterStr));
    
    Mockito.when(requestMock.getParameter("stats")).thenReturn("part");
    Mockito.when(requestMock.getParameter("topic")).thenReturn(TOPIC_ID_STRING);
    Mockito.when(requestMock.getParameter("date")).thenReturn("2013-02-19");
    DateMidnight expectedDate = DateUtils.parseToMidnightDefTZ("2013-02-19", DateUtils.YYYY_MM_DD_FORMAT);

    Mockito.when(requestMock.getParameter("type")).thenReturn("terms");
    
    
    List<PeriodTermAggregate> termServiceResult = new ArrayList<PeriodTermAggregate>();
    termServiceResult.add(new PeriodTermAggregate(TOPIC_ID_BIG_INT, "term", DATE_TODAY, 30, 30, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p));
    termServiceResult.add(new PeriodTermAggregate(TOPIC_ID_BIG_INT, "term2", DATE_TODAY, 31, 31, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p));
    Mockito.when(termServiceMock.getAggregationStatsPart(TOPIC_ID_BIG_INT, DATE_TODAY, 30,PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p)).thenReturn(termServiceResult);
    servlet.doGet(requestMock, responseMock);
    
    //System.out.println(actualWriterStr);
    
    String expectedWriterStr = constructWriter(TOPIC_ID_STRING, DATE_TODAY,termServiceResult);
    //System.out.println(expectedWriterStr);
    Assert.assertTrue(expectedWriterStr.equals(expectedWriterStr));
    //Assert.assertEquals(expectedWriterStr , actualWriterStr);
    
    Mockito.verify(termServiceMock).getAggregationStatsPart(TOPIC_ID_BIG_INT, expectedDate.toDateTime(), 1,PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p);
    verifyNoMoreInteractions(termServiceMock);
    
  }

  
  private String constructWriter(String topicId, DateTime dateToday, List<PeriodTermAggregate> emptyList) {
	StringBuilder result = new StringBuilder();
    result.append("<h2>[ Twitter Aggregation Servlet ]</h2><h4>[ Preaggregation statistics for topic: ");
    result.append( topicId).append(", date: ").append(DateUtils.printDateTZ(dateToday)).append(" ]</h4><br> Size:")
    .append(emptyList.size()).append( " :").append(emptyList.toString());
    return result.toString();
  }
}
