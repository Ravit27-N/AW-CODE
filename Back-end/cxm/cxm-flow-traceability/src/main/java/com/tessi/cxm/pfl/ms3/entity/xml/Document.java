package com.tessi.cxm.pfl.ms3.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
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
@XmlType
public class Document implements Serializable {

  private long id;

  @JacksonXmlProperty(isAttribute = true, localName = "IdDoc")
  private String idDoc;

  @JacksonXmlProperty(isAttribute = true, localName = "Company")
  private String company;

  @JacksonXmlProperty(localName = "notification")
  private Notification notification;

  @XmlAttribute(name = "IdDoc")
  public String getIdDoc() {
    return idDoc;
  }

  @XmlAttribute(name = "Company")
  public String getCompany() {
    return company;
  }

  @XmlElement(name = "notification")
  public Notification getNotification() {
    return notification;
  }
}
