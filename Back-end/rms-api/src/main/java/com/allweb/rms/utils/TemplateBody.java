package com.allweb.rms.utils;

import static com.allweb.rms.utils.SystemConfigurationConstants.BASE_PATH;
import static com.allweb.rms.utils.SystemConfigurationConstants.CANDIDATE_PATH;
import static com.allweb.rms.utils.SystemConfigurationConstants.INTERVIEW_PATH;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.ReminderRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public enum TemplateBody {
  TITLE("title"),
  DESCRIPTION("description"),
  DATE_INTERVIEW("date_interview"),
  DATE_REMINDER("date_reminder"),
  CANDIDATE_NAME("candidate_name"),
  CANDIDATE_LINK("candidate_link"),
  INTERVIEW_TITLE("interview_title"),
  INTERVIEW_LINK("interview_link");

  private final String value;

  TemplateBody(String value) {
    this.value = value;
  }

  public static List<String> getTemplateBodyList() {
    return Arrays.asList(
        TITLE.getValue(),
        DESCRIPTION.getValue(),
        DATE_REMINDER.getValue(),
        CANDIDATE_NAME.getValue(),
        CANDIDATE_LINK.getValue(),
        INTERVIEW_TITLE.getValue(),
        DATE_INTERVIEW.getValue(),
        INTERVIEW_LINK.getValue());
  }

  /**
   * Set string replacements index for Template body
   *
   * @param candidateDTO object
   * @param interviewRequest object
   * @param reminderRequest object
   * @return String[]
   */
  public static String[] getReplacements(
      CandidateDTO candidateDTO,
      InterviewRequest interviewRequest,
      ReminderRequest reminderRequest,
      Map<String, String> config) {
    DateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    date.setTimeZone(TimeZone.getTimeZone("Asia/Phnom_Penh"));
    String[] replacement = new String[getTemplateBodyList().size()];
    String candidateLink =
        "<a href=\""
            + config.get(BASE_PATH.getValue())
            + "/"
            + config.get(CANDIDATE_PATH.getValue())
            + "/"
            + candidateDTO.getId()
            + "\"rel=\"noopener noreferrer\" target=\"_blank\">LINK</a>";
    String interviewLink =
        "<a href=\""
            + config.get(BASE_PATH.getValue())
            + "/"
            + config.get(INTERVIEW_PATH.getValue())
            + "/"
            + interviewRequest.getId()
            + "\"rel=\"noopener noreferrer\" target=\"_blank\">LINK</a>";

    if (reminderRequest.getDateReminder() != null)
      replacement[2] = date.format(reminderRequest.getDateReminder());
    if (interviewRequest.getDateTime() != null)
      replacement[6] = date.format(interviewRequest.getDateTime());
    replacement[0] = reminderRequest.getTitle();
    replacement[1] = reminderRequest.getDescription();
    replacement[3] = candidateDTO.getFullName();
    replacement[4] = candidateLink;
    replacement[5] = interviewRequest.getTitle();
    replacement[7] = interviewLink;
    return replacement;
  }

  public String getValue() {
    return value;
  }
}
