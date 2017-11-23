/**
 * 
 */
package com.tfedorov.social;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * @author tfedorov
 *
 */
public class TweetFeedsSpeedUtil {


	private static final int PRINT_AFTER_NUMBER_OF_TWEET = 100;
	private static final int RESET_COUNTER = 1000;
	private static long counterAll = 0;
	private static long counterPeriod = 0;
	
	private static Long startMeasured; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws TwitterException, IOException{
	    StatusListener listener = createCounterListener(RESET_COUNTER,PRINT_AFTER_NUMBER_OF_TWEET);
	    ConfigurationBuilder confBuilder = getConfigurationBuilder("/etc/acxm/config/social/application.properties");
					
	    TwitterStream twitterStream = new TwitterStreamFactory(confBuilder.build()).getInstance();
	    twitterStream.addListener(listener);
	    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    twitterStream.sample();
	}

	private static ConfigurationBuilder getConfigurationBuilder(String propertyPath) throws IOException,
			FileNotFoundException {
		Properties prop = new Properties();
	    //load a properties file
		prop.load(new FileInputStream(propertyPath));
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		confBuilder.setOAuthConsumerKey(prop.getProperty("agg.twitter.consumer.key"))
					.setOAuthConsumerSecret(prop.getProperty("agg.twitter.consumer.secret"))
					.setOAuthAccessToken(prop.getProperty("agg.twitter.access.token"))
					.setOAuthAccessTokenSecret(prop.getProperty("agg.twitter.access.secret"));
		return confBuilder;
	}

	private static StatusListener createCounterListener(final int resetСounterTweet, final int printTweetPeriod) {
		StatusListener listener = new StatusListener(){
	        public void onStatus(Status status) {

				if (counterPeriod % resetСounterTweet == 0) {
					startMeasured = new Date().getTime(); 
					counterPeriod = 0;
					System.out.println("Procceded " + counterAll + " tweets after launching.");
					System.out.println("_________________________________________________");
				}	
				counterAll++;
				counterPeriod++;
				if (counterPeriod % printTweetPeriod == 0) {
					long endMeasured = new Date().getTime();
					Double measur = (double) (endMeasured - startMeasured) / counterPeriod;
					System.out.println("Tweet come with speed = " + measur.toString() + " milisec ");
				}
	
			}
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub
				
			}
	    };
		return listener;
	}
	
	

}
