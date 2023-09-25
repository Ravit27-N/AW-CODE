package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;

  @Schema(type = "String", required = true, example = "tessi")
  @NotEmpty(message = "Client Name is required.")
  private String name;

  @Email
  private String email;

  private String contactFirstName;

  private String contactLastname;

  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private Date lastModified;

  @JsonProperty(access = Access.READ_ONLY)
  private String createdBy;

  // Metadata of file.
  private String fileId;
  private String filename;
  private long fileSize;

  @NotNull(message = "divisions is required and cannot be null!")
  private List<DivisionDto> divisions = new ArrayList<>();
  
  private Set<String> functionalities = new HashSet<>();

  private Set<Long> publicHolidays = new HashSet<>();

  private List<ClientUnloadingDto> unloads = new ArrayList<>();

  private List<SharedClientFillersDTO> fillers = new ArrayList<>();

  private boolean isPortalConfigEnable;

  private List<CustomerDepositModeDto> depositModes = new ArrayList<>();

  private AddressDto address;
  @JsonProperty(access = Access.READ_ONLY)
  private CriteriaDistributionsResponse criteriaDistributions;

  public List<DivisionDto> getDivisions() {
    if (this.divisions == null) {
      return List.of();
    }
    return divisions.stream().sorted(Comparator.comparingLong(DivisionDto::getId))
        .collect(Collectors.toList());
  }
}
