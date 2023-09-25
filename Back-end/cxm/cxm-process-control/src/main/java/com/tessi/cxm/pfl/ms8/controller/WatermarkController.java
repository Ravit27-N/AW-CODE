package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.dto.WatermarkDto;
import com.tessi.cxm.pfl.ms8.service.WatermarkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/watermark")
@RequiredArgsConstructor
@Tag(name = "Watermark", description = "Manage watermark.")
public class WatermarkController {
  private final WatermarkService watermarkService;

  @GetMapping("/{flowId}")
  public ResponseEntity<WatermarkDto> getWatermark(@PathVariable("flowId") String flowId) {
    return new ResponseEntity<>(this.watermarkService.getWatermark(flowId), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<WatermarkDto> saveWatermark(@Valid @RequestBody WatermarkDto watermarkDto) {
    return new ResponseEntity<>(this.watermarkService.saveWatermark(watermarkDto), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<WatermarkDto> updateWatermark(
      @Valid @RequestBody WatermarkDto watermarkDto) {
    return new ResponseEntity<>(this.watermarkService.updateWatermark(watermarkDto), HttpStatus.OK);
  }

  @DeleteMapping("/{flowId}")
  public ResponseEntity<HttpStatus> deleteWatermark(@PathVariable("flowId") String flowId) {
    this.watermarkService.deleteWatermark(flowId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
