package com.innovationandtrust.utils.tinyurl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "signature.tiny-url", ignoreInvalidFields = true)
public class TinyUrlProperty {
    private String token;
}
