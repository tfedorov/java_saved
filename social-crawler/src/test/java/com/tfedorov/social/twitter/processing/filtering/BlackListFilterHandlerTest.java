package com.tfedorov.social.twitter.processing.filtering;

import com.tfedorov.social.processing.ProcessingHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;

@RunWith(MockitoJUnitRunner.class)
public class BlackListFilterHandlerTest {

	private static final String BLACK_WORD_SIMPLE_TEXT = "Fuck you tweet #firstpost";
	private static final String BLACK_WORD_SIMPLE_TWEET = "{\"filter_level\":\"medium\",\"contributors\":null,\"text\":\""
			+ BLACK_WORD_SIMPLE_TEXT
			+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"firstpost\",\"indices\":[40,50]}],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":341866465747689472,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Tue Jun 04 10:38:11 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341866465747689472\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":1,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"id\":1481914742,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Chris Coates\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Tue Jun 04 10:21:06 +0000 2013\",\"default_profile_image\":true,\"followers_count\":1,\"profile_image_url_https\":\"https://si0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":3600,\"time_zone\":\"Amsterdam\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":19,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"chriswiiii\",\"id_str\":\"1481914742\",\"profile_image_url\":\"http://a0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}";

	private static final String CORRECT_SIMPLE_TWEET_TEXT = "Correct tweet #firstpost";
	private static final String CORRECT_SIMPLE_TWEET = "{\"filter_level\":\"medium\",\"contributors\":null,\"text\":\""
			+ CORRECT_SIMPLE_TWEET_TEXT
			+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"firstpost\",\"indices\":[40,50]}],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":341866465747689472,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Tue Jun 04 10:38:11 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341866465747689472\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":1,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"id\":1481914742,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Chris Coates\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Tue Jun 04 10:21:06 +0000 2013\",\"default_profile_image\":true,\"followers_count\":1,\"profile_image_url_https\":\"https://si0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":3600,\"time_zone\":\"Amsterdam\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":19,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"chriswiiii\",\"id_str\":\"1481914742\",\"profile_image_url\":\"http://a0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}";

	private static final String BLACK_WORD_RETWEET_TEXT = "Make blow job now";
	private static final String BLACK_WORD_RETWEET = "{\"filter_level\":\"low\",\"retweeted_status\":{\"contributors\":null,\"text\":\"" 
			+ BLACK_WORD_RETWEET_TEXT
			+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"ru\",\"entities\":{\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt\",\"indices\":[18,40],\"display_url\":\"meshdom.ru/cache/naj.php?\u2026\",\"url\":\"http://t.co/5PRHM5kHXp\"}],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":341858819812519936,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":3,\"created_at\":\"Tue Jun 04 10:07:48 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341858819812519936\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":103,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"id\":562614112,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":\"Proud mom. 7.30.09*\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Linda V\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Wed Apr 25 05:18:35 +0000 2012\",\"default_profile_image\":false,\"followers_count\":2,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/2168637879/002_normal.JPG\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":null,\"time_zone\":null,\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":58,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"lindasibellv\",\"id_str\":\"562614112\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/2168637879/002_normal.JPG\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null},\"contributors\":null,\"text\":\"RT @lindasibellv: "
					+ BLACK_WORD_RETWEET_TEXT
					+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"entities\":{\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt\",\"indices\":[36,58],\"display_url\":\"meshdom.ru/cache/naj.php?\u2026\",\"url\":\"http://t.co/5PRHM5kHXp\"}],\"hashtags\":[],\"user_mentions\":[{\"id\":562614112,\"name\":\"Linda V\",\"indices\":[3,16],\"screen_name\":\"lindasibellv\",\"id_str\":\"562614112\"}]},\"in_reply_to_status_id_str\":null,\"id\":341869791834947584,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Tue Jun 04 10:51:24 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341869791834947584\",\"place\":null,\"user\":{\"location\":\"Iran\",\"default_profile\":false,\"statuses_count\":116,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"117A60\",\"id\":49847510,\"following\":null,\"favourites_count\":1,\"protected\":false,\"profile_text_color\":\"050505\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"BDDCAD\",\"name\":\"Matthew Drake\",\"profile_background_color\":\"71BF65\",\"created_at\":\"Tue Jun 23 01:55:24 +0000 2009\",\"default_profile_image\":false,\"followers_count\":12,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg\",\"profile_background_image_url_https\":\"https://si0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":12600,\"time_zone\":\"Tehran\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":20,\"profile_sidebar_fill_color\":\"DDFFCC\",\"screen_name\":\"ghostkll\",\"id_str\":\"49847510\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}";

	private static final String CORRECT_WORD_RETWEET_BLACK_INCL_TEXT = "Make blow another job now" ;
	private static final String CORRECT_WORD_RETWEET_BLACK_INCL = "{\"filter_level\":\"low\",\"retweeted_status\":{\"contributors\":null,\"text\":\""
			+ CORRECT_WORD_RETWEET_BLACK_INCL_TEXT
			+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"ru\",\"entities\":{\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt\",\"indices\":[18,40],\"display_url\":\"meshdom.ru/cache/naj.php?\u2026\",\"url\":\"http://t.co/5PRHM5kHXp\"}],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":341858819812519936,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":3,\"created_at\":\"Tue Jun 04 10:07:48 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341858819812519936\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":103,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"id\":562614112,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":\"Proud mom. 7.30.09*\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Linda V\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Wed Apr 25 05:18:35 +0000 2012\",\"default_profile_image\":false,\"followers_count\":2,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/2168637879/002_normal.JPG\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":null,\"time_zone\":null,\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":58,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"lindasibellv\",\"id_str\":\"562614112\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/2168637879/002_normal.JPG\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null},\"contributors\":null,\"text\":\"RT @lindasibellv: "
					+ CORRECT_WORD_RETWEET_BLACK_INCL_TEXT
					+ "\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"entities\":{\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt\",\"indices\":[36,58],\"display_url\":\"meshdom.ru/cache/naj.php?\u2026\",\"url\":\"http://t.co/5PRHM5kHXp\"}],\"hashtags\":[],\"user_mentions\":[{\"id\":562614112,\"name\":\"Linda V\",\"indices\":[3,16],\"screen_name\":\"lindasibellv\",\"id_str\":\"562614112\"}]},\"in_reply_to_status_id_str\":null,\"id\":341869791834947584,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Tue Jun 04 10:51:24 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341869791834947584\",\"place\":null,\"user\":{\"location\":\"Iran\",\"default_profile\":false,\"statuses_count\":116,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"117A60\",\"id\":49847510,\"following\":null,\"favourites_count\":1,\"protected\":false,\"profile_text_color\":\"050505\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"BDDCAD\",\"name\":\"Matthew Drake\",\"profile_background_color\":\"71BF65\",\"created_at\":\"Tue Jun 23 01:55:24 +0000 2009\",\"default_profile_image\":false,\"followers_count\":12,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg\",\"profile_background_image_url_https\":\"https://si0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":12600,\"time_zone\":\"Tehran\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":20,\"profile_sidebar_fill_color\":\"DDFFCC\",\"screen_name\":\"ghostkll\",\"id_str\":\"49847510\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}";
	
	@Mock
	private ProcessingHandler<GeneralProcessingContext> successorFalse;

	@Mock
	private ProcessingHandler<GeneralProcessingContext> successorTrue;
	@InjectMocks
	private BlackListFilterHandler blackListFilterHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		blackListFilterHandler = new BlackListFilterHandler(successorTrue,
				successorFalse);
	}

	@Test
	public void testCorrectRetweet() throws Exception {

		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		GeneralProcessingContext standContext = contextBuilder
				.buildWithStandRetweet();
		blackListFilterHandler.process(standContext);

		Mockito.verify(successorFalse).process(standContext);
		Mockito.verifyNoMoreInteractions(successorFalse, successorTrue);

	}

	@Test
	public void testCorrectSimple() throws Exception {

		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		GeneralProcessingContext standContext = contextBuilder
				.buildAndInit(CORRECT_SIMPLE_TWEET);
		blackListFilterHandler.process(standContext);

		Mockito.verify(successorFalse).process(standContext);
		Mockito.verifyNoMoreInteractions(successorFalse, successorTrue);

	}
	
	@Test
	public void testCorrectRetweetBlackIn() throws Exception {

		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		GeneralProcessingContext standContext = contextBuilder
				.buildAndInit(CORRECT_WORD_RETWEET_BLACK_INCL);
		blackListFilterHandler.process(standContext);

		Mockito.verify(successorFalse).process(standContext);
		Mockito.verifyNoMoreInteractions(successorFalse, successorTrue);
	}

	@Test
	public void testBlackWordRetweet() throws Exception {

		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		GeneralProcessingContext standContext = contextBuilder
				.buildAndInit(BLACK_WORD_RETWEET);
		blackListFilterHandler.process(standContext);

		Mockito.verify(successorTrue).process(standContext);
		Mockito.verifyNoMoreInteractions(successorFalse, successorTrue);
	}

	@Test
	public void testBlackWordSimple() throws Exception {

		GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();
		GeneralProcessingContext standContext = contextBuilder
				.buildAndInit(BLACK_WORD_SIMPLE_TWEET);
		blackListFilterHandler.process(standContext);

		Mockito.verify(successorTrue).process(standContext);
		Mockito.verifyNoMoreInteractions(successorFalse, successorTrue);
	}

}
