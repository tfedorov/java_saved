/**
 * 
 */
package com.tfedorov.social.utils;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author tfedorov
 *
 */
public class JsonUtilsTest {

	private static final String RETWEETED_JSON = "{\"filter_level\":\"low\",\"retweeted_status\":{\"contributors\":null,\"text\":\"Pictures: Bus for the street parade http://t.co/OIUJJdocCU http://t.co/LZahICLoHj http://t.co/dlx0mA7sYs http://t.co/O1qVZVxCBo\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"eng\",\"entities\":{\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://instagram.com/p/ZQZn6KFnHG\",\"indices\":[82,104],\"display_url\":\"instagram.com/p/ZQZn6KFnHG\",\"url\":\"http://t.co/dlx0mA7sYs\"}],\"hashtags\":[],\"media\":[{\"sizes\":{\"thumb\":{\"w\":150,\"resize\":\"crop\",\"h\":150},\"small\":{\"w\":340,\"resize\":\"fit\",\"h\":453},\"large\":{\"w\":768,\"resize\":\"fit\",\"h\":1024},\"medium\":{\"w\":600,\"resize\":\"fit\",\"h\":800}},\"id\":333964672237699074,\"media_url_https\":\"https://pbs.twimg.com/media/BKJ7GZhCAAIqFAZ.jpg\",\"media_url\":\"http://pbs.twimg.com/media/BKJ7GZhCAAIqFAZ.jpg\",\"expanded_url\":\"http://twitter.com/gerardromero/status/333964672233504770/photo/1\",\"source_status_id_str\":\"333964672233504770\",\"indices\":[36,58],\"source_status_id\":333964672233504770,\"id_str\":\"333964672237699074\",\"type\":\"photo\",\"display_url\":\"pic.twitter.com/OIUJJdocCU\",\"url\":\"http://t.co/OIUJJdocCU\"},{\"sizes\":{\"thumb\":{\"w\":150,\"resize\":\"crop\",\"h\":150},\"small\":{\"w\":340,\"resize\":\"fit\",\"h\":340},\"medium\":{\"w\":600,\"resize\":\"fit\",\"h\":600},\"large\":{\"w\":1024,\"resize\":\"fit\",\"h\":1024}},\"id\":333962506475298817,\"media_url_https\":\"https://pbs.twimg.com/media/BKJ5IVbCYAEd3Hv.jpg\",\"media_url\":\"http://pbs.twimg.com/media/BKJ5IVbCYAEd3Hv.jpg\",\"expanded_url\":\"http://twitter.com/gerardromero/status/333962506471104512/photo/1\",\"source_status_id_str\":\"333962506471104512\",\"indices\":[59,81],\"source_status_id\":333962506471104512,\"id_str\":\"333962506475298817\",\"type\":\"photo\",\"display_url\":\"pic.twitter.com/LZahICLoHj\",\"url\":\"http://t.co/LZahICLoHj\"},{\"sizes\":{\"small\":{\"w\":340,\"resize\":\"fit\",\"h\":255},\"thumb\":{\"w\":150,\"resize\":\"crop\",\"h\":150},\"medium\":{\"w\":600,\"resize\":\"fit\",\"h\":450},\"large\":{\"w\":800,\"resize\":\"fit\",\"h\":600}},\"id\":333971649856692224,\"media_url_https\":\"https://pbs.twimg.com/media/BKKBcjLCYAA9g7e.jpg\",\"media_url\":\"http://pbs.twimg.com/media/BKKBcjLCYAA9g7e.jpg\",\"expanded_url\":\"http://twitter.com/LaTdP/status/333971649848303619/photo/1\",\"source_status_id_str\":\"333971649848303619\",\"indices\":[105,127],\"source_status_id\":333971649848303619,\"id_str\":\"333971649856692224\",\"type\":\"photo\",\"display_url\":\"pic.twitter.com/O1qVZVxCBo\",\"url\":\"http://t.co/O1qVZVxCBo\"}],\"user_mentions\":[]} }}";
	private static String SIMPLE_TWEET_JSON = "{\"filter_level\": \"medium\",    \"contributors\": null,    \"text\": \"tweet fashion example\",    \"geo\": null,    \"retweeted\": false,    \"in_reply_to_screen_name\": null,    \"truncated\": false,    \"lang\": \"id\",    \"entities\": {        \"symbols\": [],        \"urls\": [],        \"hashtags\": [],        \"user_mentions\": []    },    \"in_reply_to_status_id_str\": null,    \"id\": 327417474356228098,    \"source\": \"web\",    \"in_reply_to_user_id_str\": null,    \"favorited\": false,    \"in_reply_to_status_id\": null,    \"retweet_count\": 0,    \"created_at\": \"Thu Apr 25 13:43:03 +0000 2013\",    \"in_reply_to_user_id\": null,    \"favorite_count\": 0,    \"id_str\": \"327417474356228098\",    \"place\": null,    \"user\": {        \"location\": \"\",        \"default_profile\": true,        \"statuses_count\": 9,        \"profile_background_tile\": false,        \"lang\": \"eng\",        \"profile_link_color\": \"0084B4\",        \"id\": 1335458378,        \"following\": null,        \"favourites_count\": 1,        \"protected\": false,        \"profile_text_color\": \"333333\",        \"description\": null,        \"verified\": false,        \"contributors_enabled\": false,        \"profile_sidebar_border_color\": \"C0DEED\",        \"name\": \"deo billy stevino\",        \"profile_background_color\": \"C0DEED\",        \"created_at\": \"Mon Apr 08 01:12:52 +0000 2013\",        \"default_profile_image\": false,        \"followers_count\": 131,        \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"geo_enabled\": false,        \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme1/bg.png\",        \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme1/bg.png\",        \"follow_request_sent\": null,        \"url\": null,        \"utc_offset\": null,        \"time_zone\": null,        \"notifications\": null,        \"profile_use_background_image\": true,        \"friends_count\": 82,        \"profile_sidebar_fill_color\": \"DDEEF6\",        \"screen_name\": \"DStevino\",        \"id_str\": \"1335458378\",        \"profile_image_url\": \"http://a0.twimg.com/profile_images/3493192133/d9af454760dc7d381a44a97d227df02e_normal.jpeg\",        \"listed_count\": 0,        \"is_translator\": false    },    \"coordinates\": null}";
	
	@Test
	public void testGetLangFromJson() throws IOException {
	
		Assert.assertEquals("eng",JsonUtils.getFieldFromSubObject(RETWEETED_JSON,"retweeted_status","lang"));
		Assert.assertNull(JsonUtils.getFieldFromSubObject(RETWEETED_JSON,"retweeted_status","notExistingField"));

		Assert.assertNull(JsonUtils.getFieldFromSubObject(RETWEETED_JSON,"notExistingObject","lang"));
		
		Assert.assertEquals("Pictures: Bus for the street parade http://t.co/OIUJJdocCU http://t.co/LZahICLoHj http://t.co/dlx0mA7sYs http://t.co/O1qVZVxCBo",JsonUtils.getFieldFromSubObject(RETWEETED_JSON,"retweeted_status","text"));
		Assert.assertNull(JsonUtils.getFieldFromSubObject(SIMPLE_TWEET_JSON,"retweeted_status","lang"));
		Assert.assertNull(JsonUtils.getFieldFromSubObject(null,"retweeted_status","lang"));
		Assert.assertNull(JsonUtils.getFieldFromSubObject(SIMPLE_TWEET_JSON,null,"lang"));
		Assert.assertNull(JsonUtils.getFieldFromSubObject(SIMPLE_TWEET_JSON,"retweeted_status",null));
	}

	@Test
	public void testGetInFirstChildField() throws IOException {
		
		Assert.assertEquals("id",JsonUtils.getInFirstChildField(SIMPLE_TWEET_JSON,"lang"));
		Assert.assertNull(JsonUtils.getInFirstChildField(null,"lang"));
		Assert.assertNull(JsonUtils.getInFirstChildField(null,null));
		Assert.assertNull(JsonUtils.getInFirstChildField(SIMPLE_TWEET_JSON,null));
		Assert.assertNull(JsonUtils.getInFirstChildField(RETWEETED_JSON,"lang"));

		Assert.assertEquals("low",JsonUtils.getInFirstChildField(RETWEETED_JSON,"filter_level"));
		
	}
}
