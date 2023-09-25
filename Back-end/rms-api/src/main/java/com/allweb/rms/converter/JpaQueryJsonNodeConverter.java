package com.allweb.rms.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

@Component
public class JpaQueryJsonNodeConverter implements AttributeConverter<JsonNode, String> {

  private final ObjectMapper mapper;

  public JpaQueryJsonNodeConverter(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String convertToDatabaseColumn(JsonNode jsonNode) {
    return jsonNode.asText();
  }

  @Override
  public JsonNode convertToEntityAttribute(String s) {
    try {
      return this.mapper.readTree(s);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
