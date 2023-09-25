package com.allweb.rms.core.scheduler;

public final class ReminderConstants {

  public static final String BATCH_JOB_NAME_KEY = "BATCH-JOB-NAME";
  public static final String ALLOW_REPORT_SENDING_STATE_KEY = "ALLOW-BATCH-REPORT-SENDING-STATE";
  public static final String REMINDER_ID_KEY = "REMINDER-ID";
  public static final String REMINDER_KEY = "REMINDER";
  public static final String CANDIDATE_ID_KEY = "CANDIDATE-ID";
  public static final String INTERVIEW_ID_KEY = "INTERVIEW-ID";
  public static final String FIREBASE_TASK_DATA_MAP = "FIREBASE-TASK-DATA-MAP";
  public static final String FIREBASE_SENT_SUCCESS = "FIREBASE-SENT-SUCCESS";
  public static final String EMAIL_TASK_DATA_MAP = "";
  public static final String EMAIL_SENT_SUCCESS = "EMAIL-SENT-SUCCESS";
  public static final String BATCH_REMINDER = "BATCH-REMINDER";
  // Exception messages
  public static final String REMINDER_NOT_FOUND = "Reminder is not found.";
  public static final String MAIL_TEMPLATE_NOT_FOUND = "Mail template is not found.";
  public static final String CANDIDATE_NOT_FOUND = "Candidate is not found.";
  public static final String INTERVIEW_NOT_FOUND = "Interview is not found.";
  public static final String USERNAME_NOT_FOUND = "Username is not found.";
  /** Authenticated username. */
  public static final String AUTHENTICATED_USER_KEY = "AUTHENTICATED-USER";
  // Recruiter detail
  public static final String USER_EMAIL_KEY = "USER-EMAIL";
  public static final String SENDER_EMAIL_NOT_FOUND = "Sender email not found.";
  private ReminderConstants() {}
}
