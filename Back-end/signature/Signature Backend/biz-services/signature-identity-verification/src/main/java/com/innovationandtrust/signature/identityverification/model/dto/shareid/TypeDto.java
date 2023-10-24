package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request class for metadata dto. */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TypeDto {

  @JsonProperty("document_type")
  private String documentType;

  @JsonProperty("document_year")
  private String documentYear;

  private String country;
}
