package com.tfedorov.social.normalization;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.tfedorov.social.normalization.stemming.stemmers.SmartStemmer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import com.tfedorov.social.normalization.analyzing.NormalizationAnalyzingFactory;
import com.tfedorov.social.normalization.stemming.NormalizationStemmingFactory;

public class NormalizationProcessor {
  private static final String SPACE = " ";
  private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
  private static final Pattern DOUBLE_PATTERN = Pattern.compile("\\d+.\\d+|\\d+,\\d+");

  @Deprecated
  public String steamStringLine(String text, Analyzer analyzer) throws IOException {
    StringBuilder result = new StringBuilder();
    if (text != null && text.trim().length() > 0) {
      StringReader tReader = new StringReader(text);
      TokenStream tStream = analyzer.tokenStream(null, tReader);
      tStream.reset();
      CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);
      while (tStream.incrementToken()) {
        result.append(new String(term.buffer(), 0, term.length()));
        //
        result.append(SPACE);
      }
    }
    // If, for some reason, the stemming did not happen, return the original text
    if (result.length() == 0) {
      result.append(text);
    }
    return result.toString().trim();
  }

  @Deprecated
  public List<String> steamStringLineToArray(String text, Analyzer analyzer) throws IOException {
    List<String> result = new ArrayList<String>();
    if (text != null && text.trim().length() > 0) {
      StringReader tReader = new StringReader(text);
      TokenStream tStream = analyzer.tokenStream(null, tReader);
      tStream.reset();
      CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);
      while (tStream.incrementToken()) {
        result.add(new String(term.buffer(), 0, term.length()));
      }
    }
    if (result.isEmpty()) {
      result.add(text);
    }
    return result;
  }

  /**
   * NEW VERSION
   */

  public List<String> normalizeString(String string, String textLanguage) throws IOException {
    List<String> result = new ArrayList<String>();
    if (string != null && string.trim().length() > 0) {
      StringReader tReader = new StringReader(string);
      Analyzer analyzer = getAnalyzerByLanguage(textLanguage);
      TokenStream tStream = analyzer.tokenStream(null, tReader);
      tStream.reset();
      CharTermAttribute term = tStream.addAttribute(CharTermAttribute.class);
      while (tStream.incrementToken()) {
        String res = new String(term.buffer(), 0, term.length());
        if (!INTEGER_PATTERN.matcher(res).find() && !DOUBLE_PATTERN.matcher(res).find()) {
          result.add(res);
        }
      }
    }
    return result;
  }

  public String stemString(String string, String textLanguage) {
    SmartStemmer stemmer = getStemmerByLanguage(textLanguage);
    return stemmer.stem(string);
  }

  public boolean checkStemmingLanguage(String lang) {
    return NormalizationStemmingFactory.instance().getStemmersMap().containsKey(lang);
  }

  private SmartStemmer getStemmerByLanguage(String lang) {
    return NormalizationStemmingFactory.instance().getStemmerByLanguage(lang);
  }

  private Analyzer getAnalyzerByLanguage(String lang) {
    Analyzer analyzer = NormalizationAnalyzingFactory.instance().getAnalyzerByLanguage(lang);
    if (analyzer == null) {
      return NormalizationAnalyzingFactory.instance().getSimpleAnalyzer();
    } else {
      return analyzer;
    }
  }

  public void initializeNormalizingAnalyzers(Set<String> stopWords) {
    CharArraySet stopWordsSet = new CharArraySet(Version.LUCENE_42, stopWords, true);
    NormalizationAnalyzingFactory.instance().init(stopWordsSet);
  }
}
