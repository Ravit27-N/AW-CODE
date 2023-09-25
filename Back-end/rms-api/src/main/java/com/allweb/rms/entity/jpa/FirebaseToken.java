package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FIREBASE_TOKEN")
@IdClass(FirebaseTokenId.class)
public class FirebaseToken implements Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  @Id private String userId;

  @Id private String deviceId;

  @NotEmpty
  @Column(nullable = false)
  private String fcmToken;
}
