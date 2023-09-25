package com.tessi.cxm.pfl.ms8.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "WATERMARK")
@SequenceGenerator(
    name = "WATERMARK_SEQUENCE_GENERATOR",
    sequenceName = "WATERMARK_SEQUENCE",
    allocationSize = 1)
@EntityListeners({AuditingEntityListener.class})
public class Watermark implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WATERMARK_SEQUENCE_GENERATOR")
  private Long id;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false, columnDefinition = "INT default 0")
  private Integer size;

  @Column(nullable = false, columnDefinition = "INT default 0")
  private Integer rotation;

  @Column(nullable = false)
  private String color;

  @Column(nullable = false, unique = true)
  private String flowId;

  @Column(nullable = false)
  private Long ownerId;

  @LastModifiedDate private LocalDateTime lastModified;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(updatable = false, nullable = false)
  private String createdBy;

  @LastModifiedBy private String lastModifiedBy;
}
