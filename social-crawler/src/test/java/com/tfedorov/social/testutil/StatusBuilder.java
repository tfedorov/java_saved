/**
 * 
 */
package com.tfedorov.social.testutil;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

/**
 * @author tfedorov
 *
 */
public class StatusBuilder {

	public static final String STANDART_TWEET_ID = "327417474356228098";
	private static String STANDART_TWEET_JSON = "{\"filter_level\": \"medium\",    \"contributors\": null,    \"text\": \"tweet fashion example\",    \"geo\": null,    \"retweeted\": false,    \"in_reply_to_screen_name\": null,    \"truncated\": false,    \"lang\": \"id\",    \"entities\": {        \"symbols\": [],        \"urls\": [],        \"hashtags\": [],        \"user_mentions\": []    },    \"in_reply_to_status_id_str\": null,    \"id\": "+STANDART_TWEET_ID+",    \"source\": \"web\",    \"in_reply_to_user_id_str\": null,    \"favorited\": false,    \"in_reply_to_status_id\": null,    \"retweet_count\": 0,    \"created_at\": \"Thu Apr 25 13:43:03 +0000 2013\",    \"in_reply_to_user_id\": null,    \"favorite_count\": 0,    \"id_str\": \""+STANDART_TWEET_ID+"\",    \"place\": null,    \"user\": {        \"location\": \"\",        \"default_profile\": true,        \"statuses_count\": 9,        \"profile_background_tile\": false,        \"lang\": \"en\",        \"profile_link_color\": \"0084B4\",        \"id\": 1335458378,        \"following\": null,        \"favourites_count\": 1,        \"protected\": false,        \"profile_text_color\": \"333333\",        \"description\": null,        \"verified\": false,        \"contributors_enabled\": false,        \"profile_sidebar_border_color\": \"C0DEED\",        \"name\": \"deo billy stevino\",        \"profile_background_color\": \"C0DEED\",        \"created_at\": \"Mon Apr 08 01:12:52 +0000 2013\",        \"default_profile_image\": false,        \"followers_count\": 131,        \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"geo_enabled\": false,        \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme1/bg.png\",        \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme1/bg.png\",        \"follow_request_sent\": null,        \"url\": null,        \"utc_offset\": null,        \"time_zone\": null,        \"notifications\": null,        \"profile_use_background_image\": true,        \"friends_count\": 82,        \"profile_sidebar_fill_color\": \"DDEEF6\",        \"screen_name\": \"DStevino\",        \"id_str\": \"1335458378\",        \"profile_image_url\": \"http://a0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"listed_count\": 0,        \"is_translator\": false    },    \"coordinates\": null}";
	
	/**
	 * @param args
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws TwitterException {
		Status createStatus = DataObjectFactory.createStatus(STANDART_TWEET_JSON);
		System.out.println(createStatus);

	}
	
	public static Status buildStandart(){
		try {
			return DataObjectFactory.createStatus(STANDART_TWEET_JSON);
		} catch (TwitterException e) {
			System.err.println(e);
			return null;
		}
	}
	
	public static Status buildStandartWithText(String tweetId, String text){
		try {
			StringBuilder statusJson = new StringBuilder("{\"filter_level\": \"medium\",    \"contributors\": null,    \"text\": \""+text+"\",    \"geo\": null,    \"retweeted\": false,    \"in_reply_to_screen_name\": null,    \"truncated\": false,    \"lang\": \"id\",    \"entities\": {        \"symbols\": [],        \"urls\": [],        \"hashtags\": [],        \"user_mentions\": []    },    \"in_reply_to_status_id_str\": null,    \"id\": ");
			statusJson = statusJson.append(tweetId);
			statusJson = statusJson.append(",    \"source\": \"web\",    \"in_reply_to_user_id_str\": null,    \"favorited\": false,    \"in_reply_to_status_id\": null,    \"retweet_count\": 0,    \"created_at\": \"Thu Apr 25 13:43:03 +0000 2013\",    \"in_reply_to_user_id\": null,    \"favorite_count\": 0,    \"id_str\": \"");
			statusJson = statusJson.append(tweetId);
			statusJson = statusJson.append("\",    \"place\": null,    \"user\": {        \"location\": \"\",        \"default_profile\": true,        \"statuses_count\": 9,        \"profile_background_tile\": false,        \"lang\": \"en\",        \"profile_link_color\": \"0084B4\",        \"id\": 1335458378,        \"following\": null,        \"favourites_count\": 1,        \"protected\": false,        \"profile_text_color\": \"333333\",        \"description\": null,        \"verified\": false,        \"contributors_enabled\": false,        \"profile_sidebar_border_color\": \"C0DEED\",        \"name\": \"deo billy stevino\",        \"profile_background_color\": \"C0DEED\",        \"created_at\": \"Mon Apr 08 01:12:52 +0000 2013\",        \"default_profile_image\": false,        \"followers_count\": 131,        \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"geo_enabled\": false,        \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme1/bg.png\",        \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme1/bg.png\",        \"follow_request_sent\": null,        \"url\": null,        \"utc_offset\": null,        \"time_zone\": null,        \"notifications\": null,        \"profile_use_background_image\": true,        \"friends_count\": 82,        \"profile_sidebar_fill_color\": \"DDEEF6\",        \"screen_name\": \"DStevino\",        \"id_str\": \"1335458378\",        \"profile_image_url\": \"http://a0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"listed_count\": 0,        \"is_translator\": false    },    \"coordinates\": null}");
			
			return DataObjectFactory.createStatus(statusJson.toString());
		} catch (TwitterException e) {
			System.err.println(e);
			return null;
		}
	}
	
}
