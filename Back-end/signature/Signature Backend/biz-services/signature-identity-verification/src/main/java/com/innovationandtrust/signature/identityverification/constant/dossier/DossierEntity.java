package com.innovationandtrust.signature.identityverification.constant.dossier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Constants class for dossier. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DossierEntity {
  public static final String TABLE_NAME = "dossiers";
  public static final String DOSSIER_NAME = "dossier_name";
  public static final String DOSSIER_ID = "dossier_id";
  public static final String DOSSIER_FIRST_NAME = "firstname";
  public static final String PARTICIPANT_UUID = "participant_uuid";
  public static final String DOSSIER_TEL = "tel";
  public static final String DOSSIER_EMAIL = "email";
  public static final String DOSSIER_UUID = "uuid";
  public static final String DOSSIER_OTP = "otp";
  public static final String PHONE_ATTEMPTS = "phone_attempts";
  public static final String OTP_ATTEMPTS = "otp_attempts";
  public static final String RETRY_ATTEMPTS = "retry_attempts";
  public static final String OTP_EXPIRE_TIME = "otp_expire_time";
  public static final String DOSSIER_STATUS = "status";
  public static final String USER_IN_CURRENT_STEP = "user_in_current_step";
  public static final String DOSSIER_SHAREID_ID = "dossier_shareid_id";
  public static final String DOSSIER_SHAREID_ID_FK = "dossier_shareid_id_fk";
  public static final String VERIFICATION_CHOICE = "verification_choice";
  public static final String DOC_TYPE = "document_type";
  public static final String DOSSIER_CREATED_DATE = "dossier_created_date";
  public static final String DOSSIER_LAST_MODIFIED_DATE = "dossier_last_modified_date";
}
