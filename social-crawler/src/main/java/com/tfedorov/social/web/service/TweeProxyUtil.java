package com.tfedorov.social.web.service;

import java.io.IOException;
import java.math.BigDecimal;

import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import org.apache.http.client.HttpResponseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class TweeProxyUtil {

  private static final String BLANK = "";

  private static final String TWEETS = "tweets";

  private TweeProxyUtil() {}

  private static final String BR = "<BR><BR>";

  private static final String REQUEST_URL = "https://twitter.com/x/status/%1$s";

  private static String tweetSelector;

  private static HttpProxyService proxyDefault;

  @Autowired
  @Qualifier("httpProxyServiceImpl")
  public void setProxyDefault(HttpProxyService proxyDefault) {
    TweeProxyUtil.proxyDefault = proxyDefault;
  }

  @Value(value = "${latest.tweet.selector:p.js-tweet-text}")
  public void setTweetSelector(String tweetSelector) {
    TweeProxyUtil.tweetSelector = tweetSelector;
  }

  public static String processPostRequest(final TopicTermAggregate topicTerm) throws IOException {

    StringBuilder builder = new StringBuilder();
    if (topicTerm != null) {
      builder.append(getTweetText(topicTerm.getTw1Id(), proxyDefault));
      builder.append(getTweetText(topicTerm.getTw2Id(), proxyDefault));
      builder.append(getTweetText(topicTerm.getTw3Id(), proxyDefault));
    }
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();

    node.put(TWEETS, builder.toString());

    return node.toString();
  }

  private static String getTweetText(final BigDecimal id, HttpProxyService proxyDefault)
      throws IOException {
    if (id != null) {
      try {
        final String html = proxyDefault.doGet(String.format(REQUEST_URL, id));
        Element element = Jsoup.parse(html).select(String.format(tweetSelector, id)).first();
        if (element != null) {
          return element.text() + BR;
        }
      } catch (HttpResponseException exeption) {
        return BLANK;
      }
    }
    return BLANK;
  }
}
