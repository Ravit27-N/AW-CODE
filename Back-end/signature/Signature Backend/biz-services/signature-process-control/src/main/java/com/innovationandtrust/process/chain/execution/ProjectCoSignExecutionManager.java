package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CreateProjectCompleteHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.DossierProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.RequestSigningHandler;
import com.innovationandtrust.process.chain.handler.ViewerInvitationHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectCoSignExecutionManager extends ExecutionManager {

  private final DocumentProcessingHandler documentHandler;
  private final RequestSigningHandler requestSigningHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ParticipantUnorderedInvitationHandler unorderedInvitationHandler;
  private final CreateProjectCompleteHandler createProjectCompleteHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final NotificationReminderScheduleHandler reminderScheduleHandler;
  private final ViewerInvitationHandler viewerInvitationHandler;
  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;
  private final DossierProcessHandler dossierProcessHandler;

  public ProjectCoSignExecutionManager(
      DocumentProcessingHandler documentHandler,
      RequestSigningHandler requestSigningHandler,
      JsonFileProcessHandler jsonFileProcessHandler,
      ParticipantUnorderedInvitationHandler unorderedInvitationHandler,
      CreateProjectCompleteHandler createProjectCompleteHandler,
      ProjectWebHookHandler projectWebHookHandler,
      NotificationReminderScheduleHandler reminderScheduleHandler,
      ViewerInvitationHandler viewerInvitationHandler,
      ProjectExpiredScheduleHandler expiredProjectScheduleHandler,
      DossierProcessHandler dossierProcessHandler) {
    this.documentHandler = documentHandler;
    this.requestSigningHandler = requestSigningHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.unorderedInvitationHandler = unorderedInvitationHandler;
    this.createProjectCompleteHandler = createProjectCompleteHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.reminderScheduleHandler = reminderScheduleHandler;
    this.viewerInvitationHandler = viewerInvitationHandler;
    this.expiredProjectScheduleHandler = expiredProjectScheduleHandler;
    this.dossierProcessHandler = dossierProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            documentHandler,
            dossierProcessHandler,
            requestSigningHandler,
            // To create a json file
            jsonFileProcessHandler,
            viewerInvitationHandler,
            unorderedInvitationHandler,
            createProjectCompleteHandler,
            projectWebHookHandler,
            // To set notification reminder
            reminderScheduleHandler,
            expiredProjectScheduleHandler,
            // To update json file after invitation
            jsonFileProcessHandler));
  }
}
