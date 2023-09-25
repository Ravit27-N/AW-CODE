package com.allweb.rms.entity.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SequenceGenerator(
    name = "SYSTEM_SEQUENCE_GENERATOR",
    sequenceName = "SYSTEM_SEQUENCE",
    allocationSize = 1)
public class ApplicationInformation extends AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYSTEM_SEQUENCE_GENERATOR")
  private int id;

  private String version;

  private boolean initialized;
}
