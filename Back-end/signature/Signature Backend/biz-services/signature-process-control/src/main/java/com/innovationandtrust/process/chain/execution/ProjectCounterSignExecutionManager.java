package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CreateProjectCompleteHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.DossierProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.RequestSigningHandler;
import com.innovationandtrust.process.chain.handler.ViewerInvitationHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectCounterSignExecutionManager extends ExecutionManager {

  private final DocumentProcessingHandler documentHandler;

  private final RequestSigningHandler requestSigningHandler;

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;

  private final CreateProjectCompleteHandler createProjectCompleteHandler;

  private final NotificationReminderScheduleHandler reminderScheduleHandler;

  private final ViewerInvitationHandler viewerInvitationHandler;

  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;

  private final DossierProcessHandler dossierProcessHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            documentHandler,
            dossierProcessHandler,
            requestSigningHandler,
            viewerInvitationHandler,
            participantOrderInvitationHandler,
            createProjectCompleteHandler,
            reminderScheduleHandler,
            expiredProjectScheduleHandler,
            // To create a json file
            jsonFileProcessHandler));
  }
}
