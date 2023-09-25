package com.tessi.cxm.pfl.ms32.controller;

import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceResponseDto;
import com.tessi.cxm.pfl.ms32.service.FilterPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/preference/filter")
@RequiredArgsConstructor
public class FilterPreferenceController {

  private final FilterPreferenceService filterPreferenceService;

  @PostMapping
  public ResponseEntity<UserFilterPreferenceDto> save(
      @RequestBody UserFilterPreferenceDto dto, @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        filterPreferenceService.save(dto, headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @GetMapping
  public ResponseEntity<UserFilterPreferenceResponseDto> getUserFilterPreference(
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        filterPreferenceService.getUserFilterCriteria(headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }
}
