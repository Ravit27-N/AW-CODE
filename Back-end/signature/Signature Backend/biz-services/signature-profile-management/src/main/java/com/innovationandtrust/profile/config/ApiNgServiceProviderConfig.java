package com.innovationandtrust.profile.config;

import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.config.ApiNgServiceProviderConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {ApiNGProperty.class})
public class ApiNgServiceProviderConfig extends ApiNgServiceProviderConfigurer {}
