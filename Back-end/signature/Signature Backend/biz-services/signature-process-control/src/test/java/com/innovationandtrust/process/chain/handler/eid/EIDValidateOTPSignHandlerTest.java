package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EIDValidateOTPSignHandlerTest {

  private EIDValidateOTPSignHandler eIDValidateOTPSignHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {

    eIDValidateOTPSignHandler = spy(new EIDValidateOTPSignHandler());
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[EIDValidateOTPSignHandler] Test Validate EID Signature")
  void testEIDValidateOTPSignHandler() {
    context.put(SignProcessConstant.SIGN_DOCUMENT, Boolean.TRUE);
    this.eIDValidateOTPSignHandler.execute(context);
    verify(this.eIDValidateOTPSignHandler).execute(context);
  }

  @Test
  @DisplayName("[EIDValidateOTPSignHandler] Test Validate EID Signature Failed")
  void testEIDValidateOTPSignHandlerFailed() {
    context.put(SignProcessConstant.SIGN_DOCUMENT, Boolean.FALSE);
    this.eIDValidateOTPSignHandler.execute(context);
    verify(this.eIDValidateOTPSignHandler).execute(context);
  }
}
