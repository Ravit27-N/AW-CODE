package com.allweb.rms.entity.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "ACTIVITY_SEQUENCE_GENERATOR",
    sequenceName = "ACTIVITY_SEQUENCE",
    allocationSize = 1)
@Table(indexes = {@Index(name = "IDX_ACTIVITY", columnList = "title,userId")})
public class Activity extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITY_SEQUENCE_GENERATOR")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "candidate_id")
  @Schema(hidden = true)
  @JsonIgnore
  private Candidate candidate;

  private String userId;

  @Column(nullable = false)
  private String title;

  private String description;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
    setCreatedBy(this.userId);
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
    setLastModifiedBy(this.userId);
  }
}
