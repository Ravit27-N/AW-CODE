package com.tessi.cxm.pfl.ms32.dto;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Tuple;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ProductionDetailsFillers implements Serializable {
  private Long total;
  private String status;
  private String fillerGroup1;
  private String fillerGroup2;
  private String fillerGroup3;

  public ProductionDetailsFillers(Tuple tuple) {
    final String blank = AnalyticsConstants.BLANK;
    this.total = getTupleValue(tuple, 0, Long.class);
    this.status = getTupleValue(tuple, 1, String.class);

    this.fillerGroup1 = StringUtils.defaultIfBlank(getTupleValue(tuple, 2, String.class), blank);
    this.fillerGroup2 = StringUtils.defaultIfBlank(getTupleValue(tuple, 3, String.class), blank);
    this.fillerGroup3 = StringUtils.defaultIfBlank(getTupleValue(tuple, 4, String.class), blank);
  }

  private <T> T getTupleValue(Tuple tuple, int indexOfField, Class<T> tClass) {
    try {
      return tuple.get(indexOfField, tClass);
    } catch (Exception ignored) {
      return null;
    }
  }
}
