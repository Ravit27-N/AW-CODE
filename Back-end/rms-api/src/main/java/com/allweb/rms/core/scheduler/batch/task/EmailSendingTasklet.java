package com.allweb.rms.core.scheduler.batch.task;

import static com.allweb.rms.utils.SystemConfigurationConstants.BASE_PATH;
import static com.allweb.rms.utils.SystemConfigurationConstants.CANDIDATE_PATH;

import com.allweb.rms.core.mail.GenericMailMessage;
import com.allweb.rms.core.mail.MailException;
import com.allweb.rms.core.mail.MailHandler;
import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.batch.exception.TaskExecutionException;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.MailTemplate;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.entity.jpa.SystemConfiguration;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.service.MailServiceProvider;
import com.allweb.rms.utils.ReminderType;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSendingTasklet extends AbstractTasklet {
  private static final String VAR_TITLE_KEY = "${title}";
  private static final String VAR_DATE_REMINDER_KEY = "${date_reminder}";
  private static final String VAR_DESCRIPTION_KEY = "${description}";
  private static final String VAR_CANDIDATE_NAME_KEY = "${candidate_name}";
  private static final String VAR_CANDIDATE_LINK_KEY = "${candidate_link}";
  private static final String VAR_INTERVIEW_TITLE_KEY = "${interview_title}";
  private static final String VAR_DATE_INTERVIEW_KEY = "${date_interview}";

  private final MailServiceProvider mailServiceProvider;
  private final SystemConfigurationRepository systemConfigurationRepository;
  private final Map<String, String> configuration;
  private final Map<String, Object> dataMap = new HashMap<>();
  private final SimpleDateFormat simpleDateFormat;
  private String userEmail;
  private Reminder reminder;
  private Interview interview;
  private Candidate candidate;
  private MailTemplate mailTemplate;
  private boolean successState;

  public EmailSendingTasklet(
      MailServiceProvider mailServiceProvider,
      SystemConfigurationRepository systemConfigurationRepository,
      @Value("${pattern.datetime.format}") String dateFormat,
      @Value("${application.default.client.timezone}") String timezone) {
    this.mailServiceProvider = mailServiceProvider;
    this.systemConfigurationRepository = systemConfigurationRepository;
    this.configuration = this.getConfiguration();
    simpleDateFormat = new SimpleDateFormat(dateFormat);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
  }

  Map<String, String> getConfiguration() {
    return systemConfigurationRepository.findAll().stream()
        .collect(
            Collectors.toMap(
                SystemConfiguration::getConfigKey, SystemConfiguration::getConfigValue));
  }

  @Override
  protected void beforeExecute(StepContribution contribution, ChunkContext chunkContext) {
    Optional<String> paramUserEmail =
        this.getJobParameter(ReminderConstants.USER_EMAIL_KEY, chunkContext);
    paramUserEmail.ifPresent(s -> this.userEmail = s);
    ExecutionContext jobExecutionContext = this.getJobExecutionContext(chunkContext);
    reminder = (Reminder) jobExecutionContext.get(ReminderConstants.REMINDER_KEY);
    if (reminder != null) {
      mailTemplate = reminder.getReminderType().getMailTemplate();
      ReminderType reminderType = ReminderType.valueOf(reminder.getReminderType().getId());
      if (reminderType == ReminderType.SPECIAL) {
        candidate = reminder.getCandidate();
      } else if (reminderType == ReminderType.INTERVIEW) {
        interview = reminder.getInterview();
        candidate = reminder.getInterview().getCandidate();
      }
    }
  }

  @Override
  protected void validate() {
    if (userEmail == null) {
      throw new NullPointerException(ReminderConstants.SENDER_EMAIL_NOT_FOUND);
    }
    if (reminder == null) {
      throw new NullPointerException(ReminderConstants.REMINDER_NOT_FOUND);
    }
    if (mailTemplate == null) {
      throw new NullPointerException(ReminderConstants.MAIL_TEMPLATE_NOT_FOUND);
    }
    ReminderType reminderType = ReminderType.valueOf(reminder.getReminderType().getId());
    if (reminderType != ReminderType.NORMAL && candidate == null) {
      throw new NullPointerException(ReminderConstants.CANDIDATE_NOT_FOUND);
    }
    if (reminderType == ReminderType.INTERVIEW && interview == null) {
      throw new NullPointerException(ReminderConstants.INTERVIEW_NOT_FOUND);
    }
  }

  @Override
  protected RepeatStatus executeInternal(StepContribution contribution, ChunkContext chunkContext)
      throws TaskExecutionException {
    GenericMailMessage mailMessage = this.getMailMessage();
    MailHandler handler = this.mailServiceProvider.getConfiguredMailHandler();
    TaskExecutionException taskExecutionException = null;
    try {
      handler.send(mailMessage);
      successState = true;
    } catch (MailException e) {
      taskExecutionException = new TaskExecutionException(e);
    }
    if (taskExecutionException != null) {
      throw taskExecutionException;
    }
    return RepeatStatus.FINISHED;
  }

  @Override
  protected void afterExecute(StepContribution contribution, ChunkContext chunkContext) {
    this.dataMap.put(ReminderConstants.EMAIL_SENT_SUCCESS, this.successState);
    ExecutionContext stepExecutionContext = this.getStepExecutionContext(chunkContext);
    stepExecutionContext.put(ReminderConstants.EMAIL_TASK_DATA_MAP, this.dataMap);
  }

  private GenericMailMessage getMailMessage() {
    GenericMailMessage mailMessage = new GenericMailMessage();
    String from = this.mailServiceProvider.getSystemAdminEmailAddress();
    String to = this.userEmail;
    String subject = this.mailTemplate.getSubject();
    String mailBody = this.parseEmailBody(this.mailTemplate.getBody());
    mailMessage.setTo(to);
    mailMessage.setFrom(from);
    mailMessage.setSubject(subject);
    mailMessage.setBody(mailBody);
    mailMessage.setReplyTo(from);
    return mailMessage;
  }

  // Email body template parsing
  private String parseEmailBody(String emailTemplate) {
    if (reminder != null) {
      ReminderType reminderType = ReminderType.valueOf(reminder.getReminderType().getId());
      if (reminderType == ReminderType.SPECIAL) {
        return this.parseSpecialReminderEmailBody(emailTemplate);
      } else if (reminderType == ReminderType.INTERVIEW) {
        return this.parseInterviewReminderEmailBody(emailTemplate);
      } else {
        return this.parseNormalReminderEmailBody(emailTemplate);
      }
    }
    return null;
  }

  private String parseSpecialReminderEmailBody(String emailTemplate) {
    String parsed = emailTemplate;
    parsed = parsed.replace(VAR_TITLE_KEY, reminder.getTitle());
    parsed = parsed.replace(VAR_DESCRIPTION_KEY, reminder.getDescription());

    parsed =
        parsed.replace(
            VAR_DATE_REMINDER_KEY, this.simpleDateFormat.format(reminder.getDateReminder()));
    parsed = parsed.replace(VAR_CANDIDATE_NAME_KEY, getCandidateName());

    parsed = parsed.replace(VAR_CANDIDATE_LINK_KEY, getCandidateLink());
    return parsed;
  }

  private String parseInterviewReminderEmailBody(String emailTemplate) {
    String parsed = emailTemplate;
    parsed = parsed.replace(VAR_INTERVIEW_TITLE_KEY, interview.getTitle());
    parsed =
        parsed.replace(
            VAR_DATE_INTERVIEW_KEY, this.simpleDateFormat.format(interview.getDateTime()));

    parsed = parsed.replace(VAR_CANDIDATE_NAME_KEY, getCandidateName());
    parsed = parsed.replace(VAR_CANDIDATE_LINK_KEY, getCandidateLink());
    return parsed;
  }

  private String parseNormalReminderEmailBody(String emailTemplate) {
    String parsed = emailTemplate;
    parsed = parsed.replace(VAR_TITLE_KEY, reminder.getTitle());
    parsed =
        parsed.replace(
            VAR_DATE_REMINDER_KEY, this.simpleDateFormat.format(reminder.getDateReminder()));
    parsed = parsed.replace(VAR_DESCRIPTION_KEY, reminder.getDescription());
    return parsed;
  }

  private String getCandidateLink() {
    return "<a href=\""
        + this.getCandidateBasePath()
        + "/"
        + this.candidate.getId()
        + " \"rel=\"noopener noreferrer\" target=\"_blank\">"
        + getCandidateName()
        + "</a>";
  }

  private CharSequence getCandidateName() {
    return String.format(
        "%s %s %s", candidate.getSalutation(), candidate.getFirstname(), candidate.getLastname());
  }

  private String getCandidateBasePath() {
    return String.format(
        "%s%s",
        this.configuration.get(BASE_PATH.getValue()),
        this.configuration.get(CANDIDATE_PATH.getValue()));
  }
}
