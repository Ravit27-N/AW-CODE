package com.tessi.cxm.pfl.ms8.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.tessi.cxm.pfl.ms8.validators.ValidFlowDocumentAddressLineNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDocumentAddressDto {
  @NotBlank(message = "The flowId field is required.")
  private String flowId;

  @NotBlank(message = "The docId field is required.")
  private String docId;

  @ValidFlowDocumentAddressLineNumber
  private List<@Valid FlowDocumentAddressLineDto> flowDocumentAddress;
}
