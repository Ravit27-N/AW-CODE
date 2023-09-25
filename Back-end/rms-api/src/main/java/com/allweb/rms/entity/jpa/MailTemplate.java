package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "MAIL_TEMPLATE_SEQUENCE_GENERATOR",
    sequenceName = "MAIL_TEMPLATE_SEQUENCE",
    initialValue = 6,
    allocationSize = 1)
@Table
public class MailTemplate extends AbstractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "MAIL_TEMPLATE_SEQUENCE_GENERATOR")
  @Column(length = 11)
  private int id;

  @Column(length = 200, nullable = false)
  private String subject;

  @Column(columnDefinition = "text", nullable = false)
  private String body;

  @Column(columnDefinition = "boolean default true")
  private boolean active;

  private boolean deleted;

  @Column(columnDefinition = "boolean default false")
  private boolean isDeletable;

  @PrePersist
  void onCreate() {
    setActive(true);
    setDeleted(false);
    setDeletable(true);
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
