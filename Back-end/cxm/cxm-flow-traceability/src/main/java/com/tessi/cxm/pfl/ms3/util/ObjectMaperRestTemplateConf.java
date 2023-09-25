
package com.tessi.cxm.pfl.ms3.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class ObjectMaperRestTemplateConf {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}






