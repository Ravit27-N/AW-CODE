package com.tessi.cxm.pfl.ms8.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ComposedFlowResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProCompositionResponse;
import com.tessi.cxm.pfl.shared.filectrl.model.FlowFileControl;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_COMPOSITION)
public interface CompositionFeignClient {

  @PostMapping("/api/v1/composition")
  FlowProcessingResponse<ComposedFlowResponse> getComposedFile(
      @RequestParam(value = "flowType") String flowType,
      @RequestParam(value = "standardizedFileId") String standardizedFileId,
      @RequestBody FlowFileControl fileFlowTracing,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PostMapping("/api/v1/composition/campaign")
  FlowProcessingResponse<ComposedFlowResponse> getCampaignComposedFile(
      @RequestParam(value = "flowType") String flowType,
      @RequestParam(value = "standardizedFileId") String standardizedFileId,
      @RequestParam(value = "unsubscribeLink") String unsubscribeLink,
      @RequestBody PortalFlowFileControl fileFlowTracing,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/pre-composition/{fileId}/{idCreator}")
  FlowProcessingResponse<ProCompositionResponse> preComposeFlow(
      @PathVariable("fileId") String fileId,
      @PathVariable("idCreator") Long idCreator,
      @RequestParam(value = "flowType") String flowType,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PostMapping("/api/v1/pre-composition/campaign/{fileId}")
  FlowProcessingResponse<ProCompositionResponse> campaignPreComposeFlow(
      @PathVariable("fileId") String fileId,
      @RequestParam("variables") List<String> variables,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);
}
