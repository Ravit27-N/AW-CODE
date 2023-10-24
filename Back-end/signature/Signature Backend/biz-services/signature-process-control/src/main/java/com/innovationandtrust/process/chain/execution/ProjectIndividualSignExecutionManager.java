package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CreateProjectCompleteHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.DossierProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.ViewerInvitationHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectIndividualSignExecutionManager extends ExecutionManager {

  private final DocumentProcessingHandler documentProcessingHandler;
  private final ParticipantUnorderedInvitationHandler unorderedInvitationHandler;
  private final NotificationReminderScheduleHandler reminderScheduleHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ViewerInvitationHandler viewerInvitationHandler;
  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;
  private final CreateProjectCompleteHandler createProjectCompleteHandler;
  private final DossierProcessHandler dossierProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            documentProcessingHandler,
            dossierProcessHandler,
            viewerInvitationHandler,
            unorderedInvitationHandler,
            createProjectCompleteHandler,
            // set reminder schedule
            reminderScheduleHandler,
            expiredProjectScheduleHandler,
            // To create a json file for the signing process
            jsonFileProcessHandler));
  }
}
