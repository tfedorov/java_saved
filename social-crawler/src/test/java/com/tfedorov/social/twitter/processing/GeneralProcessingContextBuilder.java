/**
 * 
 */
package com.tfedorov.social.twitter.processing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.normalization.stemming.StemmingServiceImpl;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.tweet.TweetInfo;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;
import com.tfedorov.social.twitter.tracing.TweetTracingService;
import com.tfedorov.social.word.processing.WordProcessingContext;
import com.tfedorov.social.word.processing.WordsInfo;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import com.tfedorov.social.intention.processing.IntentionProcessingContext;

/**
 * Util class for testing
 * @author tfedorov
 *
 */
public class GeneralProcessingContextBuilder {
	
	
	//My last day of high school. #bittersweet
	//{"filter_level":"low","retweeted_status":{"contributors":null,"text":"My last day of running school. #bittersweet","geo":null,"retweeted":false,"in_reply_to_screen_name":null,"truncated":false,"lang":"en","entities":{"symbols":[],"urls":[],"hashtags":[{"text":"bittersweet","indices":[28,40]}],"user_mentions":[]},"in_reply_to_status_id_str":null,"id":340439493964357633,"source":"<a href=\"http://twitter.com/download/iphone\" rel=\"nofollow\">Twitter for iPhone<\/a>","in_reply_to_user_id_str":null,"favorited":false,"in_reply_to_status_id":null,"retweet_count":2,"created_at":"Fri May 31 12:07:55 +0000 2013","in_reply_to_user_id":null,"favorite_count":2,"id_str":"340439493964357633","place":null,"user":{"location":"","default_profile":true,"statuses_count":1196,"profile_background_tile":false,"lang":"en","profile_link_color":"0084B4","profile_banner_url":"https://pbs.twimg.com/profile_banners/635593656/1358913016","id":635593656,"following":null,"favourites_count":862,"protected":false,"profile_text_color":"333333","description":"I like to do a little bit of everything https://soundcloud.com/adriandnb","verified":false,"contributors_enabled":false,"profile_sidebar_border_color":"C0DEED","name":"Adrian Rodriguez","profile_background_color":"C0DEED","created_at":"Sat Jul 14 17:03:23 +0000 2012","default_profile_image":false,"followers_count":243,"profile_image_url_https":"https://si0.twimg.com/profile_images/3449849306/b5a251095b1b554eb667f0ec15911976_normal.jpeg","geo_enabled":false,"profile_background_image_url":"http://a0.twimg.com/images/themes/theme1/bg.png","profile_background_image_url_https":"https://si0.twimg.com/images/themes/theme1/bg.png","follow_request_sent":null,"url":null,"utc_offset":-25200,"time_zone":"Arizona","notifications":null,"profile_use_background_image":true,"friends_count":139,"profile_sidebar_fill_color":"DDEEF6","screen_name":"aaadriiannn","id_str":"635593656","profile_image_url":"http://a0.twimg.com/profile_images/3449849306/b5a251095b1b554eb667f0ec15911976_normal.jpeg","listed_count":0,"is_translator":false},"coordinates":null},"contributors":null,"text":"RT @aaadriiannn: My last day of running school. #bittersweet","geo":null,"retweeted":false,"in_reply_to_screen_name":null,"truncated":false,"entities":{"symbols":[],"urls":[],"hashtags":[{"text":"bittersweet","indices":[45,57]}],"user_mentions":[{"id":635593656,"name":"Adrian Rodriguez","indices":[3,15],"screen_name":"aaadriiannn","id_str":"635593656"}]},"in_reply_to_status_id_str":null,"id":340457313825595392,"source":"<a href=\"http://twitter.com/download/iphone\" rel=\"nofollow\">Twitter for iPhone<\/a>","in_reply_to_user_id_str":null,"favorited":false,"in_reply_to_status_id":null,"retweet_count":0,"created_at":"Fri May 31 13:18:43 +0000 2013","in_reply_to_user_id":null,"favorite_count":0,"id_str":"340457313825595392","place":null,"user":{"location":"TEXASSS❤","default_profile":false,"statuses_count":6351,"profile_background_tile":true,"lang":"en","profile_link_color":"009999","profile_banner_url":"https://pbs.twimg.com/profile_banners/411948055/1367530296","id":411948055,"following":null,"favourites_count":2473,"protected":false,"profile_text_color":"520D52","description":"Nothing worth having, comes easy.","verified":false,"contributors_enabled":false,"profile_sidebar_border_color":"EEEEEE","name":"Kendra Lanae","profile_background_color":"AA40B8","created_at":"Mon Nov 14 02:44:48 +0000 2011","default_profile_image":false,"followers_count":552,"profile_image_url_https":"https://si0.twimg.com/profile_images/3694851110/26652b3be8fc4d15794d1cea36540498_normal.jpeg","geo_enabled":true,"profile_background_image_url":"http://a0.twimg.com/images/themes/theme14/bg.gif","profile_background_image_url_https":"https://si0.twimg.com/images/themes/theme14/bg.gif","follow_request_sent":null,"url":null,"utc_offset":-21600,"time_zone":"Central Time (US & Canada)","notifications":null,"profile_use_background_image":true,"friends_count":257,"profile_sidebar_fill_color":"EFEFEF","screen_name":"KendraCampp","id_str":"411948055","profile_image_url":"http://a0.twimg.com/profile_images/3694851110/26652b3be8fc4d15794d1cea36540498_normal.jpeg","listed_count":0,"is_translator":false},"coordinates":null}
  public static final String STANDART_RETWEET = "{\"filter_level\":\"low\",\"retweeted_status\":{\"contributors\":null,\"text\":\"My last day of running school. #bittersweet\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"bittersweet\",\"indices\":[28,40]}],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":340439493964357633,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":2,\"created_at\":\"Fri May 31 12:07:55 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":2,\"id_str\":\"340439493964357633\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":1196,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/635593656/1358913016\",\"id\":635593656,\"following\":null,\"favourites_count\":862,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":\"I like to do a little bit of everything https://soundcloud.com/adriandnb\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Adrian Rodriguez\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Sat Jul 14 17:03:23 +0000 2012\",\"default_profile_image\":false,\"followers_count\":243,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/3449849306/b5a251095b1b554eb667f0ec15911976_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":-25200,\"time_zone\":\"Arizona\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":139,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"aaadriiannn\",\"id_str\":\"635593656\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/3449849306/b5a251095b1b554eb667f0ec15911976_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null},\"contributors\":null,\"text\":\"RT @aaadriiannn: My last day of running school. #bittersweet\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"bittersweet\",\"indices\":[45,57]}],\"user_mentions\":[{\"id\":635593656,\"name\":\"Adrian Rodriguez\",\"indices\":[3,15],\"screen_name\":\"aaadriiannn\",\"id_str\":\"635593656\"}]},\"in_reply_to_status_id_str\":null,\"id\":340457313825595392,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Fri May 31 13:18:43 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"340457313825595392\",\"place\":null,\"user\":{\"location\":\"TEXASSS\u2764\",\"default_profile\":false,\"statuses_count\":6351,\"profile_background_tile\":true,\"lang\":\"en\",\"profile_link_color\":\"009999\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/411948055/1367530296\",\"id\":411948055,\"following\":null,\"favourites_count\":2473,\"protected\":false,\"profile_text_color\":\"520D52\",\"description\":\"Nothing worth having, comes easy.\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"EEEEEE\",\"name\":\"Kendra Lanae\",\"profile_background_color\":\"AA40B8\",\"created_at\":\"Mon Nov 14 02:44:48 +0000 2011\",\"default_profile_image\":false,\"followers_count\":552,\"profile_image_url_https\":\"https://si0.twimg.com/profile_images/3694851110/26652b3be8fc4d15794d1cea36540498_normal.jpeg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme14/bg.gif\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme14/bg.gif\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":-21600,\"time_zone\":\"Central Time (US & Canada)\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":257,\"profile_sidebar_fill_color\":\"EFEFEF\",\"screen_name\":\"KendraCampp\",\"id_str\":\"411948055\",\"profile_image_url\":\"http://a0.twimg.com/profile_images/3694851110/26652b3be8fc4d15794d1cea36540498_normal.jpeg\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}\r\n";
  public static final String STANDART_RETWEET_KEYWORD = "running";
  public static final String STANDART_RETWEET_LANG = "en";
  public static final String STANDART_RETWEET_NORMAL_TEXT = "my last day of running school. #bittersweet";
  public static final String STANDART_RETWEET_ORIGINAL_TEXT = "my last day of running school. #bittersweet";
  
  public static final String STANDART_SIMPLE_LANG = "en";
   //Fat hens lay few eggs. - German Proverb
   //{"filter_level":"low","retweeted_status":{"contributors":null,"text":"Гирудотерапия при http://t.co/5PRHM5kHXp варикозе отзывы киев","geo":null,"retweeted":false,"in_reply_to_screen_name":null,"possibly_sensitive":false,"truncated":false,"lang":"ru","entities":{"symbols":[],"urls":[{"expanded_url":"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt","indices":[18,40],"display_url":"meshdom.ru/cache/naj.php?\u2026","url":"http://t.co/5PRHM5kHXp"}],"hashtags":[],"user_mentions":[]},"in_reply_to_status_id_str":null,"id":341858819812519936,"source":"web","in_reply_to_user_id_str":null,"favorited":false,"in_reply_to_status_id":null,"retweet_count":3,"created_at":"Tue Jun 04 10:07:48 +0000 2013","in_reply_to_user_id":null,"favorite_count":0,"id_str":"341858819812519936","place":null,"user":{"location":"","default_profile":true,"statuses_count":103,"profile_background_tile":false,"lang":"en","profile_link_color":"0084B4","id":562614112,"following":null,"favourites_count":0,"protected":false,"profile_text_color":"333333","description":"Proud mom. 7.30.09*","verified":false,"contributors_enabled":false,"profile_sidebar_border_color":"C0DEED","name":"Linda V","profile_background_color":"C0DEED","created_at":"Wed Apr 25 05:18:35 +0000 2012","default_profile_image":false,"followers_count":2,"profile_image_url_https":"https://si0.twimg.com/profile_images/2168637879/002_normal.JPG","geo_enabled":false,"profile_background_image_url":"http://a0.twimg.com/images/themes/theme1/bg.png","profile_background_image_url_https":"https://si0.twimg.com/images/themes/theme1/bg.png","follow_request_sent":null,"url":null,"utc_offset":null,"time_zone":null,"notifications":null,"profile_use_background_image":true,"friends_count":58,"profile_sidebar_fill_color":"DDEEF6","screen_name":"lindasibellv","id_str":"562614112","profile_image_url":"http://a0.twimg.com/profile_images/2168637879/002_normal.JPG","listed_count":0,"is_translator":false},"coordinates":null},"contributors":null,"text":"RT @lindasibellv: Гирудотерапия при http://t.co/5PRHM5kHXp варикозе отзывы киев","geo":null,"retweeted":false,"in_reply_to_screen_name":null,"possibly_sensitive":false,"truncated":false,"entities":{"symbols":[],"urls":[{"expanded_url":"http://meshdom.ru/cache/naj.php?p=nt4ylnsvymt","indices":[36,58],"display_url":"meshdom.ru/cache/naj.php?\u2026","url":"http://t.co/5PRHM5kHXp"}],"hashtags":[],"user_mentions":[{"id":562614112,"name":"Linda V","indices":[3,16],"screen_name":"lindasibellv","id_str":"562614112"}]},"in_reply_to_status_id_str":null,"id":341869791834947584,"source":"web","in_reply_to_user_id_str":null,"favorited":false,"in_reply_to_status_id":null,"retweet_count":0,"created_at":"Tue Jun 04 10:51:24 +0000 2013","in_reply_to_user_id":null,"favorite_count":0,"id_str":"341869791834947584","place":null,"user":{"location":"Iran","default_profile":false,"statuses_count":116,"profile_background_tile":false,"lang":"en","profile_link_color":"117A60","id":49847510,"following":null,"favourites_count":1,"protected":false,"profile_text_color":"050505","description":null,"verified":false,"contributors_enabled":false,"profile_sidebar_border_color":"BDDCAD","name":"Matthew Drake","profile_background_color":"71BF65","created_at":"Tue Jun 23 01:55:24 +0000 2009","default_profile_image":false,"followers_count":12,"profile_image_url_https":"https://si0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg","geo_enabled":false,"profile_background_image_url":"http://a0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg","profile_background_image_url_https":"https://si0.twimg.com/profile_background_images/19452019/pinupgirlclothing_2054_32529119.jpeg","follow_request_sent":null,"url":null,"utc_offset":12600,"time_zone":"Tehran","notifications":null,"profile_use_background_image":true,"friends_count":20,"profile_sidebar_fill_color":"DDFFCC","screen_name":"ghostkll","id_str":"49847510","profile_image_url":"http://a0.twimg.com/profile_images/2670710113/61f4a4b223a7be4cfe0d26b41b1c6d59_normal.jpeg","listed_count":0,"is_translator":false},"coordinates":null}
  public static final String STANDART_SIMPLE = "{\"filter_level\":\"medium\",\"contributors\":null,\"text\":\"Fat hens lay few eggs. - German Proverb\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":341884207653605376,\"source\":\"<a href=\\\"https://mobile.twitter.com\\\" rel=\\\"nofollow\\\">Mobile Web (M2)<a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":0,\"created_at\":\"Tue Jun 04 11:48:41 +0000 2013\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"341884207653605376\",\"place\":null,\"user\":{\"location\":\"\",\"default_profile\":true,\"statuses_count\":2,\"profile_background_tile\":false,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"id\":1482058116,\"following\":null,\"favourites_count\":0,\"protected\":false,\"profile_text_color\":\"333333\",\"description\":null,\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"Jonelle Muhn\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Tue Jun 04 11:47:41 +0000 2013\",\"default_profile_image\":true,\"followers_count\":0,\"profile_image_url_https\":\"https://si0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://a0.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://si0.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":null,\"time_zone\":null,\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":0,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"jonelleImuhno\",\"id_str\":\"1482058116\",\"profile_image_url\":\"http://a0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null}";
  public Set<String> stopWords;
  public Set<String> blackWords;
  public TweetsAggregationDao tweetsAggregationDao;
  public IntentionService intentionService;
  public TweetTracingService tweetTracingService;
  public TaskExecutionService taskExecutionService;
  public SentimentStrategy sentimentStrategy;
  public StemmingService stemmingService;
  
  public String normalizedRetweetedText;
  public String normalizedRetweetedTextWithInt;
  public String stemmedRetweetedText;
  public String normalizedTweetedText;
  public String cleanRetweetedText;
  public String stemmedTweetedText;  
  public List<String> tweetTermsWswList;
  
  public String lang;
  
  public IntentionProcessingContext intentionContext = new IntentionProcessingContext();
  public String cleanTweetedText;

  public GeneralProcessingContext build(Status tweet) {
    
    TweetInfo tweetInfo = new TweetInfo(tweet);
    if(lang != null){
    	tweetInfo.setTweetTextLang(lang);
    }else{
    	tweetInfo.setTweetTextLang("und");
    }
    
   
    TweetProcessingContext tweetContext = new TweetProcessingContext(tweetInfo);
    
    if(normalizedRetweetedText != null){
    	tweetContext.add(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT, normalizedRetweetedText);
    }
    
    if(normalizedRetweetedTextWithInt != null){
    	tweetContext.add(TweetProcessingContext.NORMALIZED_RETWEETED_TEXT_WITH_INTENTS, normalizedRetweetedTextWithInt);
    }
    
    
    if(cleanRetweetedText != null){
    	tweetContext.add(TweetProcessingContext.CLEAN_RETWEETED_TEXT, cleanRetweetedText);
    }
    
    if(cleanTweetedText != null){
    	tweetContext.add(TweetProcessingContext.CLEAN_TWEET_TEXT, cleanTweetedText);
    }
    
    
    
    if(stemmedRetweetedText != null){
    	tweetContext.add(TweetProcessingContext.STEMMED_RETWEETED_TEXT, stemmedRetweetedText);
    }
    
    if(normalizedTweetedText != null){
    	tweetContext.add(TweetProcessingContext.NORMALIZED_TWEET_TEXT, normalizedTweetedText);
    }
    
    if(stemmedTweetedText != null){
    	tweetContext.add(TweetProcessingContext.STEMMED_TWEET_TEXT, stemmedTweetedText);
    }
    
    
    if(tweetTermsWswList != null){
    	tweetContext.add(TweetProcessingContext.TWEET_TERMS_WSW_LIST, tweetTermsWswList);
    }
    
    WordsInfo wordsInfo = new WordsInfo(stopWords, blackWords);
    WordProcessingContext wordsContext = new WordProcessingContext(wordsInfo);
    
    if(stemmingService != null)
    	stemmingService.initializeAnalyzers(wordsInfo.getStopWords());
   
    ServicesContext servicesContext = new ServicesContextImpl(tweetsAggregationDao, intentionService, tweetTracingService, taskExecutionService, sentimentStrategy, stemmingService);
    GeneralProcessingContext result = new GeneralProcessingContextImpl(tweetContext, wordsContext , intentionContext ,servicesContext);
    return result;
  }
  
  public GeneralProcessingContext buildAndInit(String rawJson) throws TwitterException {
	  initStandartStopWords();
	  initStandartBlackWords();	  
	stemmingService = new StemmingServiceImpl();
		
	  return build(DataObjectFactory.createStatus(rawJson));
  }
  
  public GeneralProcessingContext buildWithStandRetweet() throws TwitterException {
	  this.lang = "en";
	  return buildAndInit(STANDART_RETWEET);
  }
  
  public GeneralProcessingContext buildWithStandSimple() throws TwitterException {
	  this.lang = "en";
	  return buildAndInit(STANDART_SIMPLE);
  }
  
  public void initStandartStopWords(){
    stopWords = new HashSet<String>();
    String[] stopWordsArray = {"a", "about", "above", "across", "after", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "an", "and", "another", "any", "anybody", "anyone", "anything", "anywhere", "are", "area", "areas", "aren't", "around", "as", "ask", "asked", "asking", "asks", "at", "away", "b", "back", "backed", "backing", "backs", "be", "became", "because", "become", "becomes", "been", "before", "began", "behind", "being", "beings", "below", "best", "better", "between", "big", "both", "but", "by", "c", "came", "can", "cannot", "can't", "case", "cases", "certain", "certainly", "clear", "clearly", "come", "could", "couldn't", "d", "did", "didn't", "differ", "different", "differently", "do", "does", "doesn't", "doing", "done", "don't", "down", "downed", "downing", "downs", "during", "e", "each", "early", "either", "end", "ended", "ending", "ends", "enough", "even", "evenly", "ever", "every", "everybody", "everyone", "everything", "everywhere", "f", "face", "faces", "fact", "facts", "far", "felt", "few", "find", "finds", "first", "for", "four", "from", "full", "fully", "further", "furthered", "furthering", "furthers", "g", "gave", "general", "generally", "get", "gets", "give", "given", "gives", "go", "going", "good", "goods", "got", "great", "greater", "greatest", "group", "grouped", "grouping", "groups", "h", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "her", "here", "here's", "hers", "herself", "he's", "high", "higher", "highest", "him", "himself", "his", "how", "however", "how's", "i", "i'd", "if", "i'll", "i'm", "important", "in", "interest", "interested", "interesting", "interests", "into", "is", "isn't", "it", "its", "it's", "itself", "i've", "j", "just", "k", "keep", "keeps", "kind", "knew", "know", "known", "knows", "l", "large", "largely", "last", "later", "latest", "least", "less", "let", "lets", "let's", "like", "likely", "long", "longer", "longest", "m", "made", "make", "making", "man", "many", "may", "me", "member", "members", "men", "might", "more", "most", "mostly", "mr", "mrs", "much", "must", "mustn't", "my", "myself", "n", "necessary", "need", "needed", "needing", "needs", "never", "new", "newer", "newest", "next", "no", "nobody", "non", "noone", "nor", "not", "nothing", "now", "nowhere", "number", "numbers", "o", "of", "off", "often", "old", "older", "oldest", "on", "once", "one", "only", "open", "opened", "opening", "opens", "or", "order", "ordered", "ordering", "orders", "other", "others", "ought", "our", "ours", "ourselves", "out", "over", "own", "p", "part", "parted", "parting", "parts", "per", "perhaps", "place", "places", "point", "pointed", "pointing", "points", "possible", "present", "presented", "presenting", "presents", "problem", "problems", "put", "puts", "q", "quite", "r", "rather", "really", "right", "room", "rooms", "s", "said", "same", "saw", "say", "says", "second", "seconds", "see", "seem", "seemed", "seeming", "seems", "sees", "several", "shall", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "show", "showed", "showing", "shows", "side", "sides", "since", "small", "smaller", "smallest", "so", "some", "somebody", "someone", "something", "somewhere", "state", "states", "still", "such", "sure", "t", "take", "taken", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "therefore", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "thing", "things", "think", "thinks", "this", "those", "though", "thought", "thoughts", "three", "through", "thus", "to", "today", "together", "too", "took", "toward", "turn", "turned", "turning", "turns", "two", "u", "under", "until", "up", "upon", "us", "use", "used", "uses", "v", "very", "w", "want", "wanted", "wanting", "wants", "was", "wasn't", "way", "ways", "we", "we'd", "well", "we'll", "wells", "went", "were", "we're", "weren't", "we've", "what", "what's", "when", "when's", "where", "where's", "whether", "which", "while", "who", "whole", "whom", "who's", "whose", "why", "why's", "will", "with", "within", "without", "won't", "work", "worked", "working", "works", "would", "wouldn't", "x", "y", "year", "years", "yes", "yet", "you", "you'd", "you'll", "young", "younger", "youngest", "your", "you're", "yours", "yourself", "yourselves", "you've", "z", "able", "ain't", "could've", "dear", "else", "how'd", "how'll", "mightn't", "might've", "must've", "neither", "should've", "that'll", "tis", "'tis", "twas", "'twas", "what'd", "when'd", "when'll", "where'd", "where'll", "who'd", "who'll", "why'd", "why'll", "would've", "lol", "a", "a través", "algunos", "aún", "cada", "capaz", "casi", "como", "cómo", "con", "cualquier", "cuando", "de", "debe", "deber", "debería", "decir", "dejar", "demasiado", "desde", "después", "dice", "dijo", "donde", "dónde", "él", "ella", "ellos", "en", "entonces", "entre", "era", "eran", "eres", "es", "eso", "esta", "está", "estamos", "están", "estos", "haber", "había", "habían", "hace", "hacer", "han", "hará", "haría", "hay", "he", "hecho", "hizo", "la", "las", "les", "lo", "los", "más", "me", "mi", "mientras", "mínimo", "ni", "no", "nosotros", "nuestro", "nunca", "o", "obtener", "otro", "para", "podía", "podrá", "podría", "podría haber", "podría haberlo hecho", "por", "Por qué", "por qué", "porque", "porqué", "propio", "puede", "punto", "que", "qué", "querido", "quién", "quiere", "se", "ser", "serías", "si", "sido", "sin embargo", "sino", "sobre", "sólo", "sólo", "son", "soy", "su", "su", "su", "suya", "tal vez no", "también", "tan", "tener", "tengo", "tenía", "tiene", "tiene", "tienes", "todos", "un", "usted", "Vamos a", "Vas", "voluntad", "voy", "y", "yo", "большинство", "будем", "будете", "бы", "былa", "были", "было", "быть", "в", "ваш", "вероятно", "воля", "все", "вы", "где", "для", "должен", "должно", "другой", "его", "ее", "если", "есть", "еще", "затем", "и", "из", "или", "имеет", "их", "к", "каждый", "как", "когда", "когда-либо", "которого", "который", "кто", "любой", "меня", "мне", "мог", "может", "мощь", "моя", "мы", "на", "Надо", "наименее", "нам", "нас", "наши", "не", "некоторые", "нельзя", "нет", "ни", "но", "о", "однако", "он", "она", "они", "от", "по", "после", "потому что", "почему", "почти", "с", "скорее", "слишком", "собственный", "среди", "так", "также", "там", "тебе", "то время как", "только", "У", "хотел", "час", "часто", "чем", "через", "что", "эти", "это", "я"}; 
    this.stopWords = new HashSet<String>(Arrays.asList(stopWordsArray));
  }

  public void initStandartBlackWords(){
    stopWords = new HashSet<String>();
    String[] blackWordsArray = {"blow job", "fuck", "shit"};
    this.blackWords = new HashSet<String>(Arrays.asList(blackWordsArray));
  }
}
