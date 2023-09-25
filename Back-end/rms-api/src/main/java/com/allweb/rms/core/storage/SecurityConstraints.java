package com.allweb.rms.core.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityConstraints {
  @Builder.Default private boolean locked = false;
  @Builder.Default private boolean readable = true;
  @Builder.Default private boolean writable = true;
}
