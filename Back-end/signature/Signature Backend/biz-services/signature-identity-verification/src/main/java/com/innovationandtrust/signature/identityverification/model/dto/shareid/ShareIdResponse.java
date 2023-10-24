package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ShareIdResponse class. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShareIdResponse {
  private String status;
  private String message;
  private Payload payload;
  private String trace;
}
