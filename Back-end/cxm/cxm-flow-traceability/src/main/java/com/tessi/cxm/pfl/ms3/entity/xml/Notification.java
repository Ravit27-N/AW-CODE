package com.tessi.cxm.pfl.ms3.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
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
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlType
public class Notification implements Serializable {

  @JacksonXmlProperty(isAttribute = true, localName = "Step")
  private String step;

  @JacksonXmlProperty(isAttribute = true, localName = "Date")
  private String date;

  @JacksonXmlProperty(isAttribute = true, localName = "Cost_real")
  private String costReal;

  @JacksonXmlProperty(isAttribute = true, localName = "Weight_real")
  private String weightReal;

  @JacksonXmlProperty(isAttribute = true, localName = "Stamp_real")
  private String stampReal;

  @JacksonXmlProperty(isAttribute = true, localName = "NumReco")
  private String numReco;

  @JacksonXmlProperty(isAttribute = true, localName = "AccuseReception")
  private String accuseReception = "";

  @XmlAttribute(name = "Step")
  public String getStep() {
    return step;
  }

  @XmlAttribute(name = "Date")
  public String getDate() {
    return date;
  }

  @XmlAttribute(name = "Cost_real")
  public String getCostReal() {
    return costReal;
  }

  @XmlAttribute(name = "Weight_real")
  public String getWeightReal() {
    return weightReal;
  }

  @XmlAttribute(name = "Stamp_real")
  public String getStampReal() {
    return stampReal;
  }

  @XmlAttribute(name = "NumReco")
  public String getNumReco() {
    return numReco;
  }

  @XmlAttribute(name = "AccuseReception")
  public String getAccuseReception() {
    return accuseReception;
  }
}
