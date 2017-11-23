package com.tfedorov.social.normalization.analyzing;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.ru.RussianLetterTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

/**
 * {@link Analyzer} for Russian language.
 * <p>
 * Supports an external list of stopwords (words that will not be indexed at all). A default set of
 * stopwords is used unless an alternative list is specified.
 * </p>
 * <a name="version"/>
 * <p>
 * You must specify the required {@link Version} compatibility when creating RussianAnalyzer:
 * <ul>
 * <li>As of 3.1, StandardTokenizer is used, Snowball stemming is done with SnowballFilter, and
 * Snowball stopwords are used by default.
 * </ul>
 */
public final class RussianNormalizationAnalyzer extends StopwordAnalyzerBase {
  /**
   * List of typical Russian stopwords. (for backwards compatibility)
   * 
   * @deprecated (3.1) Remove this for LUCENE 5.0
   */
  @Deprecated
  private static final String[] RUSSIAN_STOP_WORDS_30 = {"–∞", "–±–µ–Ј", "–±–Њ–ї–µ–µ", "–±—Л",
      "–±—Л–ї", "–±—Л–ї–∞", "–±—Л–ї–Є", "–±—Л–ї–Њ", "–±—Л—В—М", "–≤", "–≤–∞–Љ", "–≤–∞—Б",
      "–≤–µ—Б—М", "–≤–Њ", "–≤–Њ—В", "–≤—Б–µ", "–≤—Б–µ–≥–Њ", "–≤—Б–µ—Е", "–≤—Л", "–≥–і–µ", "–і–∞",
      "–і–∞–∂–µ", "–і–ї—П", "–і–Њ", "–µ–≥–Њ", "–µ–µ", "–µ–є", "–µ—О", "–µ—Б–ї–Є", "–µ—Б—В—М",
      "–µ—Й–µ", "–∂–µ", "–Ј–∞", "–Ј–і–µ—Б—М", "–Є", "–Є–Ј", "–Є–ї–Є", "–Є–Љ", "–Є—Е", "–Ї",
      "–Ї–∞–Ї", "–Ї–Њ", "–Ї–Њ–≥–і–∞", "–Ї—В–Њ", "–ї–Є", "–ї–Є–±–Њ", "–Љ–љ–µ", "–Љ–Њ–∂–µ—В", "–Љ—Л",
      "–љ–∞", "–љ–∞–і–Њ", "–љ–∞—И", "–љ–µ", "–љ–µ–≥–Њ", "–љ–µ–µ", "–љ–µ—В", "–љ–Є", "–љ–Є—Е",
      "–љ–Њ", "–љ—Г", "–Њ", "–Њ–±", "–Њ–і–љ–∞–Ї–Њ", "–Њ–љ", "–Њ–љ–∞", "–Њ–љ–Є", "–Њ–љ–Њ", "–Њ—В",
      "–Њ—З–µ–љ—М", "–њ–Њ", "–њ–Њ–і", "–њ—А–Є", "—Б", "—Б–Њ", "—В–∞–Ї", "—В–∞–Ї–∂–µ", "—В–∞–Ї–Њ–є",
      "—В–∞–Љ", "—В–µ", "—В–µ–Љ", "—В–Њ", "—В–Њ–≥–Њ", "—В–Њ–∂–µ", "—В–Њ–є", "—В–Њ–ї—М–Ї–Њ",
      "—В–Њ–Љ", "—В—Л", "—Г", "—Г–∂–µ", "—Е–Њ—В—П", "—З–µ–≥–Њ", "—З–µ–є", "—З–µ–Љ", "—З—В–Њ",
      "—З—В–Њ–±—Л", "—З—М–µ", "—З—М—П", "—Н—В–∞", "—Н—В–Є", "—Н—В–Њ", "—П"};

  /** File containing default Russian stopwords. */
  public final static String DEFAULT_STOPWORD_FILE = "russian_stop.txt";

  private static class DefaultSetHolder {
    /** @deprecated (3.1) remove this for Lucene 5.0 */
    @Deprecated
    static final CharArraySet DEFAULT_STOP_SET_30 = CharArraySet.unmodifiableSet(new CharArraySet(
        Version.LUCENE_CURRENT, Arrays.asList(RUSSIAN_STOP_WORDS_30), false));
    static final CharArraySet DEFAULT_STOP_SET;

    static {
      try {
        DEFAULT_STOP_SET =
            WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class,
                DEFAULT_STOPWORD_FILE, IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
      } catch (IOException ex) {
        // default set should always be present as it is part of the
        // distribution (JAR)
        throw new RuntimeException("Unable to load default stopword set", ex);
      }
    }
  }

  private final CharArraySet stemExclusionSet;

  /**
   * Returns an unmodifiable instance of the default stop-words set.
   * 
   * @return an unmodifiable instance of the default stop-words set.
   */
  public static CharArraySet getDefaultStopSet() {
    return DefaultSetHolder.DEFAULT_STOP_SET;
  }

  public RussianNormalizationAnalyzer(Version matchVersion) {
    this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_31)
        ? DefaultSetHolder.DEFAULT_STOP_SET
        : DefaultSetHolder.DEFAULT_STOP_SET_30);
  }

  /**
   * Builds an analyzer with the given stop words
   * 
   * @param matchVersion lucene compatibility version
   * @param stopwords a stopword set
   */
  public RussianNormalizationAnalyzer(Version matchVersion, CharArraySet stopwords) {
    this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
  }

  /**
   * Builds an analyzer with the given stop words
   * 
   * @param matchVersion lucene compatibility version
   * @param stopwords a stopword set
   * @param stemExclusionSet a set of words not to be stemmed
   */
  public RussianNormalizationAnalyzer(Version matchVersion, CharArraySet stopwords,
      CharArraySet stemExclusionSet) {
    super(matchVersion, stopwords);
    this.stemExclusionSet =
        CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet));
  }

  /**
   * Creates {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} used to tokenize all
   * the text in the provided {@link Reader}.
   * 
   * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} built from a
   *         {@link StandardTokenizer} filtered with {@link StandardFilter}, {@link LowerCaseFilter}
   *         , {@link StopFilter} , {@link KeywordMarkerFilter} if a stem exclusion set is provided,
   *         and {@link SnowballFilter}
   */
  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
    if (matchVersion.onOrAfter(Version.LUCENE_31)) {
      final Tokenizer source = new StandardTokenizer(matchVersion, reader);
      TokenStream result = new StandardFilter(matchVersion, source);
      result = new LowerCaseFilter(matchVersion, result);
      result = new StopFilter(matchVersion, result, stopwords);
      if (!stemExclusionSet.isEmpty()) {
        result = new KeywordMarkerFilter(result, stemExclusionSet);
      }
      return new TokenStreamComponents(source, result);
    } else {
      final Tokenizer source = new RussianLetterTokenizer(matchVersion, reader);
      TokenStream result = new LowerCaseFilter(matchVersion, source);
      result = new StopFilter(matchVersion, result, stopwords);
      if (!stemExclusionSet.isEmpty()) {
        result = new KeywordMarkerFilter(result, stemExclusionSet);
      }
      return new TokenStreamComponents(source, result);
    }
  }
}
