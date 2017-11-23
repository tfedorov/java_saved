package com.tfedorov.social.twitter;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tfedorov.social.word.processing.WordProcessor;

public class RegExpTestOld {

  // @Test
  public void testSplit() {
    String string = " bla1  bla2 ";

    String[] splitA = string.split(" ");
    String[] expected = new String[] {"", "bla1", "", "bla2"};
    assertArrayEquals("Split result worng!", expected, splitA);

    splitA = string.trim().split("\\s+", 0);
    expected = new String[] {"bla1", "bla2"};
    assertArrayEquals("Split result wrong!", expected, splitA);

  }

  // @Test
  public void testSplitSeparators() {

    Pattern pattern = WordProcessor.SEPARATORS;;

    String str =
        "  RT @HEADLESSGANG: ?Wake up?\t" + "?Thank Him?Eating Good??Thank Him"
            + "?Got Money??Thank Him" + "     @twuser \\my #topic#+35  ";

    String[] splitA = pattern.split(str.trim());
    String[] expected =
        new String[] {"RT", "HEADLESSGANG", "Wake", "up", "Thank", "Him", "Eating", "Good",
            "Thank", "Him", "Got", "Money", "Thank", "Him", "twuser", "my", "topic", "35"};
    assertArrayEquals("Split result wrong!", expected, splitA);

    // System.out.println(Arrays.asList(splitA));
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    String string = " fgg  hfh ";

    System.out.println(Arrays.asList(string.split(" ")));

    // ---------------------
    String s =
        "bla-bla " + "http://naishe.blogspot.com " + "http://tw.com/#!/someTEXTs  "
            + "http://ts123t1.rapi.com/#!download|13321|1313|fairy_tale.mp4 "
            + "http://www.google.com/ " + "https://www.google.com/. " + "google.com  bla-bla-bla"
            + "google.com, " + " banana google.com/test " + "123.com/test " + "ex-ample.com "
            + "http://ex-ample.com/test-url_chars?param1=val1&;par2=val+with%20spaces "
            + "something else";
    Pattern trimmer = Pattern.compile("(?:\\b(?:http|ftp|www\\.)\\S+\\b)|(?:\\b\\S+\\.com\\S*\\b)");
    // Pattern trimmer =
    // Pattern.compile("((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))");
    Matcher m = trimmer.matcher(s);
    StringBuffer out = new StringBuffer();
    int i = 1;
    System.out.println(trimmer.toString());
    while (m.find()) {
      System.out.println("|" + m.group() + "|");
      // m.appendReplacement(out, "<a href=\""+m.group()+"\">URL"+ i++
      // +"</a>");
      m.appendReplacement(out, "");
    }
    m.appendTail(out);
    System.out.println(out + "!");

    // ----------------------------
    String txt =
        "RT @HEADLESSGANG: ?Wake up?" + "?Thank Him?Eating Good??Thank Him"
            + "?Got Money??Thank Him" + "?A Job?" + "?Thank Him?Another Day??Thank Him?He ...";

    StringTokenizer tokenizer1 = new StringTokenizer(txt, " .,;!?:()[]{}<>@#&$~*=+-\"/\n\t\f\r");
    System.out.print("[");
    while (tokenizer1.hasMoreTokens()) {
      System.out.print(tokenizer1.nextToken());
      if (tokenizer1.hasMoreTokens()) {
        System.out.print(", ");
      }
    }
    System.out.print("]");

  }

  // @Test
  public void testNumericFiltering() {
    String term = "1234";

    try {
      Double.parseDouble(term);
    } catch (NumberFormatException e) {
      System.out.println("NAN");
    }
    System.out.println("is digital");
  }
}
