package com.tfedorov.social.twitter.streaming;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

/**
	 * @param args
	 */
	public final class PrintSampleStream extends StatusAdapter {
	    /**
	     * Main entry of this application.
	     *
	     * @param args
	     */
	    public static void main(String[] args) throws TwitterException {
	        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	        
	        twitterStream.setOAuthConsumer("SGwULki88ZHy8ybdtB0Q", "JPjxzHzjpyUZTExTe8UnIPj4YstzbzVrRgnjdZYhtBc");

			AccessToken accessToken = new AccessToken("936602312-kCuiRN5WGd9hCjDT4AGK5WKqjlr7kxCiCJzGIJfA", "kzoq3ByIxMsBXOvQ6S5AvpJPsZImTgrAHpYXQ81lOI");
	        
			twitterStream.setOAuthAccessToken(accessToken);
	        
	        StatusListener listener = new StatusListener() {
	            public void onStatus(Status status) {
	                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
	            }

	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            public void onScrubGeo(long userId, long upToStatusId) {
	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }

				@Override
				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub
					
				}
	        };
	        twitterStream.addListener(listener);
	        twitterStream.sample();
	    }
	}

