package com.innovationandtrust.utils.authenticationUtils;

import java.util.List;
import lombok.Getter;

public record AuthenticatedUser(@Getter String uuid, @Getter String email, @Getter List<String> grantedAuthorities) {

}