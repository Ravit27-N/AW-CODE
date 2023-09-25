package com.allweb.rms.entity.jpa;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseTokenId implements Serializable {
  /** */
  private static final long serialVersionUID = 1L;

  private String userId;

  private String deviceId;
}
