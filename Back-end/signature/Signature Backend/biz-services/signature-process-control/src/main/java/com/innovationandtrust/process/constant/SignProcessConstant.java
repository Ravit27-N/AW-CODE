package com.innovationandtrust.process.constant;

import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.share.model.project.Project;
import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignProcessConstant {

  public static final String FLOW_ID = "FLOW_ID";

  public static final String PARTICIPANT_ID = "PARTICIPANT_ID";

  public static final String DOC_ID = "DOC_ID";

  // The value of the property key is the object of SignInfo
  public static final String SIGNING_INFO = SignInfo.class.getName();

  public static final String PROJECT_KEY = Project.class.getName();

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
}
