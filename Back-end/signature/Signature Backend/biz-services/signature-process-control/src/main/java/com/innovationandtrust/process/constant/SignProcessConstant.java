package com.innovationandtrust.process.constant;

import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.share.model.project.Project;
import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignProcessConstant {
  public static final Integer MAX_ATTEMPTS = 3;

  public static final String FLOW_ID = "FLOW_ID";
  public static final String FLOW_IDS = "FLOW_IDS";

  public static final String PARTICIPANT_ID = "PARTICIPANT_ID";

  public static final String DOC_ID = "DOC_ID";

  // The value of the property key is the object of SignInfo
  public static final String SIGNING_INFO = SignInfo.class.getName();

  public static final String PROJECT_KEY = Project.class.getName();

  public static final String WEBHOOK_NOTIFICATION = "WEBHOOK_NOTIFICATION";
  public static final String WEBHOOK_EVENT = "WEBHOOK_EVENT";

  public static final String JSON_FILE_PROCESS_ACTION = JsonFileProcessAction.class.getName();

  public static final String OTP_PROCESS_ACTION = OtpProcessAction.class.getName();

  public static final String DOCUMENT_PROCESS_ACTION = DocumentProcessAction.class.getName();

  public static final String DOWNLOAD_SIGNED_DOC = FileResponse.class.getName();

  public static final String DOWNLOAD_MANIFEST = FileResponse.class.getName();

  public static final String IS_PROJECT_FINISHED = "IS_PROJECT_FINISHED";

  public static final String VIEW_DOC_FOR_SIGN = "VIEW_DOC_FOR_SIGN";

  public static final String UPLOAD_MODIFIED_DOC_FOR_SIGN = "UPLOAD_MODIFIED_DOC";

  // The value of the property key is String type
  public static final String OTP_VALUE = "OTP_VALUE";

  public static final String PHONE_NUMBER = "PHONE_NUMBER";

  public static final String COMMENT = "COMMENT";

  public static final String NEW_EXPIRE_DATE = "EXPIRE_DATE";

  public static final String BASE_URI = URI.class.getName();

  public static final String JOB_GROUP = "JOB_GROUP";

  public static final String DOCUMENTS_TO_VERIFY = "DOCUMENTS_TO_VERIFY";

  public static final String VERIFY_DOCUMENT_RESPONSE = "VERIFY_DOCUMENT_RESPONSE";

  public static final String SIGNATURE_IMAGE = "SIGNATURE_IMAGE";

  public static final String SIGNATURE_MODE = "SIGNATURE_MODE";

  public static final String FILE_ACTION = "FILE_ACTION";

  public static final String IDENTIFICATION_ID = "IDENTIFICATION_ID";

  public static final String VIDEOID_AUTHORIZATION = "VIDEOID_AUTHORIZATION";

  public static final String VIDEOID_VERIFICATION = "VIDEOID_VERIFICATION";

  public static final String VIDEO_ID = "VIDEO_ID";

  public static final String VIDEO_VERIFIED = "VIDEO_VERIFIED";

  public static final String VIDEO_VERIFIED_STATUS = "VIDEO_VERIFIED_STATUS";

  public static final String VIDEO_VERIFICATION_ID = "VIDEO_VERIFICATION_ID";

  public static final String REQUEST_SIGN = "REQUEST_SIGN";

  public static final String SIGN_DOCUMENT = "SIGN_DOCUMENT";
  public static final String OTP_CODE = "OTP_CODE";
  public static final String IDENTITY_DOCUMENTS = "IDENTITY_DOCUMENTS";
  public static final String MULTI_SIGNING_PROJECTS = "MULTI_SIGNING_PROJECTS";
  public static final String PARTICIPANT = "PARTICIPANT";
  public static final String SIGNATORY_ID = "SIGNATORY_ID";
  public static final String API_NG_SIGN_REQUEST = "API_NG_SIGN_REQUEST";
  public static final String PARTICIPANT_DOCUMENT_STATUS = "PARTICIPANT_DOCUMENT_STATUS";
  public static final String SIGNED_DATE = "SIGNED_DATE";
  public static final String PROJECTS = "PROJECTS";
  public static final String VALID_PHONE = "VALID_PHONE";
  public static final String VALID_OTP = "VALID_OTP";
  public static final String TEMP_SIGNATURE_IMAGE = "TEMP_SIGNATURE_IMAGE";
  public static final String PROJECTS_FAIL_VALIDATED = "PROJECTS_FAIL_VALIDATED";
  public static final String STATUS = "STATUS";
  public static final String IS_SIGNING_PROJECTS = "IS_SIGNING_PROJECTS";
}
