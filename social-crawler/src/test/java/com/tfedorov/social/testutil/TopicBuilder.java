/**
 * 
 */
package com.tfedorov.social.testutil;

import java.math.BigInteger;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.utils.date.DateUtils;

/**
 * @author tfedorov
 *
 */
public class TopicBuilder {

	public static final BigInteger STANDART_TOPIC_ID = new BigInteger("1");
	private static final String STANDART_COMPANY_ID = "1";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Topic buildStandart() {
		Topic topic = new Topic();
		topic.setCompany(STANDART_COMPANY_ID);
		topic.setCreated(DateUtils.getCurrentMidnight());
		topic.setCreateUserName("acxiom");
		topic.setId(STANDART_TOPIC_ID);
		topic.setKeywords("fashion,love");
		topic.setName("fashion topic");
		topic.setModified(DateUtils.getCurrentMidnight());
		topic.setStatus(0);
		return topic;
	}

}
