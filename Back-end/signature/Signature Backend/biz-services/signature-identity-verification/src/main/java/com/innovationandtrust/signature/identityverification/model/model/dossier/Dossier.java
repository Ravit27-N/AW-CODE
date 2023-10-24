package com.innovationandtrust.signature.identityverification.model.model.dossier;

import com.innovationandtrust.signature.identityverification.constant.dossier.DossierEntity;
import com.innovationandtrust.signature.identityverification.model.model.BaseEntity;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentType;
import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

/** Dossier entity. */
@AttributeOverride(
    name = "createdDate",
    column = @Column(name = DossierEntity.DOSSIER_CREATED_DATE))
@AttributeOverride(
    name = "lastModifiedDate",
    column = @Column(name = DossierEntity.DOSSIER_LAST_MODIFIED_DATE))
@Entity
@Table(name = DossierEntity.TABLE_NAME)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dossier extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = DossierEntity.DOSSIER_ID, unique = true, updatable = false)
  private String dossierId;

  @Column(name = DossierEntity.DOSSIER_FIRST_NAME)
  private String firstname;

  @Column(name = DossierEntity.DOSSIER_TEL)
  private String tel;

  @Column(name = DossierEntity.PARTICIPANT_UUID)
  private String participantUuid;

  @Column(name = DossierEntity.DOSSIER_NAME)
  private String dossierName;

  @Column(name = DossierEntity.DOSSIER_UUID, unique = true, updatable = false)
  private String uuid;

  @Column(name = DossierEntity.DOSSIER_STATUS)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = DossierEntity.USER_IN_CURRENT_STEP)
  @Enumerated(EnumType.STRING)
  private Step userInCurrentStep;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(
      name = DossierEntity.DOSSIER_SHAREID_ID,
      foreignKey = @ForeignKey(name = DossierEntity.DOSSIER_SHAREID_ID_FK))
  private DossierShareId dossierShareId;

  @Column(name = DossierEntity.VERIFICATION_CHOICE)
  @Enumerated(EnumType.STRING)
  private VerificationChoice verificationChoice;

  @Column(name = DossierEntity.DOC_TYPE)
  @Enumerated(EnumType.STRING)
  private DocumentType documentType;

  /**
   * Constructor for Dossier.
   *
   * @param dto DossierDto
   */
  public Dossier(DossierDto dto) {
    Assert.notNull(dto, "DossierDto must not be null");
    this.firstname = dto.getFirstname();
    this.tel = dto.getTel();
    this.userInCurrentStep = Step.CONFIRM;
    this.verificationChoice = dto.getVerificationChoice();
    this.dossierName = dto.getDossierName();
    this.dossierId = UUID.randomUUID().toString();
    this.verificationChoice = dto.getVerificationChoice();
    this.participantUuid = dto.getParticipantUuid();
    this.status = Status.ACTIVE;
  }

  @Override
  public String toString() {
    return new ToStringCreator(this)
        .append("dossierId", dossierId)
        .append("firstname", firstname)
        .append("tel", tel)
        .append("dossierName", dossierName)
        .append("status", status)
        .append("userInCurrentStep", userInCurrentStep)
        .append("dossierShareId", dossierShareId)
        .append("verificationChoice", verificationChoice)
        .append("documentType", documentType)
        .toString();
  }
}
