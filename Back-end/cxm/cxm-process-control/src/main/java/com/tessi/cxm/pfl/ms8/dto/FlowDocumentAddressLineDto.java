package com.tessi.cxm.pfl.ms8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDocumentAddressLineDto {
  @NotNull(message = "addressLineNumber is required.")
  @Min(value = 1, message = "The address Line Number must be between 1 and 7.")
  @Max(value = 7, message = "The address Line Number must be between 1 and 7.")
  private Integer addressLineNumber;

  @NotBlank(message = "The address text is required.")
  @Size(max = 38, message = "An address line cannot exceed 38 characters.")
  private String address;
}
