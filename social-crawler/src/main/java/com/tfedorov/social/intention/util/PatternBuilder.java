/**
 * 
 */
package com.tfedorov.social.intention.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.tfedorov.social.intention.Purchase;
import com.tfedorov.social.intention.Qualification;

/**
 * @author tfedorov
 * 
 */
public final class PatternBuilder {

  private PatternBuilder() {}

  public static Pattern buildForSingleWord(String term) {
    StringBuilder patternStr = new StringBuilder();
    patternStr.append("(^|\\s+)(");
    patternStr.append(term);
    patternStr.append(")(\\s+|$)");
    return Pattern.compile(patternStr.toString(), Pattern.CASE_INSENSITIVE);
  }

  public static Pattern buildTermAndQualifs(String searchTerm, List<Qualification> qualifications) {
    StringBuilder patternStr = new StringBuilder();
    int lenght = qualifications.size();
    int i = 0;

    String searchTermEscaped = searchTerm.replace("*", "\\*");

    if (qualifications == null || qualifications.size() == 0) {
      patternStr.append(searchTermEscaped);
    }
    for (Qualification qualification : qualifications) {
      // Qualificator &0.0 has not any sense
      if (qualification.getBefore() < 1 && qualification.getAfter() < 1) {
        continue;
      }
      String oneQuilfierPatternStr =
          buildStringTermAndSingleQualif(searchTermEscaped, qualification.getQualificationStr(),
              qualification.getBefore(), qualification.getAfter());
      patternStr.append("(").append(oneQuilfierPatternStr).append(")");
      if (i < lenght - 1) {
        patternStr.append("|");
      }
      i++;
    }

    return Pattern.compile(patternStr.toString(), Pattern.CASE_INSENSITIVE);
  }


  private static String buildStringTermAndSingleQualif(String searchTerm, String qualificationStr,
      int before, int after) {
    String leftPart = "(" + qualificationStr + "\\s+((\\w+\\s+){0," + before + "}))" + searchTerm;
    // AXS-104 Condition for cases where qualification word (e.g not, nor) should be counted before
    // search term only
    if (after == 0) {
      return leftPart;
    }
    String rightPart = "\\s*" + searchTerm + "\\s+((\\w+\\s+){0," + after + "})" + qualificationStr;
    if (before == 0) {
      return rightPart;
    }
    String patternStr = "(" + leftPart + "|" + rightPart + ")";
    return patternStr;
  }

  public static List<Pattern> buildPurchasesPattern(Purchase purchase) {
    StringBuilder primaryPattern = new StringBuilder();
    String primaryTest = purchase.getPrimaryTest().trim();
    buildPrimaryTest(primaryPattern, primaryTest);

    StringBuilder secondaryPattern = new StringBuilder();
    String secondaryTest = purchase.getSecondaryTest().trim();
    buildSecondaryTest(secondaryPattern, secondaryTest);

    Pattern priPattern = Pattern.compile(primaryPattern.toString(), Pattern.CASE_INSENSITIVE);
    Pattern secPattern = Pattern.compile(secondaryPattern.toString(), Pattern.CASE_INSENSITIVE);

    List<Pattern> patterns = new ArrayList<Pattern>();
    patterns.add(priPattern);
    patterns.add(secPattern);
    return patterns;
  }

  private static void buildSecondaryTest(StringBuilder patternStr, String secondaryTest) {
    secondaryTest = secondaryTest.replaceAll(" ", "\\\\s");
    // ^((?!.prase).)*$
    if (secondaryTest != null && !secondaryTest.equals("")) {
      // fix * condition:
      patternStr.append("^");
      if (secondaryTest.startsWith("#")) {
        patternStr.append("((?!.\\s");
        if (secondaryTest.endsWith("*")) {
          secondaryTest = secondaryTest.substring(0, secondaryTest.length() - 1);
          patternStr.append(secondaryTest.substring(1));
        } else {
          patternStr.append(secondaryTest.substring(1));
          patternStr.append("\\s");
        }
        patternStr.append(").)*");
      } else if (secondaryTest.startsWith("*")) {
        // - before *
        patternStr.append("(.");
        patternStr.append(secondaryTest);
        patternStr.append("\\s).*");
      } else if (secondaryTest.endsWith("*")) {
        // - after *
        patternStr.append("(\\s");
        patternStr.append(secondaryTest);
        patternStr.append(").*");
      }
      patternStr.append("$");
    }
  }

  private static void buildPrimaryTest(StringBuilder patternStr, String primaryTest) {
    primaryTest = primaryTest.replaceAll(" ", "\\\\s");
    // \\bphrase\\b
    patternStr.append("(");
    if (!primaryTest.startsWith("*")) {
      patternStr.append("\\b");
    }
    patternStr.append(primaryTest.replaceAll("\\*", ""));
    if (!primaryTest.endsWith("*")) {
      patternStr.append("\\b");
    }
    patternStr.append(")");
  }

}
