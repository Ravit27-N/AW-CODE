package com.innovationandtrust.share.model.project;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRequest implements Serializable {
  @Min(1)
  private Long id;

  private String signedDocUrl;

  public DocumentRequest(Document document) {
    this.id = document.getId();
    this.signedDocUrl = document.getDocUrl();
  }
}
