package com.innovationandtrust.corporate.model.entity;

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
  @Column(updatable = false)
  protected Date createdAt;

  @CreatedBy protected Long createdBy;

  @LastModifiedDate protected Date modifiedAt;

  @LastModifiedBy private Long modifiedBy;
}
