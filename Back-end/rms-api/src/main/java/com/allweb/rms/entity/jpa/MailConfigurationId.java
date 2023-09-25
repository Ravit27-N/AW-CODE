package com.allweb.rms.entity.jpa;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailConfigurationId implements Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  private int id;
  private int candidateStatus;
  private int mailTemplate;
}
