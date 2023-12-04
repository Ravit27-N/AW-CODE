package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.eid.EIDCoSignDocumentExecuteManager;
import com.innovationandtrust.process.chain.execution.eid.EIDCounterSignDocumentExecuteManager;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class EIDSigningProcessDecisionHandlerTest {

  private EIDSigningProcessDecisionHandler eIDSigningProcessDecisionHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {

    eIDSigningProcessDecisionHandler =
        spy(
            new EIDSigningProcessDecisionHandler(
                mock(EIDCoSignDocumentExecuteManager.class),
                mock(EIDCounterSignDocumentExecuteManager.class)));
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("EID Signing Process Decision Handler")
  void testEIDSigningProcessDecisionHandler() {
    this.eIDSigningProcessDecisionHandler.execute(context);
    verify(this.eIDSigningProcessDecisionHandler).execute(context);
  }
}
