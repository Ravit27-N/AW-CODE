package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.FlowFileControl;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.Base64FileSupporter;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.utils.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Handling process of initialize json file to cxm-file-control-management service.
 *
 * @author Sakal TUM
 * @author Vichet CHANN
 * @version 1.0.0
 */
@Component
@Slf4j
@AllArgsConstructor
public class CreateFileControlJsonHandler extends AbstractExecutionHandler {
  private final ModelMapper modelMapper;
  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {

    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      return this.createBatchJsonFileControl(context);
    } else {
      return this.createPortalJsonFileControl(context);
    }
  }

  private ExecutionState createBatchJsonFileControl(ExecutionContext context) {
    log.info("--- Start creating JSON ---");
    var flowFileControl = this.getFlowFileControl(context);

    log.info(
        FlowTreatmentConstants.Logs.CREATING_JSON_FILE_CONTROL_MESSAGE, flowFileControl.getUuid());
    flowFileControl.setDepositDate(this.formatDate(new Date()));

    this.fileCtrlMngtFeignClient.createJsonFile(
        flowFileControl, ProcessControlExecutionContextUtils.getBearerToken(context));

    context.put(FlowTreatmentConstants.USER_CREATOR, flowFileControl.getUserName());
    context.put(FlowTreatmentConstants.FLOW_UUID, flowFileControl.getUuid());
    context.put(FlowTreatmentConstants.SERVER_NAME, flowFileControl.getServerName());
    context.put(FlowTreatmentConstants.USERNAME, flowFileControl.getUserName());
    // setting for generate html content.
    context.put(FlowTreatmentConstants.FILE_ID, flowFileControl.getUuid());
    context.put(FlowTreatmentConstants.OWNER_ID, Long.valueOf(flowFileControl.getUserId()));

    log.info(
        FlowTreatmentConstants.Logs.JSON_FILE_CONTROL_CREATED_MESSAGE, flowFileControl.getUuid());
    log.info("--- End creating JSON ---");
    return ExecutionState.NEXT;
  }

  private ExecutionState createPortalJsonFileControl(ExecutionContext context) {
    var portalFlowFileControl = this.getPortalFlowFileControl(context);

    log.info(
        FlowTreatmentConstants.Logs.CREATING_JSON_FILE_CONTROL_MESSAGE,
        portalFlowFileControl.getUuid());

    this.fileCtrlMngtFeignClient.createPortalJsonFile(
        portalFlowFileControl, ProcessControlExecutionContextUtils.getBearerToken(context));

    log.info(
        FlowTreatmentConstants.Logs.JSON_FILE_CONTROL_CREATED_MESSAGE,
        portalFlowFileControl.getUuid());

    return ExecutionState.NEXT;
  }

  private FlowFileControl getFlowFileControl(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var flowFileControl = this.modelMapper.map(depositedFlowLaunchRequest, FlowFileControl.class);
    // Manual assign missing map fields
    flowFileControl.setName(depositedFlowLaunchRequest.getFileName());
    flowFileControl.setServerName(depositedFlowLaunchRequest.getServerName());
    flowFileControl.setUserName(depositedFlowLaunchRequest.getUserName());
    flowFileControl.setVersion("1.0");
    return flowFileControl;
  }

  private PortalFlowFileControl getPortalFlowFileControl(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var portalFlowFileControl =
        this.modelMapper.map(depositedFlowLaunchRequest, PortalFlowFileControl.class);
    // Manual assign missing map fields
    portalFlowFileControl.setDepositDate(
        this.formatDate(depositedFlowLaunchRequest.getDepositDate()));
    portalFlowFileControl.setUserId(String.valueOf(context.get(FlowTreatmentConstants.OWNER_ID, Long.class)));
    portalFlowFileControl.setUserName(depositedFlowLaunchRequest.getUserName());
    portalFlowFileControl.setVersion("1.0");
    return portalFlowFileControl;
  }

  private String formatDate(Date sourceDateTime) {
    return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(sourceDateTime);
  }
}
