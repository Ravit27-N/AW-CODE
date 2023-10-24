package com.innovationandtrust.signature.identityverification.constant.dossier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Constants class for dossier. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DossierConstants {
  public static final String USER_BANNED =
      "You are not allow to use this service please contact the operator";
  public static final String USER_FINISHED_DOCUMENT_VERIFICATION =
      "You are already finished the process steps.!";
  public static final String DOSSIER_ID_NOT_EXIST = "Dossier id with %s is not exist";
  public static final String DOCUMENT_TYPE = "document_type";
  public static final String DOCUMENT_COUNTRY = "document_country";
  public static final String DOCUMENT_FRONT = "picture_front_document";
  public static final String DOCUMENT_BACK = "picture_back_document";
  public static final String DOCUMENT_ROTATION = "document_rotation";
  public static final String DOC_VERIFICATION_URL = "/v1/document_authenticity_photo_demand/fill";
  public static final String UPLOAD_DIRECTORY = "document";
  public static final String DOSSIER_ALREADY_EXIST = "Dossier already exist";

  /** Constants class for dossier endpoint. */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Endpoint {
    public static final String CREATE_DOSSIER = "/create";
    public static final String CREATE_DOSSIERS = "/create/multi";
    public static final String FIND_BY_DOSSIER_ID = "/{dossierId}";
    public static final String UPDATE_DOSSIER_UUID = "/update";
    public static final String DOSSIER_VERIFY = "/{dossierId}/verify";
    public static final String DOSSIER_CONFIRM = "/confirm/{dossierId}";
    public static final String DOSSIER_VALIDATE = "/validate/{dossierId}";
  }
}
