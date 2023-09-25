package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.dto.FlowDocumentAddressDto;
import com.tessi.cxm.pfl.ms8.dto.FlowDocumentAddressLineDto;
import com.tessi.cxm.pfl.ms8.service.FlowDocumentAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flow-document-address")
@Tag(name = "FlowDocumentAddress", description = "Manage FlowDocumentAddress.")
@RequiredArgsConstructor
public class FlowDocumentAddressController {
  private final FlowDocumentAddressService flowDocumentAddressService;

  @GetMapping("/{flowId}/{docId}")
  public ResponseEntity<List<FlowDocumentAddressLineDto>> getFlowDocumentAddress(
      @PathVariable("flowId") String flowId, @PathVariable("docId") String docId) {
    return new ResponseEntity<>(
        flowDocumentAddressService.getFlowDocumentAddress(flowId, docId), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<FlowDocumentAddressDto> updateFlowDocumentAddress(
      @RequestBody @Valid FlowDocumentAddressDto flowDocumentAddressDto) {
    return new ResponseEntity<>(
        this.flowDocumentAddressService.updateFlowDocumentAddress(flowDocumentAddressDto),
        HttpStatus.OK);
  }
}
