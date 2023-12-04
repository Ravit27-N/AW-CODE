package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RefusedProjectValidateHandlerTest {

  private RefusedProjectValidateHandler refusedProjectValidateHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {

    refusedProjectValidateHandler = spy(new RefusedProjectValidateHandler());
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("Refused Project Validate Handler")
  void testRefusedProjectValidateHandler() {
    refusedProjectValidateHandler.execute(context);
    verify(refusedProjectValidateHandler).execute(context);
  }
}
