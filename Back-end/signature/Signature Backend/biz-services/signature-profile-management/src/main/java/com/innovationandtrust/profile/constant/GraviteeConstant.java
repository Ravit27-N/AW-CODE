package com.innovationandtrust.profile.constant;

import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.GraviteeResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraviteeConstant {
  public static final String LIST_APPLICATION_RESPONSE = "listApplicationResponse";
  public static final String PLAN_ID = "planId";
  public static final String API_ID = "apiId";
  public static final String COMPANY_UUID = "companyUuid";
  public static final String COMPANY_NAME = "companyName";
  public static final String GRAVITEE_RESPONSE_KEY = GraviteeResponse.class.getName();
  public static final String GRAVITEE_PROPERTY_KEY = GraviteeProperty.class.getName();
}
