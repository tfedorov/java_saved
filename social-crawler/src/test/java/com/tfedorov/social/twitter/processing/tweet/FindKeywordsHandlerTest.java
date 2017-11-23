/**
 * 
 */
package com.tfedorov.social.twitter.processing.tweet;

import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.TopicContextBuilder;
import junit.framework.Assert;

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

/**
 * @author tfedorov
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class FindKeywordsHandlerTest {
  @Mock
  private ProcessingHandler<GeneralProcessingContext> successorTrue;
  @InjectMocks
  private FindKeywordsHandler findKeywordsHandler = new FindKeywordsHandler(successorTrue);

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

  @Test
  public void testProcessStandRetweetImpl() throws Exception {

    GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();

    contextBuilder.normalizedRetweetedText = "my last day of running school #bittersweet ";
    contextBuilder.stemmedRetweetedText = "my last day of run school #bittersweet ";
    GeneralProcessingContext genContext = contextBuilder.buildWithStandRetweet();

    TopicProcessingContext topicContext =
        TopicContextBuilder.buildSimple(GeneralProcessingContextBuilder.STANDART_RETWEET_KEYWORD);
    genContext.add(topicContext.getContextName(), topicContext);

    findKeywordsHandler.process(genContext);
    Assert.assertEquals(Keyword.class,
        genContext.getTopicContext().get(TopicProcessingContext.KEY_WORD_FOUND).getClass());
    Keyword keyWord =
        (Keyword) genContext.getTopicContext().get(TopicProcessingContext.KEY_WORD_FOUND);
    Assert.assertEquals("running", keyWord.getNormalKeyword().trim());
    Assert.assertEquals("run", keyWord.getStemmedKeyword().trim());

    Mockito.verify(successorTrue).process(genContext);
    Mockito.verifyNoMoreInteractions(successorTrue);
  }

  @Test
  public void testProcessAnotherLangRetweet() throws Exception {

    GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();

    contextBuilder.normalizedRetweetedText = " масенькая такая run ";
    contextBuilder.stemmedRetweetedText = " масеньк так run ";
    GeneralProcessingContext genContext =
        contextBuilder
            .buildAndInit("{\"filter_level\":\"low\",\"retweeted_status\":{\"contributors\":null,\"text\":\"масенькая такая run\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"ru\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":342646311776034816,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":1,\"created_at\":\"Thu Jun 06 14:17:01 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"342646311776034816\",\"place\":null,\"user\":{\"location\":\"я дикая.\",\"default_profile\":false,\"statuses_count\":23894,\"profile_background_tile\":true,\"lang\":\"ru\",\"profile_link_color\":\"038543\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/281915153/1358102636\",\"id\":281915153,\"following\":null,\"favourites_count\":835,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":\"ешь пей жуй дуй / спи дрочи танцуй зигуй.\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"696\",\"profile_background_color\":\"ACDED6\",\"created_at\":\"Thu Apr 14 06:46:03 +0000 2011\",\"default_profile_image\":false,\"followers_count\":220,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/3535230914/7bb68ad2c6c430ea60265795c6f9dbe7_normal.jpeg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://a0.twimg.com/profile_background_images/757887190/c819bac4d1f073d8570cfdcd8f552238.jpeg\",\"profile_background_image_url_https\":\"https://si0.twimg.com/profile_background_images/757887190/c819bac4d1f073d8570cfdcd8f552238.jpeg\",\"follow_request_sent\":null,\"url\":\"http://ebashsuka.tumblr.com\",\"utc_offset\":21600,\"time_zone\":\"Almaty\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":78,\"profile_sidebar_fill_color\":\"F6F6F6\",\"screen_name\":\"ebash_suka\",\"id_str\":\"281915153\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/3535230914/7bb68ad2c6c430ea60265795c6f9dbe7_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null},\"contributors\":null,\"text\":\"RT @ebash_suka: масенькая такая\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[{\"id\":281915153,\"name\":\"696\",\"indices\":[3,14],\"screen_name\":\"ebash_suka\",\"id_str\":\"281915153\"}]},\"in_reply_to_status_id_str\":null,\"id\":342654952931459072,\"source\":\"web\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Thu Jun 06 14:51:21 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"342654952931459072\",\"place\":null,\"user\":{\"location\":\"пакистан\",\"default_profile\":false,\"statuses_count\":2966,\"profile_background_tile\":true,\"lang\":\"ru\",\"profile_link_color\":\"B40B43\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/1311435338/1369857680\",\"id\":1311435338,\"following\":null,\"favourites_count\":17,\"protected\":false,\"profile_text_color\":\"362720\",\"description\":\"ака папа\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"ПОРНУШКА\",\"profile_background_color\":\"FF6699\",\"created_at\":\"Thu Mar 28 18:45:36 +0000 2013\",\"default_profile_image\":false,\"followers_count\":32,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/3728695674/af36ecf235ef72fcb80d196b6aa27929_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/profile_background_images/880996324/dcf9c918adf8f2af9a1fdc0ba0eec258.jpeg\",\"profile_background_image_url_https\":\"https://si0.twimg.com/profile_background_images/880996324/dcf9c918adf8f2af9a1fdc0ba0eec258.jpeg\",\"follow_request_sent\":null,\"url\":\"http://vk.com/id194742195\",\"utc_offset\":21600,\"time_zone\":\"Almaty\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":19,\"profile_sidebar_fill_color\":\"E5507E\",\"screen_name\":\"HalaVakzala\",\"id_str\":\"1311435338\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/3728695674/af36ecf235ef72fcb80d196b6aa27929_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}");

    TopicProcessingContext topicContext =
        TopicContextBuilder.buildSimple(GeneralProcessingContextBuilder.STANDART_RETWEET_KEYWORD);
    genContext.add(topicContext.getContextName(), topicContext);

    findKeywordsHandler.process(genContext);
    Assert.assertNull(genContext.getTopicContext().get(TopicProcessingContext.KEY_WORD_FOUND));

    Mockito.verify(successorTrue).process(genContext);
    Mockito.verifyNoMoreInteractions(successorTrue);
  }

  @Test
  public void testProcessAnotherLangSimle() throws Exception {

    GeneralProcessingContextBuilder contextBuilder = new GeneralProcessingContextBuilder();

    contextBuilder.normalizedTweetedText = " ты чём  @marishkaIkaeva run ";
    contextBuilder.stemmedTweetedText = " ты чём  @marishkaIkaeva run ";
    GeneralProcessingContext genContext =
        contextBuilder
            .buildAndInit("{\"filter_level\":\"medium\",\"contributors\":null,\"text\":\"@marishkaIkaeva ты о чём?! run \",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":\"marishkaIkaeva\",\"truncated\":false,\"lang\":\"ru\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[{\"id\":1280718570,\"name\":\"марина икаева\",\"indices\":[0,15],\"screen_name\":\"marishkaIkaeva\",\"id_str\":\"1280718570\"}]},\"in_reply_to_status_id_str\":\"342664216630681600\",\"id\":342670207615131648,\"source\":\"web\",\"in_reply_to_user_id_str\":\"1280718570\",\"favorited\":false,\"in_reply_to_status_id\":342664216630681600,\"retweet_count\":0,\"created_at\":\"Thu Jun 06 15:51:58 +0000 2013\",\"in_reply_to_user_id\":1280718570,\"favorite_count\":0,\"id_str\":\"342670207615131648\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":false,\"statuses_count\":1191,\"profile_background_tile\":true,\"lang\":\"ru\",\"profile_link_color\":\"FF0000\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/849506424/1368625939\",\"id\":849506424,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"Кристина \",\"profile_background_color\":\"642D8B\",\"created_at\":\"Thu Sep 27 16:09:24 +0000 2012\",\"default_profile_image\":false,\"followers_count\":25,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/3662575387/39815ecd9b67c308785a32e28f693346_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/profile_background_images/869023459/9b4507149b3c0652729745dabc02b211.jpeg\",\"profile_background_image_url_https\":\"https://si0.twimg.com/profile_background_images/869023459/9b4507149b3c0652729745dabc02b211.jpeg\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":14400,\"time_zone\":\"Abu Dhabi\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":13,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"vershkovaK4\",\"id_str\":\"849506424\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/3662575387/39815ecd9b67c308785a32e28f693346_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}");

    TopicProcessingContext topicContext =
        TopicContextBuilder.buildSimple(GeneralProcessingContextBuilder.STANDART_RETWEET_KEYWORD);
    genContext.add(topicContext.getContextName(), topicContext);

    findKeywordsHandler.process(genContext);
    Assert.assertNull(genContext.getTopicContext().get(TopicProcessingContext.KEY_WORD_FOUND));

    Mockito.verify(successorTrue).process(genContext);
    Mockito.verifyNoMoreInteractions(successorTrue);
  }

}
