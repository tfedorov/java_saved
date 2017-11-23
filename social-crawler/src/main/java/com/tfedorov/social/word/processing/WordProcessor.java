package com.tfedorov.social.word.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.utils.UnicodeUtils;

public final class WordProcessor {
  
  private WordProcessor(){}

  public static final Pattern URLS_TRIMMER = Pattern
      .compile("(?:\\b(?:http|ftp|www\\.)\\S+\\b)|(?:\\b\\S+\\.com\\S*\\b)");

  // EXCLUDING ['] - used in stop words, [-] used in terms, excluded # and @ symbols
  public static final Pattern SEPARATORS =
      Pattern
          .compile("[\\.\\,\\;\\!\\?\\:\\(\\)\\[\\]\\{\\}\\<\\>\\&\\$\\%\\*\\^\\~\\*\\=\\+\\\"\\s\\/\\|\\\\]+");

  // TODO: review ranges - still errors
  public static final String EMOTIONS_SYMBOLS_PATTERN_STR = "[^\\x00-\\x7f-\\x80-\\xad]";

  public static String hasTextWordFromSet(String text, Set<String> wordsSet) {

    String textLower = text.toLowerCase();

    for (String word : wordsSet) {
      if (textLower.indexOf(word) >= 0) {
        return word;
      }
    }

    return null;
  }



  public static NormalizedTweetText normalizeText(Status tweet, String textLanguage,
      StemmingService stemmingService) {
    NormalizedTweetText result = new NormalizedTweetText();
    // get text
    String tweetText = tweet.getText();
    // clear
    tweetText = removeEmotionChars(tweetText);
    tweetText = removelinks(tweetText);
    // set clean text
    result.setCleanText(tweetText);
    // set prepared for mutching text
    result.setPreparedText(" " + tweetText.toLowerCase() + " ");
    // remove hash tags and mentions

    for (int i = 0; i < tweet.getHashtagEntities().length; i++) {
      HashtagEntity ht = tweet.getHashtagEntities()[i];
      CharSequence c = "#" + ht.getText();
      // replace
      tweetText = tweetText.replace(c, "");
    }

    for (int i = 0; i < tweet.getUserMentionEntities().length; i++) {
      UserMentionEntity um = tweet.getUserMentionEntities()[i];
      CharSequence c = "@" + um.getScreenName();
      // replace
      tweetText = tweetText.replace(c, "");
    }
    // normalize text and create terms ONLY IF TRUE
    StringBuilder normalizedTextBuilder = new StringBuilder(" ");
    StringBuilder stemmedNormalizedTextBuilder = new StringBuilder(" ");
    List<String> normalizedTerms = stemmingService.getNormalizedTerms(tweetText, textLanguage);
    List<String> stemmedNormalizedTerms = new ArrayList<String>();
    // goes by all terms and check by number
    for (String term : normalizedTerms) {
      normalizedTextBuilder.append(term + " ");
      // stem and add to stemmed normalize term and stemmed normalized text
      String stemmedTerm = stemmingService.stem(term, textLanguage);
      stemmedNormalizedTerms.add(stemmedTerm);
      stemmedNormalizedTextBuilder.append(stemmedTerm + " ");
    }

    // put normalized terms into result
    result.setTextTerms(normalizedTerms);
    result.setStemmedTextTerms(stemmedNormalizedTerms);

    // add hash tags and mention to normalized text
    for (int i = 0; i < tweet.getHashtagEntities().length; i++) {
      HashtagEntity ht = tweet.getHashtagEntities()[i];
      //
      normalizedTextBuilder.append(" #");
      normalizedTextBuilder.append(ht.getText());
      normalizedTextBuilder.append(" ");
      // TODO: revise if required
      stemmedNormalizedTextBuilder.append(" #");
      stemmedNormalizedTextBuilder.append(ht.getText());
      stemmedNormalizedTextBuilder.append(" ");
    }

    for (int i = 0; i < tweet.getUserMentionEntities().length; i++) {
      UserMentionEntity um = tweet.getUserMentionEntities()[i];
      //
      normalizedTextBuilder.append(" @");
      normalizedTextBuilder.append(um.getScreenName());
      normalizedTextBuilder.append(" ");
      // TODO: revise if required
      stemmedNormalizedTextBuilder.append(" @");
      stemmedNormalizedTextBuilder.append(um.getScreenName());
      stemmedNormalizedTextBuilder.append(" ");
    }
    // add to result
    result.setNormalizedText(normalizedTextBuilder.toString());
    result.setStemmedNormalizedText(stemmedNormalizedTextBuilder.toString());
    //
    return result;
  }

  public static String normalizeTextWithoutTerms(String text, Set<String> intentTerms) {
    StringBuilder builder = new StringBuilder(" ");

    Map<String, String> tempWords = new HashMap<String, String>();
    int i = 0;
    for (String term : intentTerms) {
      text = text.replace(term, "temp_string" + i);
      tempWords.put("temp_string" + i, term);
      i++;
    }

    String[] tokens = SEPARATORS.split(text.trim());

    for (String token : tokens) {
      String nmToken = token.trim().toLowerCase();
      String word = "";
      if (tempWords.containsKey(nmToken)) {
        word = tempWords.get(nmToken);
      } else {
        word = nmToken;
      }
      builder.append(word + " ");
    }
    return builder.toString();
  }


  public static String removelinks(String text) {
    Matcher m = URLS_TRIMMER.matcher(text);
    // TODO revise it
    StringBuffer out = new StringBuffer();

    while (m.find()) {
      m.appendReplacement(out, "");
    }
    m.appendTail(out);

    return out.toString();
  }


  public static String[] splitText(String text) {
    return SEPARATORS.split(text.trim());
  }

  public static boolean isNumber(String term) {
    try {
      Double.parseDouble(term);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  /**
   * Emotion symbols http://codepoints.net/emoticons cause mysql erros on insert tweets to db This
   * method clean-up these symbols
   * 
   * @param text
   * @return
   */
  public static String removeEmotionChars(String text) {
    return UnicodeUtils.filterEmoticons(text);
  }
}
