/**
 *
 */
package com.tfedorov.social.qualification.util;

import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.intention.Qualification;

/**
 * @author tfedorov
 */
public final class QuailficationUtil {

  private QuailficationUtil() {}

  public static List<Qualification> buildQuaificationStr(String qualificationsStr) {
    List<Qualification> qualifications = new ArrayList<Qualification>();
    if (qualificationsStr == null) {
      return qualifications;
    }
    String[] qsStr = qualificationsStr.split(",");
    if (qsStr != null && qsStr.length > 0) {
      for (String str : qsStr) {
        if (!str.isEmpty()) {
          Qualification qualification = new Qualification(str.trim());
          qualifications.add(qualification);
        }
      }
    }
    return qualifications;
  }

}
