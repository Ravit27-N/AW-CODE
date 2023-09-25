package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.service.HubService;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerServiceProvidersDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ServiceProviderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/hub")
@AllArgsConstructor
@Tag(name = "Hub Management", description = "The API endpoint to manage the hub")
public class HubController {

  private final HubService hubService;

  @Operation(
      operationId = "Get serviceProvider",
      summary = "To read service provider by Channel",
      description = "Get a serviceProvider.",
      responses =
      @ApiResponse(
          responseCode = "200",
          description = "Get serviceProvider success response."))
  @GetMapping("/configuration/service-provider")
  public ResponseEntity<ServiceProviderResponse> getServiceProvider(
      @RequestParam("channel") List<String> channel) {
    return ResponseEntity.ok(this.hubService.getServiceProvider(channel));
  }

  @Operation(
      operationId = "GetcustomerServiceProvider",
      summary = "To get customer service provider",
      description = "Get customer service provider.",
      responses =
      @ApiResponse(
          responseCode = "200",
          description = "Get a customerServiceProvider success response."))
  @GetMapping("/configuration/customer-service-provider")
  public ResponseEntity<CustomerServiceProvidersDto> getCustomerServiceProvider(
      @RequestParam("customer") String customer) {
    return ResponseEntity.ok(this.hubService.getCustomerServiceProvider(customer));
  }

  @Operation(
      operationId = "SavecustomerServiceProvider",
      summary = "To save or update customerServiceProvider",
      description = "Save a customerServiceProvider.",
      responses =
      @ApiResponse(
          responseCode = "200",
          description = "Save customerServiceProvider success response."))
  @PostMapping("/configuration/customer-service-provider")
  public ResponseEntity<CustomerServiceProvidersDto> saveCustomerServiceProvider(
      @RequestBody CustomerServiceProvidersDto customerServiceProviderDto) {
    return ResponseEntity.ok(
        this.hubService.saveCustomerServiceProvider(customerServiceProviderDto));
  }
}
