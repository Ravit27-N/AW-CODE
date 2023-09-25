package com.tessi.cxm.pfl.ms8.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlowDocumentProduction implements Serializable {

  private String urgency;
  private String color;
  private String recto;
  private String wrap;
}
