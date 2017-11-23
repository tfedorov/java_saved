/**
 * 
 */
package com.tfedorov.social.twitter.processing.terms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.Status;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.concurrency.TaskExecutionServiceImpl;
import com.tfedorov.social.testutil.StatusBuilder;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;

/**
 * @author tfedorov
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class TermsProccessorTest {

	@InjectMocks
	private TermsProccessor proccessor = new TermsProccessor();
	@Mock
	private TweetsAggregationDao daoMock;

	@Mock
	private TaskExecutionService taskExecutionServiceMock;
	
	private static final String STANDART_TERM = "testedTerm";
	private Status standartStatus = StatusBuilder.buildStandart();


	@Test
	public void testProccessTerm() {
		
		TaskExecutionServiceImpl service = new TaskExecutionServiceImpl(1);
		
		//test insert
		//proccessor.proccessTerm(standartStatus, daoMock, TopicBuilder.buildStandart() , STANDART_TERM , TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
		
	}
}
