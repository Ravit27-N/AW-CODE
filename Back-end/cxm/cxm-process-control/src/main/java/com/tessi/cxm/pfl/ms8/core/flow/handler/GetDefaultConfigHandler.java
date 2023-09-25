package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.DefaultConfigurationKey;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.CustomerConfigurationDTO;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetDefaultConfigHandler extends AbstractExecutionHandler {
  private final SettingFeignClient settingFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final Map<String, String> defaultConfig = new HashMap<>();
    final String bearerPrefixedToken =
        ProcessControlExecutionContextUtils.getBearerTokenWithPrefix(
            context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class));
    final var customer = this.getCustomerName(context);
    final var modelName = this.getModelName(context);
    try {
      CustomerConfigurationDTO customerConfiguration =
          this.settingFeignClient.getCustomerConfiguration(
              customer, modelName, bearerPrefixedToken);

      for (DefaultConfigurationKey key : DefaultConfigurationKey.values()) {
        ConfigurationEntry configEntry = customerConfiguration.getConfiguration().get(key.name());
        if (ObjectUtils.isNotEmpty(configEntry)) {
          defaultConfig.put(key.name(), configEntry.getValue());
        }
      }
    } catch (Exception e) {
      log.error("Failed to get default configuration by a model \"" + modelName + "\"", e);
    }
    context.put(ProcessControlConstants.DEFAULT_CONFIG_BY_MODEL_NAME, defaultConfig);

    return ExecutionState.NEXT;
  }

  private String getCustomerName(ExecutionContext context) {
    PortalFlowFileControl fileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (fileControl != null) {
      return fileControl.getCustomer();
    }
    return context.get(ProcessControlConstants.CUSTOMER_NAME, String.class);
  }

  private String getModelName(ExecutionContext context) {
    PortalFlowFileControl fileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (fileControl != null) {
      return fileControl.getFlow().getModelName();
    }
    return context.get(ProcessControlConstants.FLOE_MODEL_NAME, String.class);
  }
}
