package com.innovationandtrust.utils.sms;

import jakarta.validation.constraints.NotEmpty;
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
@ConfigurationProperties(prefix = "signature.sms-service")
public class SMSProperty {

    @NotEmpty(message = "sms-service `url` property is required and cannot be empty")
    private String url;
    private String accessToken;
    @NotEmpty(message = "sms-service `sender` property is required and cannot be empty")
    private String sender;
    private boolean isEnable;
    @NotEmpty(message = "sms-service `product-token` property is required and cannot be empty")
    private String productToken;

}
