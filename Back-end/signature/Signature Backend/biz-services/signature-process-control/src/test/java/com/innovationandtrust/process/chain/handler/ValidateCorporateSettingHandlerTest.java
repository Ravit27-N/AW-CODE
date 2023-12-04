package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ValidateCorporateSettingHandlerTest {
  private ValidateCorporateSettingHandler validateCorporateSettingHandler;
  private ExecutionContext context;
  private Project project;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;

  @BeforeEach
  public void setup() {

    validateCorporateSettingHandler =
        spy(new ValidateCorporateSettingHandler(corporateProfileFeignClient));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  @Test
  @DisplayName("[RefusingProcessHandler]")
  void testExecute() {
    // when
    when(this.corporateProfileFeignClient.getCompanySettingByLevel(anyString(), anyString()))
        .thenReturn(UnitTestProvider.signatureLevel(project.getSignatureLevel()));

    this.validateCorporateSettingHandler.execute(context);
    verify(this.validateCorporateSettingHandler).execute(context);
  }

  @Test
  @DisplayName("[RefusingProcessHandler] With no companyUuid")
  void testExecuteNoCompanyUuid() {

    project.getCorporateInfo().setCompanyUuid(null);

    // when
    when(this.corporateProfileFeignClient.findCorporateInfo(anyLong()))
        .thenReturn(UnitTestProvider.corporateInfo());

    when(this.corporateProfileFeignClient.getCompanySettingByLevel(anyString(), anyString()))
        .thenReturn(UnitTestProvider.signatureLevel(project.getSignatureLevel()));

    this.validateCorporateSettingHandler.execute(context);
    verify(this.validateCorporateSettingHandler).execute(context);
  }
}
