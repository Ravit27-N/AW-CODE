package com.innovationandtrust.configuration.webmvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
public class CommonWebMvcConfigurer implements WebMvcConfigurer {
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.stream()
        .filter(StringHttpMessageConverter.class::isInstance)
        .forEach(
            converter ->
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));
    converters.stream()
        .filter(MappingJackson2HttpMessageConverter.class::isInstance)
        .findFirst()
        .ifPresent(
            converter ->
                ((MappingJackson2HttpMessageConverter) converter)
                    .setDefaultCharset(StandardCharsets.UTF_8));
  }

  @Bean
  public ConfigurableServletWebServerFactory webServerFactory() {
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    factory.addConnectorCustomizers(
        connector -> {
          connector.setProperty("relaxedQueryChars", "|{}[]");
          connector.setProperty("relaxedPathChars", "\"#<>[\\]^`{|}");
        });
    return factory;
  }
}
