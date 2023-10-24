package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CloseSession {
    private boolean force = false;
    private String reason;
    @JsonProperty("manifest-data")
    private Object manifestData = new JsonEmpty();
    public CloseSession(boolean force, String reason) {
        this.force = force;
        this.reason = reason;
    }
}
