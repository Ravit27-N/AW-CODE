package com.tessi.cxm.pfl.ms8.entity;

import java.util.Date;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RESOURCE_FILE")
@SequenceGenerator(
    name = "RESOURCE_FILE_SEQUENCE_GENERATOR",
    sequenceName = "RESOURCE_FILE_SEQUENCE",
    allocationSize = 1)
@EntityListeners({AuditingEntityListener.class})
public class ResourceFile {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "RESOURCE_FILE_SEQUENCE_GENERATOR")
  private Long id;

  private String fileId;
  private String originalName;
  private String extension;
  private String type;
  private String position;
  private int numberOfPages;
  private String flowId;
  private Long ownerId;
  private String source;

  @Column(columnDefinition = "Boolean default false")
  private boolean isDefault;

  @LastModifiedDate private Date lastModified;

  @CreatedDate
  @Column(updatable = false)
  private Date createdAt;
}
