package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.enums.SignatureLevel;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.DocumentDetail;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.EIDProperty;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EIDSignProcessHandlerTest {

  private EIDSignProcessHandler eidSignProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {

    eidSignProcessHandler =
        spy(
            new EIDSignProcessHandler(
                mock(ApiNgFeignClientFacade.class),
                mock(EIDProperty.class),
                mock(ModelMapper.class),
                mock(ProjectFeignClient.class)));
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[EIDSignProcessHandler] Sign Process Handler")
  void testEIDSignProcessHandler() {
    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.QUALIFY.getValue());
    project.getDocuments().forEach(document -> document.setDetails(new ArrayList<>()));
    this.eidSignProcessHandler.execute(context);
    verify(this.eidSignProcessHandler).execute(context);
  }

  @Test
  @DisplayName("[EIDSignProcessHandler] Sign Process Handler Not Qualify")
  void testEIDSignProcessHandlerNotQualify() {
    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.SIMPLE.getValue());
    project.getDocuments().forEach(document -> document.setDetails(new ArrayList<>()));
    this.eidSignProcessHandler.execute(context);
    verify(this.eidSignProcessHandler).execute(context);
  }
}
