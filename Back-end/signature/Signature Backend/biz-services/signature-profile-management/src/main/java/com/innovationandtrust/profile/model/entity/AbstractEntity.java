package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class AbstractEntity {
  @CreatedDate
  @Column(name = "created_at", updatable = false)
  protected Date createdAt;

  @Column(name = "created_by")
  @CreatedBy protected Long createdBy;

  @Column(name = "modified_at")
  @LastModifiedDate protected Date modifiedAt;

  @Column(name = "modified_by")
  @LastModifiedBy private Long modifiedBy;
}
