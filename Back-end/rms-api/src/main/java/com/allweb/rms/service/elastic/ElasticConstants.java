package com.allweb.rms.service.elastic;

public abstract class ElasticConstants {
  public static final String PAINLESS_SCRIPT_LANGUAGE = "painless";
  public static final String INTERVIEW_UPDATE_REQUEST_KEY = "update-interview";
  public static final String INTERVIEW_INSERT_REQUEST_KEY = "insert-interview";
  public static final String INTERVIEW_HARD_DELETE_REQUEST_KEY = "hard-delete-interview";
  public static final String CANDIDATE_UPDATE_REQUEST_KEY = "update-candidate";
  public static final String CANDIDATE_INSERT_REQUEST_KEY = "insert-candidate";
  public static final String CANDIDATE_HARD_REQUEST_KEY = "hard-delete-candidate";
  public static final String REMINDER_UPDATE_REQUEST_KEY = "update-reminder";
  public static final String REMINDER_INSERT_REQUEST_KEY = "insert-reminder";
  public static final String REMINDER_HARD_DELETE_REQUEST_KEY = "hard-delete-reminder";
  public static final String INTERVIEW_OBJECT_KEY = "interview";
  public static final String REMINDER_OBJECT_KEY = "reminder";
  public static final String CANDIDATE_OBJECT_KEY = "candidate";
  public static final String UNIVERSITY_ID_LIST_KEY = "university-id-list";
  public static final String OPERATION_KEY = "operation";
  public static final String INSERT_OPERATION = "insert";
  public static final String UPDATE_OPERATION = "update";
  public static final String DELETE_OPERATION = "delete";
  private ElasticConstants() {}
}
