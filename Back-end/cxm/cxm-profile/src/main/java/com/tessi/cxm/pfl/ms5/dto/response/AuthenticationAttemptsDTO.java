package com.tessi.cxm.pfl.ms5.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationAttemptsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(type = "boolean")
    private Boolean isBlocked;

    @Schema(type = "Long")
    private long currentCountOfRemainingAttempts = 0;

    @Schema(type = "Long")
    private long minutesRemaining;

    @Schema(type = "boolean")
    private Boolean forceToChangePassword;
}
