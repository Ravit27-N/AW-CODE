package com.innovationandtrust.process.chain.handler.webhook;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.model.corporateprofile.NotificationDto;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.WebHookResult;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.webhook.WebHookFeignClient;
import com.innovationandtrust.utils.webhook.model.NotificationProjectStatus;
import feign.Feign;
import feign.Target;
import feign.form.spring.SpringFormEncoder;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Request to api callback for each company. */
@Slf4j
@Component
public class ProjectWebHookHandler extends AbstractExecutionHandler {
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final WebHookFeignClient webHookFeignClient;

  /** To new webhook feignClient. */
  public ProjectWebHookHandler(CorporateProfileFeignClient corporateProfileFeignClient) {
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.webHookFeignClient =
        Feign.builder()
            .encoder(
                new SpringFormEncoder(
                    new SpringEncoder(
                        () ->
                            new HttpMessageConverters(new RestTemplate().getMessageConverters()))))
            .target(Target.EmptyTarget.create(WebHookFeignClient.class));
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var notificationDto =
        context.get(SignProcessConstant.WEBHOOK_NOTIFICATION, NotificationDto.class);
    var event = context.get(SignProcessConstant.WEBHOOK_EVENT, String.class);
    var comment = context.get(SignProcessConstant.COMMENT, String.class);

    if (Objects.nonNull(event)) {
      notificationDto = getNotificationConfig(notificationDto, project, event);
      // If it is still null, means no webhook event.
      if (Objects.nonNull(notificationDto)) {
        this.notifyWithProjectStatus(project, notificationDto, event, comment);
        // Reset webhook event
        context.put(SignProcessConstant.WEBHOOK_EVENT, null);
        context.put(SignProcessConstant.WEBHOOK_NOTIFICATION, notificationDto);
      }
    }
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private NotificationDto getNotificationConfig(NotificationDto notificationDto, Project project, String event) {
    if (Objects.isNull(notificationDto)) {
      var notificationList =
          this.corporateProfileFeignClient.findNotificationConfigByCompanyId(
              project.getCorporateInfo().getCompanyId());
      if (!notificationList.isEmpty()) {
        //  Currently we use only webhook callback
        return notificationList.get(0);
      }
      var webHookResult = new WebHookResult();
      webHookResult.setCallAt(new Date());
      webHookResult.setEvent(event);
      webHookResult.setUrl(ProjectEventConstant.NO_CONFIGURATION);
      project.getWebHookResults().add(webHookResult);
    }
    return notificationDto;
  }

  private void notifyWithProjectStatus(
      Project project, NotificationDto notificationDto, String event, String comment) {
    var webHookResult = new WebHookResult();
    try {
      webHookResult.setCallAt(new Date());
      webHookResult.setUrl(notificationDto.getUrl());
      webHookResult.setEvent(event);

      this.webHookFeignClient.call(
          URI.create(notificationDto.getUrl()),
          NotificationProjectStatus.builder()
              .projectId(project.getId())
              .projectStatus(project.getStatus())
              .projectExpireDate(project.getDetail().getExpireDate())
              .reason(comment)
              .onEvent(event)
              .build(),
          CommonUsages.getBasicAuthToken(
              notificationDto.getUserName(), notificationDto.getPassword()));

    } catch (FeignClientRequestException e) {
      webHookResult.setErrorMessage(
          String.format("FeignClient error: %s, with status: %s ", e.getMessage(), e.getStatus()));
      log.warn("Webhook feignClient:", e);
    } catch (Exception e) {
      webHookResult.setErrorMessage("Exception error:" + e.getMessage());
      log.warn("Webhook of project error:", e);
    } finally {
      project.getWebHookResults().add(webHookResult);
    }
  }
}
