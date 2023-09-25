package com.allweb.rms.entity.dto;

import com.allweb.rms.converter.JpaQueryJsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserRole implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  @NotNull @NotEmpty private String name;
  private String description;

  @Schema(
      example =
          "[\n"
              + "    {\n"
              + "      \"moduleId\": 0,\n"
              + "      \"userRoleId\": \"string\",\n"
              + "      \"viewAble\": true,\n"
              + "      \"insertAble\": true,\n"
              + "      \"deleteAble\": true,\n"
              + "      \"editAble\": true\n"
              + "    }\n"
              + "  ]")
  @Convert(converter = JpaQueryJsonNodeConverter.class)
  private transient JsonNode userRoleDetails;

  public JsonNode getUserRoleDetails() {
    if (this.userRoleDetails == null) {
      return JsonNodeFactory.instance.arrayNode();
    }
    return userRoleDetails;
  }
}
