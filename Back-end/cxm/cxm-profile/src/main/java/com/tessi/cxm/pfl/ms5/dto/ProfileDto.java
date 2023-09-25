package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class ProfileDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private long id;

  @Size(max = 128)
  @NotEmpty(message = "Name field is required!")
  @Schema(type = "string", required = true, example = "Concepteur")
  private String name;

  @Size(max = 128)
  @Schema(type = "string", example = "Concepteur MOA")
  private String displayName;

  @JsonProperty("functionalities")
  private List<ProfileDetailDto> functionalities;

  @JsonProperty(value = "createdOn", access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private String createdBy;

  @JsonProperty(value = "modifiedOn", access = Access.READ_ONLY)
  private Date lastModified;

  private Long clientId;

  private String clientName;
  private Long ownerId;
}
