package com.tessi.cxm.pfl.ms11.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SequenceGenerator(
    name = "CHANNEL_METADATA_SEQUENCE_GENERATOR",
    sequenceName = "CHANNEL_METADATA_SEQUENCE",
    allocationSize = 1)
public class ChannelMetadata extends BaseEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CHANNEL_METADATA_SEQUENCE_GENERATOR")
  private Long id;
  private String customer;
  private String type;
  private long order;
  private String value;

  @PrePersist
  void initCreatedAt() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  void updateLastModified() {
    this.setLastModified(new Date());
  }
}
