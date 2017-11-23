package com.tfedorov.social.normalization.analyzing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class NormalizationAnalyzingFactory {
  private static final String SIMPLE_ANALYZER = "simple";

  private static NormalizationAnalyzingFactory instance;

  // Porter stemming processors list by language
  private Map<String, Analyzer> porterAnalyzers;

  public void init(CharArraySet stopWords) {
    porterAnalyzers = new HashMap<String, Analyzer>();
    // /
    // initialize porter processors
    // /
    // ar - Arabic
    porterAnalyzers.put("ar", new ArabicNormalizationAnalyzer(Version.LUCENE_42));

    // bg - Bulgarian
    porterAnalyzers.put("bg", new BulgarianNormalizationAnalyzer(Version.LUCENE_42));

    // br - Brazilian
    porterAnalyzers.put("br", new BrazilianNormalizationAnalyzer(Version.LUCENE_42));

    // ca - Catalan
    porterAnalyzers.put("ca", new CatalanNormalizationAnalyzer(Version.LUCENE_42));

    // cjk - CJK
    porterAnalyzers.put("cjk", new CJKAnalyzer(Version.LUCENE_42));

    // ja - japan
    porterAnalyzers.put("ja", new CJKAnalyzer(Version.LUCENE_42));

    // ko - Korean
    porterAnalyzers.put("ko", new CJKAnalyzer(Version.LUCENE_42));

    // zh - chinese
    porterAnalyzers.put("zh", new ChineseNormalizationAnalyzer(Version.LUCENE_42));

    // cz - Czech
    porterAnalyzers.put("cz", new CzechNormalizationAnalyzer(Version.LUCENE_42));

    // da - Danish
    porterAnalyzers.put("da", new DanishNormalizationAnalyzer(Version.LUCENE_42));

    // de - German
    porterAnalyzers.put("de", new GermanNormalizationAnalyzer(Version.LUCENE_42));

    // el - Greek
    porterAnalyzers.put("el", new GreekNormalizationAnalyzer(Version.LUCENE_42));

    // en - English
    porterAnalyzers.put("en", new EnglishNormalizationAnalyzer(Version.LUCENE_42, stopWords));

    // es - Spanish
    porterAnalyzers.put("es", new SpanishNormalizationAnalyzer(Version.LUCENE_42, stopWords));

    // eu - Basque
    porterAnalyzers.put("eu", new BasqueNormalizationAnalyzer(Version.LUCENE_42));

    // fa - Persian
    porterAnalyzers.put("fa", new PersianAnalyzer(Version.LUCENE_42));

    // fi - Finnish
    porterAnalyzers.put("fi", new FinnishNormalizationAnalyzer(Version.LUCENE_42));

    // fr - French
    porterAnalyzers.put("fr", new FrenchNormalizationAnalyzer(Version.LUCENE_42));

    // ga - Irish
    porterAnalyzers.put("ga", new IrishNormalizationAnalyzer(Version.LUCENE_42));

    // gl - Galician
    porterAnalyzers.put("gl", new GalicianNormalizationAnalyzer(Version.LUCENE_42));

    // hi - Hindi
    porterAnalyzers.put("hi", new HindiNormalizationAnalyzer(Version.LUCENE_42));

    // hu - Hungarian
    porterAnalyzers.put("hu", new HungarianNormalizationAnalyzer(Version.LUCENE_42));

    // hy - Armenian
    porterAnalyzers.put("hy", new ArmenianNormalizationAnalyzer(Version.LUCENE_42));

    // id - Indonesian
    porterAnalyzers.put("id", new IndonesianNormalizationAnalyzer(Version.LUCENE_42));

    // it - Italian
    porterAnalyzers.put("it", new ItalianNormalizationAnalyzer(Version.LUCENE_42));

    // lv - Latvian
    porterAnalyzers.put("lv", new LatvianNormalizationAnalyzer(Version.LUCENE_42));

    // nl - Dutch
    porterAnalyzers.put("nl", new DutchNormalizationAnalyzer(Version.LUCENE_42));

    // no - Norwegian
    porterAnalyzers.put("no", new NorwegianNormalizationAnalyzer(Version.LUCENE_42));

    // pt - Portuguese
    porterAnalyzers.put("pt", new PortugueseNormalizationAnalyzer(Version.LUCENE_42));

    // ro - Romanian
    porterAnalyzers.put("ro", new RomanianNormalizationAnalyzer(Version.LUCENE_42));

    // ru - Russian
    porterAnalyzers.put("ru", new RussianNormalizationAnalyzer(Version.LUCENE_42, stopWords));

    // sv - Swedish
    porterAnalyzers.put("sv", new SwedishNormalizationAnalyzer(Version.LUCENE_42));

    // th - Thai
    porterAnalyzers.put("th", new ThaiAnalyzer(Version.LUCENE_42));

    // tr - Turkish
    porterAnalyzers.put("tr", new TurkishNormalizationAnalyzer(Version.LUCENE_42));

    // Simple analyzer for each unknown language
    porterAnalyzers.put(SIMPLE_ANALYZER, new SimpleAnalyzer(Version.LUCENE_42));

    // Make map as unmodifiable
    porterAnalyzers = Collections.unmodifiableMap(porterAnalyzers);
  }

  public Map<String, Analyzer> getAnalyzerMap() {
    return porterAnalyzers;
  }

  public static NormalizationAnalyzingFactory instance() {
    if (instance == null) {
      instance = new NormalizationAnalyzingFactory();
    }
    return instance;
  }

  public Analyzer getAnalyzerByLanguage(String language) {
    return porterAnalyzers.get(language);
  }

  public Analyzer getSimpleAnalyzer() {
    return porterAnalyzers.get(SIMPLE_ANALYZER);
  }
}
