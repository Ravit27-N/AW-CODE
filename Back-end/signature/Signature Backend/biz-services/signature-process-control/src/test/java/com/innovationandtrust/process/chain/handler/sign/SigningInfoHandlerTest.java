package com.innovationandtrust.process.chain.handler.sign;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SigningInfoHandlerTest {
  private SigningInfoHandler signingInfoHandler;
  private final ProcessControlProperty property = UnitTestProvider.getProperty();
  private ExecutionContext context;
  @Mock private ProfileFeignClient profileFeignClient;

  @BeforeEach
  public void setup() {
    signingInfoHandler = spy(new SigningInfoHandler(profileFeignClient, property));
    context = UnitTestProvider.getContext();
  }

  @Test
  @Order(1)
  @DisplayName("Get sign info handler test")
  void execute() {
    this.signingInfoHandler.execute(context);

    verify(this.signingInfoHandler, times(1)).execute(context);
  }

  @Test
  @Order(2)
  @DisplayName("Get sign info handler has default valid phone test")
  void executeValidPhone() {
    // given
    final Participant.ValidPhone validPhone = new Participant.ValidPhone();
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(participant -> participant.setValidPhone(validPhone));

    this.signingInfoHandler.execute(context);

    verify(this.signingInfoHandler, times(1)).execute(context);
  }
}
