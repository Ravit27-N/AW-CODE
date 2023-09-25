package com.tessi.cxm.pfl.ms8.config;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.shared.service.mirrorlink.HmacSHA256MirrorService;
import com.tessi.cxm.pfl.shared.service.mirrorlink.MirrorTokenService;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MirrorTokenConfig {

  private static final byte[] SECRET = FlowTreatmentConstants.MIRROR_TOKEN_SECRET.getBytes(
      StandardCharsets.UTF_8);

  @Bean
  HmacSHA256MirrorService hmacSHA256MirrorService(ObjectMapper objectMapper) {
    return new HmacSHA256MirrorService(SECRET, objectMapper);
  }


  @Bean
  MirrorTokenService mirrorLinkService(HmacSHA256MirrorService hmacSHA256MirrorService) {
    return new MirrorTokenService(hmacSHA256MirrorService);
  }
}
