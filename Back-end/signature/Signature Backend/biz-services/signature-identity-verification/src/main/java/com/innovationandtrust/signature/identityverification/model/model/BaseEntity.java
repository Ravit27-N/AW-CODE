package com.innovationandtrust.signature.identityverification.model.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Base entity for all entities. */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  protected Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  protected Date lastModifiedDate;
}
