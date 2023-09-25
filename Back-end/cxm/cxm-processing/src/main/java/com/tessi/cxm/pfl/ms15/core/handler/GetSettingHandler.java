package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetSettingHandler extends AbstractExecutionHandler {
  private final ObjectMapper mapper;
  private final SettingFeignClient settingFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var token = context.get(ProcessingConstant.TOKEN_KEY, String.class);
    var idCreator = context.get(ProcessingConstant.ID_CREATOR, Long.class);
    var modelName = context.get(ProcessingConstant.MODEL_NAME, String.class);
    var flowType = context.get(ProcessingConstant.FLOW_TYPE, String.class);
    var response = this.settingFeignClient.extractSetting(modelName, idCreator, flowType, token);
    if (Objects.isNull(response.getData())) {
      if (log.isDebugEnabled()) {
        log.debug("No configuration setting for this flowType {}", flowType);
      }
      log.info("No configuration setting for this flowType {}", flowType);
      return ExecutionState.END;
    }
    var configPath = getConfigPath(response);
    context.put(ProcessingConstant.CONFIG_PATH, configPath);
    return ExecutionState.NEXT;
  }

  private String getConfigPath(FlowProcessingResponse<SettingResponse> response) {
    return mapper.convertValue(response.getData(), PortalSettingResponse.class).getConfigPath();
  }
}
