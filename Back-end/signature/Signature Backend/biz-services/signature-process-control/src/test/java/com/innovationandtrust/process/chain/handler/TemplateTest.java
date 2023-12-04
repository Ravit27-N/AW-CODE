package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class TemplateTest {
  private DossierProcessHandler dossierProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    dossierProcessHandler = spy(new DossierProcessHandler(any()));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[template]")
  void template() {
    this.dossierProcessHandler.execute(context);
    verify(this.dossierProcessHandler).execute(context);
  }
}
