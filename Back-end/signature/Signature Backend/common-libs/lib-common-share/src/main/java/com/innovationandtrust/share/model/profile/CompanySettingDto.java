package com.innovationandtrust.share.model.profile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Company Parameters. */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettingDto implements Serializable {

  @NotNull private Long id;

  @NotEmpty private String companyUuid;

  @NotEmpty private String signatureLevel;

  private String personalTerms;

  private String identityTerms;

  private String documentTerms;

  private String channelReminder;

  private Set<String> fileType = new HashSet<>();

  private String companyChannel;

  private Set<String> companyFileType = new HashSet<>();
}
