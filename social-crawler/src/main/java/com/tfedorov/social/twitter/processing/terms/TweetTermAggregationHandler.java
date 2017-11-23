package com.tfedorov.social.twitter.processing.terms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.word.processing.WordProcessor;
import twitter4j.Status;

import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.word.processing.NGrammsProccessor;

public abstract class TweetTermAggregationHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private final TermsProccessor processor = new TermsProccessor();

  public TweetTermAggregationHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @Override
  protected final void processImpl(GeneralProcessingContext context) {
    TweetProcessingContext twContext = context.getTweetContext();
    TopicProcessingContext topicContext = context.getTopicContext();

    ServicesContext svContext = context.getServicesContext();

    TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();

    // check context if there topic keyword was found in tweet text
    if (topicContext.get(TopicProcessingContext.KEY_WORD_FOUND) != null) {

      @SuppressWarnings("unchecked")
      List<String> stemmedTweetTermsWithoutStopWords =
          (List<String>) twContext.get(TweetProcessingContext.STEMMED_TWEET_TERMS_WSW_LIST);

      Topic topic = topicContext.getTopicInfo().getTopic();

      Set<String> topicKeywordLCSet = topicContext.getTopicInfo().getWordsSetLCSet();
      Set<String> topicStemmedKeywordLCSetLocal =
          new HashSet<String>(topicContext.getTopicInfo().getStemmedWordsSetLCSet());

      Status status = twContext.getTweetInfo().getTweet();

      // In term should not appeared key words in not default language
      String tweetTextLang = twContext.getTweetInfo().getTweetTextLang();
      if (!StemmingService.DEFAULT_LANGUAGE.equalsIgnoreCase(tweetTextLang)) {
        StemmingService stemmingService = context.getServicesContext().getStemmingService();
        for (String keyWord : topicKeywordLCSet) {
          topicStemmedKeywordLCSetLocal.add(stemmingService.stemWithoutHistory(keyWord,
              tweetTextLang));
        }
      }

      // Next line sorts list in alphabetical order and removes duplicates
      List<String> sortedTweetTermsWithoutStopWords =
      // new ArrayList<String>(new TreeSet<String>(tweetTermsWithoutStopWords));
          new ArrayList<String>(new TreeSet<String>(stemmedTweetTermsWithoutStopWords));

      List<String> unigramms =
          createUniGrammaList(sortedTweetTermsWithoutStopWords, topicKeywordLCSet,
              topicStemmedKeywordLCSetLocal);
      List<String> bigramms = createBiGrammaList(unigramms);

      List<String> trigramms = createTriGrammaList(unigramms);

      if (processTerms()) {
        for (String term : unigramms) {
          processor.proccessTerm(status, tweetsAggregationDao, topic, term,
              TopicTermAggregate.AGGREGATE_TYPE.topic_terms, context.getServicesContext()
                  .getTaskExecutionService());
        }
      }

      if (processBiTerms()) {
        for (String term : bigramms) {
          processor.proccessTerm(status, tweetsAggregationDao, topic, term,
              TopicTermAggregate.AGGREGATE_TYPE.topic_bi_terms, context.getServicesContext()
                  .getTaskExecutionService());
        }
      }

      if (processTriTerms()) {
        for (String term : trigramms) {
          processor.proccessTerm(status, tweetsAggregationDao, topic, term,
              TopicTermAggregate.AGGREGATE_TYPE.topic_tri_terms, context.getServicesContext()
                  .getTaskExecutionService());
        }
      }
    }
  }

  public List<String> createUniGrammaList(List<String> tweetTermsWithoutStopWords,
      Set<String> topicKeywordLCSet, Set<String> topicStemmedKeywordLCSet) {
    List<String> interList = new ArrayList<String>();
    for (String term : tweetTermsWithoutStopWords) {
      term = term.trim();
      if (term.length() >= 3 && !topicKeywordLCSet.contains(term)
          && !topicStemmedKeywordLCSet.contains(term) && !WordProcessor.isNumber(term)) {
        interList.add(term);
      }
    }
    return interList;
  }

  public List<String> createBiGrammaList(List<String> interList) {
    return NGrammsProccessor.splitTo2Gramms(interList);
  }

  public List<String> createTriGrammaList(List<String> interList) {
    return NGrammsProccessor.splitToNGramms(interList, 3);
  }

  protected boolean processTerms() {
    return true;
  }

  protected boolean processBiTerms() {
    return true;
  }

  protected boolean processTriTerms() {
    return true;
  }
}
