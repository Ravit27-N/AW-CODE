package com.tessi.cxm.pfl.ms8.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Entity(name = "FLOW_DOCUMENT_ADDRESS")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
@IdClass(FlowDocumentAddressId.class)
public class FlowDocumentAddress implements Serializable {

  @Id
  @Column(nullable = false)
  private String flowId;

  @Id
  @Column(nullable = false)
  private String docId;

  @Id
  @Column(nullable = false)
  private Integer addressLineNumber;

  @Column(updatable = false)
  private String originalAddress;

  private String modifiedAddress;

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
