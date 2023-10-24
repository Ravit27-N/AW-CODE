package com.innovationandtrust.utils.aping.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOtpRequest implements Serializable {

  @NotEmpty private String actor;

  private List<@Valid String> documents;

  private int length = 6;

  private boolean numeric = true;

  private int ttl = 6000;

  private String tag;

  public GenerateOtpRequest(String actor, List<@Valid String> documents, String tag) {
    this.actor = actor;
    this.documents = documents;
    this.tag = tag;
  }
}
