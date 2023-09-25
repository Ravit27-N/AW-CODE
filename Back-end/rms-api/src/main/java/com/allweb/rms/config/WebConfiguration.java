package com.allweb.rms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Value("${pattern.datetime.format}")
  private String dateTimeFormat;

  @Value("${pattern.time.format}")
  private String timeFormat;

  @Value("${pattern.date.format}")
  private String dateFormat;

  @Value("${cors.origin.url:http://10.2.7.2:8084}")
  private List<String> urlOrigin;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] array = urlOrigin.toArray(new String[0]);
    registry.addMapping("/api/v1/connector/**").allowedOrigins(array).allowCredentials(true);
    registry
        .addMapping("/api/v1/**")
        .allowedOrigins(array)
        .allowedMethods(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name());
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperBuilderCustomizer() {
    return jacksonObjectMapperBuilder ->
        jacksonObjectMapperBuilder
            .serializers(
                new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)),
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormat)),
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
            .deserializers(
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)),
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timeFormat)),
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
  }

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.timeZone(TimeZone.getTimeZone("Asia/Bangkok"));

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
    objectMapper.registerModule(new Jdk8Module());

    builder.configure(objectMapper);
    return builder;
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .mediaType("xml", MediaType.APPLICATION_XML)
        .mediaType("json", MediaType.APPLICATION_JSON)
        .favorParameter(true)
        .defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new Jaxb2RootElementHttpMessageConverter());
  }
}
