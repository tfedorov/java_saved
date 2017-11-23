package com.tfedorov.social.twitter.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.twitter.aggregation.TweetsAggregationService;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;


public class TwitterStatusListener implements StatusListener {
	
	private static Logger logger = LoggerFactory.getLogger(TwitterStatusListener.class);
	
	private TweetsAggregationService aggregationService;
	
	private TweetTracingService tracingService;
	
	private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();
	
	public TwitterStatusListener(TweetsAggregationService aggregationService, TweetTracingService tracingService) {
		this.aggregationService = aggregationService;
		this.tracingService = tracingService;
	}


	@Override
	public void onException(Exception arg0) {
		EtmPoint perfPoint = getPerformancePoint(".onException():"+arg0.getClass()) ;
		logger.error("[ERROR]", arg0);
		perfPoint.collect();
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		EtmPoint perfPoint = getPerformancePoint(".onDeletionNotice()") ;
		perfPoint.collect();
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		EtmPoint perfPoint = getPerformancePoint(".onScrubGeo()") ;
		perfPoint.collect();
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		EtmPoint perfPoint = getPerformancePoint(".onStallWarning()") ;
		logger.warn("[STALL WARNING]:" + arg0);
		perfPoint.collect();

	}

	@Override
	public void onStatus(Status status) {
		EtmPoint perfPoint = getPerformancePoint(".onStatus()") ;
		try {
			aggregationService.processStatus(status);
			
		} catch (Exception e) {
			try {
				tracingService.dumpTweetWithError(status, e);
			} catch(Exception et) {
				//just warn - don't brake general exception notification flow
				logger.warn("Error within error dump", et);
			}
			onException(e);
		} finally {
			perfPoint.collect();
		}
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		EtmPoint perfPoint = getPerformancePoint(".onTrackLimitationNotice()") ;
		logger.info("[TRACK LIMITATION NOTICE] = " + arg0);
		perfPoint.collect();

	}
	
	protected EtmPoint getPerformancePoint(String name) {
		return performanceMonitor
				.createPoint(new StringBuilder(TwitterStatusListener.class.toString())
					.append(name).toString());
	}

}
