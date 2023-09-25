package com.tessi.cxm.pfl.ms5.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUsersProfilesRequestDTO implements Serializable {

  @NotEmpty
  @ArraySchema(
      schema = @Schema(type = "string", example = "userId1", description = "List of user's ids."))
  private List<String> userIds;

  @NotEmpty private List<Long> profiles;
}
