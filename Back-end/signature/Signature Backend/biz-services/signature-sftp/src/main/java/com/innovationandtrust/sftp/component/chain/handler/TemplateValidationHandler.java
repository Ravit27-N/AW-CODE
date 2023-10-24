package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.restclient.ProfileFeignClient;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateValidationHandler extends AbstractExecutionHandler {

  private final ProfileFeignClient profileFeignClient;

  private final FileErrorHandler fileErrorHandler;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SftpProcessConstant.PROJECT_KEY, ProjectModel.class);
    var request = context.get(SftpProcessConstant.SFTP_FILE_REQUEST, SftpFileRequest.class);

    if (Objects.isNull(project.getTemplateId())) {
      return ExecutionState.NEXT;
    }

    Optional<Template> templateOpt =
        this.profileFeignClient.findTemplateById(project.getTemplateId(), request.getToken());
    if (templateOpt.isPresent()) {
      var template = templateOpt.get();
      boolean isValidApproval =
          project.isValidParticipantsByRole(RoleConstant.ROLE_APPROVAL, template.getApproval());
      if (!isValidApproval) {
        this.fileErrorHandler.errorNotMatchTemplate(request);
        return ExecutionState.END;
      }
      boolean isValidSignatories =
          project.isValidParticipantsByRole(RoleConstant.ROLE_SIGNATORY, template.getSignature());
      if (!isValidSignatories) {
        this.fileErrorHandler.errorNotMatchTemplate(request);
        return ExecutionState.END;
      }
    } else {
      this.fileErrorHandler.errorTemplateNotFound(request);
      return ExecutionState.END;
    }

    return ExecutionState.NEXT;
  }
}
