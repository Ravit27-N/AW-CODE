package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDto implements Serializable {
    private Long id;

    @JsonProperty(access = Access.READ_ONLY)
    private Integer totalEmployees;

    @NotNull
    @NotEmpty
    private String name;

    @Size(min = 14, max = 14, message = "Siret number must be only 14 characters.")
    @NotNull
    @NotEmpty
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
    private String uuid;

    @JsonProperty(access = Access.READ_ONLY)
    private Long createdBy;

    @JsonProperty(access = Access.READ_ONLY)
    private Long modifiedBy;

    @JsonProperty(access = Access.READ_ONLY)
    private Date createdAt;

    @JsonProperty(access = Access.READ_ONLY)
    private Date modifiedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyDto that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
