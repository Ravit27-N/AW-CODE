package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CreateProjectCompleteHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.DossierProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.ViewerInvitationHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectIndividualSignExecutionManager extends ExecutionManager {

  private final DocumentProcessingHandler documentProcessingHandler;
  private final ParticipantUnorderedInvitationHandler unorderedInvitationHandler;
  private final NotificationReminderScheduleHandler reminderScheduleHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ViewerInvitationHandler viewerInvitationHandler;
  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;
  private final CreateProjectCompleteHandler createProjectCompleteHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final DossierProcessHandler dossierProcessHandler;

  public ProjectIndividualSignExecutionManager(
      DocumentProcessingHandler documentProcessingHandler,
      ParticipantUnorderedInvitationHandler unorderedInvitationHandler,
      NotificationReminderScheduleHandler reminderScheduleHandler,
      JsonFileProcessHandler jsonFileProcessHandler,
      ViewerInvitationHandler viewerInvitationHandler,
      ProjectExpiredScheduleHandler expiredProjectScheduleHandler,
      CreateProjectCompleteHandler createProjectCompleteHandler,
      ProjectWebHookHandler projectWebHookHandler,
      DossierProcessHandler dossierProcessHandler) {
    this.documentProcessingHandler = documentProcessingHandler;
    this.unorderedInvitationHandler = unorderedInvitationHandler;
    this.reminderScheduleHandler = reminderScheduleHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.viewerInvitationHandler = viewerInvitationHandler;
    this.expiredProjectScheduleHandler = expiredProjectScheduleHandler;
    this.createProjectCompleteHandler = createProjectCompleteHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.dossierProcessHandler = dossierProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            documentProcessingHandler,
            dossierProcessHandler,
            // To create a json file
            jsonFileProcessHandler,
            viewerInvitationHandler,
            unorderedInvitationHandler,
            createProjectCompleteHandler,
            projectWebHookHandler,
            // set reminder schedule
            reminderScheduleHandler,
            expiredProjectScheduleHandler,
            // To update json file after invitation
            jsonFileProcessHandler));
  }
}
