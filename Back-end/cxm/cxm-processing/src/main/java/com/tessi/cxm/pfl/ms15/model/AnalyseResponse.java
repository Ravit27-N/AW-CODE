package com.tessi.cxm.pfl.ms15.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyseResponse implements Serializable {
  private String modelName;
  private String docName;
  private int pageNumber;
  private String idDest;
  private int state;
  private String description;
  private String filiere;
  private String mail;
  private int phone;
  private String urgency;
  private String recto;
  private int color;
  private String wrap;
  private int archiving;
  private Map<String, String> address;
  private List<String> metadata;
  private String filigrane;
  private String backgroundFirst;
  private String backgroundFirstExist;
  private String positionFirst;
  private String background;
  private String backgroundExist;
  private String position;
  private String backgroundLast;
  private String backgroundLastExist;
  private String positionLast;

  public String getNbPages() {
    return String.valueOf(pageNumber);
  }

  public String getAnalyse() {
    return (state == 0) ? "KO" : "OK";
  }

  public String getChannel() {
    return filiere;
  }

  public String getRecipientID() {
    return idDest;
  }

  public String getEmailRecipient() {
    return mail;
  }

  public String getColor() {
    return String.valueOf(color);
  }

  public String getZipCode() {
    final TreeMap<String, String> treeMap = new TreeMap<>(address);
    final String address =
        treeMap.entrySet().stream()
            .filter(entryMap -> StringUtils.isNotEmpty(entryMap.getValue()))
            .max(Entry.comparingByKey())
            .orElse(treeMap.lastEntry())
            .getValue();
    return address.length() < 5 ? address : address.substring(0, 5);
  }

  public List<String> getMetadata() {
    if (CollectionUtils.isEmpty(metadata)) {
      return IntStream.range(0, 5).mapToObj(ignored -> "").collect(Collectors.toList());
    }
    IntStream.range(metadata.size(), 5).forEach(ignored -> metadata.add(""));
    return metadata;
  }

  public String getFiller1() {
    return getMetadata().get(0);
  }

  public String getFiller2() {
    return getMetadata().get(1);
  }

  public String getFiller3() {
    return getMetadata().get(2);
  }

  public String getFiller4() {
    return getMetadata().get(3);
  }

  public String getFiller5() {
    return getMetadata().get(4);
  }
}
