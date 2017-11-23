package com.tfedorov.social.normalization.stemming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tfedorov.social.normalization.stemming.stemmers.ArabicSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.ArmenianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.BasqueSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.BulgarianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.CatalanSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.CzechSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.DanishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.DutchSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.EnglishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.FinnishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.FrenchSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.GalicianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.GermanSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.GreekSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.HindiSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.HungarianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.IndonesianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.IrishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.ItalianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.LatvianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.NorwegianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.PortugueseSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.RomanianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.RussianSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.SmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.SpanishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.SwedishSmartStemmer;
import com.tfedorov.social.normalization.stemming.stemmers.TurkishSmartStemmer;

public final class NormalizationStemmingFactory {
  private static volatile NormalizationStemmingFactory instance;

  // Porter stemming processors list by language
  private Map<String, SmartStemmer> stemmers;

  private NormalizationStemmingFactory() {
    stemmers = new HashMap<String, SmartStemmer>();
    // /
    // initialize porter processors
    // /
    // ar - Arabic
    stemmers.put("ar", new ArabicSmartStemmer());

    // bg - Bulgarian
    stemmers.put("bg", new BulgarianSmartStemmer());

    // br - Brazilian
    // stemmers.put("br", new BrazilianSmartStemmer());

    // ca - Catalan
    stemmers.put("ca", new CatalanSmartStemmer());

    // cz - Czech
    stemmers.put("cz", new CzechSmartStemmer());

    // da - Danish
    stemmers.put("da", new DanishSmartStemmer());

    // de - German
    stemmers.put("de", new GermanSmartStemmer());

    // el - Greek
    stemmers.put("el", new GreekSmartStemmer());

    // en - English
    stemmers.put("en", new EnglishSmartStemmer());

    // es - Spanish
    stemmers.put("es", new SpanishSmartStemmer());

    // eu - Basque
    stemmers.put("eu", new BasqueSmartStemmer());

    // fi - Finnish
    stemmers.put("fi", new FinnishSmartStemmer());

    // fr - French
    stemmers.put("fr", new FrenchSmartStemmer());

    // ga - Irish
    stemmers.put("ga", new IrishSmartStemmer());

    // gl - Galician
    stemmers.put("gl", new GalicianSmartStemmer());

    // hi - Hindi
    stemmers.put("hi", new HindiSmartStemmer());

    // hu - Hungarian
    stemmers.put("hu", new HungarianSmartStemmer());

    // hy - Armenian
    stemmers.put("hy", new ArmenianSmartStemmer());

    // id - Indonesian
    stemmers.put("id", new IndonesianSmartStemmer());

    // it - Italian
    stemmers.put("it", new ItalianSmartStemmer());

    // lv - Latvian
    stemmers.put("lv", new LatvianSmartStemmer());

    // nl - Dutch
    stemmers.put("nl", new DutchSmartStemmer());

    // no - Norwegian
    stemmers.put("no", new NorwegianSmartStemmer());

    // pt - Portuguese
    stemmers.put("pt", new PortugueseSmartStemmer());

    // ro - Romanian
    stemmers.put("ro", new RomanianSmartStemmer());

    // ru - Russian
    stemmers.put("ru", new RussianSmartStemmer());

    // sv - Swedish
    stemmers.put("sv", new SwedishSmartStemmer());

    // tr - Turkish
    stemmers.put("tr", new TurkishSmartStemmer());

    // Make map as unmodifiable
    stemmers = Collections.unmodifiableMap(stemmers);
  }

  public Map<String, SmartStemmer> getStemmersMap() {
    return stemmers;
  }

  public static NormalizationStemmingFactory instance() {
    NormalizationStemmingFactory localInstance = instance;
    if (instance == null) {
      synchronized (NormalizationStemmingFactory.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance  = new NormalizationStemmingFactory();
          localInstance = instance;
        }
      }
    }
    return instance;
  }

  public SmartStemmer getStemmerByLanguage(String language) {
    return stemmers.get(language);
  }
}
