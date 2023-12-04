package com.innovationandtrust.process.chain.handler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class DossierProcessHandlerTest {
  private DossierProcessHandler dossierProcessHandler;
  @Mock private SignatureIdentityVerificationFeignClient verificationFeignClient;
  private Project project;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    dossierProcessHandler = spy(new DossierProcessHandler(verificationFeignClient));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  @Test
  @DisplayName("[Dossier Process]")
  void dossierProcess() {
    this.dossierProcessHandler.execute(context);
    verify(this.dossierProcessHandler).execute(context);
  }

  @Test
  @DisplayName("[Dossier Process] Simple level")
  void dossierProcessSimpleLevel() {
    project.setSignatureLevel(SignatureSettingLevel.SIMPLE.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);

    this.dossierProcessHandler.execute(context);
    verify(this.dossierProcessHandler).execute(context);
  }
}
