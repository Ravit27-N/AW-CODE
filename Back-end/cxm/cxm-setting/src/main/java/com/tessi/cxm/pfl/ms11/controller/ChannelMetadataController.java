package com.tessi.cxm.pfl.ms11.controller;

import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataRequestDto;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataResponseDto;
import com.tessi.cxm.pfl.ms11.service.ChannelMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/setting/channel-metadata")
@Tag(name = "Channel Metadata", description = "The API endpoints to manage channel metadata")
public class ChannelMetadataController {

  private final ChannelMetaDataService channelMetaDataService;

  @PostMapping
  @Operation(
      operationId = "postChannel-metadata",
      summary = "To create channel metadata",
      description = "Post channel metadata. It return data created",
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "Save channel metadata success response."))
  public ResponseEntity<ChannelMetadataRequestDto> save(
      @RequestBody @Valid ChannelMetadataRequestDto channelMetadataRequestDto) {
    return ResponseEntity.ok(this.channelMetaDataService.save(channelMetadataRequestDto));
  }

  @GetMapping
  public ResponseEntity<ChannelMetadataResponseDto> getChannelMetadataPerTypes(
      @RequestParam(value = "customer", defaultValue = "", required = false) String customer,
      @RequestParam("types") List<String> type) {
    return ResponseEntity.ok(
        this.channelMetaDataService.getChannelMetadataPerTypes(customer, type));
  }
}
