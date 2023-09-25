package com.tessi.cxm.pfl.ms3.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Model class to handle data binding XML value.
 *
 * @author Piseth Khon
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "job")
@XmlRootElement(name = "job")
public class Job implements Serializable {

  @JacksonXmlProperty(isAttribute = true, localName = "jobMsn")
  private String jobMsn;
  @JacksonXmlProperty(isAttribute = true, localName = "JobPath")
  private String jobPath;
  @JacksonXmlProperty(isAttribute = true, localName = "ServerName")
  private String serverName;
  @JacksonXmlProperty(isAttribute = true, localName = "jobDate")
  private String jobDate;
  @JacksonXmlProperty(isAttribute = true, localName = "Type")
  private String type;
  @JacksonXmlProperty(isAttribute = true, localName = "Version")
  private String version;
  @JacksonXmlProperty(localName = "document")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<Document> documents;

  @XmlAttribute(name = "jobMsn")
  public String getJobMsn() {
    return jobMsn;
  }

  @XmlAttribute(name = "JobPath")
  public String getJobPath() {
    return jobPath;
  }

  @XmlAttribute(name = "ServerName")
  public String getServerName() {
    return serverName;
  }

  @XmlAttribute(name = "jobDate")
  public String getJobDate() {
    return jobDate;
  }

  @XmlAttribute(name = "Type")
  public String getType() {
    return type;
  }

  @XmlAttribute(name = "Version")
  public String getVersion() {
    return version;
  }

  @XmlElement(name = "document")
  public List<Document> getDocuments() {
    return documents;
  }
}
