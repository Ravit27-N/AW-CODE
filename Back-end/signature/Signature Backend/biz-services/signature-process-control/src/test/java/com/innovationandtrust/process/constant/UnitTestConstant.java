package com.innovationandtrust.process.constant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
Note: These value base on json file
 */
@Slf4j
@RequiredArgsConstructor
public final class UnitTestConstant {
  public static final String JSON = ".json";
  public static final String FLOW_ID = "3b0d2981-111e-4462-a7bc-f43b64f8303d";
  public static final String COMPANY_UUID = "3b0d2981-111e-4462-a7bc-f43b64f8303d";
  public static final String VIDEO_ID = "c8d3439d-68f4-45c5-bcdd-d2a6286a820b";
  public static final String OTP_CODE = "902173";
  public static final String EXPIRED_DATE = "10-10-2020";
  public static final String PHONE_NUMBER = "+855 012 8334 7342";
  public static final String UUID = "b17c7e52-c605-4f0d-a809-1f3449008343";
  public static final String DOC_ID = "6eba5088-6d5e-4c5f-a0ba-2ed38ed3eb0d";
  // This value got from json test
  public static final String CREATOR_UUID = "bfecf77e-3779-4746-b16d-309357abba80";
  // This value got from json test
  public static final Long CREATOR_ID = 10L;
  // Token should content the same, companyUuid, flowId, uuid
  public static final String TOKEN =
      "DUCBtmFRpgN0Ya8McWP7K0C6jSqxC9UITjL-vS-pW_rMptQ8qsDg_vwT9ieODA_rgMMMvdoBHohk6HEIs8Yp_cRpLlTDXd9jcM3hBwbFkxJJBGLxmChbbvJNawaXl7jD0S-w8J7dZtCvkAwbqGLPDUYpwhy4aFN18eGfnF2G9-0";
  public static final String JSON_FILE = FLOW_ID + JSON;
  public static final String FILE_PATH =
      "/UnitTest/file_control/3b0d2981-111e-4462-a7bc-f43b64f8303d.json";
  public static final String FILE_PATH_ORIGINAL =
      "/UnitTest/file_control/3b0d2981-111e-4462-a7bc-f43b64f8303d_original.json";

  public static final String DOC_PATH =
      "/3b0d2981-111e-4462-a7bc-f43b64f8303d/document/57d6361c-0ef1-460e-9284-1b76fa8d22a0.pdf";

  public static final String EMAIL = "signature@certigna.fr";
  public static final String ASSERT_EXCEPTION = "The exception should be.";
  public static final String ASSERT_TRUE = "The result should be true.";
  public static final String ASSERT_EQUALS = "The result should be equal.";
  public static final String ASSERT_NOT_NULL = "The result should not be null.";
  public static final String VERIFICATION_ID = "8dc33083-a5d8-4a65-90b4-f359de701c5c";
  public static final String IDENTITY_ID = "58674ac9-afa7-443d-ac87-69e2f0e80825";
  public static final String IMAGE_DOC_ID = "58674ac9-afa7-443d-ac87-69e2f0e80825.png";
  public static final String IMAGE_NOT_EXIST = "59674ac9-afa7-443d-ac87-69e2f0e80825.png";
  public static final String DOC_BASE64 =
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACXBIWXMAAAsTAAALEwEAmpwYAAAADUlEQVR4nGNgYGAwBgAAOAA0Knr9CgAAAABJRU5ErkJggg==";
}
