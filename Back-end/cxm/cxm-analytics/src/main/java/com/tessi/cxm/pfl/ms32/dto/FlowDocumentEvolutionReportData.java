package com.tessi.cxm.pfl.ms32.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowDocumentEvolutionReportData implements Serializable {

  private String channel;
  private List<ValueByDate> data;


  public static ValueByDate createValue(String date, Long value) {
    return new ValueByDate(date, value);
  }

  public static class ValueByDate {

    private String date;
    private Long value;

    public ValueByDate(String dateString, Long value) {
      this.value = value;
      this.date = dateString;
    }

    public String getDate() {
      return this.date;
    }

    public Long getValue() {
      return this.value;
    }

    public void setValue(Long value) {
      this.value = value;
    }

    public void setDate(String value) {
      this.date = value;
    }
  }
}
