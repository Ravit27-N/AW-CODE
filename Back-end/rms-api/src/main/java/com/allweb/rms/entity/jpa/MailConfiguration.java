package com.allweb.rms.entity.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "MAIL_CONFIGURATION_SEQUENCE_GENERATOR",
    sequenceName = "MAIL_CONFIGURATION_SEQUENCE",
    allocationSize = 1)
@Table
@IdClass(MailConfigurationId.class)
public class MailConfiguration extends AbstractEntity implements Serializable {
  /*
   *
   * */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "MAIL_CONFIGURATION_SEQUENCE_GENERATOR")
  @Column(length = 11)
  private int id;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @JsonIgnore
  @Schema(hidden = true)
  private MailTemplate mailTemplate;

  @Id
  @OneToOne(fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @JsonIgnore
  @Schema(hidden = true)
  private CandidateStatus candidateStatus;

  @Column(length = 50, nullable = false)
  private String title;

  @Column(name = "\"from\"", nullable = false, length = 128)
  private String from;

  @Column(name = "\"to\"", nullable = false)
  @ElementCollection
  private List<String> to;

  @ElementCollection private List<String> cc;

  private boolean active;
  private boolean deleted;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
