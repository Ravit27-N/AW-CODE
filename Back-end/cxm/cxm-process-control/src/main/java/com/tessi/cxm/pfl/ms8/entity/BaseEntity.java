package com.tessi.cxm.pfl.ms8.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity implements Serializable {
  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @CreatedDate
  @Column(updatable = false)
  private Date createdAt;

  @LastModifiedBy private String lastModifiedBy;

  @LastModifiedDate private Date lastModified;
}
