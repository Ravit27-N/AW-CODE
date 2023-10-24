package com.innovationandtrust.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** All project detail type. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetailTypeConstant {
  public static final String APPROVAL = "approval";
  public static final String SIGNATORY = "signatory";
  public static final String RECIPIENT = "receipt";
  public static final String VIEWER = "viewer";
}
