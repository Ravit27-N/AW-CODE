package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration class for ShareId client. */
@Configuration
@EnableConfigurationProperties
public class WebClientConfigurer extends CommonWebMvcConfigurer {}
