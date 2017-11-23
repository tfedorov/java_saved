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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

// for javadoc

/**
 * {@link Analyzer} for French language.
 * <p>
 * Supports an external list of stopwords (words that will not be indexed at all) and an external
 * list of exclusions (word that will not be stemmed, but indexed). A default set of stopwords is
 * used unless an alternative list is specified, but the exclusion list is empty by default.
 * </p>
 * 
 * <a name="version"/>
 * <p>
 * You must specify the required {@link Version} compatibility when creating FrenchAnalyzer:
 * <ul>
 * <li>As of 3.6, FrenchLightStemFilter is used for less aggressive stemming.
 * <li>As of 3.1, Snowball stemming is done with SnowballFilter, LowerCaseFilter is used prior to
 * StopFilter, and ElisionFilter and Snowball stopwords are used by default.
 * <li>As of 2.9, StopFilter preserves position increments
 * </ul>
 * 
 * <p>
 * <b>NOTE</b>: This class uses the same {@link Version} dependent settings as
 * {@link StandardAnalyzer}.
 * </p>
 */
public final class FrenchNormalizationAnalyzer extends StopwordAnalyzerBase {

  /**
   * Extended list of typical French stopwords.
   * 
   * @deprecated (3.1) remove in Lucene 5.0 (index bw compat)
   */
  @Deprecated
  private final static String[] FRENCH_STOP_WORDS = {"a", "afin", "ai", "ainsi", "apr√®s",
      "attendu", "au", "aujourd", "auquel", "aussi", "autre", "autres", "aux", "auxquelles",
      "auxquels", "avait", "avant", "avec", "avoir", "c", "car", "ce", "ceci", "cela", "celle",
      "celles", "celui", "cependant", "certain", "certaine", "certaines", "certains", "ces", "cet",
      "cette", "ceux", "chez", "ci", "combien", "comme", "comment", "concernant", "contre", "d",
      "dans", "de", "debout", "dedans", "dehors", "del√†", "depuis", "derri√®re", "des",
      "d√©sormais", "desquelles", "desquels", "dessous", "dessus", "devant", "devers", "devra",
      "divers", "diverse", "diverses", "doit", "donc", "dont", "du", "duquel", "durant", "d√®s",
      "elle", "elles", "en", "entre", "environ", "est", "et", "etc", "etre", "eu", "eux",
      "except√©", "hormis", "hors", "h√©las", "hui", "il", "ils", "j", "je", "jusqu", "jusque",
      "l", "la", "laquelle", "le", "lequel", "les", "lesquelles", "lesquels", "leur", "leurs",
      "lorsque", "lui", "l√†", "ma", "mais", "malgr√©", "me", "merci", "mes", "mien", "mienne",
      "miennes", "miens", "moi", "moins", "mon", "moyennant", "m√™me", "m√™mes", "n", "ne", "ni",
      "non", "nos", "notre", "nous", "n√©anmoins", "n√іtre", "n√іtres", "on", "ont", "ou", "outre",
      "o√є", "par", "parmi", "partant", "pas", "pass√©", "pendant", "plein", "plus", "plusieurs",
      "pour", "pourquoi", "proche", "pr√®s", "puisque", "qu", "quand", "que", "quel", "quelle",
      "quelles", "quels", "qui", "quoi", "quoique", "revoici", "revoil√†", "s", "sa", "sans",
      "sauf", "se", "selon", "seront", "ses", "si", "sien", "sienne", "siennes", "siens", "sinon",
      "soi", "soit", "son", "sont", "sous", "suivant", "sur", "ta", "te", "tes", "tien", "tienne",
      "tiennes", "tiens", "toi", "ton", "tous", "tout", "toute", "toutes", "tu", "un", "une", "va",
      "vers", "voici", "voil√†", "vos", "votre", "vous", "vu", "v√іtre", "v√іtres", "y", "√†",
      "√Іa", "√®s", "√©t√©", "√™tre", "√і"};

  /** File containing default French stopwords. */
  public final static String DEFAULT_STOPWORD_FILE = "french_stop.txt";

  /** Default set of articles for ElisionFilter */
  public static final CharArraySet DEFAULT_ARTICLES = CharArraySet
      .unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList("l", "m", "t", "qu",
          "n", "s", "j", "d", "c", "jusqu", "quoiqu", "lorsqu", "puisqu"), true));

  /**
   * Contains words that should be indexed but not stemmed.
   */
  private final CharArraySet excltable;

  /**
   * Returns an unmodifiable instance of the default stop-words set.
   * 
   * @return an unmodifiable instance of the default stop-words set.
   */
  public static CharArraySet getDefaultStopSet() {
    return DefaultSetHolder.DEFAULT_STOP_SET;
  }

  private static class DefaultSetHolder {
    /** @deprecated (3.1) remove this in Lucene 5.0, index bw compat */
    @Deprecated
    static final CharArraySet DEFAULT_STOP_SET_30 = CharArraySet.unmodifiableSet(new CharArraySet(
        Version.LUCENE_CURRENT, Arrays.asList(FRENCH_STOP_WORDS), false));
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

  /**
   * Builds an analyzer with the default stop words ({@link #getDefaultStopSet}).
   */
  public FrenchNormalizationAnalyzer(Version matchVersion) {
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
  public FrenchNormalizationAnalyzer(Version matchVersion, CharArraySet stopwords) {
    this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
  }

  /**
   * Builds an analyzer with the given stop words
   * 
   * @param matchVersion lucene compatibility version
   * @param stopwords a stopword set
   * @param stemExclutionSet a stemming exclusion set
   */
  public FrenchNormalizationAnalyzer(Version matchVersion, CharArraySet stopwords,
      CharArraySet stemExclutionSet) {
    super(matchVersion, stopwords);
    this.excltable =
        CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclutionSet));
  }

  /**
   * Creates {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} used to tokenize all
   * the text in the provided {@link Reader}.
   * 
   * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} built from a
   *         {@link StandardTokenizer} filtered with {@link StandardFilter}, {@link ElisionFilter},
   *         {@link LowerCaseFilter}, {@link StopFilter}, {@link KeywordMarkerFilter} if a stem
   *         exclusion set is provided, and {@link FrenchLightStemFilter}
   */
  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
    if (matchVersion.onOrAfter(Version.LUCENE_31)) {
      final Tokenizer source = new StandardTokenizer(matchVersion, reader);
      TokenStream result = new StandardFilter(matchVersion, source);
      result = new ElisionFilter(result, DEFAULT_ARTICLES);
      result = new LowerCaseFilter(matchVersion, result);
      result = new StopFilter(matchVersion, result, stopwords);
      if (!excltable.isEmpty()) {
        result = new KeywordMarkerFilter(result, excltable);
      }
      return new TokenStreamComponents(source, result);
    } else {
      final Tokenizer source = new StandardTokenizer(matchVersion, reader);
      TokenStream result = new StandardFilter(matchVersion, source);
      result = new StopFilter(matchVersion, result, stopwords);
      if (!excltable.isEmpty()) {
        result = new KeywordMarkerFilter(result, excltable);
      }
      // Convert to lowercase after stemming!
      return new TokenStreamComponents(source, new LowerCaseFilter(matchVersion, result));
    }
  }
}
