package com.innovationandtrust.profile.Constant;

import com.innovationandtrust.profile.constant.TemplateType;
import com.innovationandtrust.profile.model.dto.TemplateDto;
import com.innovationandtrust.profile.model.dto.TemplateMessageDto;
import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.share.constant.NotificationChannel;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ScenarioStepConstant;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Constant {
  public static final String templateName = "Template";
  public static final long templateId = 1L;
  public static final long userId = 1L;
  public static final String search = "search";
  public static final String templateBaseApi = "v1/templates";

  public static String path(String... context) {
    StringBuilder fullPath = new StringBuilder();
    for (String path : context) {
      fullPath.append("/").append(path);
    }
    return fullPath.toString();
  }

  public static TemplateDto getTemplateDto() {
    TemplateMessageDto templateMessageDto =
        new TemplateMessageDto(templateId, "Tittle invitation", "Message invitation", 1, 1);

    return TemplateDto.builder()
        .id(templateId)
        .step(1)
        .name(templateName)
        .folderId(1L)
        .businessUnitId(1L)
        .createdBy(userId)
        .signature(1)
        .approval(0)
        .viewer(0)
        .recipient(0)
        .favorite(true)
        .usedCount(1)
        .type(TemplateType.DEFAULT.name())
        .signProcess(ScenarioStepConstant.COSIGN)
        .participants(Collections.emptyList())
        .notificationService(NotificationChannel.EMAIL.getName())
        .templateMessage(templateMessageDto)
        .build();
  }

  public static UserParticipantDto getParticipantDto() {
    return UserParticipantDto.builder()
        .templateId(templateId)
        .userId(userId)
        .firstName("Anna")
        .lastName("ECO")
        .email("signature@ccertigna.fr")
        .phone("010222200")
        .sortOrder(0)
        .role(ParticipantRole.SIGNATORY.getRole())
        .build();
  }
}
