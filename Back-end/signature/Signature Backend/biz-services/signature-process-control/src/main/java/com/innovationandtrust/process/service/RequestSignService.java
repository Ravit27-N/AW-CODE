package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.ProjectIndividualSignExecutionManager;
import com.innovationandtrust.process.chain.execution.expired.UpdateProjectExecutionManager;
import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.utils.DateUtil;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidTTLValueException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestSignService {

  private final ProjectCoSignExecutionManager coSignExecutionManager;

  private final ProjectCounterSignExecutionManager counterSignExecutionManager;

  private final ProjectIndividualSignExecutionManager individualSignExecutionManager;

  private final CorporateProfileFeignClient corporateProfileFeignClient;

  private final UpdateProjectExecutionManager updateProjectExecutionManager;

  private final ApiNGProperty apiNgProperty;

  private final CompleteSignProcessExecutionManager completeSignProcessExecutionManager;

  /**
   * Handling a process of request to sign the documents.
   *
   * @param project refers to an object of {@link Project}
   */
  public void requestSign(Project project) {
    var checkDate = DateUtil.plushDays(new Date(), 1);
    var expiredDate = project.getDetail().getExpireDate();
    if (DateUtil.removeTime(checkDate).after(expiredDate)) {
      throw new InvalidTTLValueException("You must select a date in the future");
    }
    var context = new ExecutionContext();
    if (!StringUtils.hasText(project.getFlowId())) {
      project.setFlowId(UUID.randomUUID().toString());
    }
    if (Objects.isNull(project.getCorporateInfo())) {
      var corporateInfo =
          this.corporateProfileFeignClient.findCorporateInfo(project.getCreatedBy());
      ProcessControlUtils.checkCompanyInfo(corporateInfo, project.getFlowId());
      project.setCorporateInfo(corporateInfo);
      log.info("Corporate info: {}", project.getCorporateInfo());
    }
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_DOC_FOR_SIGN);
    this.setUri(context, project.getCorporateInfo().getCompanyUuid());
    switch (project.getTemplate().getSignProcess()) {
      case COUNTER_SIGN -> {
        // To create a json file to control the signing process flow
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.CREATE);
        this.counterSignExecutionManager.execute(context);
      }
      case COSIGN -> this.coSignExecutionManager.execute(context);
      default -> {
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.CREATE);
        this.individualSignExecutionManager.execute(context);
      }
    }
  }

  /**
   * Handling the process of updating project expire date.
   *
   * @param flowId refers to unique flowId of {@link Project}
   * @param expireDate refers to new expire date of {@link Project}
   */
  public void updateExpireDate(String flowId, String expireDate) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    context.put(SignProcessConstant.NEW_EXPIRE_DATE, Date.from(Instant.parse(expireDate)));
    this.updateProjectExecutionManager.execute(context);
  }

  private void setUri(ExecutionContext context, String companyUuid) {
    log.info("Setting context path for: {} to URI ... ", companyUuid);
    context.put(
        SignProcessConstant.BASE_URI,
        URI.create(apiNgProperty.getUrl() + companyUuid + apiNgProperty.getContextPath()));
  }

  public FileResponse downloadManifest(String flowId) {
    var ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    ctx.put(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_MANIFEST);
    this.completeSignProcessExecutionManager.execute(ctx);
    return ctx.get(SignProcessConstant.DOWNLOAD_MANIFEST, FileResponse.class);
  }

  public boolean isFinished(String flowId) {
    var ctx = new ExecutionContext();
    ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    ctx.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.CHECK_PROJECT_FINISH);
    this.completeSignProcessExecutionManager.execute(ctx);
    return ctx.get(SignProcessConstant.IS_PROJECT_FINISHED, Boolean.class);
  }
}
