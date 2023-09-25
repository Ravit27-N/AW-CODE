package com.tessi.cxm.pfl.ms5.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserResponsesDTO {

  private List<QueryUserResponseDTO> users;
}
