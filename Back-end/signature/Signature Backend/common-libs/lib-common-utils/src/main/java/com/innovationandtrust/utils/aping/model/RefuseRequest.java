package com.innovationandtrust.utils.aping.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefuseRequest extends RecipientRequest {
  private String reason;

  public RefuseRequest(String actorUrl, List<String> documentUrls, String tag, String reason) {
    super(actorUrl, documentUrls, tag);
    this.reason = reason;
  }
}
