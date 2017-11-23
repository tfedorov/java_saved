package com.tfedorov.social.intention;

import java.util.ArrayList;
import java.util.List;

public class IntentLexicon {

  private Long id;

  private String searchTerm;

  private List<Qualification> qualifications = new ArrayList<Qualification>();

  @Override
  public String toString() {
    return searchTerm + ";" + getQualificationsAsString();
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the searchTerm
   */
  public String getSearchTerm() {
    return searchTerm;
  }

  /**
   * @param searchTerm the searchTerm to set
   */
  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  /**
   * @return the qualifications
   */
  public List<Qualification> getQualifications() {
    return qualifications;
  }

  public String getQualificationsAsString() {
    StringBuilder buffer = new StringBuilder("");
    int counter = 1;
    for (Qualification qualification : this.qualifications) {
      buffer.append(qualification.toString());
      if (counter < this.qualifications.size()) {
        buffer.append(",");
      }
      counter++;
    }
    return buffer.toString();
  }

  /**
   * @param qualifications the qualifications to set
   */
  public void setQualifications(List<Qualification> qualifications) {
    this.qualifications = qualifications;
  }

}
