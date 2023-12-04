package com.innovationandtrust.process.chain.handler.eid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.EIDProperty;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
class RequestToSignHandlerTest {

  private RequestToSignHandler requestToSignHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {

    requestToSignHandler =
        spy(new RequestToSignHandler(mock(ApiNgFeignClientFacade.class), mock(EIDProperty.class)));
    context = UnitTestProvider.getContext();
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[RequestToSignHandler] Request to Sign Process Handler")
  void testRequestToSignHandler() {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.QUALIFY.getValue());
    this.requestToSignHandler.execute(context);
    verify(this.requestToSignHandler).execute(context);
  }

  @Test
  @DisplayName("[RequestToSignHandler] Request to Sign Process Not Qualify Project")
  void testRequestToSignHandlerNotQualify() {
    this.requestToSignHandler.execute(context);
    verify(this.requestToSignHandler).execute(context);
  }

  @Test
  @DisplayName("[RequestToSignHandler] Request to Sign Process OTP Failed")
  void testRequestToSignHandlerOTPFailed() {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.QUALIFY.getValue());
    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            (Participant participant) -> {
              participant.setErrorValidationOtp(3);
            });
    final var exception =
        assertThrows(
            BadRequestException.class,
            () -> this.requestToSignHandler.execute(context),
            UnitTestConstant.ASSERT_EXCEPTION);
    log.info("[Exception thrown]: {}", exception.getMessage());

    assertEquals(
        "Cannot request sign (OTP validation has been tried many times).",
        exception.getMessage(),
        UnitTestConstant.ASSERT_EQUALS);
  }
}
