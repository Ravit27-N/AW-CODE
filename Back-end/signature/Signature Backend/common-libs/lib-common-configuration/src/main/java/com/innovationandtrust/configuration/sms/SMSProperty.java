package com.innovationandtrust.configuration.sms;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("signature.sms-service")
public class SMSProperty {

    @NotEmpty(message = "sms-service `url` property is required and cannot be empty")
    private String url;
    private String accessToken;
    @NotEmpty(message = "sms-service `sender` property is required and cannot be empty")
    private String sender;
    @NotEmpty(message = "sms-service `is-enable` property is required and cannot be empty")
    private Boolean isEnable;
    @NotEmpty(message = "sms-service `product-token` property is required and cannot be empty")
    private String productToken;

}
