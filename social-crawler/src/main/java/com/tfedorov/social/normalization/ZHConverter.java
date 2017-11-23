package com.tfedorov.social.normalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZHConverter {

  private ZHConverter(){}

  private Properties charMap = new Properties();
  private Set<String> conflictingSets = new HashSet<String>();

  public static final int TRADITIONAL = 0;
  public static final int SIMPLIFIED = 1;
  private static final int NUM_OF_CONVERTERS = 2;
  private static final ZHConverter[] CONVERTORS = new ZHConverter[NUM_OF_CONVERTERS];
  private static final String[] PROPERTY_FILES = new String[2];

  private static final Logger LOGGER = LoggerFactory.getLogger(ZHConverter.class);

  static {
    PROPERTY_FILES[TRADITIONAL] = "zhconverter/zh2Hant.properties";
    PROPERTY_FILES[SIMPLIFIED] = "zhconverter/zh2Hans.properties";
  }


  /**
   * @param converterType 0 for traditional and 1 for simplified
   */
  public static ZHConverter getInstance(int converterType) {

    if (converterType >= 0 && converterType < NUM_OF_CONVERTERS) {

      if (CONVERTORS[converterType] == null) {
        synchronized (ZHConverter.class) {
          if (CONVERTORS[converterType] == null) {
            CONVERTORS[converterType] = new ZHConverter(PROPERTY_FILES[converterType]);
          }
        }
      }
      return CONVERTORS[converterType];

    } else {
      return null;
    }
  }

  public static String convert(Reader text, int converterType) {
    ZHConverter instance = getInstance(converterType);
    return instance.convert(text);
  }


  private ZHConverter(String propertyFile) {

    InputStream is = null;

    is = getClass().getResourceAsStream(propertyFile);

    // File propertyFile = new File("C:/Temp/testMDB/TestTranslator/abc.txt");
    if (is != null) {
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new InputStreamReader(is));
        charMap.load(reader);
      } catch (IOException e) {
        LOGGER.error("Error with loading file", e);
      } finally {
        try {
          if (reader != null) {
            reader.close();
          }
          if (is != null) {
            is.close();
          }
        } catch (IOException e) {
          LOGGER.error("Error with closing stream", e);
        }
      }
    }
    initializeHelper();
  }

  private void initializeHelper() {
    Map<String, Integer> stringPossibilities = new HashMap<String, Integer>();
    Iterator<?> iter = charMap.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      if (key.length() >= 1) {

        for (int i = 0; i < (key.length()); i++) {
          String keySubstring = key.substring(0, i + 1);
          if (stringPossibilities.containsKey(keySubstring)) {
            Integer integer = (Integer) (stringPossibilities.get(keySubstring));
            stringPossibilities.put(keySubstring, integer + 1);
          } else {
            stringPossibilities.put(keySubstring, 1);
          }
        }
      }
    }

    iter = stringPossibilities.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      if ((Integer) (stringPossibilities.get(key)) > 1) {
        conflictingSets.add(key);
      }
    }
  }

  public String convert(Reader in) {
    StringBuilder outString = new StringBuilder();
    StringBuilder stackString = new StringBuilder();
    try {
      int el = in.read();
      while (el != -1) {

        char c = (char) el;
        String key = "" + c;
        stackString.append(key);

        if (charMap
            .containsKey(stackString.toString())) {
          outString.append(charMap.get(stackString.toString()));
          stackString.setLength(0);
        } else {
          CharSequence sequence = stackString.subSequence(0, stackString.length() - 1);
          stackString.delete(0, stackString.length() - 1);
          flushStack(outString, new StringBuilder(sequence));
        }
        //
        el = in.read();
      }
    } catch (IOException e) {
      LOGGER.error("Error with reading input stream", e);
    }
    flushStack(outString, stackString);

    return outString.toString();
  }


  private void flushStack(StringBuilder outString, StringBuilder stackString) {
    while (stackString.length() > 0) {
      if (charMap.containsKey(stackString.toString())) {
        outString.append(charMap.get(stackString.toString()));
        stackString.setLength(0);

      } else {
        outString.append("" + stackString.charAt(0));
        stackString.delete(0, 1);
      }

    }
  }


  String parseOneChar(String c) {

    if (charMap.containsKey(c)) {
      return (String) charMap.get(c);

    }
    return c;
  }


}
