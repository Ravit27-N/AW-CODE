package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.ProjectDto;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class GetUserInfoHandlerTest {
  private GetUserInfoHandler getUserInfoHandler;
  @Mock private ProjectFeignClient projectFeignClient;
  private Project project;
  private ExecutionContext context;
  private ModelMapper modelMapper;

  @BeforeEach
  public void setup() {
    modelMapper = new ModelMapper();
    getUserInfoHandler = spy(new GetUserInfoHandler(projectFeignClient));
    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  @Test
  @DisplayName("[Get User Info handler]")
  void testGetUserInfo() {
    // given
    var projectDto = this.modelMapper.map(project, ProjectDto.class);

    // when
    when(this.projectFeignClient.findExternalById(anyLong())).thenReturn(projectDto);

    this.getUserInfoHandler.execute(context);
    verify(this.getUserInfoHandler).execute(context);
  }
}
