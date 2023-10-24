package com.innovationandtrust.profile.Constant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.boot.json.JsonParseException;

public abstract class AbstractTest {
  protected byte[] toJson(Object object) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writeValueAsBytes(object);
  }

  protected String mapToJson(Object obj) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(obj);
  }

  protected <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, IOException {
    ObjectMapper objectMapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.IGNORE_UNDEFINED);
    return objectMapper.readValue(json, clazz);
  }

  protected <T> T mapFromJsonT(String json, TypeReference<T> clazz)
      throws JsonParseException, IOException {
    ObjectMapper objectMapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    return objectMapper.readValue(json, clazz);
  }
}
