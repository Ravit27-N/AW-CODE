package com.innovationandtrust.profile.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "signature.profile-front-end")
public class FrontEndProperty {
    private String frontEndBaseUrl;
    private String superAdminUrl;
}
