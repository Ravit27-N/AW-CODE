package com.tessi.cxm.pfl.ms5.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginAttemptDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "UserName field is required!")
    @Schema(type = "string", required = true, example = "exemple@tessi.fr")
    private String userName;

    @NotNull
    @Schema(type = "boolean", required = true)
    private Boolean loginStatus = true;

    @Schema(type = "String")
    private String password;
}
