package com.innovationandtrust.utils.keycloak.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company implements Serializable {

  public static final String SEPARATOR = ";";

  private Long id;
  private String name;
  private String uuid;

  public Company(String strCompany) {
    var data = strCompany.split(SEPARATOR);
    if (data.length > 0) {
      switch (data.length) {
        case 2 -> {
          this.id = Long.parseLong(data[0]);
          this.name = data[1];
        }
        case 3 -> {
          this.id = Long.parseLong(data[0]);
          this.name = data[1];
          this.uuid = data[2];
        }
        default -> this.id = Long.parseLong(data[0]);
      }
    }
  }

  public String convertToString(Long id, String name) {
    return String.format("%s;%s", id, name);
  }

  public String convertToString(Long id, String name, String uuid) {
    return String.format("%s;%s;%s", id, name, uuid);
  }

  public String getStringCompany() {
    return this.convertToString(this.id, this.name, this.uuid);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company company)) return false;
    return Objects.equals(getId(), company.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
