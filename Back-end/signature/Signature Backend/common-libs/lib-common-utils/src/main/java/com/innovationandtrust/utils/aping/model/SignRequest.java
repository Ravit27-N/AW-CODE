package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignRequest implements Serializable {
  // actor url
  private String actor;
  // Collection of doc urls
  private List<SignCriteria> documents;
  // certificate url
  private String certificate;
  private String tag;

  public SignRequest(String actor, String certificate, String tag) {
    this.actor = actor;
    this.certificate = certificate;
    this.tag = tag;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SignCriteria implements Serializable {
    @JsonProperty("document-url")
    private String docUrl;

    private String base64;

    @JsonProperty("visual-parameters")
    private Criteria criteria;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Criteria implements Serializable {

    @JsonProperty("page-number")
    private Integer pageNumber;

    private int width;
    private int height;
    private int x;
    private int y;

    @JsonProperty("text")
    private String signature;

    @JsonProperty("image-content")
    private String signImage;

    @JsonProperty("text-align")
    private int textAlign;

    @JsonProperty("font-size")
    private int fontSize;
  }
}
