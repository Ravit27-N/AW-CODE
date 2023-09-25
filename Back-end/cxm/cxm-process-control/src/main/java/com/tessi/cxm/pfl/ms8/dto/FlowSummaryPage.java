package com.tessi.cxm.pfl.ms8.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlowSummaryPage implements Serializable {
  private int nbPages;
  private int nbDocuments;
  private Map<String, Integer> documents;
}
