package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.google.common.base.CharMatcher;
import com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress;
import com.tessi.cxm.pfl.ms8.repository.FlowDocumentAddressRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class SaveOriginalDocumentAddressHandler extends AbstractExecutionHandler {
  private final FlowDocumentAddressRepository flowDocumentAddressRepository;
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var flowDocuments =
        context
            .get(FlowTreatmentConstants.PORTAL_DOCUMENT, PortalDocumentResponse.class)
            .getDocument()
            .getFlowDocuments();
    String preferredUsername = AuthenticationUtils.getPreferredUsername();
    List<FlowDocumentAddress> flowDocumentAddresses =
        flowDocuments.stream()
            .flatMap(
                fDoc -> {
                  if (CollectionUtils.isEmpty(fDoc.getAddress())) {
                    return Stream.empty();
                  }
                  return fDoc.getAddress().entrySet().stream()
                      .filter(
                          docAddress ->
                              StringUtils.hasText(docAddress.getKey())
                                  && StringUtils.hasText(docAddress.getValue()))
                      .map(
                          docAddress -> {
                            var lineNumber =
                                Integer.parseInt(
                                    CharMatcher.inRange('0', '9').retainFrom(docAddress.getKey()));
                            FlowDocumentAddress flowDocumentAddress = new FlowDocumentAddress();
                            flowDocumentAddress.setAddressLineNumber(lineNumber);
                            flowDocumentAddress.setOriginalAddress(docAddress.getValue());
                            flowDocumentAddress.setOwnerId(
                                depositedFlowLaunchRequest.getIdCreator());
                            flowDocumentAddress.setCreatedBy(preferredUsername);
                            flowDocumentAddress.setCreatedAt(LocalDateTime.now());
                            flowDocumentAddress.setFlowId(depositedFlowLaunchRequest.getUuid());
                            flowDocumentAddress.setDocId(fDoc.getUuid());
                            return flowDocumentAddress;
                          });
                })
            .collect(Collectors.toList());
    this.flowDocumentAddressRepository.saveAll(flowDocumentAddresses);
    return ExecutionState.NEXT;
  }
}
