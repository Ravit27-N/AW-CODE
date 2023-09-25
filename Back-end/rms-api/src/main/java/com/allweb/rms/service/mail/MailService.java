package com.allweb.rms.service.mail;

import static com.allweb.rms.utils.SystemConfigurationConstants.MAIL_PROVIDER;
import static com.allweb.rms.utils.SystemConfigurationConstants.MAIL_SENDER;
import static com.allweb.rms.utils.SystemConfigurationConstants.SENDGRID;
import static com.allweb.rms.utils.TemplateBody.getReplacements;
import static com.allweb.rms.utils.TemplateBody.getTemplateBodyList;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.ReminderRequest;
import com.allweb.rms.entity.jpa.MailConfiguration;
import com.allweb.rms.entity.jpa.MailTemplate;
import com.allweb.rms.exception.MailConfigurationNotFoundException;
import com.allweb.rms.exception.MailTemplateNotFoundException;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.MailConfigurationRepository;
import com.allweb.rms.repository.jpa.MailTemplateRepository;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.service.mail.config.AbstractConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
  private static final String MSG_MAIL = "No Mail template found in configuration";
  private static final String MSG_STATUS = "No Mail Configuration found for this status";
  /** Mail Service */
  private final GmailService gmailService;
  private final SendGridMailService gridMailService;
  private final MailConfigurationRepository repository;
  private final MailTemplateRepository templateRepository;
  private final CandidateRepository candidateRepository;
  @Autowired private ModelMapper mapper;

  @Autowired
  protected MailService(
      SystemConfigurationRepository systemMailConfigurationRepository,
      ModelMapper modelMapper,
      GmailService gmailService,
      SendGridMailService gridMailService,
      MailConfigurationRepository repository,
      MailTemplateRepository mailTemplateRepository,
      CandidateRepository candidateRepository) {
    super(systemMailConfigurationRepository, modelMapper);
    this.gmailService = gmailService;
    this.gridMailService = gridMailService;
    this.repository = repository;
    this.templateRepository = mailTemplateRepository;
    this.candidateRepository = candidateRepository;
  }

  /** Set mail status change */
  @Async
  public void setMailStatusChange(CandidateDTO candidateDTO) {
    MailConfiguration mailConfig = this.getMailConfiguration(candidateDTO.getStatusId());
    MailTemplate mailTemplateById = this.getMailTemplateById(mailConfig.getMailTemplate().getId());
    mailTemplateById.setBody(
        replaceString(
            mailTemplateById.getBody(),
            getTemplateBodyList().toArray(new String[0]),
            getReplacements(
                candidateDTO, new InterviewRequest(), new ReminderRequest(), config())));
    sender(
        mailConfig.getFrom(),
        new HashSet<>(mailConfig.getTo()),
        new HashSet<>(mailConfig.getCc()),
        mailConfig.getTitle(),
        mailTemplateById.getBody());
  }

  /** Set mail interview */
  @Async
  public void setMailInterview(InterviewRequest mailInterview) {
    CandidateDTO candidateDTO = this.getCandidate(mailInterview.getCandidateId());
    String f = config().get(MAIL_SENDER.getValue());
    String defaultMailTemplate = this.getDefaultMailTemplate().getBody();
    defaultMailTemplate =
        replaceString(
            defaultMailTemplate,
            getTemplateBodyList().toArray(new String[0]),
            getReplacements(candidateDTO, mailInterview, new ReminderRequest(), config()));
    sender(
        f,
        new HashSet<>(Collections.singletonList(candidateDTO.getEmail())),
        new HashSet<>(),
        mailInterview.getTitle(),
        defaultMailTemplate);
  }

  /** Check provider to send */
  @Async
  public void sender(String from, Set<String> to, Set<String> cc, String subject, String body) {
    LOGGER.info("Mail Service running.....");
    if (config().get(MAIL_PROVIDER.getValue()).equals(SENDGRID.getValue())) {
      gridMailService.sendHTML(from, to, cc, subject, body);
    } else {
      gmailService.sendHTML(from, to, cc, subject, body);
    }
  }

  /**
   * Meth use to replaceString
   *
   * @param master string
   * @param target string[]
   * @param replacement string[]
   * @return string
   */
  public String replaceString(String master, String[] target, String[] replacement) {
    ArrayList<String> newArray = new ArrayList<>();
    for (String s : target) {
      newArray.add("${" + s + "}");
    }
    return StringUtils.replaceEach(master, newArray.toArray(new String[0]), replacement);
  }

  @Cacheable(value = "mailTemplateCaching")
  public MailTemplate getDefaultMailTemplate() {
    return templateRepository
        .findById(5)
        .orElseThrow(
            () ->
                new MailTemplateNotFoundException(
                    MSG_MAIL)); // Default Mail: Send invitation to candidate for Interview
  }

  @Cacheable(value = "mailTemplateCaching")
  public MailTemplate getMailTemplateById(int id) {
    return templateRepository
        .findById(id)
        .orElseThrow(() -> new MailTemplateNotFoundException(MSG_MAIL));
  }

  @Cacheable(value = "candidateCaching")
  public CandidateDTO getCandidate(int id) {
    return mapper.map(
        candidateRepository
            .findById(id)
            .orElseThrow(() -> new MailConfigurationNotFoundException(MSG_STATUS)),
        CandidateDTO.class);
  }

  @Cacheable(value = "mailConfigurationCaching")
  public MailConfiguration getMailConfiguration(int id) {
    return repository
        .findByCandidateStatusIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new MailConfigurationNotFoundException(MSG_STATUS));
  }
}
