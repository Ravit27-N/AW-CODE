package com.innovationandtrust.profile.model.projection;

import lombok.Getter;

public record Company(@Getter Long id, @Getter String name, @Getter String siret, @Getter String uuid) {}
