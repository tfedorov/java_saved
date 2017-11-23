package com.tfedorov.social.utils;

public final class UnicodeUtils {

  private UnicodeUtils() {}

  public static final int MIN_EMOTICONS_CODE_POINT = 0x1F600;

  public static final int MAX_SEMOTICONS_CODE_POINT = 0x1F650;

  public static String filterEmoticons(String str) {

    StringBuilder sb = new StringBuilder(str.length());
    for (char c : str.toCharArray()) {
      if (isAlowed(c)) {
        sb.append(c);
      }
    }

    return sb.toString();
  }

  private static boolean isAlowed(char c) {
    return !isHighSurrogate(c) && !isLowSurrogate(c) && !isEmoticons(c);
  }

  public static boolean isEmoticons(char c) {
    return MIN_EMOTICONS_CODE_POINT <= c & c < MAX_SEMOTICONS_CODE_POINT;
  }

  public static boolean isHighSurrogate(char c) {
    return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIGH_SURROGATES;
  }

  public static boolean isLowSurrogate(char c) {
    return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LOW_SURROGATES;
  }

}
