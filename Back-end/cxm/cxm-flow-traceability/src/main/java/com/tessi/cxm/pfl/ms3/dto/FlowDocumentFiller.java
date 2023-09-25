package com.tessi.cxm.pfl.ms3.dto;

import com.tessi.cxm.pfl.shared.model.SharedPairValue;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class FlowDocumentFiller extends SharedPairValue implements Serializable {
  private int order;

  public FlowDocumentFiller(String key, String value, int order) {
    super(key, value);
    this.order = order;
  }
}
