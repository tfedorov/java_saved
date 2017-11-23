package com.tfedorov.social.twitter.processing.terms;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.tfedorov.social.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate.AGGREGATE_TYPE;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class TermsProccessor
{
	private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();
	private Logger logger = LoggerFactory.getLogger(TermsProccessor.class);

	public void proccessTerm(Status status, TweetsAggregationDao tweetsAggregationDao, Topic topic, String term,
			AGGREGATE_TYPE type, TaskExecutionService taskExecutionService)
	{
		EtmPoint perfPoint = getPerformancePoint(new StringBuilder(".processTerm():").append(type).toString());

		try {
			TopicTermAggregate ttaN = new TopicTermAggregate(topic.getId(), term, DateUtils.convertToDateMidnight(status.getCreatedAt()), 1, type);

			BigInteger tweetId = new BigDecimal(status.getId()).toBigInteger();
			int updatedNumber = tweetsAggregationDao.updateAggregationTopicTermCalc(ttaN, tweetId);
			// if aggregate by topic_id, term, day exists
			if (updatedNumber == 0) {
				tweetsAggregationDao.insertAggregationTopicTerm(ttaN, tweetId);
			} else if (updatedNumber > 1) {
				logger.warn("Duplicates found in topic_terms aggregations table");
			}

		} finally {
			perfPoint.collect();
		}

    }

    protected EtmPoint getPerformancePoint(String name) {
		return performanceMonitor
				.createPoint(new StringBuilder(TermsProccessor.class.toString())
                        .append(name).toString());
	}
}
