package com.innovationandtrust.process.chain.handler.expired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ProjectExpiredScheduleHandlerTest {
  private ProjectExpiredScheduleHandler projectExpiredScheduleHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    projectExpiredScheduleHandler =
        spy(
            new ProjectExpiredScheduleHandler(
                mock(SettingProperties.class), mock(SchedulerHandler.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ProjectExpiredScheduleHandler]")
  void testExecute() {
    // given

    this.projectExpiredScheduleHandler.execute(context);
    verify(this.projectExpiredScheduleHandler).execute(context);
  }
}
