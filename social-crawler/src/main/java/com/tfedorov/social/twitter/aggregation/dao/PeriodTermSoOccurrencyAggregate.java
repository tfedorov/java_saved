package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;

import org.joda.time.base.BaseDateTime;

public class PeriodTermSoOccurrencyAggregate extends PeriodTermAggregate {
	
	private String firstTerm;
	
	private String secondTerm;
	
	private long firtsTermCount;
	
	private long secondTermCount;

	public PeriodTermSoOccurrencyAggregate(BigInteger topicId, String firstTerm, long firstTermCount, String secondTerm,
			long secondTermCount, long soOccurrencyCount, 	BaseDateTime date, int period) {
		super(topicId, firstTerm+" "+secondTerm, date, period, soOccurrencyCount, AGGREGATE_TYPE_MAPPING.topic_bi_terms_p);
		
		this.firstTerm = firstTerm;
		this.secondTerm = secondTerm;
		this.firtsTermCount = firstTermCount;
		this.secondTermCount = secondTermCount;
	}


	public String getFirstTerm() {
		return firstTerm;
	}


	public String getSecondTerm() {
		return secondTerm;
	}


	public long getFirtsTermCount() {
		return firtsTermCount;
	}


	public long getSecondTermCount() {
		return secondTermCount;
	}

}
