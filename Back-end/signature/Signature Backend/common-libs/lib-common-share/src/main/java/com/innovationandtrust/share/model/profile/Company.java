package com.innovationandtrust.share.model.profile;

import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Company implements Serializable {
  private Long id;
  private String name;
  private String siret;
  private String logo;
  private String mobile;
  private String email;
  private String contactFirstName;
  private String contactLastName;
  private String fixNumber;
  private String addressLine1;
  private String addressLine2;
  private String postalCode;
  private String state;
  private String country;
  private String city;
  private String territory;
  private boolean isArchiving;
  private Long createdBy;
  private String uuid;
  private List<CorporateSettingDto> theme;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
