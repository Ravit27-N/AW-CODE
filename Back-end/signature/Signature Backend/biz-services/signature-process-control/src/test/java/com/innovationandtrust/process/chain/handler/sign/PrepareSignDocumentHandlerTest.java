package com.innovationandtrust.process.chain.handler.sign;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class PrepareSignDocumentHandlerTest {
  private PrepareSignDocumentHandler prepareSignDocumentHandler;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {
    ModelMapper modelMapper = new ModelMapper();

    prepareSignDocumentHandler =
        spy(
            new PrepareSignDocumentHandler(
                apiNgFeignClient, modelMapper, UnitTestProvider.fileProvider()));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[PrepareSignDocumentHandler]")
  void testExecute() {

    this.prepareSignDocumentHandler.execute(context);
    verify(this.prepareSignDocumentHandler).execute(context);
  }
}
