package com.tfedorov.social.normalization.stemming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.util.Version;

public final class StemmingAnalyzerFactory {
  
  private static volatile StemmingAnalyzerFactory instance;

  // Porter stemming processors list by language
  private Map<String, Analyzer> porterAnalyzers;

  private StemmingAnalyzerFactory() {
    porterAnalyzers = new HashMap<String, Analyzer>();
    // /
    // initialize porter processors
    // /
    // ar - Arabic
    porterAnalyzers.put("ar", new ArabicAnalyzer(Version.LUCENE_42));

    // bg - Bulgarian
    porterAnalyzers.put("bg", new BulgarianAnalyzer(Version.LUCENE_42));

    // br - Brazilian
    porterAnalyzers.put("br", new BrazilianAnalyzer(Version.LUCENE_42));

    // ca - Catalan
    porterAnalyzers.put("ca", new CatalanAnalyzer(Version.LUCENE_42));

    // cjk - CJK
    porterAnalyzers.put("cjk", new CJKAnalyzer(Version.LUCENE_42));

    // cn - Chinese
    // Chinese analyzer is deprecated - stemming can't be applied for chinese
    // porterAnalyzers.put("cn", new ChineseAnalyzer());

    // cz - Czech
    porterAnalyzers.put("cz", new CzechAnalyzer(Version.LUCENE_42));

    // da - Danish
    porterAnalyzers.put("da", new DanishAnalyzer(Version.LUCENE_42));

    // de - German
    porterAnalyzers.put("de", new GermanAnalyzer(Version.LUCENE_42));

    // el - Greek
    porterAnalyzers.put("el", new GreekAnalyzer(Version.LUCENE_42));

    // en - English
    porterAnalyzers.put("en", new EnglishAnalyzer(Version.LUCENE_42));

    // es - Spanish
    porterAnalyzers.put("es", new SpanishAnalyzer(Version.LUCENE_42));

    // eu - Basque
    porterAnalyzers.put("eu", new BasqueAnalyzer(Version.LUCENE_42));

    // fa - Persian
    porterAnalyzers.put("fa", new PersianAnalyzer(Version.LUCENE_42));

    // fi - Finnish
    porterAnalyzers.put("fi", new FinnishAnalyzer(Version.LUCENE_42));

    // fr - French
    porterAnalyzers.put("fr", new FrenchAnalyzer(Version.LUCENE_42));

    // ga - Irish
    porterAnalyzers.put("ga", new IrishAnalyzer(Version.LUCENE_42));

    // gl - Galician
    porterAnalyzers.put("gl", new GalicianAnalyzer(Version.LUCENE_42));

    // hi - Hindi
    porterAnalyzers.put("hi", new HindiAnalyzer(Version.LUCENE_42));

    // hu - Hungarian
    porterAnalyzers.put("hu", new HungarianAnalyzer(Version.LUCENE_42));

    // hy - Armenian
    porterAnalyzers.put("hy", new ArmenianAnalyzer(Version.LUCENE_42));

    // id - Indonesian
    porterAnalyzers.put("id", new IndonesianAnalyzer(Version.LUCENE_42));

    // it - Italian
    porterAnalyzers.put("it", new ItalianAnalyzer(Version.LUCENE_42));

    // lv - Latvian
    porterAnalyzers.put("lv", new LatvianAnalyzer(Version.LUCENE_42));

    // nl - Dutch
    porterAnalyzers.put("nl", new DutchAnalyzer(Version.LUCENE_42));

    // no - Norwegian
    porterAnalyzers.put("no", new NorwegianAnalyzer(Version.LUCENE_42));

    // pt - Portuguese
    porterAnalyzers.put("pt", new PortugueseAnalyzer(Version.LUCENE_42));

    // ro - Romanian
    porterAnalyzers.put("ro", new RomanianAnalyzer(Version.LUCENE_42));

    // ru - Russian
    porterAnalyzers.put("ru", new RussianAnalyzer(Version.LUCENE_42));

    // sv - Swedish
    porterAnalyzers.put("sv", new SwedishAnalyzer(Version.LUCENE_42));

    // th - Thai
    porterAnalyzers.put("th", new ThaiAnalyzer(Version.LUCENE_42));

    // tr - Turkish
    porterAnalyzers.put("tr", new TurkishAnalyzer(Version.LUCENE_42));

    // Make map as unmodifiable
    porterAnalyzers = Collections.unmodifiableMap(porterAnalyzers);
  }

  public Map<String, Analyzer> getPorterAnalyzerMap() {
    return porterAnalyzers;
  }

  public static StemmingAnalyzerFactory instance() {
    StemmingAnalyzerFactory localInstance = instance;
    if (instance == null) {
      synchronized (StemmingAnalyzerFactory.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance  = new StemmingAnalyzerFactory();
          localInstance = instance;
        }
      }
    }
    return instance;
  }

  public Analyzer getAnalyzerByLanguage(String language) {
    return porterAnalyzers.get(language);
  }
}
