package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payload {
    @JsonProperty("access_token")
    private String accessToken;
    private int expiresIn;
}
