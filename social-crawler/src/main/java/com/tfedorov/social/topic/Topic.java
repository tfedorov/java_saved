package com.tfedorov.social.topic;

import java.io.Serializable;
import java.math.BigInteger;

import com.tfedorov.social.utils.JsonDateSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.base.BaseDateTime;

public class Topic implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -6424462626554371439L;

  public static final int STATUS_NEW = 0;

  public static final int STATUS_COLLECTING = 1;

  public static final int STATUS_PREPARING_ANALYSIS = 2;

  public static final int STATUS_ACTIVE = 3;

  public static final int STATUS_DISABLED = -1;

  public static final int STATUS_DELETED = -2;

  // TODO: add limitation on UI for symbols can be entered for topics
  public static final String TOPIC_KEYWORDS_SEPARATOR = ",";

  private BigInteger id;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private String name;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private String keywords;

  @JsonSerialize(include = Inclusion.NON_NULL)
  private int status;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "ctime")
  private BaseDateTime created;

  @JsonSerialize(include = Inclusion.NON_NULL)
  @JsonProperty(value = "mtime")
  private BaseDateTime modified;

  @JsonProperty(value = "cuser")
  @JsonSerialize(include = Inclusion.NON_NULL)
  private String createUserName;

  @JsonProperty(value = "muser")
  @JsonSerialize(include = Inclusion.ALWAYS)
  private String modifydUserName;

  @JsonIgnore
  private BigInteger company;

  @JsonIgnore
  private TopicType type;

  /**
   * @return the id
   */
  public BigInteger getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(BigInteger id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the keywords
   */
  public String getKeywords() {
    return keywords;
  }

  /**
   * @param keywords the keywords to set
   */
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  /**
   * @return the status
   */
  public int getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * @return the created
   */
  @JsonSerialize(using = JsonDateSerializer.class)
  public BaseDateTime getCreated() {
    return created;
  }

  /**
   * @param created the created to set
   */
  public void setCreated(BaseDateTime created) {
    this.created = created;
  }

  /**
   * @return the modified
   */
  @JsonSerialize(using = JsonDateSerializer.class)
  public BaseDateTime getModified() {
    return modified;
  }

  /**
   * @param modified the modified to set
   */
  public void setModified(BaseDateTime modified) {
    this.modified = modified;
  }

  /**
   * @return the createUserName
   */
  public String getCreateUserName() {
    return createUserName;
  }

  /**
   * @param createUserName the createUserName to set
   */
  public void setCreateUserName(String createUserName) {
    this.createUserName = createUserName;
  }

  /**
   * @return the modifydUserName
   */
  public String getModifydUserName() {
    return modifydUserName;
  }

  /**
   * @param modifydUserName the modifydUserName to set
   */
  public void setModifydUserName(String modifydUserName) {
    this.modifydUserName = modifydUserName;
  }

  /**
   * @return the companyId
   */
  @JsonProperty(value = "company")
  public String getCompany() {
    return (company == null) ? null : company.toString();
  }

  /**
   * @param companyId the companyId to set
   */
  public void setCompany(String companyId) {
    this.company = (companyId == null) ? null : new BigInteger(companyId);
  }

  public void setMuser(String muser) {
    this.modifydUserName = muser;
  }

  public void setCuser(String cuser) {
    this.createUserName = cuser;
  }

  public TopicType getType() {
    return type;
  }

  public void setType(TopicType type) {
    this.type = type;
  }

}
