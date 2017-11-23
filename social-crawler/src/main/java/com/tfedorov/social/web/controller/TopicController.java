package com.tfedorov.social.web.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.tfedorov.social.utils.date.DateUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tfedorov.social.clustering.ClusteringAlgorithmsFactory;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.twitter.aggregation.TermsAggregationService;
import com.tfedorov.social.twitter.aggregation.TweetsAggregationService;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate.AGGREGATE_TYPE_MAPPING;
import com.tfedorov.social.twitter.aggregation.dao.TopicMentionAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.service.TopicService;
import com.tfedorov.social.utils.JsonUtils;
import com.tfedorov.social.web.service.TweeProxyUtil;

@Controller
@RequestMapping(ControllerUri.TOPIC_CONTROLLER_URI)
public class TopicController extends ExceptionController {

  private static final String DEGAULT_DATE_RANGE = "7";
  private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
  private static final String DMP_WEB_USERNAME = "<dmp-web>";
  private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final String TIME_ZONE_ID = "PST";
  public static final DateTimeZone TIME_ZONE = DateTimeZone.forTimeZone(TimeZone
      .getTimeZone(TIME_ZONE_ID));

  @Autowired
  private TopicService topicUpdateService;

  @Autowired
  private TweetsAggregationDao tweetsAggregationDao;

  @Autowired
  private TweetsAggregationService tweetsAggregationService;

  @Autowired
  private TermsAggregationService termsService;


  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public @ResponseBody
  String updateTopic(@ModelAttribute Topic topic, HttpServletRequest request) {
    if (topic.getModifydUserName() == null || topic.getModifydUserName().isEmpty()) {
      topic.setModifydUserName(DMP_WEB_USERNAME);
    }
    topicUpdateService.asyncTopicUpdate(topic);
    return ControllerResponse.STATUS_SUCCESS;
  }

  @RequestMapping(value = "/remove", method = RequestMethod.POST)
  public @ResponseBody
  String removeTopic(@ModelAttribute Topic topic) {
    tweetsAggregationService.deleteTopic(topic);
    return ControllerResponse.STATUS_SUCCESS;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public @ResponseBody
  String createTopic(@ModelAttribute Topic topic, HttpServletRequest request) {
    if (topic.getCreateUserName() == null || topic.getCreateUserName().isEmpty()) {
      topic.setCreateUserName(DMP_WEB_USERNAME);
    }
    topicUpdateService.insertTopic(topic);
    return ControllerResponse.STATUS_SUCCESS;
  }

  @RequestMapping(value = "/list", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getTopicList(@RequestParam(value = "username", required = true) String username,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "orderBy", required = false, defaultValue = "ctime") String orderBy,
      @RequestParam(value = "desc", required = false, defaultValue = "true") boolean isDesc)
      throws IOException, JSONException {
    List<Topic> topics =
        topicUpdateService.getByUserIdSorted(username, offset, limit, orderBy, isDesc);
    return JsonUtils.wrapTableResponse(topics).toString();
  }

  @RequestMapping(value = "/list/{type}", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getTypeTopics(@PathVariable(value = "type") TopicType type,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "orderBy", required = false, defaultValue = "ctime") String orderBy,
      @RequestParam(value = "desc", required = false, defaultValue = "true") boolean isDesc)
      throws IOException, JSONException {
    return JsonUtils.wrapTableResponse(
        topicUpdateService.getTypeTopics(type, offset, limit, orderBy, isDesc)).toString();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  Topic getTopic(@PathVariable(value = "id") BigInteger topicId) {
    return topicUpdateService.getTopicById(topicId);
  }

  @RequestMapping(value = "/{id}/terms/uno", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getTags(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "limit", required = false, defaultValue = "30") int limit,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws IOException, JSONException {
    int period = Integer.parseInt(daysRange);
    AGGREGATE_TYPE_MAPPING type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;
    return termsService.selectTermsAggregates(topicId, period, type, limit).toString();
  }


  @RequestMapping(value = "/{id}/terms/bi", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getBiTags(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "limit", required = false, defaultValue = "30") int limit,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws IOException, JSONException {
    int period = Integer.parseInt(daysRange);
    AGGREGATE_TYPE_MAPPING type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p;
    return termsService.selectTermsAggregates(topicId, period, type, limit).toString();
  }

  @RequestMapping(value = "/{id}/terms/tri", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getTriTags(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "limit", required = false, defaultValue = "30") int limit,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws IOException, JSONException {

    int period = Integer.parseInt(daysRange);
    AGGREGATE_TYPE_MAPPING type = PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_tri_terms_p;
    return termsService.selectTermsAggregates(topicId, period, type, limit).toString();
  }

  @RequestMapping(value = "/{id}/terms/cluster", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getClusters(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "limit", required = false, defaultValue = "50") int limit,
      @RequestParam(value = "cluster", required = false, defaultValue = "10") int clusterCount,
      @RequestParam(value = "date", required = false) String date,
      @RequestParam(value = "days_range", required = false, defaultValue = "1") int daysRange,
      @RequestParam(value = "algorithm", required = false, defaultValue = ClusteringAlgorithmsFactory.JGRAPHT_STOREWAGNER) String algorithm,
      @RequestParam(value = "weightAlgorithm", required = false, defaultValue = "0") int weightAlgorithm)
      throws JsonGenerationException, JsonMappingException, IOException {
    final DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN).withZone(TIME_ZONE);
    return termsService.getClusterData(topicId, limit, clusterCount,
        date != null ? fmt.parseDateTime(date).toDateMidnight() : null, daysRange, algorithm,
        weightAlgorithm).toString();
  }

  @RequestMapping(value = "/{id}/terms/cluster/algorithms", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getClusterAlgorithms() {
    return termsService.getClusterAlgorithms().toString();
  }

  @RequestMapping(value = "/{id}/tweets/popular", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getRetweets(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "orderBy", required = false, defaultValue = "retweets") String orderBy,
      @RequestParam(value = "desc", required = false, defaultValue = "true") boolean isDesc,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws JsonGenerationException, JsonMappingException, IOException, JSONException {

    List<TopicTweetAggregate> tweets =
        tweetsAggregationDao.selectAggregationTopicTweetByFilter(topicId, offset, limit,
            DateUtils.getIntervalToToday(Integer.valueOf(daysRange)), orderBy, isDesc,
            TopicTweetAggregate.AGGREGATE_TYPE.popular_tweets);

    return JsonUtils.wrapTableResponse(tweets).toString();

  }

  @RequestMapping(value = "/{id}/tweets/intents", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getIntents(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "orderBy", required = false, defaultValue = "retweets") String orderBy,
      @RequestParam(value = "desc", required = false, defaultValue = "true") boolean isDesc,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws JsonGenerationException, JsonMappingException, IOException, JSONException {
    List<TopicTweetAggregate> tweets =
        tweetsAggregationDao.selectAggregationTopicTweetByFilter(topicId, offset, limit,
            DateUtils.getIntervalToToday(Integer.valueOf(daysRange)), orderBy, isDesc,
            TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);
    return JsonUtils.wrapTableResponse(tweets).toString();
  }

  @RequestMapping(value = "/{id}/tweets/keyIntents", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getKeywordIntents(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(value = "orderBy", required = false, defaultValue = "retweets") String orderBy,
      @RequestParam(value = "desc", required = false, defaultValue = "true") boolean isDesc,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws JsonGenerationException, JsonMappingException, IOException, JSONException {
    List<TopicTweetAggregate> tweets =
        tweetsAggregationDao.selectAggregationTopicTweetByFilter(topicId, offset, limit,
            DateUtils.getIntervalToToday(Integer.valueOf(daysRange)), orderBy, isDesc,
            TopicTweetAggregate.AGGREGATE_TYPE.keyword_intention_tweets);
    return JsonUtils.wrapTableResponse(tweets).toString();
  }

  @RequestMapping(value = "/{id}/tweets/latest", method = RequestMethod.POST, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getLatestTweets(@PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "term", required = true) String term,
      @RequestParam(value = "date", required = false) String date,
      @RequestParam(value = "days_range", required = false, defaultValue = "1") String daysRange,
      @RequestParam(value = "term_type_map", required = true) AGGREGATE_TYPE_MAPPING termTypeMapping)
      throws JsonGenerationException, JsonMappingException, IOException, JSONException {

    final DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN).withZone(TIME_ZONE);
    TopicTermAggregate topic;
    if (date == null) {
      topic =
          termsService.getLatestTweetIdsByRange(topicId, term, Integer.valueOf(daysRange),
              termTypeMapping);
    } else {
      topic =
          termsService.getLatestTweetIds(topicId, term, fmt.parseDateTime(date).toDateMidnight(),
              termTypeMapping);
    }
    return TweeProxyUtil.processPostRequest(topic);
  }

  @RequestMapping(value = "/{id}/mentions", method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8)
  public @ResponseBody
  String getMentions(
      @PathVariable(value = "id") BigInteger topicId,
      @RequestParam(value = "days_range", required = false, defaultValue = DEGAULT_DATE_RANGE) String daysRange)
      throws JsonGenerationException, JsonMappingException, IOException, JSONException {
    List<TopicMentionAggregate> mentions =
        tweetsAggregationDao.selectAggregationTopicMentionByFilter(topicId,
            DateUtils.getIntervalToToday(Integer.valueOf(daysRange)));
    return JsonUtils.wrapTableResponse(mentions).toString();
  }
}
